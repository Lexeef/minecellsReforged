package com.github.mim1q.minecells.particle;

import com.github.mim1q.minecells.particle.colored.ColoredParticle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;

public class SpeckleParticle extends ColoredParticle {
    public SpeckleParticle(ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int color) {
        super(level, x, y, z, velocityX, velocityY, velocityZ, color);
        this.xd = velocityX;
        this.yd = velocityY;
        this.zd = velocityZ;
        this.quadSize = 0.15F + level.getRandom().nextFloat() * 0.2F;
        this.lifetime = 30;
    }

    @Override
    public void tick() {
        super.tick();
        float progress = this.age / (float) this.lifetime;
        this.alpha = 1.0F - progress;
        this.friction = 1.0F - progress * 0.2F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
