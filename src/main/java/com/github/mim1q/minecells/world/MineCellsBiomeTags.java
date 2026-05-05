package com.github.mim1q.minecells.world;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public final class MineCellsBiomeTags {
    public static final TagKey<Biome> VALID_PORTAL_BIOMES = TagKey.create(Registries.BIOME, MineCells.id("valid_portal_biomes"));

    private MineCellsBiomeTags() {
    }
}
