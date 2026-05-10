package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.registry.MineCellsEntities;
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
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GrenadierEntity extends MineCellsMonsterEntity {
    private static final EntityDataAccessor<Integer> SHOOT_COOLDOWN = SynchedEntityData.defineId(GrenadierEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SHOOT_CHARGING = SynchedEntityData.defineId(GrenadierEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHOOT_RELEASING = SynchedEntityData.defineId(GrenadierEntity.class, EntityDataSerializers.BOOLEAN);

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
    protected void registerGoals() {
        goalSelector.addGoal(0, new GrenadierShootGoal(this));
        goalSelector.addGoal(1, new JumpBackGoal(this));
        goalSelector.addGoal(2, new FloatGoal(this));
        goalSelector.addGoal(3, new WalkTowardsTargetGoal(this, 1.0D, true, 6.0D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && getShootCooldown() > 0) {
            setShootCooldown(getShootCooldown() - 1);
        }
        jumpBackCooldown--;
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
        tag.putInt("jumpBackCooldown", jumpBackCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setShootCooldown(tag.getInt("shootCooldown"));
        jumpBackCooldown = tag.getInt("jumpBackCooldown");
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
        private LivingEntity target;
        private int ticks;

        private GrenadierShootGoal(GrenadierEntity entity) {
            this.entity = entity;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            target = entity.getTarget();
            return target != null
                && target.isAlive()
                && entity.getShootCooldown() <= 0
                && entity.distanceTo(target) <= 16.0D
                && entity.getSensing().hasLineOfSight(target)
                && entity.getRandom().nextFloat() < 0.3F;
        }

        @Override
        public void start() {
            ticks = 0;
            entity.setShootCharging(true);
            entity.setShootReleasing(false);
            entity.playSound(entity.getShootChargeSound(), 1.0F, 1.0F);
        }

        @Override
        public boolean canContinueToUse() {
            return target != null && target.isAlive() && ticks < 20;
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
            if (target == null) {
                return;
            }
            entity.getLookControl().setLookAt(target, 360.0F, 360.0F);
            entity.getNavigation().stop();
            if (ticks == 10 && !entity.level().isClientSide) {
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
            ticks++;
        }
    }

    private static final class JumpBackGoal extends Goal {
        private final GrenadierEntity entity;
        private LivingEntity target;
        private int ticks;

        private JumpBackGoal(GrenadierEntity entity) {
            this.entity = entity;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            target = entity.getTarget();
            return target != null
                && target.isAlive()
                && entity.jumpBackCooldown <= 0
                && entity.distanceTo(target) < 5.0D
                && entity.getRandom().nextFloat() < 0.3F;
        }

        @Override
        public void start() {
            ticks = 0;
        }

        @Override
        public boolean canContinueToUse() {
            return target != null && target.isAlive() && ticks < 20;
        }

        @Override
        public void stop() {
            entity.jumpBackCooldown = 20;
            target = null;
        }

        @Override
        public void tick() {
            if (target == null) {
                return;
            }
            entity.getLookControl().setLookAt(target, 360.0F, 360.0F);
            entity.getNavigation().stop();
            if (ticks == 10) {
                Vec3 away = entity.position().subtract(target.position());
                if (away.lengthSqr() < 1.0E-4D) {
                    away = new Vec3(entity.getRandom().nextDouble() - 0.5D, 0.0D, entity.getRandom().nextDouble() - 0.5D);
                }
                entity.setDeltaMovement(away.normalize().scale(0.45D).add(0.0D, 0.3D, 0.0D));
                entity.hasImpulse = true;
            }
            ticks++;
        }
    }
}
