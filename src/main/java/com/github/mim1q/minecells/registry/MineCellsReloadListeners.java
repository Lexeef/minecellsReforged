package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.data.SpawnerRuneReloadListener;
import net.minecraftforge.event.AddReloadListenerEvent;

public final class MineCellsReloadListeners {
    private static final SpawnerRuneReloadListener SPAWNER_RUNES = new SpawnerRuneReloadListener();

    private MineCellsReloadListeners() {
    }

    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(SPAWNER_RUNES);
    }

    public static SpawnerRuneReloadListener spawnerRunes() {
        return SPAWNER_RUNES;
    }
}
