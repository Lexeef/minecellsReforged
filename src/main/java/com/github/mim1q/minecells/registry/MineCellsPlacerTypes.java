package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.world.feature.tree.PromenadeFoliagePlacer;
import com.github.mim1q.minecells.world.feature.tree.PromenadeShrubTrunkPlacer;
import com.github.mim1q.minecells.world.feature.tree.PromenadeTreeTrunkPlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class MineCellsPlacerTypes {
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACER_TYPES =
        DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, MineCells.MOD_ID);
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPES =
        DeferredRegister.create(Registries.FOLIAGE_PLACER_TYPE, MineCells.MOD_ID);

    public static final RegistryObject<TrunkPlacerType<PromenadeTreeTrunkPlacer>> PROMENADE_TRUNK =
        TRUNK_PLACER_TYPES.register("promenade_trunk", () -> new TrunkPlacerType<>(PromenadeTreeTrunkPlacer.CODEC));
    public static final RegistryObject<TrunkPlacerType<PromenadeShrubTrunkPlacer>> PROMENADE_SHRUB =
        TRUNK_PLACER_TYPES.register("promenade_shrub", () -> new TrunkPlacerType<>(PromenadeShrubTrunkPlacer.CODEC));
    public static final RegistryObject<FoliagePlacerType<PromenadeFoliagePlacer>> PROMENADE_FOLIAGE_PLACER =
        FOLIAGE_PLACER_TYPES.register("promenade_foliage_placer", () -> new FoliagePlacerType<>(PromenadeFoliagePlacer.CODEC));

    private MineCellsPlacerTypes() {
    }

    public static void register(IEventBus eventBus) {
        TRUNK_PLACER_TYPES.register(eventBus);
        FOLIAGE_PLACER_TYPES.register(eventBus);
    }
}
