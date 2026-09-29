package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.util.MineCellsExplosion;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GrenadeProjectileEntity extends Projectile {
    private static final EntityDataAccessor<Integer> FUSE = SynchedEntityData.defineId(GrenadeProjectileEntity.class, EntityDataSerializers.INT);

    private Vec3 shootVector = Vec3.ZERO;
    private boolean shouldResetVelocity;
    protected float damage = 10.0F;
    protected float radius = 6.0F;

    public GrenadeProjectileEntity(EntityType<? extends GrenadeProjectileEntity> type, Level level) {
        super(type, level);
    }

    /** Called from {@link #defineSynchedData()} during construction, so it must not rely on subclass fields. */
    public int getMaxFuse() {
        return 10 + random.nextInt(5);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(FUSE, getMaxFuse());
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
        }

        move(MoverType.SELF, getDeltaMovement());
    }

    public void explode() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        LivingEntity attacker = getOwner() instanceof LivingEntity living ? living : null;
        MineCellsExplosion.explode(serverLevel, this, attacker, position(), damage, radius);
    }

    public int getFuse() {
        return entityData.get(FUSE);
    }

    public void setFuse(int fuse) {
        entityData.set(FUSE, fuse);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setFuse(tag.getInt("fuse"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("fuse", getFuse());
    }
}
