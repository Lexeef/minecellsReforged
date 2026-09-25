package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedDashGoal;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MutatedBatEntity extends MineCellsMonsterEntity {
    private static final EntityDataAccessor<Boolean> DASH_CHARGING = SynchedEntityData.defineId(MutatedBatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DASH_RELEASING = SynchedEntityData.defineId(MutatedBatEntity.class, EntityDataSerializers.BOOLEAN);

    private int dashCooldown = 80;

    public MutatedBatEntity(EntityType<? extends MutatedBatEntity> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 0, true);
        this.setNoGravity(true);
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
        setPos(getX(), getY() + 3.0D, getZ());
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DASH_CHARGING, false);
        entityData.define(DASH_RELEASING, false);
    }

    @Override
    protected boolean canUseEliteAura() {
        return false;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new TimedDashGoal<>(this, s -> {
            s.cooldownGetter = () -> dashCooldown;
            s.cooldownSetter = cooldown -> dashCooldown = cooldown;
            s.stateSetter = this::switchDashState;
            s.chargeSound = MineCellsSounds.MUTATED_BAT_CHARGE.get();
            s.releaseSound = MineCellsSounds.MUTATED_BAT_RELEASE.get();
            s.speed = 0.8F;
            s.damage = 6.0F;
            s.defaultCooldown = 80;
            s.actionTick = 20;
            s.alignTick = 19;
            s.chance = 0.075F;
            s.length = 60;
            s.margin = 0.25D;
            s.particle = MineCellsParticles.SPECKLE.get().get(0xFF0000);
        }, mob -> mob.getTarget() != null && mob.distanceTo(mob.getTarget()) < 8.0D));
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 3.0D, false));
        goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        addDefaultTargetGoals();
    }

    @Override
    public void tick() {
        super.tick();
        if (isDashCharging() && level().isClientSide) {
            for (int i = 0; i < 5; i++) {
                ParticleUtils.addParticle(
                    level(),
                    MineCellsParticles.CHARGE.get(),
                    position().add(0.0D, 0.2D, 0.0D),
                    Vec3.ZERO
                );
            }
        }
        dashCooldown = Math.max(0, dashCooldown - 1);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new MutatedBatNavigation(this, level);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    protected void switchDashState(TimedActionGoal.State state, boolean value) {
        switch (state) {
            case CHARGE -> setDashCharging(value);
            case RELEASE -> setDashReleasing(value);
            default -> {
            }
        }
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
            .add(Attributes.MAX_HEALTH, 4.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.0D)
            .add(Attributes.FLYING_SPEED, 0.3D)
            .add(Attributes.ATTACK_DAMAGE, 2.0D)
            .add(Attributes.FOLLOW_RANGE, 18.0D);
    }

    public static class MutatedBatNavigation extends FlyingPathNavigation {
        public MutatedBatNavigation(Monster host, Level level) {
            super(host, level);
            setCanOpenDoors(false);
            setCanFloat(false);
            setCanPassDoors(true);
        }

        @Override
        public Path createPath(Entity entity, int accuracy) {
            return createPath(entity.blockPosition().above(2), accuracy);
        }
    }
}
