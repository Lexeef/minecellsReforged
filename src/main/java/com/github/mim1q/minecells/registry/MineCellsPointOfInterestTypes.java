package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;

import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;
import java.util.stream.Collectors;

public final class MineCellsPointOfInterestTypes {
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, MineCells.MOD_ID);

    public static final RegistryObject<PoiType> KINGDOM_PORTAL = POI_TYPES.register(
        "kingdom_portal",
        () -> new PoiType(getAllStates(MineCellsBlocks.KINGDOM_PORTAL_CORE.get()), 1, 1)
    );

    private MineCellsPointOfInterestTypes() {
    }

    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
    }

    private static Set<BlockState> getAllStates(Block block) {
        return block.getStateDefinition().getPossibleStates().stream().collect(Collectors.toSet());
    }
}
