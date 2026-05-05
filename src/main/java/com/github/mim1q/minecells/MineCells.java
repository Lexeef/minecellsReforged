package com.github.mim1q.minecells;

import com.mojang.logging.LogUtils;
import com.github.mim1q.minecells.config.MineCellsConfig;
import com.github.mim1q.minecells.network.MineCellsNetwork;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsCreativeTabs;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsFluids;
import com.github.mim1q.minecells.registry.MineCellsGameRules;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsPointOfInterestTypes;
import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import com.github.mim1q.minecells.registry.MineCellsStructureProcessorTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(MineCells.MOD_ID)
public class MineCells {
    public static final String MOD_ID = "minecells";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MineCells(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        MineCellsSounds.register(modEventBus);
        MineCellsParticles.register(modEventBus);
        MineCellsStatusEffects.register(modEventBus);
        MineCellsFluids.register(modEventBus);
        MineCellsEntities.register(modEventBus);
        MineCellsBlocks.register(modEventBus);
        MineCellsBlockEntities.register(modEventBus);
        MineCellsItems.register(modEventBus);
        MineCellsCreativeTabs.register(modEventBus);
        MineCellsRecipeTypes.register(modEventBus);
        MineCellsPointOfInterestTypes.register(modEventBus);
        MineCellsStructureProcessorTypes.register(modEventBus);
        MineCellsGameRules.init();
        MineCellsNetwork.init();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(MineCellsEntities::registerAttributes);
        context.registerConfig(ModConfig.Type.COMMON, MineCellsConfig.COMMON_SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, MineCellsConfig.CLIENT_SPEC);
        MinecraftForge.EVENT_BUS.register(this);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> MineCellsClient::init);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Initializing Mine Cells Forge port");
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
