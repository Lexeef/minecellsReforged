package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RancidRatEntity extends MineCellsMonsterEntity {
    private static final EntityDataAccessor<Integer> LEAP_COOLDOWN = SynchedEntityData.defineId(RancidRatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> LEAP_CHARGING = SynchedEntityData.defineId(RancidRatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LEAP_RELEASING = SynchedEntityData.defineId(RancidRatEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationProperty torsoRotation = new AnimationProperty(0.0F);

    public RancidRatEntity(EntityType<? extends RancidRatEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(LEAP_COOLDOWN, getLeapMaxCooldown());
        entityData.define(LEAP_CHARGING, false);
        entityData.define(LEAP_RELEASING, false);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new LeapAttackGoal(this, 10, 20, 0.5F));
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        addDefaultTargetGoals();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (isLeapCharging()) {
                torsoRotation.setupTransitionTo(-30.0F, 5.0F);
            } else {
                torsoRotation.setupTransitionTo(0.0F, 5.0F);
            }
        } else if (getLeapCooldown() > 0) {
            setLeapCooldown(getLeapCooldown() - 1);
        }
    }

    public boolean isLeapCharging() {
        return entityData.get(LEAP_CHARGING);
    }

    public void setLeapCharging(boolean charging) {
        entityData.set(LEAP_CHARGING, charging);
    }

    public boolean isLeapReleasing() {
        return entityData.get(LEAP_RELEASING);
    }

    public void setLeapReleasing(boolean releasing) {
        entityData.set(LEAP_RELEASING, releasing);
    }

    public int getLeapCooldown() {
        return entityData.get(LEAP_COOLDOWN);
    }

    public void setLeapCooldown(int ticks) {
        entityData.set(LEAP_COOLDOWN, Math.max(0, ticks));
    }

    public int getLeapMaxCooldown() {
        return 20 + getRandom().nextInt(20);
    }

    public float getLeapDamage() {
        return 15.0F;
    }

    public double getLeapRange() {
        return 5.0D;
    }

    public SoundEvent getLeapChargeSound() {
        return MineCellsSounds.RANCID_RAT_CHARGE.get();
    }

    public SoundEvent getLeapReleaseSound() {
        return MineCellsSounds.LEAPING_ZOMBIE_RELEASE.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("leapCooldown", getLeapCooldown());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setLeapCooldown(tag.getInt("leapCooldown"));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return MineCellsMonsterEntity.createBaseAttributes()
            .add(Attributes.MAX_HEALTH, 10.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.4D)
            .add(Attributes.ATTACK_DAMAGE, 5.0D)
            .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    private static final class LeapAttackGoal extends Goal {
        private final RancidRatEntity entity;
        private final int actionTick;
        private final int lengthTicks;
        private final float chance;
        private final Set<UUID> attacked = new HashSet<>();
        private LivingEntity target;
        private int ticks;

        private LeapAttackGoal(RancidRatEntity entity, int actionTick, int lengthTicks, float chance) {
            this.entity = entity;
            this.actionTick = actionTick;
            this.lengthTicks = lengthTicks;
            this.chance = chance;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = entity.getTarget();
            return target != null
                && target.isAlive()
                && entity.getLeapCooldown() == 0
                && entity.getSensing().hasLineOfSight(target)
                && entity.getY() >= target.getY()
                && entity.getRandom().nextFloat() < chance
                && entity.distanceTo(target) <= entity.getLeapRange();
        }

        @Override
        public void start() {
            this.target = entity.getTarget();
            this.ticks = 0;
            this.attacked.clear();
            entity.setLeapCharging(true);
            entity.setLeapReleasing(false);
            entity.playSound(entity.getLeapChargeSound(), 1.0F, 1.0F);
        }

        @Override
        public boolean canContinueToUse() {
            return target != null && target.isAlive() && (ticks < lengthTicks || !entity.onGround());
        }

        @Override
        public void stop() {
            entity.setLeapCharging(false);
            entity.setLeapReleasing(false);
            entity.setLeapCooldown(entity.getLeapMaxCooldown());
            this.target = null;
        }

        @Override
        public void tick() {
            if (target == null) {
                return;
            }

            if (ticks < actionTick) {
                entity.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 0.01D);
                entity.getLookControl().setLookAt(target, 360.0F, 360.0F);
                entity.getNavigation().stop();
            } else if (ticks == actionTick) {
                entity.setLeapCharging(false);
                entity.setLeapReleasing(true);
                Vec3 velocity = target.position().subtract(entity.position()).multiply(0.3D, 0.05D, 0.3D).add(0.0D, 0.3D, 0.0D);
                if (velocity.length() > 3.0D) {
                    velocity = velocity.normalize().scale(3.0D);
                }
                entity.setDeltaMovement(velocity);
                entity.hasImpulse = true;
                entity.playSound(entity.getLeapReleaseSound(), 1.0F, 1.0F);
            } else if (!entity.onGround()) {
                for (Player player : entity.level().getEntitiesOfClass(Player.class, entity.getBoundingBox().inflate(0.33D), player -> !attacked.contains(player.getUUID()))) {
                    player.hurt(entity.damageSources().mobAttack(entity), entity.getLeapDamage());
                    attacked.add(player.getUUID());
                }
            }

            ticks++;
        }
    }
}
