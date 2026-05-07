package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.registry.MineCellsParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpawnRuneParticlesS2CPacket {
    private final AABB box;

    public SpawnRuneParticlesS2CPacket(AABB box) {
        this.box = box;
    }

    public static void encode(SpawnRuneParticlesS2CPacket packet, FriendlyByteBuf buf) {
        buf.writeDouble(packet.box.minX);
        buf.writeDouble(packet.box.minY);
        buf.writeDouble(packet.box.minZ);
        buf.writeDouble(packet.box.maxX);
        buf.writeDouble(packet.box.maxY);
        buf.writeDouble(packet.box.maxZ);
    }

    public static SpawnRuneParticlesS2CPacket decode(FriendlyByteBuf buf) {
        return new SpawnRuneParticlesS2CPacket(new AABB(
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble()
        ));
    }

    public static void handle(SpawnRuneParticlesS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) {
                return;
            }

            for (int i = 0; i < 10; i++) {
                double x = lerp(level.random.nextDouble(), packet.box.minX, packet.box.maxX);
                double y = lerp(level.random.nextDouble(), packet.box.minY, packet.box.maxY);
                double z = lerp(level.random.nextDouble(), packet.box.minZ, packet.box.maxZ);
                Vec3 velocity = new Vec3(-0.1D, -0.1D, -0.1D).scale(level.random.nextDouble() * 0.5D + 0.5D);
                level.addParticle(MineCellsParticles.SPECKLE.get().get(0xFF6A00), x, y, z, velocity.x, velocity.y, velocity.z);
            }

            AABB innerBox = packet.box.inflate(-0.1D, -0.1D, -0.1D);
            for (int i = 0; i < 10; i++) {
                double x = lerp(level.random.nextDouble(), innerBox.minX, innerBox.maxX);
                double y = lerp(level.random.nextDouble(), innerBox.minY, innerBox.maxY);
                double z = lerp(level.random.nextDouble(), innerBox.minZ, innerBox.maxZ);
                Vec3 velocity = new Vec3(-0.02D, -0.02D, -0.02D).scale(level.random.nextDouble() * 0.5D + 0.5D);
                level.addParticle(ParticleTypes.CLOUD, x, y, z, velocity.x, velocity.y, velocity.z);
            }
        });
        context.setPacketHandled(true);
    }

    private static double lerp(double delta, double min, double max) {
        return min + delta * (max - min);
    }
}
