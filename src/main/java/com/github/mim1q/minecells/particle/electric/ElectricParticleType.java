package com.github.mim1q.minecells.particle.electric;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.phys.Vec3;

public class ElectricParticleType extends ParticleType<ElectricParticleEffect> {
    public ElectricParticleType() {
        super(true, ElectricParticleEffect.DESERIALIZER);
    }

    @Override
    public Codec<ElectricParticleEffect> codec() {
        return ElectricParticleEffect.CODEC;
    }

    public ParticleOptions get(Vec3 direction, int length, int color, float size) {
        return new ElectricParticleEffect(direction, length, color, size, true);
    }
}
