package com.github.mim1q.minecells.entity.nonliving;

import com.github.mim1q.minecells.block.MineCellsBlockTags;
import com.github.mim1q.minecells.config.MineCellsSyncedConfig;
import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.entity.SewersTentacleEntity;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.network.MineCellsNetwork;
import com.github.mim1q.minecells.network.s2c.ElevatorDestroyedS2CPacket;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.ParticleUtils;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ElevatorEntity extends Entity {
    private static final EntityDataAccessor<Boolean> MOVING = SynchedEntityData.defineId(ElevatorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> GOING_UP = SynchedEntityData.defineId(ElevatorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ROTATED = SynchedEntityData.defineId(ElevatorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> VELOCITY_MODIFIER = SynchedEntityData.defineId(ElevatorEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> MIN_Y = SynchedEntityData.defineId(ElevatorEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MAX_Y = SynchedEntityData.defineId(ElevatorEntity.class, EntityDataSerializers.INT);

    private boolean unbreakable = false;
    protected double serverY;
    protected int interpolationSteps = 0;
    protected int stoppedTicks = 0;
    protected boolean setup = false;
    private boolean wasMoving = false;
    private boolean poweredTop = true;
    private boolean poweredBottom = true;

    private final float maxSpeed = MineCellsSyncedConfig.elevatorSpeed();
    private final float acceleration = MineCellsSyncedConfig.elevatorAcceleration();
    private final float damage = MineCellsSyncedConfig.elevatorDamage();

    protected final ArrayList<LivingEntity> hitEntities = new ArrayList<>();

    public ElevatorEntity(EntityType<? extends ElevatorEntity> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
        this.noCulling = true;
        this.noPhysics = true;
        this.serverY = this.getY();
    }

    public static ElevatorEntity spawn(Level level, int x, int z, int minY, int maxY, boolean isRotated, boolean isGoingUp) {
        ElevatorEntity elevator = new ElevatorEntity(MineCellsEntities.ELEVATOR.get(), level);
        elevator.setPos(x + 0.5D, isGoingUp ? maxY : minY, z + 0.5D);
        elevator.setMaxY(maxY);
        elevator.setMinY(minY);
        elevator.setRotated(isRotated);
        elevator.setGoingUp(isGoingUp);
        level.addFreshEntity(elevator);
        return elevator;
    }

    public void setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(MOVING, false);
        entityData.define(GOING_UP, false);
        entityData.define(ROTATED, false);
        entityData.define(VELOCITY_MODIFIER, 0.0F);
        entityData.define(MIN_Y, (int) getY());
        entityData.define(MAX_Y, (int) getY());
    }

    @Override
    public void tick() {
        super.tick();
        double nextY = getY() + getDeltaMovement().y;

        if (!level().isClientSide) {
            float modifiedAcceleration = isGoingUp() ? acceleration : -acceleration;
            setVelocityModifier(Mth.clamp(modifiedAcceleration + getVelocityModifier(), -1.0F, 1.0F));
            setDeltaMovement(0.0D, maxSpeed * getVelocityModifier(), 0.0D);
            hurtMarked = true;

            boolean canMove = !(nextY < getMinY() || nextY > getMaxY());
            if (isMoving()) {
                stoppedTicks = 0;
            } else {
                if (stoppedTicks == 1) {
                    playSound(MineCellsSounds.ELEVATOR_STOP.get(), 1.0F, 1.0F);
                }
                stoppedTicks++;
            }

            if (!canMove) {
                if (isGoingUp()) {
                    setVelocityModifier(Math.max(getVelocityModifier(), 0.0F));
                } else {
                    setVelocityModifier(Math.min(getVelocityModifier(), 0.0F));
                }
            }
            setMoving(canMove);
            interpolationSteps = 0;
            handlePassengers();
            handleRedstone();
        } else {
            if (wasMoving && !isMoving() && !isGoingUp()) {
                BlockPos pos = new BlockPos(getBlockX(), getMinY() - 1, getBlockZ());
                BlockState state = level().getBlockState(pos);
                if (!state.isAir() && nextY < getMinY()) {
                    ParticleOptions particle = new BlockParticleOption(ParticleTypes.BLOCK, state);
                    for (int i = 0; i < 20; i++) {
                        double rx = random.nextDouble() - 0.5D;
                        double rz = random.nextDouble() - 0.5D;
                        Vec3 vel = new Vec3(rx, 0.1D, rz).normalize();
                        ParticleUtils.addParticle(level(), particle, position().add(vel), vel.scale(10.0D));
                        ParticleUtils.addParticle(level(), ParticleTypes.CAMPFIRE_COSY_SMOKE, position().add(vel), vel.scale(0.01D));
                    }
                }
            }
            wasMoving = isMoving();
        }

        if (interpolationSteps > 0) {
            double e = getY() + (serverY - getY()) / (double) interpolationSteps;
            interpolationSteps--;
            setPos(getX(), e, getZ());
        }

        double clampedY = Mth.clamp(getY() + getDeltaMovement().y, getMinY(), getMaxY());
        setPos(getX(), clampedY, getZ());

        if (isMoving()) {
            if (level().isClientSide) {
                double z = isRotated() ? 1.0D : 0.0D;
                double x = 1.0D - z;
                spawnMovementParticles(new Vec3(-x, 0.0D, -z));
                spawnMovementParticles(new Vec3(x, 0.0D, z));
            } else if (!isGoingUp()) {
                handleEntitiesBelow();
            }
        }

        if (shouldBeRemoved()) {
            discardElevator();
        }
    }

    private boolean shouldBeRemoved() {
        BlockPos pos0 = blockPosition().west();
        BlockPos pos1 = blockPosition().east();
        if (isRotated()) {
            pos0 = blockPosition().south();
            pos1 = blockPosition().north();
        }
        BlockState state0 = level().getBlockState(pos0);
        BlockState state1 = level().getBlockState(pos1);
        if (state0.getBlock() instanceof ChainBlock && state1.getBlock() instanceof ChainBlock) {
            return state0.getValue(ChainBlock.AXIS) != Direction.Axis.Y && state1.getValue(ChainBlock.AXIS) != Direction.Axis.Y;
        }
        return true;
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        if (attacker instanceof Player player && !unbreakable) {
            if (player.getMainHandItem().getItem() instanceof AxeItem) {
                discardElevator();
                return true;
            }
        }
        return false;
    }

    @Override
    public void kill() {
        discardElevator();
    }

    private void discardElevator() {
        if (!level().isClientSide) {
            ItemStack[] items = {
                new ItemStack(Blocks.CHAIN, random.nextInt(3) + 2),
                new ItemStack(MineCellsBlocks.PUTRID_PLANKS.get(), 1),
                new ItemStack(MineCellsItems.ELEVATOR_MECHANISM.get(), random.nextInt(3))
            };
            for (ItemStack itemStack : items) {
                level().addFreshEntity(new ItemEntity(level(), getX(), getY(), getZ(), itemStack));
            }
            playSound(SoundEvents.WOOD_BREAK, 1.0F, 1.0F);
            MineCellsNetwork.CHANNEL.send(
                PacketDistributor.TRACKING_ENTITY.with(() -> this),
                new ElevatorDestroyedS2CPacket(getX(), getY(), getZ())
            );
            discard();
        }
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int lerpSteps, boolean teleport) {
        setPos(x, getY(), z);
        serverY = y;
        interpolationSteps = lerpSteps;
    }

    protected void spawnMovementParticles(Vec3 offset) {
        for (int i = 0; i < 5; i++) {
            double rx = (random.nextDouble() - 0.5D) * 0.5D;
            double rz = (random.nextDouble() - 0.5D) * 0.5D;
            ParticleUtils.addParticle(
                level(),
                ParticleTypes.ELECTRIC_SPARK,
                position().add(offset),
                new Vec3(rx, isGoingUp() ? -1.0D : 1.0D, rz)
            );
        }
    }

    public void handlePassengers() {
        if (isMoving()) {
            addPassengers();
        } else if (stoppedTicks > 1) {
            ejectPassengers();
            hitEntities.clear();
        }
    }

    public void addPassengers() {
        List<LivingEntity> entities = level().getEntitiesOfClass(
            LivingEntity.class,
            getBoundingBox().inflate(0.0D, 0.5D, 0.0D),
            this::canBePassenger
        );
        for (LivingEntity e : entities) {
            if (e instanceof PathfinderMob pathAware) {
                pathAware.getNavigation().stop();
            }
            e.startRiding(this);
        }
    }

    protected boolean canBePassenger(LivingEntity entity) {
        return !hitEntities.contains(entity)
            && entity.getY() >= getY()
            && !entity.isShiftKeyDown()
            && !(entity instanceof SewersTentacleEntity);
    }

    public void handleEntitiesBelow() {
        List<LivingEntity> entities = level().getEntitiesOfClass(
            LivingEntity.class,
            getBoundingBox().move(0.0D, -1.0D, 0.0D),
            e -> !hitEntities.contains(e)
        );
        for (LivingEntity e : entities) {
            if (!hitEntities.contains(e)) {
                e.setDeltaMovement(e.position()
                    .subtract(position())
                    .normalize()
                    .multiply(3.0D, 0.0D, 3.0D)
                    .add(0.0D, 0.5D, 0.0D));
                e.hurt(MineCellsDamageSource.ELEVATOR.get(level()), damage);
                hitEntities.add(e);
            }
        }

        if (!entities.isEmpty()
            && !level().isClientSide
            && getFirstPassenger() instanceof ServerPlayer player
            && level().getServer() != null) {
            Advancement advancement = level().getServer().getAdvancements().getAdvancement(MineCells.id("elevator"));
            if (advancement != null) {
                player.getAdvancements().award(advancement, "entity_squashed");
            }
        }
    }

    public void handleRedstone() {
        boolean top = checkSignal(getMaxY());
        boolean bottom = checkSignal(getMinY());
        if (top && !poweredTop && !isGoingUp()) {
            startMoving(true, true);
        } else if (bottom && !poweredBottom && isGoingUp()) {
            startMoving(false, true);
        }
        poweredTop = top;
        poweredBottom = bottom;
    }

    protected boolean checkSignal(int y) {
        final Vec3i[] offsets = {
            new Vec3i(-2, 0, 0),
            new Vec3i(0, 0, -2),
            new Vec3i(0, 0, 2),
            new Vec3i(2, 0, 0)
        };
        BlockPos pos = new BlockPos(getBlockX(), y, getBlockZ());
        for (Vec3i offset : offsets) {
            if (level().getBestNeighborSignal(pos.offset(offset)) > 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean validateShaft(Level level, int x, int z, int minY, int maxY, boolean rotated) {
        final Vec3i[] offsets = {
            new Vec3i(-1, 0, -1),
            new Vec3i(-1, 0, 0),
            new Vec3i(-1, 0, 1),
            new Vec3i(0, 0, -1),
            new Vec3i(0, 0, 0),
            new Vec3i(0, 0, 1),
            new Vec3i(1, 0, -1),
            new Vec3i(1, 0, 0),
            new Vec3i(1, 0, 1),
        };
        int chain0 = 1;
        int chain1 = 7;
        if (rotated) {
            chain0 = 3;
            chain1 = 5;
        }
        for (int i = 0; i < 9; i++) {
            boolean chain = i == chain0 || i == chain1;
            for (int y = minY; y <= maxY + 1; y++) {
                if (i == 4 && (y == minY || y == maxY)) {
                    continue;
                }
                BlockPos offsetPos = new BlockPos(x, y, z).offset(offsets[i]);
                BlockState state = level.getBlockState(offsetPos);
                if (chain) {
                    if (!(state.is(MineCellsBlockTags.ELEVATOR_CHAINS) && state.hasProperty(ChainBlock.AXIS))
                        || state.getValue(ChainBlock.AXIS) != Direction.Axis.Y) {
                        return false;
                    }
                } else if (state.isSolidRender(level, offsetPos)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean startMoving(boolean isGoingUp, boolean fromRedstone) {
        if ((!isMoving() || fromRedstone)
            && validateShaft(level(), getBlockX(), getBlockZ(), getMinY(), getMaxY(), isRotated())) {
            if (!level().isClientSide && (stoppedTicks > 5 || fromRedstone)) {
                setGoingUp(isGoingUp);
                if (!isMoving() && stoppedTicks > 5) {
                    setVelocityModifier(0.0F);
                    playSound(MineCellsSounds.ELEVATOR_START.get(), 1.0F, 1.0F);
                }
                setMoving(true);
            }
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 hitPos, InteractionHand hand) {
        return interact(player, hand);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        boolean result = startMoving(!isGoingUp(), false);
        if (result && !level().isClientSide) {
            addPassengers();
        }
        return result ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return new ItemStack(MineCellsBlocks.ELEVATOR_ASSEMBLER.get());
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public void positionRider(Entity passenger, MoveFunction callback) {
        callback.accept(passenger, passenger.xo, getY() + 0.5D, passenger.zo);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        if (isMoving()) {
            return new Vec3(passenger.xo, getY(), passenger.zo);
        }
        return new Vec3(passenger.xo, getY() + 0.5D, passenger.zo);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return true;
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        if (type != MoverType.SELF) {
            return;
        }
        super.move(type, movement);
    }

    @Override
    public boolean canBeCollidedWith() {
        return !isMoving();
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    public boolean isMoving() { return entityData.get(MOVING); }
    public void setMoving(boolean moving) { entityData.set(MOVING, moving); }
    public boolean isGoingUp() { return entityData.get(GOING_UP); }
    public void setGoingUp(boolean goingUp) { entityData.set(GOING_UP, goingUp); }
    public boolean isRotated() { return entityData.get(ROTATED); }
    public void setRotated(boolean rotated) { entityData.set(ROTATED, rotated); }
    public float getVelocityModifier() { return entityData.get(VELOCITY_MODIFIER); }
    public void setVelocityModifier(float velocityModifier) { entityData.set(VELOCITY_MODIFIER, velocityModifier); }
    public int getMaxY() { return entityData.get(MAX_Y); }
    public void setMaxY(int maxY) { entityData.set(MAX_Y, maxY); }
    public int getMinY() { return entityData.get(MIN_Y); }
    public void setMinY(int minY) { entityData.set(MIN_Y, minY); }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setGoingUp(tag.getBoolean("up"));
        setMinY(tag.getInt("minY"));
        setMaxY(tag.getInt("maxY"));
        setRotated(tag.getBoolean("rotated"));
        setup = tag.getBoolean("setup");
        poweredTop = tag.getBoolean("poweredTop");
        poweredBottom = tag.getBoolean("poweredBottom");
        unbreakable = tag.getBoolean("unbreakable");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("up", isGoingUp());
        tag.putInt("minY", getMinY());
        tag.putInt("maxY", getMaxY());
        tag.putBoolean("rotated", isRotated());
        tag.putBoolean("setup", setup);
        tag.putBoolean("poweredTop", poweredTop);
        tag.putBoolean("poweredBottom", poweredBottom);
        tag.putBoolean("unbreakable", unbreakable);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
