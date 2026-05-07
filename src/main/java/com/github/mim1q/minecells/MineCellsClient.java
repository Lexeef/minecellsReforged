package com.github.mim1q.minecells;

import com.github.mim1q.minecells.client.MineCellsItemDescriptionTooltips;
import com.github.mim1q.minecells.client.screen.CellCrafterScreen;
import com.github.mim1q.minecells.client.renderer.NoopMineCellsMonsterRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.ArrowSignBlockEntityRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.FlagBlockEntityRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.ReturnStoneBlockEntityRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.statue.DecorativeStatueBlockEntityRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.statue.KingStatueModel;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsFluids;
import com.github.mim1q.minecells.registry.MineCellsMenus;
import com.github.mim1q.minecells.screen.cellcrafter.CellCrafterMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class MineCellsClient {
    private MineCellsClient() {
    }

    static void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(MineCellsClient::clientSetup);
        modEventBus.addListener(MineCellsClient::registerLayerDefinitions);
        modEventBus.addListener(MineCellsClient::registerEntityRenderers);
        MinecraftForge.EVENT_BUS.register(new MineCellsItemDescriptionTooltips());
    }

    private static void clientSetup(final FMLClientSetupEvent event) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MineCells.LOGGER.info("Initializing Mine Cells client"));
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(MineCellsFluids.STILL_SEWAGE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(MineCellsFluids.FLOWING_SEWAGE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(MineCellsFluids.STILL_ANCIENT_SEWAGE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(MineCellsFluids.FLOWING_ANCIENT_SEWAGE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.BIG_CHAIN.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.UNBREAKABLE_CHAIN.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.PUTRID_DOOR.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.PUTRID_TRAPDOOR.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.PUTRID_BOARDS.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.ARROW_SIGN.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.WILTED_LEAVES.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.ORANGE_WILTED_LEAVES.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.RED_WILTED_LEAVES.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.WILTED_WALL_LEAVES.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.ORANGE_WILTED_WALL_LEAVES.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.RED_WILTED_WALL_LEAVES.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.WILTED_HANGING_LEAVES.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.ORANGE_WILTED_HANGING_LEAVES.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.RED_WILTED_HANGING_LEAVES.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.PUTRID_SAPLING.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.ORANGE_PUTRID_SAPLING.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.RED_PUTRID_SAPLING.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.CHAIN_PILE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.RUNIC_VINE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.RUNIC_VINE_PLANT.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.SMALL_CRATE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.BRITTLE_BARREL.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.CAGE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.BROKEN_CAGE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.SPIKES.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.ALCHEMY_EQUIPMENT_0.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.ALCHEMY_EQUIPMENT_1.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.ALCHEMY_EQUIPMENT_2.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.PRISON_TORCH.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.PROMENADE_TORCH.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.RAMPARTS_TORCH.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.SHOCKWAVE_FLAME.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.SHOCKWAVE_FLAME_PLAYER.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.HANGED_SKELETON.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.SKELETON.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.HANGED_CORPSE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.CORPSE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.HANGED_ROTTING_CORPSE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.ROTTING_CORPSE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.KING_STATUE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.FLAG_POLE.get(), RenderType.cutout());
            MineCellsBlocks.FLAG_BLOCKS.forEach(flag -> ItemBlockRenderTypes.setRenderLayer(flag.get(), RenderType.cutout()));
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.BARRIER_RUNE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.SOLID_BARRIER.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.TELEPORTER_CORE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.TELEPORTER_FRAME.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.DOORWAY_FRAME.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.UNBREAKABLE_DOORWAY_FRAME.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.OVERWORLD_DOORWAY.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.PRISON_DOORWAY.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.PROMENADE_DOORWAY.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.INSUFFERABLE_CRYPT_DOORWAY.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.RAMPARTS_DOORWAY.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.BLACK_BRIDGE_DOORWAY.get(), RenderType.cutout());
            MenuScreens.register(MineCellsMenus.CELL_CRAFTER.get(), (CellCrafterMenu menu, Inventory inventory, Component title) -> new CellCrafterScreen(menu, inventory, title));
        });
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MineCellsBlockEntities.FLAG_BLOCK_ENTITY.get(), FlagBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(MineCellsBlockEntities.DECORATIVE_STATUE_BLOCK_ENTITY.get(), DecorativeStatueBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(MineCellsBlockEntities.RETURN_STONE.get(), ReturnStoneBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(MineCellsBlockEntities.ARROW_SIGN.get(), ArrowSignBlockEntityRenderer::new);
        MineCellsEntities.MONSTERS.forEach(entityType -> event.registerEntityRenderer(entityType.get(), NoopMineCellsMonsterRenderer::new));
    }

    private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FlagBlockEntityRenderer.FLAG_LAYER, () -> FlagBlockEntityRenderer.BiomeBannerBlockEntityModel.createLayer(false));
        event.registerLayerDefinition(FlagBlockEntityRenderer.FLAG_LARGE_LAYER, () -> FlagBlockEntityRenderer.BiomeBannerBlockEntityModel.createLayer(true));
        event.registerLayerDefinition(DecorativeStatueBlockEntityRenderer.KING_STATUE_LAYER, KingStatueModel::createLayer);
        event.registerLayerDefinition(ArrowSignBlockEntityRenderer.ARROW_SIGN_LAYER, ArrowSignBlockEntityRenderer::createLayer);
    }
}
