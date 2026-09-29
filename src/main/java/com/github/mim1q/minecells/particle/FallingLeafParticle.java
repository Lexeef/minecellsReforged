package com.github.mim1q.minecells.particle;

import com.github.mim1q.minecells.particle.colored.ColoredParticle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;

public class FallingLeafParticle extends ColoredParticle {
    private final float rotationSpeed;

    public FallingLeafParticle(ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int color) {
        super(level, x, y + 5, z, velocityX, velocityY, velocityZ, color);
        this.rotationSpeed = level.random.nextFloat() * 0.4F - 0.2F;
        setAlpha(0f);
        if (y < 60 || y > 130) {
            remove();
            this.lifetime = 0;
            return;
        }
        this.xd = 0.05F + 0.1F * level.random.nextFloat();
        this.yd = -0.15F - 0.1F * level.random.nextFloat();
        this.zd = 0.05F + 0.1F * level.random.nextFloat();
        this.quadSize = 0.25F + level.random.nextFloat() * 0.25F;
        this.lifetime = 50 + level.random.nextInt(100);
        this.hasPhysics = true;
    }

    @Override
    public void setSpriteFromAge(SpriteSet spriteSet) {
        this.setSprite(spriteSet.get(level.random));
    }

    @Override
    public float getQuadSize(float tickDelta) {
        if (age <= 10) {
            setAlpha(age / 10f);
        }
        int left = lifetime - age;
        if (left <= 10) {
            setAlpha(left / 10f);
        }
        oRoll = roll;
        if (!onGround) {
            roll = (age + tickDelta) * rotationSpeed;
        }
        return super.getQuadSize(tickDelta);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
