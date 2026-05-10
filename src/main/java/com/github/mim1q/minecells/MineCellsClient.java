package com.github.mim1q.minecells;

import com.github.mim1q.minecells.client.MineCellsItemDescriptionTooltips;
import com.github.mim1q.minecells.client.MineCellsItemProperties;
import com.github.mim1q.minecells.client.screen.CellCrafterScreen;
import com.github.mim1q.minecells.client.renderer.SpriteMineCellsMonsterRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.ArrowSignBlockEntityRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.DoorwayPortalBlockEntityRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.FlagBlockEntityRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.RiftBlockEntityRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.ReturnStoneBlockEntityRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.SpawnerRuneBlockEntityRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.statue.DecorativeStatueBlockEntityRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.statue.KingStatueModel;
import com.github.mim1q.minecells.client.renderer.projectile.GrenadeProjectileRenderer;
import com.github.mim1q.minecells.client.renderer.monster.GrenadierModel;
import com.github.mim1q.minecells.client.renderer.monster.LeapingZombieModel;
import com.github.mim1q.minecells.client.renderer.monster.MineCellsMonsterModelLayers;
import com.github.mim1q.minecells.client.renderer.monster.MutatedBatModel;
import com.github.mim1q.minecells.client.renderer.monster.ModelBackedMineCellsMonsterRenderer;
import com.github.mim1q.minecells.client.renderer.monster.ProtectorModel;
import com.github.mim1q.minecells.client.renderer.monster.RancidRatModel;
import com.github.mim1q.minecells.client.renderer.monster.RunnerModel;
import com.github.mim1q.minecells.client.renderer.monster.ScorpionModel;
import com.github.mim1q.minecells.client.renderer.monster.ShieldbearerModel;
import com.github.mim1q.minecells.client.renderer.monster.ShockerModel;
import com.github.mim1q.minecells.client.renderer.monster.UndeadArcherModel;
import com.github.mim1q.minecells.client.world.FoggyDimensionSpecialEffects;
import com.github.mim1q.minecells.client.world.PromenadeDimensionSpecialEffects;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsFluids;
import com.github.mim1q.minecells.registry.MineCellsMenus;
import net.minecraft.resources.ResourceLocation;
import com.github.mim1q.minecells.screen.cellcrafter.CellCrafterMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class MineCellsClient {
    private MineCellsClient() {
    }

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        MineCells.LOGGER.info("Initializing Mine Cells client");
        MinecraftForge.EVENT_BUS.register(new MineCellsItemDescriptionTooltips());
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
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.SPAWNER_RUNE.get(), RenderType.cutout());
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
            MineCellsItemProperties.register();
        });
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MineCellsBlockEntities.FLAG_BLOCK_ENTITY.get(), FlagBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(MineCellsBlockEntities.DECORATIVE_STATUE_BLOCK_ENTITY.get(), DecorativeStatueBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(MineCellsBlockEntities.RETURN_STONE.get(), ReturnStoneBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(MineCellsBlockEntities.ARROW_SIGN.get(), ArrowSignBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(MineCellsBlockEntities.DOORWAY.get(), DoorwayPortalBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(MineCellsBlockEntities.RIFT.get(), RiftBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(MineCellsBlockEntities.SPAWNER_RUNE.get(), SpawnerRuneBlockEntityRenderer::new);
        event.registerEntityRenderer(MineCellsEntities.GRENADE.get(), GrenadeProjectileRenderer::new);
        event.registerEntityRenderer(MineCellsEntities.LEAPING_ZOMBIE.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new LeapingZombieModel(context.bakeLayer(MineCellsMonsterModelLayers.LEAPING_ZOMBIE)), 0.35F, MineCells.id("textures/entity/leaping_zombie/leaping_zombie.png"), MineCells.id("textures/entity/leaping_zombie/leaping_zombie_glow.png")));
        event.registerEntityRenderer(MineCellsEntities.GRENADIER.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new GrenadierModel(context.bakeLayer(MineCellsMonsterModelLayers.GRENADIER)), 0.35F, MineCells.id("textures/entity/grenadier/grenadier.png"), MineCells.id("textures/entity/grenadier/grenadier_glow.png")));
        event.registerEntityRenderer(MineCellsEntities.SHOCKER.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new ShockerModel(context.bakeLayer(MineCellsMonsterModelLayers.SHOCKER)), 0.5F, MineCells.id("textures/entity/shocker/shocker.png"), MineCells.id("textures/entity/shocker/shocker_glow.png")));
        event.registerEntityRenderer(MineCellsEntities.UNDEAD_ARCHER.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new UndeadArcherModel(context.bakeLayer(MineCellsMonsterModelLayers.UNDEAD_ARCHER)), 0.35F, MineCells.id("textures/entity/undead_archer.png"), null));
        event.registerEntityRenderer(MineCellsEntities.RUNNER.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new RunnerModel(context.bakeLayer(MineCellsMonsterModelLayers.RUNNER)), 0.4F, MineCells.id("textures/entity/runner/runner.png"), MineCells.id("textures/entity/runner/runner_glow.png")));
        event.registerEntityRenderer(MineCellsEntities.PROTECTOR.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new ProtectorModel(context.bakeLayer(MineCellsMonsterModelLayers.PROTECTOR)), 0.5F, MineCells.id("textures/entity/protector/protector.png"), MineCells.id("textures/entity/protector/protector_glow.png")));
        event.registerEntityRenderer(MineCellsEntities.SHIELDBEARER.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new ShieldbearerModel(context.bakeLayer(MineCellsMonsterModelLayers.SHIELDBEARER)), 0.35F, MineCells.id("textures/entity/shieldbearer.png"), null));
        event.registerEntityRenderer(MineCellsEntities.RANCID_RAT.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new RancidRatModel(context.bakeLayer(MineCellsMonsterModelLayers.RANCID_RAT)), 0.14F, MineCells.id("textures/entity/rancid_rat/rancid_rat.png"), MineCells.id("textures/entity/rancid_rat/rancid_rat_glow.png")));
        event.registerEntityRenderer(MineCellsEntities.MUTATED_BAT.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new MutatedBatModel(context.bakeLayer(MineCellsMonsterModelLayers.MUTATED_BAT)), 0.18F, MineCells.id("textures/entity/mutated_bat.png"), null));
        event.registerEntityRenderer(MineCellsEntities.SCORPION.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new ScorpionModel(context.bakeLayer(MineCellsMonsterModelLayers.SCORPION)), 0.26F, MineCells.id("textures/entity/scorpion/scorpion.png"), MineCells.id("textures/entity/scorpion/scorpion_glow.png")));
        MineCellsEntities.MONSTERS.stream()
            .filter(entityType -> entityType != MineCellsEntities.LEAPING_ZOMBIE)
            .filter(entityType -> entityType != MineCellsEntities.GRENADIER)
            .filter(entityType -> entityType != MineCellsEntities.SHOCKER)
            .filter(entityType -> entityType != MineCellsEntities.UNDEAD_ARCHER)
            .filter(entityType -> entityType != MineCellsEntities.RUNNER)
            .filter(entityType -> entityType != MineCellsEntities.PROTECTOR)
            .filter(entityType -> entityType != MineCellsEntities.SHIELDBEARER)
            .filter(entityType -> entityType != MineCellsEntities.RANCID_RAT)
            .filter(entityType -> entityType != MineCellsEntities.MUTATED_BAT)
            .filter(entityType -> entityType != MineCellsEntities.SCORPION)
            .forEach(entityType -> event.registerEntityRenderer(entityType.get(), SpriteMineCellsMonsterRenderer::new));
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FlagBlockEntityRenderer.FLAG_LAYER, () -> FlagBlockEntityRenderer.BiomeBannerBlockEntityModel.createLayer(false));
        event.registerLayerDefinition(FlagBlockEntityRenderer.FLAG_LARGE_LAYER, () -> FlagBlockEntityRenderer.BiomeBannerBlockEntityModel.createLayer(true));
        event.registerLayerDefinition(DecorativeStatueBlockEntityRenderer.KING_STATUE_LAYER, KingStatueModel::createLayer);
        event.registerLayerDefinition(ArrowSignBlockEntityRenderer.ARROW_SIGN_LAYER, ArrowSignBlockEntityRenderer::createLayer);
        event.registerLayerDefinition(MineCellsMonsterModelLayers.LEAPING_ZOMBIE, LeapingZombieModel::createLayer);
        event.registerLayerDefinition(MineCellsMonsterModelLayers.GRENADIER, GrenadierModel::createLayer);
        event.registerLayerDefinition(MineCellsMonsterModelLayers.SHOCKER, ShockerModel::createLayer);
        event.registerLayerDefinition(MineCellsMonsterModelLayers.UNDEAD_ARCHER, UndeadArcherModel::createLayer);
        event.registerLayerDefinition(MineCellsMonsterModelLayers.RUNNER, RunnerModel::createLayer);
        event.registerLayerDefinition(MineCellsMonsterModelLayers.PROTECTOR, ProtectorModel::createLayer);
        event.registerLayerDefinition(MineCellsMonsterModelLayers.SHIELDBEARER, ShieldbearerModel::createLayer);
        event.registerLayerDefinition(MineCellsMonsterModelLayers.RANCID_RAT, RancidRatModel::createLayer);
        event.registerLayerDefinition(MineCellsMonsterModelLayers.MUTATED_BAT, MutatedBatModel::createLayer);
        event.registerLayerDefinition(MineCellsMonsterModelLayers.SCORPION, ScorpionModel::createLayer);
    }

    @SubscribeEvent
    public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(MineCells.id("foggy"), new FoggyDimensionSpecialEffects());
        event.register(MineCells.id("promenade"), new PromenadeDimensionSpecialEffects());
    }
}
