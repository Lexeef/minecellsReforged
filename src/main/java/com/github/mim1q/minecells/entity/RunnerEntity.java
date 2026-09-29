package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedTeleportGoal;
import com.github.mim1q.minecells.entity.ai.goal.WalkTowardsTargetGoal;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import com.github.mim1q.minecells.util.ParticleUtils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public class RunnerEntity extends MineCellsMonsterEntity {
    private static final EntityDataAccessor<Boolean> ATTACK_CHARGING = SynchedEntityData.defineId(RunnerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ATTACK_RELEASING = SynchedEntityData.defineId(RunnerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TELEPORT_CHARGING = SynchedEntityData.defineId(RunnerEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationProperty bendAngle = new AnimationProperty(0.0F);
    public final AnimationProperty swingChargeProgress = new AnimationProperty(0.0F);
    public final AnimationProperty swingReleaseProgress = new AnimationProperty(0.0F);

    private int attackCooldown;
    private int teleportCooldown;

    public RunnerEntity(EntityType<? extends RunnerEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ATTACK_CHARGING, false);
        entityData.define(ATTACK_RELEASING, false);
        entityData.define(TELEPORT_CHARGING, false);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new RunnerTimedAttackGoal(this, s -> {
            s.cooldownSetter = cooldown -> attackCooldown = cooldown;
            s.cooldownGetter = () -> attackCooldown;
            s.stateSetter = this::switchAttackState;
            s.chargeSound = MineCellsSounds.GRENADIER_CHARGE.get();
            s.releaseSound = MineCellsSounds.SWIPE.get();
            s.defaultCooldown = 35;
            s.actionTick = 12;
            s.length = 20;
        }));
        goalSelector.addGoal(1, new WalkTowardsTargetGoal(this, 1.2D, false));
        goalSelector.addGoal(2, new TimedTeleportGoal<>(this, s -> {
            s.cooldownSetter = cooldown -> {
                teleportCooldown = cooldown;
                attackCooldown = 40;
            };
            s.cooldownGetter = () -> teleportCooldown;
            s.stateSetter = this::switchTeleportState;
            s.defaultCooldown = 100;
            s.actionTick = 20;
            s.length = 40;
        }, null));
        goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        addDefaultTargetGoals();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && isTeleportCharging()) {
            for (int i = 0; i < 5; i++) {
                ParticleUtils.addParticle(level(), MineCellsParticles.CHARGE.get(), position().add(0.0D, getBbHeight() * 0.5D, 0.0D), Vec3.ZERO);
            }
        }
        if (!level().isClientSide) {
            attackCooldown = Math.max(0, attackCooldown - 1);
            teleportCooldown = Math.max(0, teleportCooldown - 1);
        }
    }

    public void switchAttackState(TimedActionGoal.State state, boolean value) {
        switch (state) {
            case CHARGE -> setAttackCharging(value);
            case RELEASE -> setAttackReleasing(value);
            default -> {
            }
        }
    }

    public void switchTeleportState(TimedActionGoal.State state, boolean value) {
        if (state == TimedActionGoal.State.CHARGE) {
            setTeleportCharging(value);
        }
    }

    public boolean isAttackCharging() {
        return entityData.get(ATTACK_CHARGING);
    }

    public void setAttackCharging(boolean value) {
        entityData.set(ATTACK_CHARGING, value);
    }

    public boolean isAttackReleasing() {
        return entityData.get(ATTACK_RELEASING);
    }

    public void setAttackReleasing(boolean value) {
        entityData.set(ATTACK_RELEASING, value);
    }

    public boolean isTeleportCharging() {
        return entityData.get(TELEPORT_CHARGING);
    }

    public void setTeleportCharging(boolean value) {
        entityData.set(TELEPORT_CHARGING, value);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("attackCooldown", attackCooldown);
        tag.putInt("teleportCooldown", teleportCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        attackCooldown = tag.getInt("attackCooldown");
        teleportCooldown = tag.getInt("teleportCooldown");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return MineCellsMonsterEntity.createBaseAttributes()
            .add(Attributes.MAX_HEALTH, 25.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.3D)
            .add(Attributes.ATTACK_DAMAGE, 8.0D)
            .add(Attributes.FOLLOW_RANGE, 14.0D);
    }

    private static final class RunnerTimedAttackGoal extends TimedActionGoal<RunnerEntity> {
        private LivingEntity target;

        private RunnerTimedAttackGoal(RunnerEntity entity, Consumer<TimedActionSettings> settingsConsumer) {
            super(entity, settingsConsumer, runner -> runner.teleportCooldown < 80);
        }

        @Override
        public boolean canUse() {
            target = entity.getTarget();
            return target != null
                && target.isAlive()
                && target.isAttackable()
                && entity.distanceTo(target) < 1.5D
                && super.canUse();
        }

        @Override
        public void tick() {
            if (target != null) {
                entity.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 0.001D);
                entity.getLookControl().setLookAt(target);
            }
            super.tick();
        }

        @Override
        protected void runAction() {
            if (target != null && target.isAlive() && entity.distanceTo(target) < 2.5D) {
                entity.doHurtTarget(target);
            }
        }
    }
}
