package com.github.mim1q.minecells.entity.nonliving.projectile;

import com.github.mim1q.minecells.entity.nonliving.SimpleProjectileEntity;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class MagicOrbEntity extends SimpleProjectileEntity {
    public MagicOrbEntity(EntityType<? extends SimpleProjectileEntity> entityType, Level level) {
        super(entityType, level);
    }

    public MagicOrbEntity(EntityType<? extends SimpleProjectileEntity> entityType, Level level, LivingEntity owner) {
        super(entityType, level, owner);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            spawnParticles();
        } else if (tickCount > 300 || getDeltaMovement().lengthSqr() < 0.01D) {
            discard();
        }
    }

    protected void spawnParticles() {
        var particle = MineCellsParticles.SPECKLE.get().get(0xFFAAFF);
        if (tickCount == 1) {
            ParticleUtils.addAura(level(), position().add(0.0D, 0.25D, 0.0D), particle, 15, 0.0D, 0.5D);
        }
        ParticleUtils.addAura(level(), position().add(0.0D, 0.25D, 0.0D), particle, 3, 0.5D, 0.0D);
        ParticleUtils.addAura(level(), position().add(0.0D, 0.25D, 0.0D), particle, 3, 0.0D, 0.0D);
    }

    @Override
    public float getDamage() {
        return 4.0F;
    }
}
