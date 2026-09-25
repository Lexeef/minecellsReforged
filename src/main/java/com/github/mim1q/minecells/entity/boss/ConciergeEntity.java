package com.github.mim1q.minecells.entity.boss;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.ai.goal.ShockwaveGoal;
import com.github.mim1q.minecells.entity.ai.goal.ShockwaveGoal.ShockwaveType;
import com.github.mim1q.minecells.entity.ai.goal.TargetRandomPlayerGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedAuraGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedDashGoal;
import com.github.mim1q.minecells.entity.ai.goal.WalkTowardsTargetGoal;
import com.github.mim1q.minecells.entity.ai.goal.concierge.ConciergePunchGoal;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.util.ScreenShakeUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import com.github.mim1q.minecells.util.animation.AnimationProperty.EasingFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

import static java.lang.Math.min;

public class ConciergeEntity extends MineCellsBossEntity {
    private static final EntityDataAccessor<Boolean> LEAP_CHARGING = SynchedEntityData.defineId(ConciergeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LEAP_RELEASING = SynchedEntityData.defineId(ConciergeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHOCKWAVE_CHARGING = SynchedEntityData.defineId(ConciergeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHOCKWAVE_RELEASING = SynchedEntityData.defineId(ConciergeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AURA_CHARGING = SynchedEntityData.defineId(ConciergeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AURA_RELEASING = SynchedEntityData.defineId(ConciergeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PUNCH_CHARGING = SynchedEntityData.defineId(ConciergeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PUNCH_RELEASING = SynchedEntityData.defineId(ConciergeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SCREAMING = SynchedEntityData.defineId(ConciergeEntity.class, EntityDataSerializers.BOOLEAN);

    private int leapCooldown = 0;
    private int shockwaveCooldown = 0;
    private int auraCooldown = 0;
    private int punchCooldown = 0;
    private int sharedCooldown = 100;

    private BlockPos spawnPos = null;
    private int stage = 0;
    private int stageTimer = 20000;

    private boolean canAttack = false;
    private boolean goalsInit = false;

    public AnimationProperty leapChargeAnimation = new AnimationProperty(0.0F, MathUtils::easeOutQuad);
    public AnimationProperty leapReleaseAnimation = new AnimationProperty(0.0F);
    public AnimationProperty waveChargeAnimation = new AnimationProperty(0.0F);
    public AnimationProperty waveReleaseAnimation = new AnimationProperty(0.0F);
    public AnimationProperty punchChargeAnimation = new AnimationProperty(0.0F);
    public AnimationProperty punchReleaseAnimation = new AnimationProperty(0.0F);
    public AnimationProperty deathStartAnimation = new AnimationProperty(0.0F);
    public AnimationProperty deathFallAnimation = new AnimationProperty(0.0F);
    public AnimationProperty screamAnimation = new AnimationProperty(0.0F);

    private final Set<AnimationProperty> aliveAnimations = Set.of(
        leapChargeAnimation, leapReleaseAnimation, waveChargeAnimation, waveReleaseAnimation, punchChargeAnimation,
        punchReleaseAnimation, screamAnimation
    );

    public ConciergeEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        goalsInit = true;

        if (targetSelector.getAvailableGoals().isEmpty()) {
            targetSelector.addGoal(1, new HurtByTargetGoal(this));
            targetSelector.addGoal(0, new TargetRandomPlayerGoal<>(this));
        }

        if (stage == 0) {
            return;
        }

        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 32.0F));
        goalSelector.addGoal(3, new WalkTowardsTargetGoal(this, 1.0D + stage * 0.15D, false, 2.0D));

        if (stage > 1) {
            goalSelector.addGoal(0, new TimedDashGoal<>(this, settings -> {
                settings.onGround = true;
                settings.jumpHeight = 0.6D;
                settings.cooldownGetter = () -> leapCooldown + sharedCooldown;
                settings.cooldownSetter = cooldown -> {
                    leapCooldown = getStageAdjustedValue(8 * 20, 6 * 20, 4 * 20);
                    sharedCooldown = getStageAdjustedValue(80, 60, 20);
                };
                settings.defaultCooldown = 8 * 20;
                settings.stateSetter = handleStateChange(LEAP_CHARGING, LEAP_RELEASING);
                settings.chargeSound = MineCellsSounds.CONCIERGE_LEAP_CHARGE.get();
                settings.landSound = MineCellsSounds.CONCIERGE_LEAP_LAND.get();
                settings.chance = 0.3F;
                settings.alignTick = getStageAdjustedValue(30, 30, 25);
                settings.actionTick = getStageAdjustedValue(40, 35, 25);
                settings.speed = 1.25F;
                settings.minDistance = 5.0D;
                settings.overshoot = 1.5D;
                settings.length = 60;
                settings.margin = 1.0D;
                settings.damage = getDamage(2.5F);
                settings.particle = MineCellsParticles.SPECKLE.get().get(0xFF4000);
                settings.onLand = this::shakeScreenAfterLeap;
            }, e -> e.canAttack()
                && e.getTarget() != null
                && e.distanceTo(e.getTarget()) > 8.0D || stage == 3)
            );
        }

        goalSelector.addGoal(0, new ShockwaveGoal<>(this, settings -> {
            settings.cooldownGetter = () -> shockwaveCooldown + sharedCooldown;
            settings.cooldownSetter = cooldown -> {
                shockwaveCooldown = getStageAdjustedValue(10 * 20, 9 * 20, 8 * 20);
                sharedCooldown = getStageAdjustedValue(100, 80, 20);
            };
            settings.defaultCooldown = 8 * 20;
            settings.stateSetter = handleStateChange(SHOCKWAVE_CHARGING, SHOCKWAVE_RELEASING);
            settings.shockwaveBlock = MineCellsBlocks.SHOCKWAVE_FLAME.get();
            settings.shockwaveRadius = 20;
            settings.shockwaveInterval = getStageAdjustedValue(2.0F, 1.5F, 1.0F);
            settings.shockwaveType = ShockwaveType.CIRCLE;
            settings.chargeSound = MineCellsSounds.CONCIERGE_SHOCKWAVE_CHARGE.get();
            settings.releaseSound = MineCellsSounds.CONCIERGE_SHOCKWAVE_RELEASE.get();
            settings.chance = 0.2F;
            settings.length = 50;
            settings.shockwaveDamage = getDamage(1.5F);
            settings.actionTick = 30;
        }, ConciergeEntity::canAttack));

        goalSelector.addGoal(0, new TimedAuraGoal<>(this, settings -> {
            settings.cooldownGetter = () -> auraCooldown + sharedCooldown;
            settings.cooldownSetter = cooldown -> {
                auraCooldown = getStageAdjustedValue(20 * 20, 16 * 20, 8 * 20);
                sharedCooldown = getStageAdjustedValue(40, 40, 0);
            };
            settings.defaultCooldown = 30 * 20;
            settings.stateSetter = handleStateChange(AURA_CHARGING, AURA_RELEASING);
            settings.chargeSound = MineCellsSounds.CONCIERGE_AURA_CHARGE.get();
            settings.endSound = MineCellsSounds.CONCIERGE_AURA_RELEASE.get();
            settings.length = 120;
            settings.actionTick = 60;
            settings.radius = 4.0D;
            settings.damage = getDamage(1.5F);
            settings.chance = 0.02F;
        }, e -> e.level().getNearestPlayer(e, 5.0D) != null));

        goalSelector.addGoal(0, new ConciergePunchGoal(this, settings -> {
            settings.cooldownGetter = () -> punchCooldown;
            settings.cooldownSetter = cooldown -> punchCooldown = getStageAdjustedValue(80, 40, 10);
            settings.defaultCooldown = 4 * 20;
            settings.stateSetter = handleStateChange(PUNCH_CHARGING, PUNCH_RELEASING);
            settings.chargeSound = MineCellsSounds.CONCIERGE_PUNCH_CHARGE.get();
            settings.releaseSound = MineCellsSounds.CONCIERGE_PUNCH_RELEASE.get();
            settings.length = 30;
            settings.actionTick = 20;
            settings.damage = getDamage(2.5F);
            settings.knockback = getStageAdjustedValue(2.0D, 3.0D, 4.0D);
        }));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(LEAP_CHARGING, false);
        entityData.define(LEAP_RELEASING, false);
        entityData.define(SHOCKWAVE_CHARGING, false);
        entityData.define(SHOCKWAVE_RELEASING, false);
        entityData.define(AURA_CHARGING, false);
        entityData.define(AURA_RELEASING, false);
        entityData.define(PUNCH_CHARGING, false);
        entityData.define(PUNCH_RELEASING, false);
        entityData.define(SCREAMING, false);
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
        spawnPos = this.blockPosition();
        setYRot(180.0F);
        setYBodyRot(180.0F);
        yRotO = yBodyRotO = yHeadRotO = 180.0F;
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            var pos = position().add(0.0D, 1.0D, 0.0D);
            if (entityData.get(AURA_CHARGING)) {
                ParticleUtils.addAura(level(), pos, MineCellsParticles.AURA.get(), 5, 4.5D, -0.03D);
            } else if (entityData.get(AURA_RELEASING)) {
                ParticleUtils.addAura(level(), pos, MineCellsParticles.AURA.get(), 40, 4.0D, 0.01D);
                ParticleUtils.addAura(level(), pos, MineCellsParticles.AURA.get(), 10, 1.0D, 0.3D);
            }
        } else {
            if (spawnPos != null && getTarget() == null && canChangeStage()) {
                getNavigation().moveTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 1.3D);
            }
            stageTimer++;
            if (stage == 0 && getTarget() != null) {
                setStage(1);
            }
            if (stage == 1 && getHealth() / getMaxHealth() <= 0.66F && canChangeStage()) {
                setStage(2);
            }
            if (stage == 2 && getHealth() / getMaxHealth() <= 0.33F && canChangeStage()) {
                setStage(3);
            }
            entityData.set(SCREAMING, stageTimer <= 60);
            canAttack = true;
            if (stageTimer <= 80) {
                getNavigation().stop();
                Player nearestPlayer = level().getNearestPlayer(this, 100.0D);
                if (nearestPlayer != null) {
                    getLookControl().setLookAt(nearestPlayer, 360.0F, 360.0F);
                }
                canAttack = false;
            } else if (!goalsInit && isAlive()) {
                this.registerGoals();
            }
        }
    }

    private void setStage(int stage) {
        if (this.stage == stage) {
            return;
        }
        this.stage = stage;
        this.stageTimer = 0;
        clearGoals();
        addEffect(new MobEffectInstance(MineCellsStatusEffects.PROTECTED.get(), 100, 0, false, false, false));
        playSound(MineCellsSounds.CONCIERGE_SHOUT.get(), 2.0F, 1.0F);

        if (level() instanceof ServerLevel serverLevel) {
            ScreenShakeUtils.shakeAround(
                serverLevel,
                position(),
                1.0F,
                80,
                40.0D,
                60.0D,
                "minecells:concierge_roar"
            );
        }
    }

    private void clearGoals() {
        this.goalsInit = false;
        goalSelector.getAvailableGoals().forEach(WrappedGoal::stop);
        goalSelector.removeAllGoals(Objects::nonNull);
    }

    @Override
    protected void tickDeath() {
        clearGoals();
        ++this.deathTime;
        if (this.level().isClientSide) {
            aliveAnimations.forEach(animation -> animation.setupTransitionTo(0.0F, 5.0F, Mth::lerp));
            deathStartAnimation.setupTransitionTo(1.0F, 30.0F);
            if (this.deathTime >= 60) {
                deathFallAnimation.setupTransitionTo(1.0F, 60.0F);
            }
            if (this.deathTime >= 155) {
                level().addParticle(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() - 1.0D, getZ(), 0.0D, 0.0D, 0.0D);
            }
        } else {
            if (this.deathTime == 15) {
                shakeScreenOnDeath(0.25F, 10);
                playSound(MineCellsSounds.CONCIERGE_LEAP_LAND.get(), 0.8F, 1.1F);
            }
            if (this.deathTime == 85) {
                shakeScreenOnDeath(0.5F, 20);
                playSound(MineCellsSounds.CONCIERGE_LEAP_LAND.get(), 1.0F, 0.1F);
            }
            if (this.deathTime >= 160 && !this.isRemoved()) {
                shakeScreenOnDeath(1.0F, 60);
                playSound(MineCellsSounds.CONJUNCTIVIUS_DEATH.get(), 0.8F, 0.9F);
                this.level().broadcastEntityEvent(this, EntityEvent.POOF);
                this.remove(Entity.RemovalReason.KILLED);
            }
        }
    }

    private void shakeScreenOnDeath(float intensity, int duration) {
        if (level() instanceof ServerLevel serverLevel) {
            ScreenShakeUtils.shakeAround(
                serverLevel,
                position(),
                intensity,
                duration,
                10.0D,
                30.0D,
                "minecells:concierge_death"
            );
        }
    }

    @Override
    protected void decrementCooldowns() {
        leapCooldown--;
        shockwaveCooldown--;
        auraCooldown--;
        punchCooldown--;
        if (sharedCooldown > 0) {
            sharedCooldown--;
        }
    }

    @Override
    protected void processAnimations() {
        if (isDeadOrDying()) {
            return;
        }

        setupTransition(LEAP_CHARGING, leapChargeAnimation, 8.0F, 3.0F, MathUtils::lerp);
        setupTransition(LEAP_RELEASING, leapReleaseAnimation, 12.0F, 20.0F, MathUtils::lerp);
        setupTransition(SHOCKWAVE_CHARGING, waveChargeAnimation, 10.0F, 30.0F, MathUtils::lerp);
        setupTransition(SHOCKWAVE_RELEASING, waveReleaseAnimation, 10.0F, 20.0F, MathUtils::easeOutBack);
        setupTransition(PUNCH_CHARGING, punchChargeAnimation, 10.0F, 3.0F, MathUtils::easeOutQuad);
        setupTransition(PUNCH_RELEASING, punchReleaseAnimation, 10.0F, 6.0F, MathUtils::easeOutBack);
        setupTransition(SCREAMING, screamAnimation, 5.0F, 20.0F, MathUtils::easeOutQuad);
    }

    private boolean canChangeStage() {
        return !entityData.get(LEAP_CHARGING)
            && !entityData.get(LEAP_RELEASING)
            && !entityData.get(SHOCKWAVE_CHARGING)
            && !entityData.get(SHOCKWAVE_RELEASING)
            && !entityData.get(PUNCH_CHARGING)
            && !entityData.get(PUNCH_RELEASING);
    }

    private void setupTransition(
        EntityDataAccessor<Boolean> data,
        AnimationProperty animation,
        float onDuration,
        float offDuration,
        EasingFunction onEasing
    ) {
        if (entityData.get(data)) {
            animation.setupTransitionTo(1.0F, onDuration, onEasing);
        } else {
            animation.setupTransitionTo(0.0F, offDuration);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        float percentage = getStageAdjustedValue(1.0F, 0.75F, 0.5F);
        boolean result = super.hurt(source, amount * percentage);
        if (result) {
            walkAnimation.setSpeed(0.5F);
        }
        return result;
    }

    public boolean canAttack() {
        return canAttack && !isDeadOrDying();
    }

    @SafeVarargs
    private <T> T getStageAdjustedValue(T... stages) {
        return stages[min(stage, stages.length - 1)];
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        super.playStepSound(pos, state);
        playSound(MineCellsSounds.CONCIERGE_STEP.get(), 0.8F, random.nextFloat() * 0.2F + 0.8F);

        if (level().isClientSide) {
            return;
        }

        if (level() instanceof ServerLevel serverLevel) {
            ScreenShakeUtils.shakeAround(
                serverLevel,
                position(),
                1.0F,
                5,
                2.0D,
                20.0D,
                "minecells:concierge_step"
            );
        }
    }

    private void shakeScreenAfterLeap() {
        if (level().isClientSide) {
            return;
        }
        if (level() instanceof ServerLevel serverLevel) {
            ScreenShakeUtils.shakeAround(
                serverLevel,
                position(),
                1.0F,
                20,
                10.0D,
                30.0D,
                "minecells:concierge_leap"
            );
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        if (level().isClientSide || level().getServer() == null) {
            return;
        }

        var server = level().getServer();
        var advancement = server.getAdvancements().getAdvancement(MineCells.id("concierge"));

        if (advancement == null) {
            return;
        }

        level().getEntitiesOfClass(Player.class, AABB.ofSize(position(), 128.0D, 128.0D, 128.0D), Objects::nonNull).forEach(
            player -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.getAdvancements().award(advancement, "concierge_killed");
                }
            }
        );
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("leapCooldown", leapCooldown);
        tag.putInt("shockwaveCooldown", shockwaveCooldown);
        tag.putInt("auraCooldown", auraCooldown);
        tag.putInt("punchCooldown", punchCooldown);
        tag.putInt("sharedCooldown", sharedCooldown);
        if (spawnPos != null) {
            tag.putLong("spawnPos", spawnPos.asLong());
        }
        tag.putInt("stage", stage);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        leapCooldown = tag.getInt("leapCooldown");
        shockwaveCooldown = tag.getInt("shockwaveCooldown");
        auraCooldown = tag.getInt("auraCooldown");
        punchCooldown = tag.getInt("punchCooldown");
        sharedCooldown = tag.getInt("sharedCooldown");
        if (tag.contains("spawnPos")) {
            spawnPos = BlockPos.of(tag.getLong("spawnPos"));
        }
        stage = tag.getInt("stage");

        clearGoals();
        registerGoals();
    }

    public static AttributeSupplier.Builder createConciergeAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 300.0D)
            .add(Attributes.FOLLOW_RANGE, 40.0D)
            .add(Attributes.ARMOR, 5.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.18D)
            .add(Attributes.ATTACK_DAMAGE, 4.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }
}
