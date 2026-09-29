package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.block.fluid.SewageFluid;
import com.github.mim1q.minecells.MineCells;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public final class MineCellsFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, MineCells.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MineCells.MOD_ID);

    public static final RegistryObject<FluidType> SEWAGE_TYPE = FLUID_TYPES.register("sewage", () -> new SewageFluidType(MineCells.id("block/fluid/toxic_sewage"), MineCells.id("block/fluid/toxic_sewage_flowing"), FluidType.Properties.create()
        .canSwim(true)
        .canDrown(true)
        .canConvertToSource(false)
        .rarity(Rarity.COMMON)));
    public static final RegistryObject<FluidType> ANCIENT_SEWAGE_TYPE = FLUID_TYPES.register("ancient_sewage", () -> new SewageFluidType(MineCells.id("block/fluid/ancient_sewage"), MineCells.id("block/fluid/ancient_sewage_flowing"), FluidType.Properties.create()
        .canSwim(true)
        .canDrown(true)
        .canConvertToSource(false)
        .rarity(Rarity.COMMON)));

    public static final RegistryObject<FlowingFluid> STILL_SEWAGE = FLUIDS.register("sewage", () -> new SewageFluid.Source(sewageProperties(), SewageFluid.SEWAGE_BUBBLE_COLOR));
    public static final RegistryObject<FlowingFluid> FLOWING_SEWAGE = FLUIDS.register("flowing_sewage", () -> new SewageFluid.Flowing(sewageProperties(), SewageFluid.SEWAGE_BUBBLE_COLOR));
    public static final RegistryObject<FlowingFluid> STILL_ANCIENT_SEWAGE = FLUIDS.register("ancient_sewage", () -> new SewageFluid.Source(ancientSewageProperties(), SewageFluid.ANCIENT_SEWAGE_BUBBLE_COLOR));
    public static final RegistryObject<FlowingFluid> FLOWING_ANCIENT_SEWAGE = FLUIDS.register("flowing_ancient_sewage", () -> new SewageFluid.Flowing(ancientSewageProperties(), SewageFluid.ANCIENT_SEWAGE_BUBBLE_COLOR));

    private MineCellsFluids() {
    }

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }

    private static ForgeFlowingFluid.Properties sewageProperties() {
        return new ForgeFlowingFluid.Properties(SEWAGE_TYPE, STILL_SEWAGE, FLOWING_SEWAGE)
            .bucket(MineCellsItems.SEWAGE_BUCKET)
            .block(MineCellsBlocks.SEWAGE)
            .slopeFindDistance(4)
            .levelDecreasePerBlock(1)
            .tickRate(5)
            .explosionResistance(100.0F);
    }

    private static ForgeFlowingFluid.Properties ancientSewageProperties() {
        return new ForgeFlowingFluid.Properties(ANCIENT_SEWAGE_TYPE, STILL_ANCIENT_SEWAGE, FLOWING_ANCIENT_SEWAGE)
            .bucket(MineCellsItems.ANCIENT_SEWAGE_BUCKET)
            .block(MineCellsBlocks.ANCIENT_SEWAGE)
            .slopeFindDistance(4)
            .levelDecreasePerBlock(1)
            .tickRate(5)
            .explosionResistance(100.0F);
    }

    private static class SewageFluidType extends FluidType {
        private final ResourceLocation stillTexture;
        private final ResourceLocation flowingTexture;

        private SewageFluidType(ResourceLocation stillTexture, ResourceLocation flowingTexture, Properties properties) {
            super(properties);
            this.stillTexture = stillTexture;
            this.flowingTexture = flowingTexture;
        }

        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions() {
                @Override
                public ResourceLocation getStillTexture() {
                    return stillTexture;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return flowingTexture;
                }

                @Override
                public int getTintColor() {
                    return 0xFFFFFFFF;
                }
            });
        }
    }
}
