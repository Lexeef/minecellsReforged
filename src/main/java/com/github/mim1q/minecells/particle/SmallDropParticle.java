package com.github.mim1q.minecells.particle;

import com.github.mim1q.minecells.particle.colored.ColoredParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;

public class SmallDropParticle extends ColoredParticle {
    public SmallDropParticle(ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int color) {
        super(level, x, y, z, velocityX, velocityY, velocityZ, color);
        this.gravity = 0.7f;
    }

    @Override
    public void tick() {
        super.tick();
        if (onGround) {
            remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
