package com.github.mim1q.minecells.entity.nonliving;

import com.github.mim1q.minecells.item.weapon.TentacleItem;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import com.github.mim1q.minecells.util.MathUtils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.network.NetworkHooks;

public class TentacleWeaponEntity extends Entity {
    private static final EntityDataAccessor<Boolean> RETRACTING = SynchedEntityData.defineId(TentacleWeaponEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> TARGET_X = SynchedEntityData.defineId(TentacleWeaponEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TARGET_Y = SynchedEntityData.defineId(TentacleWeaponEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TARGET_Z = SynchedEntityData.defineId(TentacleWeaponEntity.class, EntityDataSerializers.FLOAT);

    private Vec3 startingPos = Vec3.ZERO;
    private Player owner;
    private boolean pulling;
    private ItemStack stack = ItemStack.EMPTY;
    private final AnimationProperty length = new AnimationProperty(0.0F, AnimationProperty::easeOutQuad);

    public TentacleWeaponEntity(EntityType<? extends TentacleWeaponEntity> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    public static TentacleWeaponEntity create(Level level, Player owner, Vec3 targetPos, ItemStack stack) {
        TentacleWeaponEntity entity = MineCellsEntities.TENTACLE_WEAPON.get().create(level);
        if (entity == null) {
            return null;
        }
        entity.owner = owner;
        entity.setPos(owner.getX(), owner.getY() + 1.5D, owner.getZ());
        entity.setTargetPos(targetPos);
        entity.startingPos = entity.position();
        entity.stack = stack.copy();
        entity.startRiding(owner, true);
        return entity;
    }

    @Override
    public void tick() {
        super.tick();
        if (isRetracting()) {
            length.setupTransitionTo(0.0F, 10.0F);
        } else {
            length.setupTransitionTo(1.0F, 10.0F);
        }

        if (!level().isClientSide) {
            tickServer();
        }
    }

    @Override
    public void rideTick() {
        super.rideTick();
        if (getVehicle() instanceof Player player) {
            double zOffset = player.getMainArm() == HumanoidArm.RIGHT ? -0.35D : 0.35D;
            Vec3 offset = MathUtils.vectorRotateY(new Vec3(0.0D, 1.0D, zOffset), MathUtils.radians(player.yBodyRot));
            setPos(player.getX() + offset.x, player.getY() + offset.y, player.getZ() + offset.z);
        }
    }

    private void tickServer() {
        if (owner == null || !isPassenger()) {
            discard();
            return;
        }

        if (isRetracting()) {
            float currentLength = getLength(0.0F);
            if (currentLength >= 0.01F) {
                pullOwner();
            }
            owner.resetFallDistance();
            if (currentLength <= 0.01F && tickCount > 30) {
                discard();
            }
            return;
        }

        AABB hitBox = AABB.ofSize(getEndPos(getLength(1.0F)), 0.75D, 0.75D, 0.75D);
        for (Entity entity : level().getEntities(this, hitBox, candidate -> candidate != owner)) {
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }
            playSound(MineCellsSounds.TENTACLE_RELEASE.get(), 0.5F, 1.0F);
            living.hurt(damageSources().playerAttack(owner), (float) TentacleItem.ABILITY_DAMAGE.calculate(
                com.github.mim1q.minecells.valuecalculators.ValueCalculatorContext.of(owner, stack, living)
            ));
            setRetracting(true);
            pulling = true;
            return;
        }

        if (getLength(1.0F) >= 0.99F) {
            var targetBlockPos = net.minecraft.core.BlockPos.containing(getTargetPos());
            var state = level().getBlockState(targetBlockPos);
            if (!state.getCollisionShape(level(), targetBlockPos).isEmpty()) {
                pulling = true;
                playSound(MineCellsSounds.TENTACLE_RELEASE.get(), 0.5F, 1.0F);
            }
            setRetracting(true);
        }
    }

    private void pullOwner() {
        if (!pulling || owner == null) {
            return;
        }
        Vec3 direction = getTargetPos().add(0.0D, 2.5D, 0.0D).subtract(owner.position()).scale(0.15D);
        owner.setDeltaMovement(direction);
        owner.hurtMarked = true;
    }

    public float getLength(float tickDelta) {
        return length.update(tickCount + tickDelta);
    }

    public Vec3 getEndPos(float lengthValue) {
        return getTargetPos().subtract(startingPos).scale(lengthValue).add(startingPos);
    }

    public Vec3 getStartingPos() {
        return startingPos;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(RETRACTING, false);
        entityData.define(TARGET_X, (float) getX());
        entityData.define(TARGET_Y, (float) getY());
        entityData.define(TARGET_Z, (float) getZ());
    }

    public Vec3 getTargetPos() {
        return new Vec3(entityData.get(TARGET_X), entityData.get(TARGET_Y), entityData.get(TARGET_Z));
    }

    private void setTargetPos(Vec3 pos) {
        entityData.set(TARGET_X, (float) pos.x);
        entityData.set(TARGET_Y, (float) pos.y);
        entityData.set(TARGET_Z, (float) pos.z);
    }

    public boolean isRetracting() {
        return entityData.get(RETRACTING);
    }

    public void setRetracting(boolean retracting) {
        entityData.set(RETRACTING, retracting);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setTargetPos(new Vec3(tag.getDouble("TargetX"), tag.getDouble("TargetY"), tag.getDouble("TargetZ")));
        startingPos = new Vec3(tag.getDouble("StartingX"), tag.getDouble("StartingY"), tag.getDouble("StartingZ"));
        setRetracting(tag.getBoolean("Retracting"));
        pulling = tag.getBoolean("Pulling");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putDouble("TargetX", getTargetPos().x);
        tag.putDouble("TargetY", getTargetPos().y);
        tag.putDouble("TargetZ", getTargetPos().z);
        tag.putDouble("StartingX", startingPos.x);
        tag.putDouble("StartingY", startingPos.y);
        tag.putDouble("StartingZ", startingPos.z);
        tag.putBoolean("Retracting", isRetracting());
        tag.putBoolean("Pulling", pulling);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        startingPos = new Vec3(packet.getX(), packet.getY(), packet.getZ());
    }
}
