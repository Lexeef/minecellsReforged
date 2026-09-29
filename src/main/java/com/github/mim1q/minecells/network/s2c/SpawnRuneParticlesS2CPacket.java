package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.client.MineCellsClientPacketHandlers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.AABB;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
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
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
            MineCellsClientPacketHandlers.handleSpawnRuneParticles(packet.box)
        ));
        context.setPacketHandled(true);
    }
}
