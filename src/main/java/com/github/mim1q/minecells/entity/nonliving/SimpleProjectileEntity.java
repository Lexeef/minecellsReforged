package com.github.mim1q.minecells.entity.nonliving;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.network.NetworkHooks;

import java.util.UUID;

public class SimpleProjectileEntity extends Entity {
    private UUID ownerUuid;

    public SimpleProjectileEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.ownerUuid = null;
    }

    public SimpleProjectileEntity(EntityType<?> type, Level level, LivingEntity owner) {
        super(type, level);
        this.ownerUuid = owner.getUUID();
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 nextPos = position().add(getDeltaMovement());
        if (!level().isClientSide) {
            EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                level(),
                this,
                position(),
                nextPos,
                getBoundingBox().expandTowards(getDeltaMovement()).inflate(1.0D),
                entity -> (ownerUuid == null || !entity.getUUID().equals(ownerUuid)) && entity instanceof LivingEntity
            );
            if (entityHit != null && entityHit.getType() == HitResult.Type.ENTITY) {
                onHitEntity((LivingEntity) entityHit.getEntity());
            }

            BlockHitResult blockHit = level().clip(new ClipContext(
                position(),
                nextPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                this
            ));
            if (blockHit.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = blockHit.getBlockPos();
                onHitBlock(blockHit.getLocation(), blockPos, level().getBlockState(blockPos), blockHit.getDirection());
            }

            if (tickCount >= 20 * 60) {
                discard();
            }
        }

        setPos(nextPos.x, nextPos.y, nextPos.z);
    }

    public void onHitEntity(LivingEntity target) {
        LivingEntity owner = null;
        if (ownerUuid != null && level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(ownerUuid);
            if (entity instanceof LivingEntity living) {
                owner = living;
            }
        }
        target.hurt(damageSources().mobProjectile(this, owner), getDamage());
        discard();
    }

    public void onHitBlock(Vec3 pos, BlockPos blockPos, BlockState state, Direction side) {
        discard();
    }

    public float getDamage() {
        return 3.0F;
    }

    @Override
    public void setDeltaMovement(Vec3 velocity) {
        super.setDeltaMovement(velocity);
        double x = velocity.x;
        double y = velocity.y;
        double z = velocity.z;
        double horizontal = velocity.horizontalDistance();
        setYRot((float) (-Mth.atan2(x, z) * Mth.RAD_TO_DEG));
        setXRot((float) (-Mth.atan2(y, horizontal) * Mth.RAD_TO_DEG));
        yRotO = getYRot();
        xRotO = getXRot();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("owner")) {
            this.ownerUuid = tag.getUUID("owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (ownerUuid != null) {
            tag.putUUID("owner", ownerUuid);
        }
    }
}
