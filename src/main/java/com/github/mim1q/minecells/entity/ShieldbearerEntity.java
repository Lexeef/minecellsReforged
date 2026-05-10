package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class ShieldbearerEntity extends MineCellsMonsterEntity {
    private static final EntityDataAccessor<Boolean> DASH_CHARGING = SynchedEntityData.defineId(ShieldbearerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DASH_RELEASING = SynchedEntityData.defineId(ShieldbearerEntity.class, EntityDataSerializers.BOOLEAN);

    private int dashCooldown = 100;

    public ShieldbearerEntity(EntityType<? extends ShieldbearerEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DASH_CHARGING, false);
        entityData.define(DASH_RELEASING, false);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new ShieldDashGoal(this));
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new WalkTowardsTargetGoal(this, 1.0D, true, 1.0D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag tag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData, tag);
        setLeftHanded(false);
        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.SHIELD));
        return data;
    }

    @Override
    public void tick() {
        super.tick();
        dashCooldown = Math.max(0, dashCooldown - 1);
        if (level().isClientSide && isDashCharging()) {
            for (int i = 0; i < 5; i++) {
                level().addParticle(MineCellsParticles.CHARGE.get(), getX(), getY() + getBbHeight() * 0.5D, getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Vec3 pos = source.getSourcePosition();
        if (pos != null && !isDashReleasing()) {
            Vec3 diff = pos.subtract(position());
            float angle = (float) Mth.atan2(diff.z, diff.x) * Mth.RAD_TO_DEG + 90.0F;
            if (Mth.degreesDifferenceAbs(yBodyRot, angle) > 110.0F) {
                playSound(SoundEvents.SHIELD_BLOCK, 0.3F, 1.0F);
                return false;
            }
        }
        return super.hurt(source, amount);
    }

    public boolean isDashCharging() {
        return entityData.get(DASH_CHARGING);
    }

    public void setDashCharging(boolean value) {
        entityData.set(DASH_CHARGING, value);
    }

    public boolean isDashReleasing() {
        return entityData.get(DASH_RELEASING);
    }

    public void setDashReleasing(boolean value) {
        entityData.set(DASH_RELEASING, value);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("dashCooldown", dashCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        dashCooldown = tag.getInt("dashCooldown");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return MineCellsMonsterEntity.createBaseAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.ARMOR, 5.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.23D)
            .add(Attributes.ATTACK_DAMAGE, 5.0D)
            .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    private static final class WalkTowardsTargetGoal extends MeleeAttackGoal {
        private final double minDistance;

        private WalkTowardsTargetGoal(ShieldbearerEntity mob, double speedModifier, boolean followingTargetEvenIfNotSeen, double minDistance) {
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

    private static final class ShieldDashGoal extends Goal {
        private final ShieldbearerEntity entity;
        private final Set<Integer> hitTargets = new HashSet<>();
        private LivingEntity target;
        private Vec3 direction = Vec3.ZERO;
        private int ticks;
        private double distanceTravelled;
        private double targetDistance;

        private ShieldDashGoal(ShieldbearerEntity entity) {
            this.entity = entity;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            target = entity.getTarget();
            return target != null
                && target.isAlive()
                && entity.onGround()
                && entity.dashCooldown <= 0
                && entity.distanceTo(target) < 8.0D
                && entity.getRandom().nextFloat() < 0.25F;
        }

        @Override
        public void start() {
            ticks = 0;
            distanceTravelled = 0.0D;
            targetDistance = 0.0D;
            hitTargets.clear();
            entity.setDashCharging(true);
            entity.setDashReleasing(false);
            entity.playSound(MineCellsSounds.SHIELDBEARER_CHARGE.get(), 1.0F, 1.0F);
        }

        @Override
        public boolean canContinueToUse() {
            return target != null && target.isAlive() && ticks < 80;
        }

        @Override
        public void stop() {
            entity.setDashCharging(false);
            entity.setDashReleasing(false);
            entity.dashCooldown = 120;
            target = null;
        }

        @Override
        public void tick() {
            if (target == null) {
                return;
            }

            if (ticks <= 12) {
                Vec3 targetPos = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
                Vec3 diff = targetPos.subtract(entity.position()).multiply(1.0D, 0.0D, 1.0D);
                if (diff.lengthSqr() > 1.0E-4D) {
                    direction = diff.normalize();
                    targetDistance = Mth.clamp(diff.length(), 0.0D, 8.0D);
                }
                entity.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 0.01D);
                entity.getLookControl().setLookAt(target, 360.0F, 360.0F);
                entity.getNavigation().stop();
            } else if (ticks == 20) {
                entity.setDashCharging(false);
                entity.setDashReleasing(true);
                entity.playSound(MineCellsSounds.SHIELDBEARER_RELEASE.get(), 1.0F, 1.0F);
                entity.setDeltaMovement(direction.scale(0.65D).add(0.0D, 0.18D, 0.0D));
                entity.hasImpulse = true;
            } else if (ticks > 20) {
                if (distanceTravelled > targetDistance) {
                    entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.8D, 1.0D, 0.8D));
                } else {
                    entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.0D, 1.0D, 0.0D).add(direction.scale(0.65D)));
                    distanceTravelled += 0.65D;
                    for (LivingEntity living : entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(0.25D), other -> other != entity && !(other instanceof MineCellsMonsterEntity) && hitTargets.add(other.getId()))) {
                        living.hurt(entity.damageSources().mobAttack(entity), 6.0F);
                    }
                }
            }

            ticks++;
        }
    }
}
