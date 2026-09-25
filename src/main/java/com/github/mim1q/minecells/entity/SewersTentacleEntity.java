package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedDashGoal;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import com.google.common.collect.HashMultimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public class SewersTentacleEntity extends MineCellsMonsterEntity {
    public final AnimationProperty belowGround = new AnimationProperty(-2.0F, MathUtils::easeInOutQuad);
    public final AnimationProperty wobble = new AnimationProperty(0.0F, MathUtils::easeInOutQuad);
    public final AnimationProperty wobbleOffset = new AnimationProperty(0.0F, MathUtils::easeInOutQuad);

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(SewersTentacleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> BURIED = SynchedEntityData.defineId(SewersTentacleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SPAWNED_BY_BOSS = SynchedEntityData.defineId(SewersTentacleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DASH_CHARGING = SynchedEntityData.defineId(SewersTentacleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DASH_RELEASING = SynchedEntityData.defineId(SewersTentacleEntity.class, EntityDataSerializers.BOOLEAN);

    private static final UUID BOSS_ARMOR_UUID = UUID.fromString("1a2b3c4d-5e6f-7081-9203-a4b5c6d7e8f0");
    private static final UUID BOSS_DAMAGE_UUID = UUID.fromString("2b3c4d5e-6f70-8192-03a4-b5c6d7e8f091");
    private static final UUID BOSS_SPEED_UUID = UUID.fromString("3c4d5e6f-7081-9203-a4b5-c6d7e8f091a2");

    private static final HashMultimap<Attribute, AttributeModifier> BOSS_MODIFIERS = HashMultimap.create();

    static {
        BOSS_MODIFIERS.put(Attributes.ARMOR, new AttributeModifier(BOSS_ARMOR_UUID, "boss_armor", 5.0D, AttributeModifier.Operation.ADDITION));
        BOSS_MODIFIERS.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BOSS_DAMAGE_UUID, "boss_damage", 4.0D, AttributeModifier.Operation.ADDITION));
        BOSS_MODIFIERS.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(BOSS_SPEED_UUID, "boss_speed", 0.05D, AttributeModifier.Operation.ADDITION));
    }

    private int buriedTicks = 0;
    private int dashCooldown = 100;

    public SewersTentacleEntity(EntityType<? extends MineCellsMonsterEntity> entityType, Level level) {
        super(entityType, level);
        setMaxUpStep(0.5F);
        updateAttributeModifiers();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(VARIANT, 0);
        entityData.define(BURIED, true);
        entityData.define(SPAWNED_BY_BOSS, false);
        entityData.define(DASH_CHARGING, false);
        entityData.define(DASH_RELEASING, false);
    }

    @Override
    protected boolean canUseEliteAura() {
        return false;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new TentacleAttackGoal(this, 1.2D));
        goalSelector.addGoal(0, new TentacleSweepGoal(this, settings -> {
            settings.cooldownSetter = cooldown -> this.dashCooldown = cooldown;
            settings.cooldownGetter = () -> this.dashCooldown;
            settings.stateSetter = this::switchDashState;
            settings.speed = 0.4F;
            settings.onGround = true;
            settings.damage = 6.0F;
            settings.defaultCooldown = 100;
            settings.actionTick = 5;
            settings.alignTick = 4;
            settings.chance = 0.075F;
            settings.length = 80;
            settings.margin = 0.25D;
        }));

        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 0, false, false, null));
        targetSelector.addGoal(0, new HurtByTargetGoal(this));
    }

    private void switchDashState(TimedActionGoal.State state, boolean value) {
        if (state == TimedActionGoal.State.CHARGE && value) {
            setBuried(false);
            setMaxUpStep(0.0F);
        }
        if (state == TimedActionGoal.State.RELEASE && !value) {
            setBuried(true);
            setMaxUpStep(1.0F);
        }
        switch (state) {
            case CHARGE -> entityData.set(DASH_CHARGING, value);
            case RELEASE -> entityData.set(DASH_RELEASING, value);
            default -> {
            }
        }
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
        updateAttributeModifiers();
        float chance = random.nextFloat();
        int variant = 0;
        if (chance > 0.5F) {
            variant = chance > 0.83F ? 2 : 1;
        }
        setVariant(variant);
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            spawnMovingParticles();
            if (isAlive()) {
                if (isBuried()) {
                    belowGround.setupTransitionTo(-2.5F, 20.0F);
                    wobble.setupTransitionTo(0.0F, 15.0F);
                } else {
                    belowGround.setupTransitionTo(0.0F, 10.0F);
                    wobble.setupTransitionTo(1.0F, 20.0F);
                }
                if (getStateFromTrackedData(DASH_CHARGING, DASH_RELEASING) == TimedActionGoal.State.IDLE) {
                    wobbleOffset.setupTransitionTo(0.0F, 20.0F);
                } else {
                    wobbleOffset.setupTransitionTo(1.0F, 10.0F);
                }
            }
        } else if (isSpawnedByBoss() && tickCount > 20 * 3 * 60 && tickCount % 20 == 0) {
            hurt(damageSources().dryOut(), 5.0F);
        }
        dashCooldown--;
        buriedTicks = isBuried() ? buriedTicks + 1 : 0;
    }

    @Override
    protected void tickDeath() {
        belowGround.setupTransitionTo(-2.5F, 10.0F);
        super.tickDeath();
    }

    private void updateAttributeModifiers() {
        getAttributes().removeAttributeModifiers(BOSS_MODIFIERS);
        if (isSpawnedByBoss()) {
            getAttributes().addTransientAttributeModifiers(BOSS_MODIFIERS);
        }
    }

    protected void spawnMovingParticles() {
        BlockState blockState = level().getBlockState(BlockPos.containing(position().subtract(0.0D, 0.01D, 0.0D)));
        if (blockState.isSolidRender(level(), BlockPos.containing(position().subtract(0.0D, 0.01D, 0.0D)))) {
            ParticleOptions particle = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
            ParticleUtils.addInBox(
                level(),
                particle,
                AABB.ofSize(position().add(0.0D, 0.125D, 0.0D), 1.0D, 0.25D, 1.0D),
                isBuried() ? 10 : 5,
                new Vec3(-0.01D, -0.01D, -0.01D)
            );
        }
    }

    protected TimedActionGoal.State getStateFromTrackedData(EntityDataAccessor<Boolean> charging, EntityDataAccessor<Boolean> releasing) {
        if (entityData.get(charging)) {
            return TimedActionGoal.State.CHARGE;
        }
        if (entityData.get(releasing)) {
            return TimedActionGoal.State.RELEASE;
        }
        return TimedActionGoal.State.IDLE;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (
            damageSource.is(DamageTypeTags.IS_EXPLOSION)
                || damageSource.is(DamageTypes.FELL_OUT_OF_WORLD)
                || damageSource.is(DamageTypes.GENERIC_KILL)
                || (damageSource.getEntity() instanceof Player player && player.getAbilities().instabuild)
                || damageSource.is(DamageTypes.DRY_OUT)
        ) {
            return false;
        }
        if ((isBuried() && buriedTicks > 20) || damageSource.is(DamageTypes.IN_WALL)) {
            return true;
        }
        return super.isInvulnerableTo(damageSource);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity other) {
        if (other instanceof SewersTentacleEntity) {
            return true;
        }
        if (isBuried()) {
            return false;
        }
        return super.canCollideWith(other);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean isInvisible() {
        return isBuried() && (buriedTicks > 20 || tickCount <= 20);
    }

    public static AttributeSupplier.Builder createSewersTentacleAttributes() {
        return createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 30.0D)
            .add(Attributes.ARMOR, 2.5D)
            .add(Attributes.MOVEMENT_SPEED, 0.22D)
            .add(Attributes.ATTACK_DAMAGE, 6.0D)
            .add(Attributes.ATTACK_KNOCKBACK, 2.0D)
            .add(Attributes.FOLLOW_RANGE, 32.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    public int getVariant() {
        return entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        entityData.set(VARIANT, variant);
    }

    public boolean isBuried() {
        return entityData.get(BURIED);
    }

    public void setBuried(boolean buried) {
        entityData.set(BURIED, buried);
    }

    public boolean isSpawnedByBoss() {
        return entityData.get(SPAWNED_BY_BOSS);
    }

    public void setSpawnedByBoss(boolean spawned) {
        entityData.set(SPAWNED_BY_BOSS, spawned);
        updateAttributeModifiers();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return MineCellsSounds.SEWERS_TENTACLE_DEATH.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("variant", getVariant());
        tag.putBoolean("buried", isBuried());
        tag.putBoolean("spawnedByBoss", isSpawnedByBoss());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setVariant(tag.getInt("variant"));
        setBuried(tag.getBoolean("buried"));
        setSpawnedByBoss(tag.getBoolean("spawnedByBoss"));
    }

    public static class TentacleAttackGoal extends MeleeAttackGoal {
        private int ticks = 0;
        private boolean attacking = false;

        public TentacleAttackGoal(SewersTentacleEntity mob, double speed) {
            super(mob, speed, true);
        }

        @Override
        public void tick() {
            SewersTentacleEntity tentacle = (SewersTentacleEntity) this.mob;
            if (attacking) {
                if (ticks > 15) {
                    tentacle.setBuried(false);
                    for (Player player : tentacle.level().getNearbyPlayers(
                        TargetingConditions.DEFAULT,
                        tentacle,
                        tentacle.getBoundingBox().inflate(0.75D, 0.0D, 0.75D)
                    )) {
                        this.checkAndPerformAttack(player, player.distanceToSqr(tentacle));
                    }
                    if (ticks > 80) {
                        attacking = false;
                    }
                }
                ticks++;
            } else {
                tentacle.setBuried(true);
                int maxBuriedTicks = tentacle.isSpawnedByBoss() ? 40 : 20;
                if (tentacle.buriedTicks > maxBuriedTicks && tentacle.getTarget() != null) {
                    LivingEntity target = tentacle.getTarget();
                    double x = target.getX() + (tentacle.getRandom().nextDouble() - 0.5D) * 3.0D;
                    double z = target.getZ() + (tentacle.getRandom().nextDouble() - 0.5D) * 3.0D;
                    tentacle.getMoveControl().setWantedPosition(x, target.getY(), z, 1.0D);
                    if (target.distanceTo(tentacle) <= 1.0D) {
                        tentacle.playSound(MineCellsSounds.CHARGE.get(), 1.0F, 1.0F);
                        attacking = true;
                        ticks = 0;
                    }
                }
            }
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity target, double squaredDistance) {
            if (attacking) {
                float damage = (float) this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
                target.hurt(target.damageSources().mobAttack(this.mob), damage);
                Vec3 diffNorm = this.mob.position().subtract(target.position()).normalize();
                target.knockback(1.0D, diffNorm.x, diffNorm.z);
            }
        }

        @Override
        public void stop() {
            super.stop();
            ticks = 0;
            attacking = false;
            ((SewersTentacleEntity) this.mob).setBuried(true);
        }
    }

    public static class TentacleSweepGoal extends TimedDashGoal<SewersTentacleEntity> {
        public TentacleSweepGoal(SewersTentacleEntity entity, Consumer<TimedDashSettings> settingsConsumer) {
            super(entity, settingsConsumer, e ->
                e.getTarget() != null
                    && Math.abs(e.getTarget().getY() - e.getY()) < 0.1F
                    && e.buriedTicks > 60
                    && (e.distanceToSqr(e.getTarget()) > 16.0F || e.isSpawnedByBoss())
            );
        }

        @Override
        protected boolean shouldSlowDown() {
            return distanceTravelled > 1.25D * targetDistance;
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && !shouldSlowDown() && entity.fallDistance == 0 && !entity.horizontalCollision;
        }
    }
}
