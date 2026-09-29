package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedDashGoal;
import com.github.mim1q.minecells.entity.ai.goal.WalkTowardsTargetGoal;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.ParticleUtils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

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
        goalSelector.addGoal(0, new TimedDashGoal<>(this, s -> {
            s.cooldownSetter = cooldown -> dashCooldown = cooldown;
            s.cooldownGetter = () -> dashCooldown;
            s.stateSetter = this::switchDashState;
            s.chargeSound = MineCellsSounds.SHIELDBEARER_CHARGE.get();
            s.releaseSound = MineCellsSounds.SHIELDBEARER_RELEASE.get();
            s.speed = 0.65F;
            s.onGround = true;
            s.damage = 6.0F;
            s.defaultCooldown = 120;
            s.actionTick = 20;
            s.alignTick = 12;
            s.chance = 0.25F;
            s.length = 80;
            s.margin = 0.25D;
            s.particle = MineCellsParticles.SPECKLE.get().get(0xFF0000);
        }, mob -> {
            LivingEntity target = mob.getTarget();
            return target != null && mob.distanceTo(target) < 8.0D;
        }));
        goalSelector.addGoal(1, new WalkTowardsTargetGoal(this, 1.0D, true, 1.0D));
        goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        addDefaultTargetGoals();
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
        if (level().isClientSide && isDashCharging()) {
            for (int i = 0; i < 5; i++) {
                ParticleUtils.addParticle(level(), MineCellsParticles.CHARGE.get(), position().add(0.0D, getBbHeight() * 0.5D, 0.0D), Vec3.ZERO);
            }
        }
        dashCooldown = Math.max(0, dashCooldown - 1);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        Vec3 pos = source.getSourcePosition();
        if (pos != null) {
            Vec3 diff = pos.subtract(position());
            float angle = (float) Mth.atan2(diff.z, diff.x) * Mth.RAD_TO_DEG + 90.0F;
            if (Mth.degreesDifferenceAbs(yBodyRot, angle) > 110.0F) {
                playSound(SoundEvents.SHIELD_BLOCK, 0.3F, 1.0F);
                return true;
            }
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean canCollideWith(Entity other) {
        return super.canCollideWith(other) && !isDashReleasing();
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
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.ARMOR, 5.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.23D)
            .add(Attributes.ATTACK_DAMAGE, 5.0D)
            .add(Attributes.FOLLOW_RANGE, 16.0D);
    }
}
