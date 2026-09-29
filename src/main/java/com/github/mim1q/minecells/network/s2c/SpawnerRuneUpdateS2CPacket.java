package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.client.MineCellsClientPacketHandlers;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpawnerRuneUpdateS2CPacket {
    private final BlockPos pos;
    private final long lastActivationTime;
    private final float cooldown;

    public SpawnerRuneUpdateS2CPacket(BlockPos pos, long lastActivationTime, float cooldown) {
        this.pos = pos;
        this.lastActivationTime = lastActivationTime;
        this.cooldown = cooldown;
    }

    public static void encode(SpawnerRuneUpdateS2CPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos);
        buf.writeLong(packet.lastActivationTime);
        buf.writeFloat(packet.cooldown);
    }

    public static SpawnerRuneUpdateS2CPacket decode(FriendlyByteBuf buf) {
        return new SpawnerRuneUpdateS2CPacket(buf.readBlockPos(), buf.readLong(), buf.readFloat());
    }

    public static void handle(SpawnerRuneUpdateS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
            MineCellsClientPacketHandlers.handleSpawnerRuneUpdate(packet.pos, packet.lastActivationTime, packet.cooldown)
        ));
        context.setPacketHandled(true);
    }
}
