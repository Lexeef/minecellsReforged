package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.client.MineCellsClientPacketHandlers;

import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ExplosionS2CPacket {
    private final double x;
    private final double y;
    private final double z;
    private final double radius;

    public ExplosionS2CPacket(double x, double y, double z, double radius) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
    }

    public static void encode(ExplosionS2CPacket packet, FriendlyByteBuf buf) {
        buf.writeDouble(packet.x);
        buf.writeDouble(packet.y);
        buf.writeDouble(packet.z);
        buf.writeDouble(packet.radius);
    }

    public static ExplosionS2CPacket decode(FriendlyByteBuf buf) {
        return new ExplosionS2CPacket(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static void handle(ExplosionS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
            MineCellsClientPacketHandlers.handleExplosion(packet.x, packet.y, packet.z, packet.radius)
        ));
        context.setPacketHandled(true);
    }
}
