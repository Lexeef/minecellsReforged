package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.client.MineCellsClientPacketHandlers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class AdvancementHintsS2CPacket {
    private final Map<ResourceLocation, Boolean> completed;
    private final boolean replace;

    public AdvancementHintsS2CPacket(Map<ResourceLocation, Boolean> completed, boolean replace) {
        this.completed = completed;
        this.replace = replace;
    }

    public static void encode(AdvancementHintsS2CPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.replace);
        buf.writeMap(packet.completed, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeBoolean);
    }

    public static AdvancementHintsS2CPacket decode(FriendlyByteBuf buf) {
        boolean replace = buf.readBoolean();
        Map<ResourceLocation, Boolean> completed = buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readBoolean);
        return new AdvancementHintsS2CPacket(completed, replace);
    }

    public static void handle(AdvancementHintsS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
            MineCellsClientPacketHandlers.handleAdvancementHints(packet.completed, packet.replace)
        ));
        context.setPacketHandled(true);
    }
}
