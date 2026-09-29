package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedShootGoal;
import com.github.mim1q.minecells.entity.nonliving.projectile.ScorpionSpitEntity;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.ParticleUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ScorpionEntity extends MineCellsMonsterEntity {
    private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(ScorpionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHOOT_CHARGING = SynchedEntityData.defineId(ScorpionEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationProperty buriedProgress = new AnimationProperty(1.0F, MathUtils::lerp);
    public final AnimationProperty swingProgress = new AnimationProperty(0.0F, MathUtils::easeInOutQuad);

    private int shootCooldown = 0;
    private boolean awakeGoalsRegistered = false;

    public ScorpionEntity(EntityType<? extends ScorpionEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SLEEPING, true);
        entityData.define(SHOOT_CHARGING, false);
    }

    @Override
    protected void registerGoals() {
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 0, false, false, null));
    }

    private void registerAwakeGoals() {
        if (awakeGoalsRegistered) {
            return;
        }
        awakeGoalsRegistered = true;
        goalSelector.removeAllGoals(goal -> true);
        goalSelector.addGoal(0, new TimedShootGoal<>(this, settings -> {
            settings.cooldownGetter = () -> shootCooldown;
            settings.cooldownSetter = cooldown -> shootCooldown = cooldown;
            settings.stateSetter = this::handleShootState;
            settings.projectileCreator = this::createSpit;
            settings.chargeSound = MineCellsSounds.SCORPION_CHARGE.get();
            settings.actionTick = 20;
            settings.length = 20;
            settings.defaultCooldown = 40;
            settings.chance = 0.1F;
        }, e -> {
            LivingEntity target = e.getTarget();
            return target != null && target.isAlive() && e.hasLineOfSight(target);
        }));
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, true));
        goalSelector.addGoal(2, new FloatGoal(this));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(0, new HurtByTargetGoal(this));
    }

    private Entity createSpit(Vec3 from, Vec3 to) {
        Vec3 pos = from.add(0.0D, 0.25D, 0.0D);
        ScorpionSpitEntity spit = new ScorpionSpitEntity(MineCellsEntities.SCORPION_SPIT.get(), level(), this);
        spit.setPos(pos);
        spit.setDeltaMovement(to.subtract(pos).normalize().scale(0.8D));
        return spit;
    }

    @Override
    public void tick() {
        super.tick();
        if (isSleeping() && getTarget() != null) {
            setSleeping(false);
            playSound(MineCellsSounds.RISE.get(), 1.0F, 1.0F);
            registerAwakeGoals();
        }
        shootCooldown = Math.max(0, shootCooldown - 1);

        if (level().isClientSide) {
            if (!isSleeping()) {
                buriedProgress.setupTransitionTo(0.0F, 20.0F);
            }
            if (isShootCharging()) {
                swingProgress.setupTransitionTo(1.0F, 15.0F);
            } else {
                swingProgress.setupTransitionTo(0.0F, 10.0F);
            }
            float buried = buriedProgress.getProgress();
            if (isSleeping() || (buried > 0.0F && buried < 1.0F)) {
                spawnUnburyingParticles();
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(
                MobEffects.POISON,
                100 + 20 * (level().getDifficulty().getId() - 1),
                0
            ));
        }
        return hit;
    }

    private void spawnUnburyingParticles() {
        BlockState blockState = level().getBlockState(BlockPos.containing(position().subtract(0.0D, 0.01D, 0.0D)));
        if (blockState.canOcclude()) {
            ParticleOptions particle = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
            ParticleUtils.addInBox(
                level(),
                particle,
                AABB.ofSize(position().add(0.0D, 0.125D, 0.0D), 1.0D, 0.25D, 1.0D),
                5,
                new Vec3(-0.01D, -0.01D, -0.01D)
            );
        }
    }

    private void handleShootState(TimedActionGoal.State state, boolean value) {
        if (state == TimedActionGoal.State.CHARGE) {
            setShootCharging(value);
        }
    }

    public boolean isSleeping() {
        return entityData.get(SLEEPING);
    }

    public void setSleeping(boolean sleeping) {
        entityData.set(SLEEPING, sleeping);
    }

    public boolean isShootCharging() {
        return entityData.get(SHOOT_CHARGING);
    }

    public void setShootCharging(boolean charging) {
        entityData.set(SHOOT_CHARGING, charging);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("shootCooldown", shootCooldown);
        tag.putBoolean("sleeping", isSleeping());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        shootCooldown = tag.getInt("shootCooldown");
        if (tag.contains("sleeping")) {
            setSleeping(tag.getBoolean("sleeping"));
        }
        if (!isSleeping()) {
            registerAwakeGoals();
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return MineCellsMonsterEntity.createBaseAttributes()
            .add(Attributes.MAX_HEALTH, 25.0D)
            .add(Attributes.ARMOR, 5.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.3D)
            .add(Attributes.FOLLOW_RANGE, 16.0D)
            .add(Attributes.ATTACK_DAMAGE, 8.0D);
    }
}
