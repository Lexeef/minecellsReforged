package com.github.mim1q.minecells.entity.nonliving.projectile;

import com.github.mim1q.minecells.entity.nonliving.SimpleProjectileEntity;
import com.github.mim1q.minecells.util.ParticleUtils;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ScorpionSpitEntity extends MagicOrbEntity {
    private static final ParticleOptions PARTICLE = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SLIME_BLOCK.defaultBlockState());

    public ScorpionSpitEntity(EntityType<? extends SimpleProjectileEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = false;
    }

    public ScorpionSpitEntity(EntityType<? extends SimpleProjectileEntity> entityType, Level level, LivingEntity owner) {
        super(entityType, level, owner);
        this.noPhysics = false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && (horizontalCollision || verticalCollision)) {
            discard();
        }
    }

    @Override
    protected void spawnParticles() {
        ParticleUtils.addParticle(level(), PARTICLE, position().add(0.0D, 0.25D, 0.0D), Vec3.ZERO);
    }

    @Override
    public float getDamage() {
        return 4.0F;
    }
}
