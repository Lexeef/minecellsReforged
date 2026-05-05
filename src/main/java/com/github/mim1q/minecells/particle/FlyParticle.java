package com.github.mim1q.minecells.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class FlyParticle extends TextureSheetParticle {
    protected FlyParticle(ClientLevel level, double x, double y, double z, double yVelocity) {
        super(level, x, y, z);
        this.lifetime = 30 + level.getRandom().nextInt(20);
        this.yd = yVelocity;
        this.quadSize = 0.15F + level.getRandom().nextFloat() * 0.15F;
        this.setAlpha(0.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (level.getRandom().nextFloat() < 0.25F) {
            randomizeVelocity();
        }
        if (this.age <= 5) {
            this.setAlpha(this.age / 5.0F);
        }
        if (this.age > this.lifetime - 5) {
            this.setAlpha((this.lifetime - this.age) / 5.0F);
        }
    }

    private void randomizeVelocity() {
        this.xd = level.getRandom().nextFloat() * 0.1F - 0.05F;
        this.zd = level.getRandom().nextFloat() * 0.1F - 0.05F;
    }

    @Override
    protected float getU1() {
        float diff = super.getU1() - super.getU0();
        return this.getU0() + diff * 0.5F;
    }

    @Override
    protected float getV1() {
        float diff = super.getV1() - super.getV0();
        float multiplier = this.age % 2 == 0 ? 0.5F : 1.0F;
        return super.getV0() + diff * multiplier;
    }

    @Override
    protected float getV0() {
        float diff = super.getV1() - super.getV0();
        float multiplier = this.age % 2 == 0 ? 0.0F : 0.5F;
        return super.getV0() + diff * multiplier;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            FlyParticle particle = new FlyParticle(level, x, y, z, velocityY);
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}
