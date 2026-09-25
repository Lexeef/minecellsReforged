package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.ai.goal.WalkTowardsTargetGoal;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import java.util.EnumSet;
import java.util.function.Consumer;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.Nullable;

public class BuzzcutterEntity extends MineCellsMonsterEntity {
    private static final EntityDataAccessor<Boolean> DASH_CHARGING = SynchedEntityData.defineId(BuzzcutterEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DASH_RELEASING = SynchedEntityData.defineId(BuzzcutterEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationProperty bite = new AnimationProperty(0.0F, MathUtils::easeInOutQuad);

    private int dashCooldown = 50;

    public BuzzcutterEntity(EntityType<? extends BuzzcutterEntity> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DASH_CHARGING, false);
        entityData.define(DASH_RELEASING, false);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new BiteAttackGoal(this, s -> {
            s.cooldownGetter = () -> dashCooldown;
            s.cooldownSetter = cooldown -> dashCooldown = cooldown;
            s.stateSetter = this::switchDashState;
            s.actionTick = 15;
            s.length = 25;
            s.defaultCooldown = 50;
            s.chance = 0.2F;
            s.chargeSound = MineCellsSounds.FLY_CHARGE.get();
            s.releaseSound = MineCellsSounds.FLY_RELEASE.get();
        }));
        goalSelector.addGoal(1, new WalkTowardsTargetGoal(this, 3.0D, false));
        goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        addDefaultTargetGoals();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (isDashCharging()) {
                bite.setupTransitionTo(1.0F, 8.0F, MathUtils::easeOutBack);
            } else if (isDashReleasing()) {
                bite.setupTransitionTo(0.0F, 4.0F, MathUtils::easeInOutQuad);
            }
        } else {
            dashCooldown = Math.max(0, dashCooldown - 1);
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return !isDashCharging();
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new BuzzcutterNavigation(this, level);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return MineCellsSounds.FLY_FLY.get();
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
            .add(Attributes.MAX_HEALTH, 10.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D)
            .add(Attributes.MOVEMENT_SPEED, 0.0D)
            .add(Attributes.FLYING_SPEED, 0.33D)
            .add(Attributes.ATTACK_DAMAGE, 6.0D)
            .add(Attributes.FOLLOW_RANGE, 18.0D);
    }

    public static class BuzzcutterNavigation extends FlyingPathNavigation {
        public BuzzcutterNavigation(Monster host, Level level) {
            super(host, level);
            setCanOpenDoors(false);
            setCanFloat(false);
            setCanPassDoors(true);
        }

        @Override
        public boolean isStableDestination(BlockPos pos) {
            return !level.getBlockState(pos.below()).isAir();
        }

        @Override
        public Path createPath(Entity entity, int accuracy) {
            return createPath(entity.getX(), entity.getY() + 1.5D, entity.getZ(), accuracy);
        }
    }

    private static class BiteAttackGoal extends TimedActionGoal<BuzzcutterEntity> {
        BiteAttackGoal(BuzzcutterEntity entity, Consumer<TimedActionSettings> settingsConsumer) {
            super(entity, settingsConsumer, null);
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = entity.getTarget();
            return super.canUse() && target != null && target.distanceToSqr(entity) < 4.0D;
        }

        @Override
        public void start() {
            super.start();
            LivingEntity target = entity.getTarget();
            if (target != null) {
                entity.lookAt(target, 180.0F, 180.0F);
            }
            entity.getNavigation().stop();
        }

        @Override
        protected void runAction() {
            LivingEntity target = entity.getTarget();
            if (target != null && target.distanceToSqr(entity) < 6.0D) {
                target.hurt(entity.damageSources().mobAttack(entity), (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE));
            }
        }
    }
}
