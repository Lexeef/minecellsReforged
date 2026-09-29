package com.github.mim1q.minecells.network.c2s;

import com.github.mim1q.minecells.world.state.MineCellsData;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestSyncMineCellsPlayerDataC2SPacket {
    public static void encode(RequestSyncMineCellsPlayerDataC2SPacket packet, FriendlyByteBuf buffer) {
    }

    public static RequestSyncMineCellsPlayerDataC2SPacket decode(FriendlyByteBuf buffer) {
        return new RequestSyncMineCellsPlayerDataC2SPacket();
    }

    public static void handle(RequestSyncMineCellsPlayerDataC2SPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                MineCellsData.syncCurrentPlayerData(player, player.serverLevel());
            }
        });
        context.setPacketHandled(true);
    }
}
