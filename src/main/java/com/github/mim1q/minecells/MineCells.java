package com.github.mim1q.minecells;

import com.github.mim1q.minecells.book.PatchouliCompat;
import com.github.mim1q.minecells.command.CellsCommand;
import com.github.mim1q.minecells.command.MineCellsDataCommand;
import com.github.mim1q.minecells.command.MineCellsDumpCommand;
import com.github.mim1q.minecells.command.MineCellsTeleportCommand;
import com.github.mim1q.minecells.command.SpawnerRuneCommand;
import com.github.mim1q.minecells.command.SpecialPointCommand;
import com.github.mim1q.minecells.config.MineCellsConfig;
import com.github.mim1q.minecells.network.MineCellsNetwork;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsCreativeTabs;
import com.github.mim1q.minecells.registry.MineCellsDensityFunctionTypes;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsFeatures;
import com.github.mim1q.minecells.registry.MineCellsFluids;
import com.github.mim1q.minecells.registry.MineCellsGameRules;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsLootPoolEntryTypes;
import com.github.mim1q.minecells.registry.MineCellsMenus;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsPlacementModifierTypes;
import com.github.mim1q.minecells.registry.MineCellsPlacerTypes;
import com.github.mim1q.minecells.registry.MineCellsPointOfInterestTypes;
import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;
import com.github.mim1q.minecells.registry.MineCellsReloadListeners;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import com.github.mim1q.minecells.registry.MineCellsStructurePieceTypes;
import com.github.mim1q.minecells.registry.MineCellsStructurePlacementTypes;
import com.github.mim1q.minecells.registry.MineCellsStructureProcessorTypes;
import com.github.mim1q.minecells.registry.MineCellsStructureTypes;
import com.github.mim1q.minecells.valuecalculators.ValueCalculators;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
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
        MineCellsFeatures.register(modEventBus);
        MineCellsBlocks.register(modEventBus);
        MineCellsBlockEntities.register(modEventBus);
        MineCellsItems.register(modEventBus);
        MineCellsLootPoolEntryTypes.register(modEventBus);
        MineCellsMenus.register(modEventBus);
        MineCellsCreativeTabs.register(modEventBus);
        MineCellsRecipeTypes.register(modEventBus);
        MineCellsPointOfInterestTypes.register(modEventBus);
        MineCellsPlacerTypes.register(modEventBus);
        MineCellsPlacementModifierTypes.register(modEventBus);
        MineCellsDensityFunctionTypes.register(modEventBus);
        MineCellsStructureTypes.register(modEventBus);
        MineCellsStructurePieceTypes.register(modEventBus);
        MineCellsStructurePlacementTypes.register(modEventBus);
        MineCellsStructureProcessorTypes.register(modEventBus);
        MineCellsGameRules.init();
        MineCellsNetwork.init();
        modEventBus.addListener(MineCellsEntities::registerAttributes);
        context.registerConfig(ModConfig.Type.COMMON, MineCellsConfig.COMMON_SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, MineCellsConfig.CLIENT_SPEC);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(MineCellsDataCommand::register);
        MinecraftForge.EVENT_BUS.addListener(SpawnerRuneCommand::register);
        MinecraftForge.EVENT_BUS.addListener(MineCellsTeleportCommand::register);
        MinecraftForge.EVENT_BUS.addListener(CellsCommand::register);
        MinecraftForge.EVENT_BUS.addListener(MineCellsDumpCommand::register);
        MinecraftForge.EVENT_BUS.addListener(SpecialPointCommand::register);
        MinecraftForge.EVENT_BUS.addListener(MineCellsReloadListeners::onAddReloadListeners);
        MinecraftForge.EVENT_BUS.addListener(PatchouliCompat::onAdvancementEarned);
        MinecraftForge.EVENT_BUS.addListener(ValueCalculators::onDatapackSync);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
