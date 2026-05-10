package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class RunnerEntity extends MineCellsMonsterEntity {
    private static final EntityDataAccessor<Boolean> ATTACK_CHARGING = SynchedEntityData.defineId(RunnerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ATTACK_RELEASING = SynchedEntityData.defineId(RunnerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TELEPORT_CHARGING = SynchedEntityData.defineId(RunnerEntity.class, EntityDataSerializers.BOOLEAN);

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
        goalSelector.addGoal(0, new RunnerSlashGoal(this));
        goalSelector.addGoal(1, new RunnerTeleportGoal(this));
        goalSelector.addGoal(2, new FloatGoal(this));
        goalSelector.addGoal(3, new WalkTowardsTargetGoal(this, 1.2D, false));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        attackCooldown = Math.max(0, attackCooldown - 1);
        teleportCooldown = Math.max(0, teleportCooldown - 1);
        if (level().isClientSide && isTeleportCharging()) {
            for (int i = 0; i < 5; i++) {
                level().addParticle(MineCellsParticles.CHARGE.get(), getX(), getY() + getBbHeight() * 0.5D, getZ(), 0.0D, 0.0D, 0.0D);
            }
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

    private static final class WalkTowardsTargetGoal extends MeleeAttackGoal {
        private WalkTowardsTargetGoal(RunnerEntity mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(mob, speedModifier, followingTargetEvenIfNotSeen);
        }

        @Override
        public boolean canUse() {
            return mob.getTarget() != null && mob.distanceTo(mob.getTarget()) >= 1.5D && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return mob.getTarget() != null && mob.distanceTo(mob.getTarget()) >= 1.5D && super.canContinueToUse();
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity target, double distToEnemySqr) {
        }
    }

    private static final class RunnerSlashGoal extends Goal {
        private final RunnerEntity entity;
        private LivingEntity target;
        private int ticks;

        private RunnerSlashGoal(RunnerEntity entity) {
            this.entity = entity;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            this.target = entity.getTarget();
            return target != null
                && target.isAlive()
                && target.isAttackable()
                && entity.attackCooldown <= 0
                && entity.teleportCooldown < 80
                && entity.distanceTo(target) < 1.5D;
        }

        @Override
        public void start() {
            ticks = 0;
            entity.setAttackCharging(true);
            entity.setAttackReleasing(false);
            entity.playSound(MineCellsSounds.GRENADIER_CHARGE.get(), 0.8F, 1.1F);
        }

        @Override
        public boolean canContinueToUse() {
            return target != null && target.isAlive() && ticks < 20;
        }

        @Override
        public void stop() {
            entity.setAttackCharging(false);
            entity.setAttackReleasing(false);
            entity.attackCooldown = 35;
            target = null;
        }

        @Override
        public void tick() {
            if (target == null) {
                return;
            }
            entity.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 0.001D);
            entity.getLookControl().setLookAt(target, 360.0F, 360.0F);
            entity.getNavigation().stop();

            if (ticks == 12) {
                entity.setAttackCharging(false);
                entity.setAttackReleasing(true);
                entity.playSound(MineCellsSounds.SWIPE.get(), 1.0F, 1.0F);
                if (target.isAlive() && entity.distanceTo(target) < 2.5D) {
                    entity.doHurtTarget(target);
                }
            }
            ticks++;
        }
    }

    private static final class RunnerTeleportGoal extends Goal {
        private final RunnerEntity entity;
        private LivingEntity target;
        private int ticks;

        private RunnerTeleportGoal(RunnerEntity entity) {
            this.entity = entity;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            target = entity.getTarget();
            return target != null
                && target.isAlive()
                && entity.teleportCooldown <= 0
                && entity.distanceTo(target) > 4.0D
                && entity.distanceTo(target) < 14.0D
                && entity.getRandom().nextFloat() < 0.08F;
        }

        @Override
        public void start() {
            ticks = 0;
            entity.setTeleportCharging(true);
            entity.playSound(MineCellsSounds.TELEPORT_CHARGE.get(), 0.9F, 1.0F);
        }

        @Override
        public boolean canContinueToUse() {
            return target != null && target.isAlive() && ticks < 40;
        }

        @Override
        public void stop() {
            entity.setTeleportCharging(false);
            target = null;
        }

        @Override
        public void tick() {
            if (target == null) {
                return;
            }
            entity.getNavigation().stop();
            entity.getLookControl().setLookAt(target, 360.0F, 360.0F);
            if (ticks == 20 && !entity.level().isClientSide) {
                Vec3 away = entity.position().subtract(target.position());
                if (away.lengthSqr() < 1.0E-4D) {
                    away = new Vec3(entity.getRandom().nextDouble() - 0.5D, 0.0D, entity.getRandom().nextDouble() - 0.5D);
                }
                Vec3 dir = away.normalize();
                Vec3 destination = target.position().add(dir.scale(2.5D + entity.getRandom().nextDouble() * 2.0D));
                boolean teleported = entity.randomTeleport(destination.x, target.getY(), destination.z, true);
                if (!teleported && entity.level() instanceof ServerLevel serverLevel) {
                    teleported = entity.randomTeleport(destination.x, serverLevel.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, net.minecraft.core.BlockPos.containing(destination)).getY(), destination.z, true);
                }
                if (teleported) {
                    entity.teleportCooldown = 100;
                    entity.attackCooldown = 40;
                    entity.playSound(MineCellsSounds.TELEPORT_RELEASE.get(), 1.0F, 1.0F);
                }
                entity.setTeleportCharging(false);
            }
            ticks++;
        }
    }
}
