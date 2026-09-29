package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.client.gui.ConjunctiviusBossBarClientState;

import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;
import java.util.UUID;

public class UpdateConjunctiviusBossBarS2CPacket {
    private final UUID bossBarId;
    private final int tentacleCount;
    private final int maxTentacleCount;

    public UpdateConjunctiviusBossBarS2CPacket(UUID bossBarId, int tentacleCount, int maxTentacleCount) {
        this.bossBarId = bossBarId;
        this.tentacleCount = tentacleCount;
        this.maxTentacleCount = maxTentacleCount;
    }

    public static void encode(UpdateConjunctiviusBossBarS2CPacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.bossBarId);
        buf.writeShort(packet.tentacleCount);
        buf.writeShort(packet.maxTentacleCount);
    }

    public static UpdateConjunctiviusBossBarS2CPacket decode(FriendlyByteBuf buf) {
        return new UpdateConjunctiviusBossBarS2CPacket(buf.readUUID(), buf.readShort(), buf.readShort());
    }

    public static void handle(UpdateConjunctiviusBossBarS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
            ConjunctiviusBossBarClientState.setTentacleCount(packet.bossBarId, packet.tentacleCount, packet.maxTentacleCount)
        ));
        context.setPacketHandled(true);
    }
}
