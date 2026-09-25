package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.JumpBackGoal;
import com.github.mim1q.minecells.entity.ai.goal.ShockwaveGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.ai.goal.WalkTowardsTargetGoal;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static com.github.mim1q.minecells.util.MathUtils.radians;
import static java.lang.Math.abs;

public class SweeperEntity extends MineCellsMonsterEntity {
    private int sweepCooldown = 0;
    private int jumpbackCooldown = 0;
    private Vec3 gauntletPosition;
    private static final Vec3 GAUNTLET_OFFSET = new Vec3(-0.75D, 0.2D, -0.9D);

    public final AnimationProperty sweepCharge = new AnimationProperty(0F);
    public final AnimationProperty sweepRelease = new AnimationProperty(0F, MathUtils::easeOutBack);
    public final AnimationProperty rollCharge = new AnimationProperty(0F);

    public float rollAnimation = 0F;
    public float lastRollAnimation = 0F;

    private static final EntityDataAccessor<Boolean> SWEEP_CHARGING = SynchedEntityData.defineId(SweeperEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SWEEP_RELEASING = SynchedEntityData.defineId(SweeperEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> JUMPBACK_RELEASING = SynchedEntityData.defineId(SweeperEntity.class, EntityDataSerializers.BOOLEAN);

    public SweeperEntity(EntityType<? extends SweeperEntity> type, Level level) {
        super(type, level);
        this.moveControl = new SweeperMoveControl(this);
        this.gauntletPosition = position();
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new ShockwaveGoal<>(
            this,
            settings -> {
                settings.cooldownGetter = () -> sweepCooldown;
                settings.cooldownSetter = cooldown -> sweepCooldown = cooldown;
                settings.defaultCooldown = 40;
                settings.chance = 0.1F;
                settings.stateSetter = this::handleSweepState;
                settings.actionTick = 20;
                settings.length = 40;
                settings.chargeSound = MineCellsSounds.SWEEPER_CHARGE.get();
                settings.releaseSound = MineCellsSounds.SWEEPER_RELEASE.get();
                settings.shockwaveBlock = MineCellsBlocks.SHOCKWAVE_FLAME.get();
                settings.shockwaveDamage = 6.0F;
                settings.shockwaveInterval = 1.0F;
            },
            e -> e.getTarget() != null
                && abs(e.getTarget().getY() - e.getY()) < 2.0D
                && ((e.getNavigation().getPath() != null && e.getNavigation().getPath().canReach())
                || e.distanceToSqr(e.getTarget()) < 5.0D * 5.0D)
        ));
        goalSelector.addGoal(1, new JumpBackGoal<>(this, s -> {
            s.backStrength = 1.25D;
            s.minDistance = 4.0D;
            s.upStrength = 0.1D;
            s.defaultCooldown = 40;
            s.actionTick = 10;
            s.length = 20;
            s.chance = 0.3F;
            s.cooldownGetter = () -> jumpbackCooldown;
            s.cooldownSetter = ticks -> jumpbackCooldown = ticks;
            s.stateSetter = (state, value) -> {
                if (state == TimedActionGoal.State.RELEASE) {
                    entityData.set(JUMPBACK_RELEASING, value);
                }
            };
        }, null));
        goalSelector.addGoal(2, new WalkTowardsTargetGoal(this, 1.0D, false, 5.0D));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16.0F));
        goalSelector.addGoal(4, new FloatGoal(this));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        addDefaultTargetGoals();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SWEEP_CHARGING, false);
        entityData.define(SWEEP_RELEASING, false);
        entityData.define(JUMPBACK_RELEASING, false);
    }

    protected void handleSweepState(TimedActionGoal.State state, boolean value) {
        switch (state) {
            case CHARGE -> entityData.set(SWEEP_CHARGING, value);
            case RELEASE -> entityData.set(SWEEP_RELEASING, value);
            default -> {
            }
        }
    }

    public boolean isSweepCharging() {
        return entityData.get(SWEEP_CHARGING);
    }

    public boolean isSweepReleasing() {
        return entityData.get(SWEEP_RELEASING);
    }

    public boolean isJumpbackReleasing() {
        return entityData.get(JUMPBACK_RELEASING);
    }

    @Override
    public void tick() {
        super.tick();

        this.sweepCooldown--;
        this.jumpbackCooldown--;

        if (level().isClientSide) {
            var lastGauntletPosition = gauntletPosition;
            gauntletPosition = position().add(MathUtils.vectorRotateY(GAUNTLET_OFFSET, radians(this.yBodyRot)));
            spawnGauntletParticles(lastGauntletPosition.distanceToSqr(gauntletPosition) > 0.0001D);

            if (isSweepCharging()) {
                sweepCharge.setupTransitionTo(1.0F, 10.0F);
            } else {
                sweepCharge.setupTransitionTo(0.0F, 8.0F);
            }
            if (isSweepReleasing()) {
                sweepRelease.setupTransitionTo(1.0F, 15.0F, MathUtils::easeOutBack);
            } else {
                sweepRelease.setupTransitionTo(0.0F, 20.0F, MathUtils::easeInOutQuad);
            }

            lastRollAnimation = rollAnimation;
            if (isJumpbackReleasing()) {
                rollCharge.setupTransitionTo(1.0F, 2.0F);
                rollAnimation += 30;
            } else {
                rollCharge.setupTransitionTo(0.0F, 10.0F);
                var nearest360 = MathUtils.getClosestMultiple((int) rollAnimation, 360);
                rollAnimation = MathUtils.lerp(rollAnimation, nearest360, 0.1F);
            }
        }
    }

    public float getRollAnimation(float tickDelta) {
        return MathUtils.lerp(lastRollAnimation, rollAnimation, tickDelta);
    }

    private void spawnGauntletParticles(boolean moving) {
        var chance = moving ? 1.0F : 0.1F;
        if (random.nextFloat() < chance) {
            var randomOffset = new Vec3(random.nextDouble() - 0.5D, random.nextDouble() - 0.5D, random.nextDouble() - 0.5D).scale(0.5D);
            var pos = gauntletPosition.add(randomOffset);
            level().addParticle(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 0.0D, 0.01D, 0.0D);
        }
        if (moving) {
            var randomOffset = new Vec3(random.nextDouble() - 0.5D, 0.0D, random.nextDouble() - 0.5D).scale(0.5D);
            var pos = gauntletPosition.add(randomOffset);
            level().addParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 0.0D, 0.01D, 0.0D);
        }
    }


    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("sweepCooldown", sweepCooldown);
        tag.putInt("jumpbackCooldown", jumpbackCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        sweepCooldown = tag.getInt("sweepCooldown");
        jumpbackCooldown = tag.getInt("jumpbackCooldown");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return MineCellsMonsterEntity.createBaseAttributes()
            .add(Attributes.MAX_HEALTH, 24.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.19D)
            .add(Attributes.ATTACK_DAMAGE, 6.0D)
            .add(Attributes.FOLLOW_RANGE, 18.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
    }

    private static class SweeperMoveControl extends MoveControl {
        public SweeperMoveControl(Mob entity) {
            super(entity);
        }

        @Override
        public void tick() {
            if (this.mob.tickCount % 30 == 0 && this.operation == Operation.MOVE_TO) {
                this.mob.playSound(SoundEvents.GRINDSTONE_USE, 1.0F, 1.0F);
            }
            if (this.mob.tickCount % 30 <= 15) {
                super.tick();
            } else {
                this.mob.setSpeed(0.0F);
            }
        }
    }
}
