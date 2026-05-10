package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.world.densityfunction.CliffDensityFunction;
import com.github.mim1q.minecells.world.densityfunction.RingDensityFunction;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class MineCellsDensityFunctionTypes {
    public static final DeferredRegister<Codec<? extends DensityFunction>> DENSITY_FUNCTION_TYPES =
        DeferredRegister.create(Registries.DENSITY_FUNCTION_TYPE, MineCells.MOD_ID);

    public static final RegistryObject<Codec<? extends DensityFunction>> RING =
        DENSITY_FUNCTION_TYPES.register("ring", () -> RingDensityFunction.CODEC.codec());
    public static final RegistryObject<Codec<? extends DensityFunction>> CLIFF =
        DENSITY_FUNCTION_TYPES.register("cliff", () -> CliffDensityFunction.CODEC.codec());

    private MineCellsDensityFunctionTypes() {
    }

    public static void register(IEventBus eventBus) {
        DENSITY_FUNCTION_TYPES.register(eventBus);
    }
}
