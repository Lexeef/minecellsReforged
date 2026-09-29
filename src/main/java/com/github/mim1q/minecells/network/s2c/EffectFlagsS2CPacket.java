package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.client.MineCellsClientPacketHandlers;

import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record EffectFlagsS2CPacket(int entityId, int flags) {
    public static void encode(EffectFlagsS2CPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.entityId);
        buf.writeVarInt(packet.flags);
    }

    public static EffectFlagsS2CPacket decode(FriendlyByteBuf buf) {
        return new EffectFlagsS2CPacket(buf.readVarInt(), buf.readVarInt());
    }

    public static void handle(EffectFlagsS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
            MineCellsClientPacketHandlers.handleEffectFlags(packet.entityId, packet.flags)
        ));
        context.setPacketHandled(true);
    }
}
