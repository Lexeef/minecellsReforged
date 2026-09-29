package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.MineCells;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class MineCellsBlockTags {
    public static final TagKey<Block> CONJUNCTIVIUS_BREAKABLE = of("conjunctivius_breakable");
    public static final TagKey<Block> ELEVATOR_CHAINS = of("elevator_chains");
    public static final TagKey<Block> PROTECTED = of("protected");
    public static final TagKey<Block> TREE_ROOT_REPLACEABLE = of("tree_root_replaceable");
    public static final TagKey<Block> RETURN_STONE_TARGETS = of("return_stone_targets");

    private MineCellsBlockTags() {
    }

    private static TagKey<Block> of(String id) {
        return TagKey.create(Registries.BLOCK, MineCells.id(id));
    }
}
