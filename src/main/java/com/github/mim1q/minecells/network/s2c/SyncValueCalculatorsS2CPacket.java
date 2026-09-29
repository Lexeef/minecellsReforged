package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.valuecalculators.ValueCalculators;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.function.Supplier;
import java.util.Map;

public class SyncValueCalculatorsS2CPacket {
    private static final int MAX_JSON_LENGTH = 262144;

    private final Map<ResourceLocation, String> calculators;

    public SyncValueCalculatorsS2CPacket(Map<ResourceLocation, String> calculators) {
        this.calculators = calculators;
    }

    public static void encode(SyncValueCalculatorsS2CPacket packet, FriendlyByteBuf buf) {
        buf.writeMap(packet.calculators, FriendlyByteBuf::writeResourceLocation, (b, json) -> b.writeUtf(json, MAX_JSON_LENGTH));
    }

    public static SyncValueCalculatorsS2CPacket decode(FriendlyByteBuf buf) {
        return new SyncValueCalculatorsS2CPacket(buf.readMap(FriendlyByteBuf::readResourceLocation, b -> b.readUtf(MAX_JSON_LENGTH)));
    }

    public static void handle(SyncValueCalculatorsS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (ServerLifecycleHooks.getCurrentServer() == null) {
                ValueCalculators.applySynced(packet.calculators);
            }
        });
        context.setPacketHandled(true);
    }
}
