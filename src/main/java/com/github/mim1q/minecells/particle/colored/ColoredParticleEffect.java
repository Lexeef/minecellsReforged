package com.github.mim1q.minecells.particle.colored;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public record ColoredParticleEffect(ParticleType<?> type, int color) implements ParticleOptions {
    public static final ParticleOptions.Deserializer<ColoredParticleEffect> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public ColoredParticleEffect fromCommand(ParticleType<ColoredParticleEffect> particleType, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            return new ColoredParticleEffect(particleType, reader.readInt());
        }

        @Override
        public ColoredParticleEffect fromNetwork(ParticleType<ColoredParticleEffect> particleType, FriendlyByteBuf buffer) {
            return new ColoredParticleEffect(particleType, buffer.readInt());
        }
    };

    public static Codec<ColoredParticleEffect> createCodec(ParticleType<ColoredParticleEffect> type) {
        return RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("color").forGetter(ColoredParticleEffect::color)
        ).apply(instance, color -> new ColoredParticleEffect(type, color)));
    }

    @Override
    public ParticleType<?> getType() {
        return this.type;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeInt(this.color);
    }

    @Override
    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(this.type) + " " + this.color;
    }
}
