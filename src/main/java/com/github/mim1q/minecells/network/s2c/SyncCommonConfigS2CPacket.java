package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.config.MineCellsSyncedConfig;

import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.function.Supplier;

public class SyncCommonConfigS2CPacket {
    private final MineCellsSyncedConfig.Values values;

    public SyncCommonConfigS2CPacket(MineCellsSyncedConfig.Values values) {
        this.values = values;
    }

    public static void encode(SyncCommonConfigS2CPacket packet, FriendlyByteBuf buf) {
        packet.values.write(buf);
    }

    public static SyncCommonConfigS2CPacket decode(FriendlyByteBuf buf) {
        return new SyncCommonConfigS2CPacket(MineCellsSyncedConfig.Values.read(buf));
    }

    public static void handle(SyncCommonConfigS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (ServerLifecycleHooks.getCurrentServer() == null) {
                MineCellsSyncedConfig.applyServerValues(packet.values);
            }
        });
        context.setPacketHandled(true);
    }
}
