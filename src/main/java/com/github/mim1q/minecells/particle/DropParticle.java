package com.github.mim1q.minecells.particle;

import com.github.mim1q.minecells.particle.colored.ColoredParticle;
import com.github.mim1q.minecells.registry.MineCellsParticles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;

public class DropParticle extends ColoredParticle {
    public DropParticle(ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int color) {
        super(level, x, y, z, velocityX, velocityY, velocityZ, color);
        this.gravity = 0.8f;
        this.hasPhysics = true;
        this.lifetime = 40;
    }

    @Override
    public void tick() {
        super.tick();
        if (onGround) {
            for (int i = 0; i < 4; i++) {
                level.addParticle(
                    MineCellsParticles.SMALL_DROP.get().get(this.color),
                    x,
                    y,
                    z,
                    (random.nextDouble() - 0.5) * 0.3,
                    0.1D,
                    (random.nextDouble() - 0.5) * 0.3
                );
                remove();
            }
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
}
