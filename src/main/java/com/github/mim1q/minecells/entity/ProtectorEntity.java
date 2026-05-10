package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class ProtectorEntity extends MineCellsMonsterEntity {
    private static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(ProtectorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final float RANGE = 16.0F;
    private static final int ACTIVE_TIME = 100;
    private static final int INACTIVE_TIME = 60;

    private int stateTicks;

    public ProtectorEntity(EntityType<? extends ProtectorEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ACTIVE, false);
    }

    @Override
    public void tick() {
        super.tick();
        setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
        getNavigation().stop();

        if (level().isClientSide) {
            return;
        }

        if (isActive()) {
            applyProtectionAura();
            if (stateTicks == 1 || stateTicks % 20 == 0) {
                playSound(MineCellsSounds.BUZZ.get(), 0.25F, 1.0F);
            }
            if (stateTicks % 4 == 0 && level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(protectorParticle(), getX(), getY() + 1.0D, getZ(), 1, 0.15D, 0.15D, 0.15D, 0.0D);
            }
            if (stateTicks > ACTIVE_TIME) {
                setActive(false);
                stateTicks = 0;
            }
        } else if (stateTicks > INACTIVE_TIME) {
            setActive(true);
            stateTicks = 0;
        }

        stateTicks++;
    }

    private void applyProtectionAura() {
        AABB range = AABB.ofSize(position(), RANGE, RANGE, RANGE);
        for (Mob mob : level().getEntitiesOfClass(Mob.class, range, this::canProtect)) {
            mob.addEffect(new MobEffectInstance(MineCellsStatusEffects.PROTECTED.get(), 10, 0, false, false));
        }
    }

    private boolean canProtect(Mob mob) {
        if (mob == this) {
            return false;
        }
        EntityType<?> type = mob.getType();
        if (type == MineCellsEntities.KAMIKAZE.get() || type == MineCellsEntities.PROTECTOR.get() || type == MineCellsEntities.MUTATED_BAT.get()) {
            return false;
        }
        return type.getCategory() == MobCategory.MONSTER;
    }

    private static SimpleParticleType protectorParticle() {
        return MineCellsParticles.PROTECTOR.get();
    }

    public boolean isActive() {
        return entityData.get(ACTIVE);
    }

    public void setActive(boolean active) {
        entityData.set(ACTIVE, active);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("stateTicks", stateTicks);
        tag.putBoolean("active", isActive());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        stateTicks = tag.getInt("stateTicks");
        setActive(tag.getBoolean("active"));
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return MineCellsMonsterEntity.createBaseAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
            .add(Attributes.ARMOR, 8.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.0D)
            .add(Attributes.FOLLOW_RANGE, RANGE);
    }
}
