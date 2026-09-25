package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.client.MineCellsClientPacketHandlers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ObeliskActivationS2CPacket {
    private final int entityId;

    public ObeliskActivationS2CPacket(int entityId) {
        this.entityId = entityId;
    }

    public static void encode(ObeliskActivationS2CPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.entityId);
    }

    public static ObeliskActivationS2CPacket decode(FriendlyByteBuf buf) {
        return new ObeliskActivationS2CPacket(buf.readVarInt());
    }

    public static void handle(ObeliskActivationS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
            MineCellsClientPacketHandlers.handleObeliskActivation(packet.entityId)
        ));
        context.setPacketHandled(true);
    }
}
