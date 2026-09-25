package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.network.MineCellsNetwork;
import com.github.mim1q.minecells.network.s2c.AdvancementHintsS2CPacket;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Replaces Fabric's {@code ClientAdvancementManagerMixin}: tells clients which hint-marker
 * advancements are completed so block-entity hint markers can hide.
 */
@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MineCellsAdvancementHintEvents {
    public static final List<ResourceLocation> HINT_ADVANCEMENTS = List.of(
        MineCells.id("cell_crafter"),
        MineCells.id("vine_rune"),
        MineCells.id("elite")
    );
    private static final Set<UUID> PENDING = ConcurrentHashMap.newKeySet();

    private MineCellsAdvancementHintEvents() {
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            send(player, true);
        }
    }

    @SubscribeEvent
    public static void onProgress(AdvancementEvent.AdvancementProgressEvent event) {
        markIfTracked(event.getEntity().getUUID(), event.getAdvancement());
    }

    @SubscribeEvent
    public static void onEarn(AdvancementEvent.AdvancementEarnEvent event) {
        markIfTracked(event.getEntity().getUUID(), event.getAdvancement());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || PENDING.isEmpty()) {
            return;
        }
        MinecraftServer server = event.getServer();
        for (UUID id : Set.copyOf(PENDING)) {
            PENDING.remove(id);
            ServerPlayer player = server.getPlayerList().getPlayer(id);
            if (player != null) {
                send(player, false);
            }
        }
    }

    private static void markIfTracked(UUID playerId, Advancement advancement) {
        if (HINT_ADVANCEMENTS.contains(advancement.getId())) {
            PENDING.add(playerId);
        }
    }

    private static void send(ServerPlayer player, boolean replace) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }
        Map<ResourceLocation, Boolean> completed = new HashMap<>();
        for (ResourceLocation id : HINT_ADVANCEMENTS) {
            Advancement advancement = server.getAdvancements().getAdvancement(id);
            if (advancement != null) {
                completed.put(id, player.getAdvancements().getOrStartProgress(advancement).isDone());
            }
        }
        MineCellsNetwork.sendToPlayer(player, new AdvancementHintsS2CPacket(completed, replace));
    }
}
