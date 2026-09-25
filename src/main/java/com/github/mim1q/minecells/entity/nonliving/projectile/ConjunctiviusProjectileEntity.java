package com.github.mim1q.minecells.entity.nonliving.projectile;

import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.entity.nonliving.SimpleProjectileEntity;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ConjunctiviusProjectileEntity extends MagicOrbEntity {
    private float damage = 8.0F;

    public ConjunctiviusProjectileEntity(EntityType<? extends SimpleProjectileEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ConjunctiviusProjectileEntity(Level level, ConjunctiviusEntity owner) {
        super(MineCellsEntities.CONJUNCTIVIUS_PROJECTILE.get(), level, owner);
    }

    public static void spawn(Level level, Vec3 pos, Vec3 target, ConjunctiviusEntity owner) {
        spawn(level, pos, target, owner, owner.getDamage(1.0F));
    }

    public static void spawn(Level level, Vec3 pos, Vec3 target, ConjunctiviusEntity owner, float damage) {
        Vec3 velocity = target.subtract(pos).normalize();
        ConjunctiviusProjectileEntity projectile = new ConjunctiviusProjectileEntity(level, owner);
        projectile.setPos(pos.x, pos.y, pos.z);
        projectile.setDeltaMovement(velocity.scale(1.2D));
        projectile.damage = damage;
        level.addFreshEntity(projectile);
    }

    @Override
    public float getDamage() {
        return this.damage;
    }

    @Override
    protected void spawnParticles() {
        Vec3 inverseVelocity = getDeltaMovement().scale(-0.4D - random.nextDouble() * 0.2D);
        level().addParticle(
            MineCellsParticles.SPECKLE.get().get(0x00FF40),
            this.xo + (random.nextDouble() - 0.5D) * 0.25D,
            this.yo + getBbHeight() / 2.0D + (random.nextDouble() - 0.5D) * 0.25D,
            this.zo + (random.nextDouble() - 0.5D) * 0.25D,
            inverseVelocity.x, inverseVelocity.y, inverseVelocity.z
        );
        if (random.nextFloat() < 0.2F) {
            level().addParticle(
                MineCellsParticles.ELECTRICITY.get().get(getLookAngle().scale(-1.0D), 3, 0x00FF40, 0.25F),
                xo, yo + getBbHeight() / 2.0D, zo,
                getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z
            );
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("damage")) {
            this.damage = nbt.getFloat("damage");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putFloat("damage", this.damage);
    }
}
