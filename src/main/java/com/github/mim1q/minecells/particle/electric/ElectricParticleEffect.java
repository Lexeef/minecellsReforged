package com.github.mim1q.minecells.particle.electric;

import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public record ElectricParticleEffect(
    Vec3 direction,
    int length,
    int color,
    float size,
    boolean isMainBranch
) implements ParticleOptions {
    public static final ParticleOptions.Deserializer<ElectricParticleEffect> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public ElectricParticleEffect fromCommand(ParticleType<ElectricParticleEffect> particleType, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            double x = reader.readDouble();
            double y = reader.readDouble();
            double z = reader.readDouble();
            int length = reader.readInt();
            int color = reader.readInt();
            float size = reader.readFloat();
            boolean isMainBranch = reader.readBoolean();
            return new ElectricParticleEffect(new Vec3(x, y, z), length, color, size, isMainBranch);
        }

        @Override
        public ElectricParticleEffect fromNetwork(ParticleType<ElectricParticleEffect> particleType, FriendlyByteBuf buffer) {
            double x = buffer.readDouble();
            double y = buffer.readDouble();
            double z = buffer.readDouble();
            int length = buffer.readInt();
            int color = buffer.readInt();
            float size = buffer.readFloat();
            boolean isMainBranch = buffer.readBoolean();
            return new ElectricParticleEffect(new Vec3(x, y, z), length, color, size, isMainBranch);
        }
    };

    public static final Codec<ElectricParticleEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Vec3.CODEC.fieldOf("direction").forGetter(ElectricParticleEffect::direction),
        Codec.INT.fieldOf("length").forGetter(ElectricParticleEffect::length),
        Codec.INT.fieldOf("color").forGetter(ElectricParticleEffect::color),
        Codec.FLOAT.fieldOf("size").forGetter(ElectricParticleEffect::size),
        Codec.BOOL.fieldOf("isMainBranch").forGetter(ElectricParticleEffect::isMainBranch)
    ).apply(instance, ElectricParticleEffect::new));

    @Override
    public ParticleType<?> getType() {
        return MineCellsParticles.ELECTRICITY.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeDouble(this.direction.x);
        buffer.writeDouble(this.direction.y);
        buffer.writeDouble(this.direction.z);
        buffer.writeInt(this.length);
        buffer.writeInt(this.color);
        buffer.writeFloat(this.size);
        buffer.writeBoolean(this.isMainBranch);
    }

    @Override
    public String writeToString() {
        return String.format(
            "Electric: direction %f %f %f, length: %d, color: 0x%08X, size: %f, isMainBranch: %b",
            this.direction.x,
            this.direction.y,
            this.direction.z,
            this.length,
            this.color,
            this.size,
            this.isMainBranch
        );
    }
}
