package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.MineCells;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class MineCellsItemTags {
    public static final TagKey<Item> DISCARD_IN_HIGH_DIMENSIONS = of("discard_in_high_dimensions");
    public static final TagKey<Item> BOWS_ACCEPTING_POWER = of("bows/accepting_power");
    public static final TagKey<Item> BOWS_ACCEPTING_PUNCH = of("bows/accepting_punch");
    public static final TagKey<Item> BOWS_ACCEPTING_INFINITY = of("bows/accepting_infinity");
    public static final TagKey<Item> BOWS_ACCEPTING_FLAME = of("bows/accepting_flame");
    public static final TagKey<Item> BOWS_ACCEPTING_QUICK_CHARGE = of("bows/accepting_quick_charge");

    private MineCellsItemTags() {
    }

    private static TagKey<Item> of(String id) {
        return TagKey.create(Registries.ITEM, MineCells.id(id));
    }
}
