package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.dimension.MineCellsDimensionGraph;
import com.github.mim1q.minecells.world.state.MineCellsData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = com.github.mim1q.minecells.MineCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MineCellsPlayerDataSync {
    private static final MineCellsDimensionGraph DIMENSION_GRAPH = new MineCellsDimensionGraph();

    private MineCellsPlayerDataSync() {
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            MineCellsData.syncCurrentPlayerData(player, player.serverLevel());
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            MineCellsData.syncCurrentPlayerData(player, player.serverLevel());
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            MineCellsData.syncCurrentPlayerData(player, player.serverLevel());
            DIMENSION_GRAPH.rescueIfStuck(player);
        }
    }
}
