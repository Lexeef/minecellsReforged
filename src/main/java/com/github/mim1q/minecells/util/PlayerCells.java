package com.github.mim1q.minecells.util;

import net.minecraft.world.entity.player.Player;

/**
 * Legacy Fabric cell counter ({@code PlayerEntityAccessor#getCells}), stored in the player's Forge persistent data.
 * Not copied on respawn, matching Fabric where the counter lived on the player entity.
 */
public final class PlayerCells {
    private static final String KEY = "minecells:cells";

    private PlayerCells() {
    }

    public static int get(Player player) {
        return player.getPersistentData().getInt(KEY);
    }

    public static void set(Player player, int amount) {
        player.getPersistentData().putInt(KEY, Math.max(0, amount));
    }
}
