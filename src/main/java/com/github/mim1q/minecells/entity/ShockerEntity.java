package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShockerEntity extends MineCellsMonsterEntity {
    private static final EntityDataAccessor<Integer> AURA_COOLDOWN = SynchedEntityData.defineId(ShockerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> AURA_CHARGING = SynchedEntityData.defineId(ShockerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AURA_RELEASING = SynchedEntityData.defineId(ShockerEntity.class, EntityDataSerializers.BOOLEAN);

    public ShockerEntity(EntityType<? extends ShockerEntity> type, Level level) {
        super(type, level);
        this.noCulling = true;
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(AURA_COOLDOWN, 50);
        entityData.define(AURA_CHARGING, false);
        entityData.define(AURA_RELEASING, false);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new ShockerAuraGoal(this, 6.0D, 15, 60, 1.0F));
    }

    @Override
    protected boolean canUseEliteAura() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && getAuraCooldown() > 0) {
            setAuraCooldown(getAuraCooldown() - 1);
        }
        if (level().isClientSide) {
            Vec3 pos = position().add(0.0D, getBbHeight() * 0.5D, 0.0D);
            if (isAuraCharging()) {
                for (int i = 0; i < 10; i++) {
                    ParticleUtils.addParticle(level(), MineCellsParticles.CHARGE.get(), pos, Vec3.ZERO);
                }
            } else if (isAuraReleasing()) {
                ParticleUtils.addAura(level(), pos, MineCellsParticles.AURA.get(), 100, 5.5D, 0.01D);
                ParticleUtils.addAura(level(), pos, MineCellsParticles.AURA.get(), 10, 1.0D, 0.3D);
                Vec3 direction = new Vec3(random.nextGaussian(), random.nextGaussian(), random.nextGaussian());
                level().addParticle(
                    MineCellsParticles.ELECTRICITY.get().get(direction, 4, 0xFFf19f95, 1.0F),
                    pos.x,
                    pos.y,
                    pos.z,
                    0.0D,
                    0.0D,
                    0.0D
                );
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
        setNoGravity(true);
        setPos(getX(), getY() + 1.5D, getZ());
        float yaw = random.nextFloat() * 360.0F;
        setYRot(yaw);
        setYHeadRot(yaw);
        setYBodyRot(yaw);
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            return false;
        }
        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            amount *= 0.2F;
        }
        return super.hurt(source, amount);
    }

    public boolean isAuraCharging() {
        return entityData.get(AURA_CHARGING);
    }

    public void setAuraCharging(boolean charging) {
        entityData.set(AURA_CHARGING, charging);
    }

    public boolean isAuraReleasing() {
        return entityData.get(AURA_RELEASING);
    }

    public void setAuraReleasing(boolean releasing) {
        entityData.set(AURA_RELEASING, releasing);
    }

    public int getAuraCooldown() {
        return entityData.get(AURA_COOLDOWN);
    }

    public void setAuraCooldown(int cooldown) {
        entityData.set(AURA_COOLDOWN, Math.max(0, cooldown));
    }

    public int getAuraMaxCooldown() {
        return 60 + random.nextInt(40);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return MineCellsSounds.SHOCKER_DEATH.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("auraCooldown", getAuraCooldown());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setAuraCooldown(tag.getInt("auraCooldown"));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return MineCellsMonsterEntity.createBaseAttributes()
            .add(Attributes.MAX_HEALTH, 15.0D)
            .add(Attributes.FOLLOW_RANGE, 20.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
            .add(Attributes.ARMOR, 10.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.0D)
            .add(Attributes.ATTACK_DAMAGE, 0.0D);
    }

    private static final class ShockerAuraGoal extends Goal {
        private final ShockerEntity entity;
        private final double radius;
        private final int actionTick;
        private final int lengthTicks;
        private final float chance;
        private int ticks;

        private ShockerAuraGoal(ShockerEntity entity, double radius, int actionTick, int lengthTicks, float chance) {
            this.entity = entity;
            this.radius = radius;
            this.actionTick = actionTick;
            this.lengthTicks = lengthTicks;
            this.chance = chance;
        }

        @Override
        public boolean canUse() {
            Player closest = entity.level().getNearestPlayer(entity, radius - 1.0D);
            return entity.getAuraCooldown() == 0
                && closest != null
                && entity.hasLineOfSight(closest)
                && !(closest.isCreative() || closest.isSpectator())
                && entity.getRandom().nextFloat() < chance;
        }

        @Override
        public void start() {
            ticks = 0;
            entity.setAuraCharging(true);
            entity.playSound(MineCellsSounds.SHOCKER_CHARGE.get(), 1.0F, 1.0F);
        }

        @Override
        public boolean canContinueToUse() {
            return ticks < lengthTicks;
        }

        @Override
        public void stop() {
            entity.setAuraCharging(false);
            entity.setAuraReleasing(false);
            entity.setAuraCooldown(entity.getAuraMaxCooldown());
        }

        @Override
        public void tick() {
            if (ticks == actionTick) {
                entity.setAuraCharging(false);
                entity.setAuraReleasing(true);
                entity.playSound(MineCellsSounds.SHOCKER_RELEASE.get(), 1.0F, 1.0F);
            } else if (ticks >= actionTick && ticks % 2 == 0) {
                List<Entity> players = entity.level().getEntities(
                    entity,
                    entity.getBoundingBox().inflate(radius),
                    e -> e instanceof Player && entity.distanceTo(e) <= radius
                );
                for (Entity player : players) {
                    player.hurt(MineCellsDamageSource.AURA.get(entity.level(), entity), 5.0F);
                }
            }
            ticks++;
        }
    }
}
