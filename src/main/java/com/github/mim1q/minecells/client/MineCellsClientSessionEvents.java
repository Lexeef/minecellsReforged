package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.config.MineCellsSyncedConfig;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.network.c2s.RequestSyncMineCellsPlayerDataC2SPacket;
import com.github.mim1q.minecells.network.MineCellsNetwork;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class MineCellsClientSessionEvents {
    private MineCellsClientSessionEvents() {
    }

    @SubscribeEvent
    public static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        MineCellsClientData.resetSessionData();
        MineCellsNetwork.CHANNEL.sendToServer(new RequestSyncMineCellsPlayerDataC2SPacket());
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        MineCellsClientData.resetSessionData();
        MineCellsSyncedConfig.clearServerValues();
    }
}
