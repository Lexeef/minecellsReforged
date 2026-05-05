package com.github.mim1q.minecells.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class ProtectorParticle extends TextureSheetParticle {
    protected final float maxSize;

    protected ProtectorParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.setLifetime(Mth.nextInt(level.random, 10, 30));
        this.roll = level.random.nextFloat() * Mth.PI * 2.0F;
        this.oRoll = this.roll;
        this.maxSize = this.getLifetime() * 0.05F;
    }

    @Override
    public void tick() {
        super.tick();
        this.setAlpha(1.0F - ((float) this.age / this.getLifetime()));
    }

    @Override
    public float getQuadSize(float tickDelta) {
        return this.maxSize * (((float) this.age + tickDelta) / this.getLifetime());
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
            ProtectorParticle particle = new ProtectorParticle(level, x, y, z);
            particle.setParticleSpeed(velocityX, velocityY, velocityZ);
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}
