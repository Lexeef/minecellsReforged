package com.github.mim1q.minecells.registry;

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
    private static final ResourceLocation WATER_STILL = new ResourceLocation("block/water_still");
    private static final ResourceLocation WATER_FLOW = new ResourceLocation("block/water_flow");

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, MineCells.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MineCells.MOD_ID);

    public static final RegistryObject<FluidType> SEWAGE_TYPE = FLUID_TYPES.register("sewage", () -> new TintedFluidType(0xFFA2E751, FluidType.Properties.create()
        .canSwim(true)
        .canDrown(true)
        .canConvertToSource(false)
        .rarity(Rarity.COMMON)));
    public static final RegistryObject<FluidType> ANCIENT_SEWAGE_TYPE = FLUID_TYPES.register("ancient_sewage", () -> new TintedFluidType(0xFFE0C93B, FluidType.Properties.create()
        .canSwim(true)
        .canDrown(true)
        .canConvertToSource(false)
        .rarity(Rarity.COMMON)));

    public static final RegistryObject<FlowingFluid> STILL_SEWAGE = FLUIDS.register("sewage", () -> new ForgeFlowingFluid.Source(sewageProperties()));
    public static final RegistryObject<FlowingFluid> FLOWING_SEWAGE = FLUIDS.register("flowing_sewage", () -> new ForgeFlowingFluid.Flowing(sewageProperties()));
    public static final RegistryObject<FlowingFluid> STILL_ANCIENT_SEWAGE = FLUIDS.register("ancient_sewage", () -> new ForgeFlowingFluid.Source(ancientSewageProperties()));
    public static final RegistryObject<FlowingFluid> FLOWING_ANCIENT_SEWAGE = FLUIDS.register("flowing_ancient_sewage", () -> new ForgeFlowingFluid.Flowing(ancientSewageProperties()));

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

    private static class TintedFluidType extends FluidType {
        private final int tintColor;

        private TintedFluidType(int tintColor, Properties properties) {
            super(properties);
            this.tintColor = tintColor;
        }

        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions() {
                @Override
                public ResourceLocation getStillTexture() {
                    return WATER_STILL;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return WATER_FLOW;
                }

                @Override
                public int getTintColor() {
                    return tintColor;
                }
            });
        }
    }
}
