package com.github.mim1q.minecells.particle;

import com.github.mim1q.minecells.particle.colored.ColoredParticle;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;

public class RisingBubbleParticle extends ColoredParticle {
    public RisingBubbleParticle(ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int color) {
        super(level, x, y, z, velocityX, velocityY, velocityZ, color);
        this.yd = velocityY;
        this.friction = 1.0F;
        this.lifetime = 60 + random.nextInt(40);
        setAlpha(0.2F + random.nextFloat() * 0.5F);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime || this.yd <= 0.008D) {
            remove();
            int count = random.nextInt(3) + 1;
            for (int i = 0; i < count; i++) {
                level.addParticle(
                    MineCellsParticles.SMALL_DROP.get().get(this.color),
                    x, y, z,
                    (random.nextDouble() - 0.5D) * 0.3D, 0.1D, (random.nextDouble() - 0.5D) * 0.3D
                );
            }
        } else {
            this.y += this.yd;
            this.yd -= 0.001D;
            setPos(x, y, z);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
