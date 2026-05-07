package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.client.MineCellsClientData;
import com.github.mim1q.minecells.world.state.PlayerSpecificMineCellsData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

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
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null) {
                MineCellsClientData.setPlayerData(new PlayerSpecificMineCellsData(packet.tag));
            }
        });
        context.setPacketHandled(true);
    }

    public static void send(ServerPlayer player, PlayerSpecificMineCellsData data) {
        com.github.mim1q.minecells.network.MineCellsNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncMineCellsPlayerDataS2CPacket(data));
    }
}
