package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GrenadeProjectileEntity extends Projectile {
    private static final EntityDataAccessor<Integer> FUSE = SynchedEntityData.defineId(GrenadeProjectileEntity.class, EntityDataSerializers.INT);

    private Vec3 shootVector = Vec3.ZERO;
    private boolean shouldResetVelocity;
    private float damage = 10.0F;
    private float radius = 6.0F;

    public GrenadeProjectileEntity(EntityType<? extends GrenadeProjectileEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(FUSE, 10 + random.nextInt(5));
    }

    public void shoot(Vec3 velocity) {
        this.shouldResetVelocity = true;
        this.shootVector = velocity;
    }

    @Override
    public void tick() {
        super.tick();
        if (shouldResetVelocity) {
            shouldResetVelocity = false;
            setDeltaMovement(shootVector);
        }

        int fuse = getFuse() - 1;
        if (onGround()) {
            setDeltaMovement(getDeltaMovement().multiply(0.7D, 0.0D, 0.7D));
            setFuse(fuse);
        }

        if (!level().isClientSide) {
            if (fuse <= 0) {
                explode();
                discard();
                return;
            }
            setDeltaMovement(getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        } else {
            level().addParticle(MineCellsParticles.CHARGE.get(), getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
        }

        move(MoverType.SELF, getDeltaMovement());
    }

    private void explode() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Vec3 center = position();
        AABB range = AABB.ofSize(center, radius * 2.0D, radius * 2.0D, radius * 2.0D);
        Entity owner = getOwner();
        for (LivingEntity living : level().getEntitiesOfClass(LivingEntity.class, range, target -> target.isAlive() && target != owner && !(target instanceof MineCellsMonsterEntity))) {
            double distance = Math.sqrt(living.distanceToSqr(center));
            if (distance > radius) {
                continue;
            }
            float scaledDamage = (float) (damage * (1.0D - distance / radius));
            if (scaledDamage > 0.0F) {
                living.hurt(damageSources().mobProjectile(this, owner instanceof LivingEntity livingOwner ? livingOwner : null), scaledDamage);
            }
        }
        serverLevel.sendParticles(ParticleTypes.EXPLOSION, getX(), getY(), getZ(), 4, 0.25D, 0.25D, 0.25D, 0.02D);
        serverLevel.playSound(null, blockPosition(), MineCellsSounds.EXPLOSION.get(), SoundSource.HOSTILE, 0.8F, 1.0F);
    }

    public int getFuse() {
        return entityData.get(FUSE);
    }

    public void setFuse(int fuse) {
        entityData.set(FUSE, fuse);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setFuse(tag.getInt("fuse"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("fuse", getFuse());
    }
}
