package com.github.mim1q.minecells.dimension;

import com.github.mim1q.minecells.world.state.MineCellsData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

public final class MineCellsDimensionGraph {
    private final Map<MineCellsDimension, Node> graph = new HashMap<>();

    public MineCellsDimensionGraph() {
        Node overworld = add(MineCellsDimension.OVERWORLD);
        Node prison = add(MineCellsDimension.PRISONERS_QUARTERS, overworld);
        Node promenade = add(MineCellsDimension.PROMENADE_OF_THE_CONDEMNED, prison);
        add(MineCellsDimension.INSUFFERABLE_CRYPT, prison);
        Node ramparts = add(MineCellsDimension.RAMPARTS, promenade);
        add(MineCellsDimension.BLACK_BRIDGE, ramparts);
    }

    private Node add(MineCellsDimension dimension, Node... upstream) {
        Node node = new Node(dimension, upstream);
        graph.put(dimension, node);
        return node;
    }

    public boolean canTraverseToOverworld(MineCellsDimension dimension, BiPredicate<MineCellsDimension, MineCellsDimension> edgePredicate) {
        Node node = graph.get(dimension);
        return node != null && node.canTraverseToOverworld(edgePredicate);
    }

    public void rescueIfStuck(ServerPlayer player) {
        if (!MineCellsDimension.isMineCellsDimension(player.serverLevel())) {
            return;
        }
        MineCellsDimension current = MineCellsDimension.of(player.serverLevel());
        if (current == null) {
            return;
        }
        MineCellsData.PlayerData data = MineCellsData.getPlayerData(player, player.serverLevel(), player.blockPosition());
        boolean canEscape = canTraverseToOverworld(current, (from, to) -> data.getPortalData(from.id(), to.id()).isPresent());
        if (canEscape) {
            return;
        }

        player.sendSystemMessage(Component.translatable("chat.minecells.stuck_message"));
        net.minecraft.server.level.ServerLevel savedSpawnLevel = player.getServer().getLevel(player.getRespawnDimension());
        if (savedSpawnLevel != null && player.getRespawnPosition() != null) {
            player.teleportTo(savedSpawnLevel,
                player.getRespawnPosition().getX() + 0.5D,
                player.getRespawnPosition().getY(),
                player.getRespawnPosition().getZ() + 0.5D,
                player.getYRot(),
                player.getXRot());
            return;
        }

        net.minecraft.server.level.ServerLevel overworld = player.getServer().overworld();
        player.teleportTo(overworld,
            overworld.getSharedSpawnPos().getX() + 0.5D,
            overworld.getSharedSpawnPos().getY(),
            overworld.getSharedSpawnPos().getZ() + 0.5D,
            player.getYRot(),
            player.getXRot());
    }

    private record Node(MineCellsDimension dimension, Node... upstream) {
        private boolean canTraverseToOverworld(BiPredicate<MineCellsDimension, MineCellsDimension> edgePredicate) {
            if (dimension == MineCellsDimension.OVERWORLD) {
                return true;
            }
            for (Node next : upstream) {
                if (edgePredicate.test(dimension, next.dimension) && next.canTraverseToOverworld(edgePredicate)) {
                    return true;
                }
            }
            return false;
        }
    }
}
