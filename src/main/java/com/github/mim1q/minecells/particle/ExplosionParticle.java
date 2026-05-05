package com.github.mim1q.minecells.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class ExplosionParticle extends TextureSheetParticle {
    private final float targetRadius;
    private float currentRadius;

    protected ExplosionParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.setParticleSpeed(0.0D, 0.0D, 0.0D);
        this.setLifetime(5);
        this.targetRadius = 2.0F;
        this.currentRadius = 0.0F;
    }

    @Override
    public float getQuadSize(float tickDelta) {
        return this.currentRadius;
    }

    @Override
    protected int getLightColor(float tint) {
        return 0xF000F0;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age < 3) {
            this.currentRadius = Mth.clampedLerp(0.0F, this.targetRadius, this.age / 2.0F);
        } else {
            this.currentRadius = Mth.clampedLerp(this.targetRadius, 0.0F, (this.age - 2) / 6.0F);
        }
        this.setAlpha(this.currentRadius / this.targetRadius);
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
            ExplosionParticle particle = new ExplosionParticle(level, x, y, z);
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}
