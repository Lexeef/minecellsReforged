package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.config.MineCellsConfig;
import com.github.mim1q.minecells.config.MineCellsSyncedConfig;
import com.github.mim1q.minecells.network.MineCellsNetwork;
import com.github.mim1q.minecells.network.s2c.SyncCommonConfigS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MineCellsConfigSyncEvents {
    private MineCellsConfigSyncEvents() {
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            MineCellsNetwork.sendToPlayer(player, new SyncCommonConfigS2CPacket(MineCellsSyncedConfig.captureLocal())
            );
        }
    }

    @Mod.EventBusSubscriber(modid = MineCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ModBus {
        private ModBus() {
        }

        @SubscribeEvent
        public static void onConfigReloading(ModConfigEvent.Reloading event) {
            if (event.getConfig().getSpec() != MineCellsConfig.COMMON_SPEC) {
                return;
            }
            var server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                server.execute(() -> MineCellsNetwork.CHANNEL.send(
                    PacketDistributor.ALL.noArg(),
                    new SyncCommonConfigS2CPacket(MineCellsSyncedConfig.captureLocal())
                ));
            }
        }
    }
}
