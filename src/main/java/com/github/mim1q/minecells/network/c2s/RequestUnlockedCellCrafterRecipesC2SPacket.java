package com.github.mim1q.minecells.network.c2s;

import com.github.mim1q.minecells.network.s2c.SendUnlockedCellCrafterRecipesS2CPacket;

import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestUnlockedCellCrafterRecipesC2SPacket {
    public static void encode(RequestUnlockedCellCrafterRecipesC2SPacket packet, net.minecraft.network.FriendlyByteBuf buffer) {
    }

    public static RequestUnlockedCellCrafterRecipesC2SPacket decode(net.minecraft.network.FriendlyByteBuf buffer) {
        return new RequestUnlockedCellCrafterRecipesC2SPacket();
    }

    public static void handle(RequestUnlockedCellCrafterRecipesC2SPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                SendUnlockedCellCrafterRecipesS2CPacket.send(player);
            }
        });
        context.setPacketHandled(true);
    }
}
