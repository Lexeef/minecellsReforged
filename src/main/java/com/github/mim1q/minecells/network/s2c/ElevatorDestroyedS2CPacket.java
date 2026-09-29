package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.client.MineCellsClientPacketHandlers;

import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ElevatorDestroyedS2CPacket {
    private final double x;
    private final double y;
    private final double z;

    public ElevatorDestroyedS2CPacket(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void encode(ElevatorDestroyedS2CPacket packet, FriendlyByteBuf buf) {
        buf.writeDouble(packet.x);
        buf.writeDouble(packet.y);
        buf.writeDouble(packet.z);
    }

    public static ElevatorDestroyedS2CPacket decode(FriendlyByteBuf buf) {
        return new ElevatorDestroyedS2CPacket(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static void handle(ElevatorDestroyedS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
            MineCellsClientPacketHandlers.handleElevatorDestroyed(packet.x, packet.y, packet.z)
        ));
        context.setPacketHandled(true);
    }
}
