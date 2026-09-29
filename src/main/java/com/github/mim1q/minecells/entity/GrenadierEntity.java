package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.animation.AnimationProperty;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GrenadierEntity extends MineCellsMonsterEntity {
    private static final EntityDataAccessor<Integer> SHOOT_COOLDOWN = SynchedEntityData.defineId(GrenadierEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SHOOT_CHARGING = SynchedEntityData.defineId(GrenadierEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHOOT_RELEASING = SynchedEntityData.defineId(GrenadierEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationProperty additionalRotation = new AnimationProperty(0.0F);

    private int jumpBackCooldown;

    public GrenadierEntity(EntityType<? extends GrenadierEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SHOOT_COOLDOWN, 50);
        entityData.define(SHOOT_CHARGING, false);
        entityData.define(SHOOT_RELEASING, false);
    }

    @Override
    protected boolean canUseEliteAura() {
        return false;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new GrenadierShootGoal(this, 10, 20));
        goalSelector.addGoal(1, new com.github.mim1q.minecells.entity.ai.goal.JumpBackGoal<>(this, s -> {
            s.minDistance = 5.0D;
            s.defaultCooldown = 20;
            s.actionTick = 10;
            s.length = 20;
            s.chance = 0.3F;
            s.cooldownGetter = () -> jumpBackCooldown;
            s.cooldownSetter = ticks -> jumpBackCooldown = ticks;
        }, null));
        goalSelector.addGoal(2, new WalkTowardsTargetGoal(this, 1.0D, true, 6.0D));
        goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16.0F));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        addPlayerTargetGoal(1);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (isShootCharging()) {
                additionalRotation.setupTransitionTo(240.0F, 15.0F);
            } else if (isShootReleasing()) {
                additionalRotation.setupTransitionTo(-30.0F, 10.0F);
            } else {
                additionalRotation.setupTransitionTo(0.0F, 20.0F);
            }
        } else {
            if (getShootCooldown() > 0) {
                setShootCooldown(getShootCooldown() - 1);
            }
            jumpBackCooldown--;
        }
    }

    @Override
    public int getMaxFallDistance() {
        return 3;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return MineCellsSounds.LEAPING_ZOMBIE_DEATH.get();
    }

    public boolean isShootCharging() {
        return entityData.get(SHOOT_CHARGING);
    }

    public void setShootCharging(boolean charging) {
        entityData.set(SHOOT_CHARGING, charging);
    }

    public boolean isShootReleasing() {
        return entityData.get(SHOOT_RELEASING);
    }

    public void setShootReleasing(boolean releasing) {
        entityData.set(SHOOT_RELEASING, releasing);
    }

    public int getShootCooldown() {
        return entityData.get(SHOOT_COOLDOWN);
    }

    public void setShootCooldown(int ticks) {
        entityData.set(SHOOT_COOLDOWN, Math.max(0, ticks));
    }

    public int getShootMaxCooldown() {
        return 20 + random.nextInt(40);
    }

    public SoundEvent getShootChargeSound() {
        return MineCellsSounds.GRENADIER_CHARGE.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("shootCooldown", getShootCooldown());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setShootCooldown(tag.getInt("shootCooldown"));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return MineCellsMonsterEntity.createBaseAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.2D)
            .add(Attributes.FOLLOW_RANGE, 16.0D)
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.ARMOR, 4.0D)
            .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    private static final class WalkTowardsTargetGoal extends net.minecraft.world.entity.ai.goal.MeleeAttackGoal {
        private final double minDistance;

        private WalkTowardsTargetGoal(GrenadierEntity mob, double speedModifier, boolean followingTargetEvenIfNotSeen, double minDistance) {
            super(mob, speedModifier, followingTargetEvenIfNotSeen);
            this.minDistance = minDistance;
        }

        @Override
        public boolean canUse() {
            return mob.getTarget() != null && super.canUse() && mob.distanceTo(mob.getTarget()) >= minDistance;
        }

        @Override
        public boolean canContinueToUse() {
            return mob.getTarget() != null && super.canContinueToUse() && mob.distanceTo(mob.getTarget()) >= minDistance;
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity target, double distToEnemySqr) {
        }
    }

    private static final class GrenadierShootGoal extends Goal {
        private final GrenadierEntity entity;
        private final int actionTick;
        private final int lengthTicks;
        private LivingEntity target;
        private int ticks;

        private GrenadierShootGoal(GrenadierEntity entity, int actionTick, int lengthTicks) {
            this.entity = entity;
            this.actionTick = actionTick;
            this.lengthTicks = lengthTicks;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity candidate = entity.getTarget();
            return candidate != null
                && entity.getShootCooldown() == 0
                && entity.getSensing().hasLineOfSight(candidate)
                && entity.getRandom().nextFloat() < 0.3F;
        }

        @Override
        public void start() {
            target = entity.getTarget();
            ticks = 0;
            entity.setShootCharging(true);
            entity.setShootReleasing(false);
            entity.playSound(entity.getShootChargeSound(), 1.0F, 1.0F);
        }

        @Override
        public boolean canContinueToUse() {
            return ticks < lengthTicks && target != null && target.isAlive();
        }

        @Override
        public void stop() {
            entity.setShootCharging(false);
            entity.setShootReleasing(false);
            entity.setShootCooldown(entity.getShootMaxCooldown());
            target = null;
        }

        @Override
        public void tick() {
            if (target != null) {
                entity.getLookControl().setLookAt(target);
                entity.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 0.01D);
                if (ticks == actionTick) {
                    shoot(target);
                }
            }
            ticks++;
        }

        private void shoot(LivingEntity target) {
            entity.setShootCharging(false);
            entity.setShootReleasing(true);
            Vec3 targetPos = target.position().add(entity.random.nextDouble() * 2.0D - 1.0D, 0.0D, entity.random.nextDouble() * 2.0D - 1.0D);
            Vec3 entityPos = entity.position();
            Vec3 delta = targetPos.subtract(entityPos).scale(0.035D).add(0.0D, 0.5D, 0.0D);
            GrenadeProjectileEntity grenade = new GrenadeProjectileEntity(MineCellsEntities.GRENADE.get(), entity.level());
            grenade.setPos(entityPos.add(0.0D, 1.5D, 0.0D));
            grenade.setOwner(entity);
            grenade.shoot(delta);
            entity.level().addFreshEntity(grenade);
        }
    }

}
