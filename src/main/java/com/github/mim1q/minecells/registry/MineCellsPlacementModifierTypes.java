package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.world.placement.ExcludeChunkMultiplesPlacementModifier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class MineCellsPlacementModifierTypes {
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPES =
        DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, MineCells.MOD_ID);

    public static final RegistryObject<PlacementModifierType<ExcludeChunkMultiplesPlacementModifier>> EXCLUDE_CHUNK_MULTIPLES =
        register("exclude_chunk_multiples", ExcludeChunkMultiplesPlacementModifier.CODEC);

    private MineCellsPlacementModifierTypes() {
    }

    public static void register(IEventBus eventBus) {
        PLACEMENT_MODIFIER_TYPES.register(eventBus);
    }

    private static <P extends PlacementModifier> RegistryObject<PlacementModifierType<P>> register(String id, com.mojang.serialization.Codec<P> codec) {
        return PLACEMENT_MODIFIER_TYPES.register(id, () -> () -> codec);
    }
}
