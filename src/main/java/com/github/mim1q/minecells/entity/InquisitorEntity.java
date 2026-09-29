package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.TimedShootGoal;
import com.github.mim1q.minecells.entity.nonliving.projectile.MagicOrbEntity;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.animation.AnimationProperty;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class InquisitorEntity extends MineCellsMonsterEntity {
    public final AnimationProperty armUpProgress = new AnimationProperty(0.0F);

    private static final EntityDataAccessor<Integer> SHOOT_COOLDOWN = SynchedEntityData.defineId(InquisitorEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SHOOT_CHARGING = SynchedEntityData.defineId(InquisitorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHOOT_RELEASING = SynchedEntityData.defineId(InquisitorEntity.class, EntityDataSerializers.BOOLEAN);

    public InquisitorEntity(EntityType<? extends InquisitorEntity> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SHOOT_COOLDOWN, 0);
        entityData.define(SHOOT_CHARGING, false);
        entityData.define(SHOOT_RELEASING, false);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new TimedShootGoal<>(this, settings -> {
            settings.cooldownGetter = this::getShootCooldown;
            settings.cooldownSetter = ignored -> setShootCooldown(getShootMaxCooldown());
            settings.stateSetter = (state, value) -> {
                switch (state) {
                    case CHARGE -> setShootCharging(value);
                    case RELEASE -> setShootReleasing(value);
                    default -> {
                    }
                }
            };
            settings.chargeSound = MineCellsSounds.INQUISITOR_CHARGE.get();
            settings.releaseSound = MineCellsSounds.INQUISITOR_RELEASE.get();
            settings.defaultCooldown = 40;
            settings.actionTick = 10;
            settings.length = 20;
            settings.chance = 0.3F;
            settings.projectileCreator = this::createMagicOrb;
            settings.updateEveryTick = false;
        }, e -> {
            LivingEntity target = e.getTarget();
            return target != null && target.isAlive() && e.hasLineOfSight(target);
        }));
        goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        addDefaultTargetGoals();
    }

    private MagicOrbEntity createMagicOrb(Vec3 from, Vec3 to) {
        LivingEntity target = getTarget();
        Vec3 entityPos = position().add(0.0D, 2.25D, 0.0D);
        Vec3 targetPos = target != null
            ? target.position().add(0.0D, 1.3D, 0.0D)
            : to;
        Vec3 velocity = targetPos.subtract(entityPos).normalize();
        MagicOrbEntity orb = new MagicOrbEntity(MineCellsEntities.MAGIC_ORB.get(), level(), this);
        orb.setPos(entityPos);
        orb.setDeltaMovement(velocity);
        return orb;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && getShootCooldown() > 0) {
            setShootCooldown(getShootCooldown() - 1);
        }
        if (level().isClientSide) {
            if (isShootCharging() || isShootReleasing()) {
                armUpProgress.setupTransitionTo(1.0F, 10.0F);
            } else {
                armUpProgress.setupTransitionTo(0.0F, 20.0F);
            }
        }
    }

    @Override
    public int getMaxFallDistance() {
        return 3;
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
        return 40 + random.nextInt(40);
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
            .add(Attributes.MOVEMENT_SPEED, 0.15D)
            .add(Attributes.FOLLOW_RANGE, 24.0D)
            .add(Attributes.MAX_HEALTH, 25.0D)
            .add(Attributes.ARMOR, 2.0D)
            .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }
}
