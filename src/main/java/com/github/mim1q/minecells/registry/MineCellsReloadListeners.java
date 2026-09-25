package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.data.SpawnerRuneReloadListener;
import com.github.mim1q.minecells.valuecalculators.ValueCalculators;
import net.minecraftforge.event.AddReloadListenerEvent;

public final class MineCellsReloadListeners {
    private static final SpawnerRuneReloadListener SPAWNER_RUNES = new SpawnerRuneReloadListener();
    private static final ValueCalculators.ReloadListener VALUE_CALCULATORS = new ValueCalculators.ReloadListener();

    private MineCellsReloadListeners() {
    }

    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(SPAWNER_RUNES);
        event.addListener(VALUE_CALCULATORS);
    }

    public static SpawnerRuneReloadListener spawnerRunes() {
        return SPAWNER_RUNES;
    }
}
