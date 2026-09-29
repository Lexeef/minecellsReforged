package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.loot.SpecialWeaponLootEntry;
import com.github.mim1q.minecells.MineCells;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class MineCellsLootPoolEntryTypes {
    public static final DeferredRegister<LootPoolEntryType> LOOT_POOL_ENTRY_TYPES =
        DeferredRegister.create(Registries.LOOT_POOL_ENTRY_TYPE, MineCells.MOD_ID);

    public static final RegistryObject<LootPoolEntryType> SPECIAL_WEAPON =
        LOOT_POOL_ENTRY_TYPES.register("special_weapon", () -> new LootPoolEntryType(new SpecialWeaponLootEntry.Serializer()));

    private MineCellsLootPoolEntryTypes() {
    }

    public static void register(IEventBus eventBus) {
        LOOT_POOL_ENTRY_TYPES.register(eventBus);
    }
}
