package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import com.github.mim1q.minecells.util.ParticleUtils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProtectorEntity extends MineCellsMonsterEntity {
    private static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(ProtectorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final float RANGE = 16.0F;
    private static final int ACTIVE_TIME = 100;
    private static final int INACTIVE_TIME = 60;

    private int stateTicks;
    public List<Entity> trackedEntities = List.of();

    public ProtectorEntity(EntityType<? extends ProtectorEntity> type, Level level) {
        super(type, level);
        noCulling = true;
    }

    @Override
    protected void registerGoals() {
    }

    @Override
    protected boolean canUseEliteAura() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ACTIVE, false);
    }

    @Override
    public void tick() {
        if (isActive()) {
            List<Entity> entities = level().getEntities(
                this,
                AABB.ofSize(position(), RANGE, RANGE, RANGE),
                entity -> canProtect(entity) && entity.distanceTo(this) < RANGE
            );
            trackedEntities = entities;

            if (!level().isClientSide) {
                if (stateTicks == 1 || stateTicks % 20 == 0) {
                    playSound(MineCellsSounds.BUZZ.get(), 0.25F, 1.0F);
                }
                if (stateTicks > ACTIVE_TIME) {
                    setActive(false);
                    stateTicks = 0;
                }
                for (Entity entity : entities) {
                    ((LivingEntity) entity).addEffect(new MobEffectInstance(MineCellsStatusEffects.PROTECTED.get(), 5, 0, false, false));
                }
            } else if (!trackedEntities.isEmpty()) {
                ParticleUtils.addParticle(level(), MineCellsParticles.PROTECTOR.get(), position().add(0.0D, 1.0D, 0.0D), Vec3.ZERO);
            }
        } else {
            trackedEntities = List.of();
        }
        if (!level().isClientSide) {
            if (!isActive() && stateTicks > INACTIVE_TIME) {
                setActive(true);
                stateTicks = 0;
            }
            stateTicks++;
        }

        super.tick();
        setPos(xo, getY(), zo);
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
        float rotation = random.nextFloat() * 360.0F;
        setYRot(rotation);
        setYBodyRot(rotation);
        setYHeadRot(rotation);
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    protected static boolean canProtect(Entity entity) {
        if (entity instanceof KamikazeEntity || entity instanceof ProtectorEntity || entity instanceof MutatedBatEntity) {
            return false;
        }
        return entity instanceof Monster;
    }

    public boolean isActive() {
        return entityData.get(ACTIVE);
    }

    public void setActive(boolean active) {
        entityData.set(ACTIVE, active);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("stateTicks", stateTicks);
        tag.putBoolean("active", isActive());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
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
            .add(Attributes.ATTACK_DAMAGE, 2.0D)
            .add(Attributes.FOLLOW_RANGE, RANGE);
    }
}
