package com.github.mim1q.minecells;

import com.mojang.logging.LogUtils;
import com.github.mim1q.minecells.config.MineCellsConfig;
import com.github.mim1q.minecells.registry.MineCellsGameRules;
import com.github.mim1q.minecells.registry.MineCellsSounds;
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
        MineCellsGameRules.init();
        modEventBus.addListener(this::commonSetup);
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
