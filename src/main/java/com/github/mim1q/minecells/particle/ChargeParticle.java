package com.github.mim1q.minecells.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class ChargeParticle extends TextureSheetParticle {
    protected final float maxSize;

    protected ChargeParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.setLifetime(Mth.nextInt(level.random, 10, 25));
        this.roll = level.random.nextFloat() * Mth.PI * 2.0F;
        this.oRoll = this.roll;
        this.maxSize = this.getLifetime() * 0.1F;
        this.setAlpha(0.0F);
    }

    @Override
    public void tick() {
        this.setAlpha(0.1F + ((float) this.age * 3.0F) / (float) this.getLifetime());
        super.tick();
    }

    @Override
    public float getQuadSize(float tickDelta) {
        return Math.max(this.maxSize - (this.age + tickDelta) * 0.2F, 0.0F);
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
            ChargeParticle particle = new ChargeParticle(level, x, y, z);
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}
