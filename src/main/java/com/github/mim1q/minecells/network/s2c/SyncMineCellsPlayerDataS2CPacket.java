package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.client.MineCellsClientPacketHandlers;
import com.github.mim1q.minecells.world.state.PlayerSpecificMineCellsData;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncMineCellsPlayerDataS2CPacket {
    private final CompoundTag tag;

    public SyncMineCellsPlayerDataS2CPacket(PlayerSpecificMineCellsData data) {
        this(data.save());
    }

    public SyncMineCellsPlayerDataS2CPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public static void encode(SyncMineCellsPlayerDataS2CPacket packet, FriendlyByteBuf buffer) {
        buffer.writeNbt(packet.tag);
    }

    public static SyncMineCellsPlayerDataS2CPacket decode(FriendlyByteBuf buffer) {
        CompoundTag tag = buffer.readNbt();
        return new SyncMineCellsPlayerDataS2CPacket(tag == null ? new CompoundTag() : tag);
    }

    public static void handle(SyncMineCellsPlayerDataS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
            MineCellsClientPacketHandlers.handleSyncPlayerData(packet.tag)
        ));
        context.setPacketHandled(true);
    }

    public static void send(ServerPlayer player, PlayerSpecificMineCellsData data) {
        com.github.mim1q.minecells.network.MineCellsNetwork.sendToPlayer(player, new SyncMineCellsPlayerDataS2CPacket(data));
    }
}
