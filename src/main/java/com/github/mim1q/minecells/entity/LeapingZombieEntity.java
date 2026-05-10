package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.registry.MineCellsSounds;
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

public class LeapingZombieEntity extends MineCellsMonsterEntity {
    private static final EntityDataAccessor<Integer> LEAP_COOLDOWN = SynchedEntityData.defineId(LeapingZombieEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> LEAP_CHARGING = SynchedEntityData.defineId(LeapingZombieEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LEAP_RELEASING = SynchedEntityData.defineId(LeapingZombieEntity.class, EntityDataSerializers.BOOLEAN);

    public LeapingZombieEntity(EntityType<? extends LeapingZombieEntity> type, Level level) {
        super(type, level);
        noCulling = true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(LEAP_COOLDOWN, 50);
        entityData.define(LEAP_CHARGING, false);
        entityData.define(LEAP_RELEASING, false);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new LeapAttackGoal(this, 15, 20, 0.1F));
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.3D, false));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, LivingEntity.class, 6.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && getLeapCooldown() > 0) {
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
        return 20 + getRandom().nextInt(60);
    }

    public float getLeapDamage() {
        return 6.0F;
    }

    public double getLeapRange() {
        return 15.0D;
    }

    public SoundEvent getLeapChargeSound() {
        return MineCellsSounds.LEAPING_ZOMBIE_CHARGE.get();
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
            .add(Attributes.MOVEMENT_SPEED, 0.2D)
            .add(Attributes.FOLLOW_RANGE, 20.0D)
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.ARMOR, 3.0D)
            .add(Attributes.ATTACK_DAMAGE, 4.0D)
            .add(Attributes.ATTACK_KNOCKBACK, 1.0D);
    }

    private static final class LeapAttackGoal extends Goal {
        private final LeapingZombieEntity entity;
        private final int actionTick;
        private final int lengthTicks;
        private final float chance;
        private final Set<UUID> attacked = new HashSet<>();
        private LivingEntity target;
        private int ticks;

        private LeapAttackGoal(LeapingZombieEntity entity, int actionTick, int lengthTicks, float chance) {
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
