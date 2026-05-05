package com.github.mim1q.minecells.particle.colored;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public abstract class ColoredParticle extends TextureSheetParticle {
    protected final int color;

    public ColoredParticle(ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int color) {
        super(level, x, y, z, velocityX, velocityY, velocityZ);
        this.color = color;
        int r = color >> 16;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        this.rCol = r / 255.0F;
        this.gCol = g / 255.0F;
        this.bCol = b / 255.0F;
    }

    @FunctionalInterface
    public interface ColoredParticleConstructor {
        ColoredParticle create(ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int color);
    }

    public static class Factory implements ParticleProvider<ColoredParticleEffect> {
        private final SpriteSet spriteSet;
        private final ColoredParticleConstructor constructor;

        public Factory(SpriteSet spriteSet, ColoredParticleConstructor constructor) {
            this.spriteSet = spriteSet;
            this.constructor = constructor;
        }

        @Override
        public Particle createParticle(ColoredParticleEffect parameters, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            ColoredParticle particle = this.constructor.create(level, x, y, z, velocityX, velocityY, velocityZ, parameters.color());
            particle.setSpriteFromAge(this.spriteSet);
            return particle;
        }
    }
}
