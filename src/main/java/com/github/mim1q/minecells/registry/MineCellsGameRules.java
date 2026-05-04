package com.github.mim1q.minecells.registry;

import net.minecraft.world.level.GameRules;

public final class MineCellsGameRules {
    public static final GameRules.Key<GameRules.BooleanValue> MOBS_DROP_CELLS = GameRules.register(
        "minecells.mobsDropCells",
        GameRules.Category.MOBS,
        GameRules.BooleanValue.create(false)
    );

    public static final GameRules.Key<GameRules.BooleanValue> SUFFOCATION_FIX = GameRules.register(
        "minecells.suffocationFix",
        GameRules.Category.MISC,
        GameRules.BooleanValue.create(true)
    );

    private MineCellsGameRules() {
    }

    public static void init() {
    }
}
