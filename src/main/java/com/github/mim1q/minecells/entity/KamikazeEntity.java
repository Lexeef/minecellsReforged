package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MineCellsExplosion;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class KamikazeEntity extends MineCellsMonsterEntity {
    public final AnimationProperty rotation = new AnimationProperty(180.0F);

    private static final EntityDataAccessor<Integer> FUSE = SynchedEntityData.defineId(KamikazeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(KamikazeEntity.class, EntityDataSerializers.BOOLEAN);

    public KamikazeEntity(EntityType<? extends KamikazeEntity> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 0, true);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(FUSE, -1);
        entityData.define(SLEEPING, true);
    }

    @Override
    protected boolean canUseEliteAura() {
        return false;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(3, new KamikazeFlyGoal(this, 1.0D));
        goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(0, new KamikazeAttackGoal(this, 1.0D, 3.0D));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 0, true, true, null));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(
        ServerLevelAccessor level,
        DifficultyInstance difficulty,
        MobSpawnType reason,
        @Nullable SpawnGroupData spawnData,
        @Nullable CompoundTag dataTag
    ) {
        setPos(getX(), getY() + 0.25D, getZ());
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            clientTick();
        } else {
            serverTick();
        }
    }

    private void clientTick() {
        if (isSleeping()) {
            rotation.setupTransitionTo(180.0F, 0.1F);
        } else {
            rotation.setupTransitionTo(0.0F, 20.0F);
        }
    }

    private void serverTick() {
        int fuse = getFuse();
        if (fuse == 0 && isAlive()) {
            explode();
            level().playSound(
                null,
                getX(),
                getY(),
                getZ(),
                MineCellsSounds.KAMIKAZE_DEATH.get(),
                SoundSource.HOSTILE,
                1.0F,
                1.0F
            );
            discard();
        }
        if (fuse >= 0) {
            setFuse(fuse - 1);
        }
    }

    public void explode() {
        if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {
            MineCellsExplosion.explode(serverLevel, this, this, position(), 20.0F, 6.0F);
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(false);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    public int getFuse() {
        return entityData.get(FUSE);
    }

    public void setFuse(int fuse) {
        entityData.set(FUSE, fuse);
    }

    @Override
    public boolean isSleeping() {
        return entityData.get(SLEEPING);
    }

    public void setSleeping(boolean sleeping) {
        entityData.set(SLEEPING, sleeping);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return MineCellsSounds.KAMIKAZE_DEATH.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("sleeping", isSleeping());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setSleeping(tag.getBoolean("sleeping"));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return MineCellsMonsterEntity.createBaseAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.2D)
            .add(Attributes.FLYING_SPEED, 3.0D)
            .add(Attributes.ATTACK_DAMAGE, 1.0D)
            .add(Attributes.ATTACK_KNOCKBACK, 0.0D)
            .add(Attributes.FOLLOW_RANGE, 16.0D)
            .add(Attributes.MAX_HEALTH, 2.0D);
    }

    public static class KamikazeFlyGoal extends WaterAvoidingRandomFlyingGoal {
        private final KamikazeEntity entity;

        public KamikazeFlyGoal(KamikazeEntity entity, double speed) {
            super(entity, speed);
            this.entity = entity;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !entity.isSleeping();
        }
    }

    public static class KamikazeAttackGoal extends Goal {
        private final KamikazeEntity entity;
        private final double speed;
        private final double distance;

        public KamikazeAttackGoal(KamikazeEntity entity, double speed, double distance) {
            this.entity = entity;
            this.speed = speed;
            this.distance = distance;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return entity.getTarget() != null;
        }

        @Override
        public boolean canContinueToUse() {
            return entity.getTarget() != null && entity.getTarget().isAlive();
        }

        @Override
        public void start() {
            entity.setSleeping(false);
            entity.level().playSound(
                null,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                MineCellsSounds.KAMIKAZE_WAKE.get(),
                SoundSource.HOSTILE,
                1.0F,
                1.0F
            );
        }

        @Override
        public void tick() {
            Entity target = entity.getTarget();
            if (target == null) {
                return;
            }
            Vec3 pos = target.position();
            entity.getMoveControl().setWantedPosition(pos.x, pos.y + 1.5D, pos.z, speed);
            entity.getLookControl().setLookAt(target);
            if (entity.distanceTo(target) <= distance && entity.getFuse() < 0) {
                entity.setFuse(30);
                entity.level().playSound(
                    null,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    MineCellsSounds.KAMIKAZE_CHARGE.get(),
                    SoundSource.HOSTILE,
                    1.0F,
                    1.0F
                );
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
