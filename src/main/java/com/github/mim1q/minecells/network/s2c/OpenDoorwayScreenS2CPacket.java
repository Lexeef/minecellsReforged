package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.client.MineCellsClientPacketHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record OpenDoorwayScreenS2CPacket(BlockPos doorwayPos, BlockPos anchor) {
    public static void encode(OpenDoorwayScreenS2CPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.doorwayPos);
        buf.writeBlockPos(packet.anchor);
    }

    public static OpenDoorwayScreenS2CPacket decode(FriendlyByteBuf buf) {
        return new OpenDoorwayScreenS2CPacket(buf.readBlockPos(), buf.readBlockPos());
    }

    public static void handle(OpenDoorwayScreenS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
            MineCellsClientPacketHandlers.handleOpenDoorwayScreen(packet.doorwayPos, packet.anchor)
        ));
        context.setPacketHandled(true);
    }
}
