package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.world.placement.BetterRandomSpreadPlacement;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class MineCellsStructurePlacementTypes {
    public static final DeferredRegister<StructurePlacementType<?>> STRUCTURE_PLACEMENT_TYPES = DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, MineCells.MOD_ID);

    public static final RegistryObject<StructurePlacementType<BetterRandomSpreadPlacement>> BETTER_RANDOM_SPREAD =
        register("better_random_spread", BetterRandomSpreadPlacement.CODEC);

    private MineCellsStructurePlacementTypes() {
    }

    public static void register(IEventBus eventBus) {
        STRUCTURE_PLACEMENT_TYPES.register(eventBus);
    }

    private static <SP extends StructurePlacement> RegistryObject<StructurePlacementType<SP>> register(String id, com.mojang.serialization.Codec<SP> codec) {
        return STRUCTURE_PLACEMENT_TYPES.register(id, () -> () -> codec);
    }
}
