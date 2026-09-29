package com.github.mim1q.minecells.particle.colored;

import com.mojang.serialization.Codec;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public class ColoredParticleType extends ParticleType<ColoredParticleEffect> {
    public ColoredParticleType() {
        super(true, ColoredParticleEffect.DESERIALIZER);
    }

    @Override
    public Codec<ColoredParticleEffect> codec() {
        return ColoredParticleEffect.createCodec(this);
    }

    public ParticleOptions get(int color) {
        return new ColoredParticleEffect(this, color);
    }
}
