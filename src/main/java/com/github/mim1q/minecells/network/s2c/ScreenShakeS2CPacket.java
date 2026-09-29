package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.client.ScreenShakeClientEffects;

import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ScreenShakeS2CPacket {
    private final float intensity;
    private final int durationTicks;
    private final String modifier;

    public ScreenShakeS2CPacket(float intensity, int durationTicks) {
        this(intensity, durationTicks, "");
    }

    public ScreenShakeS2CPacket(float intensity, int durationTicks, String modifier) {
        this.intensity = intensity;
        this.durationTicks = durationTicks;
        this.modifier = modifier == null ? "" : modifier;
    }

    public static void encode(ScreenShakeS2CPacket packet, FriendlyByteBuf buf) {
        buf.writeFloat(packet.intensity);
        buf.writeVarInt(packet.durationTicks);
        buf.writeUtf(packet.modifier, 64);
    }

    public static ScreenShakeS2CPacket decode(FriendlyByteBuf buf) {
        return new ScreenShakeS2CPacket(buf.readFloat(), buf.readVarInt(), buf.readUtf(64));
    }

    public static void handle(ScreenShakeS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
            ScreenShakeClientEffects.apply(packet.intensity, packet.durationTicks, packet.modifier)
        ));
        context.setPacketHandled(true);
    }
}
