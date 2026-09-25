package com.github.mim1q.minecells.entity.nonliving.obelisk;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.data.spawner_runes.SpawnerRuneController;
import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;
import net.minecraft.core.BlockPos;
import com.github.mim1q.minecells.network.MineCellsNetwork;
import com.github.mim1q.minecells.network.s2c.ObeliskActivationS2CPacket;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public abstract class ObeliskEntity extends Entity {
    private static final EntityDimensions HIDDEN_DIMENSIONS = EntityDimensions.scalable(1.75F, 0.0F);
    private static final EntityDataAccessor<Boolean> HIDDEN = SynchedEntityData.defineId(ObeliskEntity.class, EntityDataSerializers.BOOLEAN);

    private boolean wasHidden = true;
    private int activatedTicks = 1000;
    private int riseTicks = 1000;
    public final AnimationProperty bury = new AnimationProperty(0.0F, MathUtils::easeInOutQuad);
    public final AnimationProperty glow = new AnimationProperty(0.0F, MathUtils::easeInOutQuad);

    public ObeliskEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
        this.noCulling = true;
    }

    public abstract Item getActivationItem();

    public abstract ResourceLocation getSpawnerRuneDataId();

    public abstract AABB getBox();

    protected abstract void postProcessEntity(Entity entity);

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            clientTick();
        } else {
            serverTick();
        }
        activatedTicks++;
        riseTicks++;
    }

    protected void clientTick() {
        bury.setupTransitionTo(isHidden() ? 50.0F : 0.0F, 40.0F);
        if (bury.getProgress() > 0.0F && bury.getProgress() < 1.0F) {
            spawnRiseParticles();
        }
        glow.setupTransitionTo(activatedTicks < 100 ? 1.0F : 0.0F, 10.0F);
        if (activatedTicks <= 40) {
            spawnActivationParticles(activatedTicks);
        }
    }

    protected void serverTick() {
        if (tickCount % 20 == 0) {
            boolean hidden = isEntityPresent();
            setHidden(hidden);
            setPose(hidden ? Pose.SLEEPING : Pose.STANDING);
            if (hidden != wasHidden) {
                riseTicks = 0;
            }
            wasHidden = hidden;
        }
        if (tickCount % 3 == 0 && riseTicks > 10 && riseTicks < 49) {
            playSound(SoundEvents.STONE_STEP, 1.0F, random.nextFloat() * 0.5F + 0.25F);
            playSound(SoundEvents.GRAVEL_HIT, 0.3F, random.nextFloat() * 0.5F + 0.5F);
        }
        if (activatedTicks == 40 && !isEntityPresent() && level() instanceof ServerLevel serverLevel) {
            List<Entity> entities = SpawnerRuneController.spawnEntities(serverLevel, getSpawnerRuneDataId(), blockPosition(), this::postProcessEntity);
            for (Entity entity : entities) {
                try {
                    postProcessEntity(entity);
                    if (entity instanceof MineCellsMonsterEntity monster) {
                        monster.spawnRunePos = blockPosition();
                    }
                } catch (ClassCastException e) {
                    MineCells.LOGGER.warn("Failed to post process entity {} by obelisk at pos: {}", entity, blockPosition());
                }
            }
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return pose == Pose.SLEEPING ? HIDDEN_DIMENSIONS : super.getDimensions(pose);
    }

    protected void spawnRiseParticles() {
        var stateBelow = level().getBlockState(blockPosition().below());
        ParticleUtils.addInBox(
            level(),
            new BlockParticleOption(ParticleTypes.BLOCK, stateBelow),
            AABB.ofSize(position(), 2.5D, 0.25D, 2.0D),
            50,
            new Vec3(-1.0D, 1.0D, -1.0D)
        );
    }

    public void resetActivatedTicks() {
        activatedTicks = 0;
    }

    protected void spawnActivationParticles(int activatedTicks) {
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (isHidden() || activatedTicks < 100 || isEntityPresent()) {
            return InteractionResult.FAIL;
        }
        ItemStack stack = player.getItemInHand(hand);
        if (getActivationItem() == null || stack.is(getActivationItem())) {
            if (!level().isClientSide) {
                playSound(MineCellsSounds.OBELISK.get(), 1.0F, 1.0F);
                activatedTicks = 0;
                if (getActivationItem() != null && !player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                MineCellsNetwork.CHANNEL.send(
                    PacketDistributor.TRACKING_ENTITY.with(() -> this),
                    new ObeliskActivationS2CPacket(getId())
                );
            }
            return InteractionResult.SUCCESS;
        }
        player.displayClientMessage(Component.translatable("chat.minecells.obelisk_item_message", getActivationItem().getDescription()), true);
        return InteractionResult.FAIL;
    }

    protected boolean isEntityPresent() {
        BlockPos pos = blockPosition();
        return !level().getEntitiesOfClass(
            MineCellsMonsterEntity.class,
            getBox(),
            monster -> monster.isAlive() && pos.equals(monster.spawnRunePos)
        ).isEmpty();
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 hitPos, InteractionHand hand) {
        return interact(player, hand);
    }

    @Override
    public boolean canBeCollidedWith() {
        return !isHidden();
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(HIDDEN, true);
    }

    public void setHidden(boolean hidden) {
        entityData.set(HIDDEN, hidden);
    }

    public boolean isHidden() {
        return entityData.get(HIDDEN);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setHidden(tag.getBoolean("hidden"));
        if (tag.contains("activatedTicks")) {
            activatedTicks = tag.getInt("activatedTicks");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("hidden", isHidden());
        tag.putInt("activatedTicks", activatedTicks);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
