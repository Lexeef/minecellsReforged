package com.github.mim1q.minecells;

import com.github.mim1q.minecells.client.MineCellsItemDescriptionTooltips;
import com.github.mim1q.minecells.client.MineCellsItemProperties;
import com.github.mim1q.minecells.client.renderer.blockentity.ArrowSignBlockEntityRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.BarrierControllerRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.CellCrafterBlockEntityRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.DoorwayPortalBlockEntityRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.FlagBlockEntityRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.ReturnStoneBlockEntityRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.RiftBlockEntityRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.RunicVinePlantBlockEntityRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.statue.DecorativeStatueBlockEntityRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.statue.KingStatueModel;
import com.github.mim1q.minecells.client.renderer.blockentity.TeleporterBlockEntityRenderer;
import com.github.mim1q.minecells.client.renderer.boss.ConjunctiviusEntityModel;
import com.github.mim1q.minecells.client.renderer.boss.ConjunctiviusEntityRenderer;
import com.github.mim1q.minecells.client.renderer.boss.ConjunctiviusEyeRenderer;
import com.github.mim1q.minecells.client.renderer.boss.ConjunctiviusSpikeRenderer;
import com.github.mim1q.minecells.client.renderer.boss.ConjunctiviusTentacleRenderer;
import com.github.mim1q.minecells.client.renderer.ConciergeEntityRenderer;
import com.github.mim1q.minecells.client.renderer.monster.BuzzcutterModel;
import com.github.mim1q.minecells.client.renderer.monster.ConciergeEntityModel;
import com.github.mim1q.minecells.client.renderer.monster.DisgustingWormModel;
import com.github.mim1q.minecells.client.renderer.monster.GrenadierModel;
import com.github.mim1q.minecells.client.renderer.monster.InquisitorModel;
import com.github.mim1q.minecells.client.renderer.monster.InquisitorRenderer;
import com.github.mim1q.minecells.client.renderer.monster.KamikazeModel;
import com.github.mim1q.minecells.client.renderer.monster.LeapingZombieModel;
import com.github.mim1q.minecells.client.renderer.monster.MineCellsMonsterModelLayers;
import com.github.mim1q.minecells.client.renderer.monster.ModelBackedMineCellsMonsterRenderer;
import com.github.mim1q.minecells.client.renderer.monster.MutatedBatModel;
import com.github.mim1q.minecells.client.renderer.monster.ProtectorModel;
import com.github.mim1q.minecells.client.renderer.monster.ProtectorRenderer;
import com.github.mim1q.minecells.client.renderer.monster.RancidRatModel;
import com.github.mim1q.minecells.client.renderer.monster.RunnerModel;
import com.github.mim1q.minecells.client.renderer.monster.ScorpionModel;
import com.github.mim1q.minecells.client.renderer.monster.SewersTentacleModel;
import com.github.mim1q.minecells.client.renderer.monster.SewersTentacleRenderer;
import com.github.mim1q.minecells.client.renderer.monster.ShieldbearerModel;
import com.github.mim1q.minecells.client.renderer.monster.ShockerModel;
import com.github.mim1q.minecells.client.renderer.monster.ShockerRenderer;
import com.github.mim1q.minecells.client.renderer.monster.SweeperModel;
import com.github.mim1q.minecells.client.renderer.monster.UndeadArcherModel;
import com.github.mim1q.minecells.client.renderer.nonliving.ElevatorEntityRenderer;
import com.github.mim1q.minecells.client.renderer.nonliving.SpawnerRuneRenderer;
import com.github.mim1q.minecells.client.renderer.obelisk.ObeliskEntityRenderer;
import com.github.mim1q.minecells.client.renderer.projectile.BillboardProjectileRenderer;
import com.github.mim1q.minecells.client.renderer.projectile.ConjunctiviusProjectileRenderer;
import com.github.mim1q.minecells.client.renderer.projectile.CustomArrowEntityRenderer;
import com.github.mim1q.minecells.client.renderer.projectile.GrenadeProjectileRenderer;
import com.github.mim1q.minecells.client.renderer.projectile.TentacleWeaponEntityRenderer;
import com.github.mim1q.minecells.client.renderer.SpriteMineCellsMonsterRenderer;
import com.github.mim1q.minecells.client.screen.CellCrafterScreen;
import com.github.mim1q.minecells.client.world.FoggyDimensionSpecialEffects;
import com.github.mim1q.minecells.client.world.PromenadeDimensionSpecialEffects;
import com.github.mim1q.minecells.config.MineCellsConfig;
import com.github.mim1q.minecells.item.weapon.bow.CustomArrowType;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
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
        MinecraftForge.EVENT_BUS.register(new MineCellsItemDescriptionTooltips());
        if (net.minecraftforge.fml.ModList.get().isLoaded("patchouli")) {
            com.github.mim1q.minecells.book.PatchouliClientCompat.init();
        }
        event.enqueueWork(() -> {
            net.minecraft.client.renderer.Sheets.addWoodType(MineCellsBlocks.PUTRID_WOOD_TYPE);
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
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.CELL_CRAFTER.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.UNBREAKABLE_CELL_CRAFTER.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.WILTED_GRASS_BLOCK.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.BLOOMROCK_WILTED_GRASS_BLOCK.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.BROKEN_CAGE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.SPIKES.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.ALCHEMY_EQUIPMENT_0.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.ALCHEMY_EQUIPMENT_1.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.ALCHEMY_EQUIPMENT_2.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.PRISON_TORCH.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.PROMENADE_TORCH.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.RAMPARTS_TORCH.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.SEWERS_TORCH.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(MineCellsBlocks.ANCIENT_SEWERS_TORCH.get(), RenderType.cutout());
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
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void addEffectOverlayLayers(EntityRenderersEvent.AddLayers event) {
        for (net.minecraft.world.entity.EntityType<?> type : net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getValues()) {
            net.minecraft.client.renderer.entity.LivingEntityRenderer renderer;
            try {
                renderer = event.getRenderer((net.minecraft.world.entity.EntityType) type);
            } catch (ClassCastException notLiving) {
                continue;
            }
            if (renderer != null) {
                renderer.addLayer(new com.github.mim1q.minecells.client.renderer.layer.EffectOverlayLayer<>(renderer));
            }
        }
        for (String skin : event.getSkins()) {
            net.minecraft.client.renderer.entity.LivingEntityRenderer renderer = event.getSkin(skin);
            if (renderer != null) {
                renderer.addLayer(new com.github.mim1q.minecells.client.renderer.layer.EffectOverlayLayer<>(renderer));
            }
        }
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MineCellsBlockEntities.FLAG_BLOCK_ENTITY.get(), FlagBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(MineCellsBlockEntities.DECORATIVE_STATUE_BLOCK_ENTITY.get(), DecorativeStatueBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(MineCellsBlockEntities.RETURN_STONE.get(), ReturnStoneBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(MineCellsBlockEntities.ARROW_SIGN.get(), ArrowSignBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(MineCellsBlockEntities.SIGN.get(), net.minecraft.client.renderer.blockentity.SignRenderer::new);
        event.registerBlockEntityRenderer(MineCellsBlockEntities.DOORWAY.get(), DoorwayPortalBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(MineCellsBlockEntities.RIFT.get(), RiftBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(MineCellsBlockEntities.TELEPORTER.get(), TeleporterBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(MineCellsBlockEntities.BARRIER_CONTROLLER.get(), BarrierControllerRenderer::new);
        event.registerBlockEntityRenderer(MineCellsBlockEntities.CELL_CRAFTER.get(), CellCrafterBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(MineCellsBlockEntities.RUNIC_VINE_PLANT.get(), RunicVinePlantBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(MineCellsBlockEntities.SPAWNER_RUNE.get(), SpawnerRuneRenderer.BlockEntity::new);
        event.registerEntityRenderer(MineCellsEntities.SPAWNER_RUNE.get(), SpawnerRuneRenderer.Entity::new);
        event.registerEntityRenderer(MineCellsEntities.GRENADE.get(), GrenadeProjectileRenderer::grenade);
        event.registerEntityRenderer(MineCellsEntities.BIG_GRENADE.get(), GrenadeProjectileRenderer::bigGrenade);
        event.registerEntityRenderer(MineCellsEntities.DISGUSTING_WORM_EGG.get(), GrenadeProjectileRenderer::disgustingWormEgg);
        event.registerEntityRenderer(MineCellsEntities.TENTACLE_WEAPON.get(), TentacleWeaponEntityRenderer::new);
        event.registerEntityRenderer(MineCellsEntities.CUSTOM_ARROW.get(), CustomArrowEntityRenderer::new);
        event.registerEntityRenderer(MineCellsEntities.ELEVATOR.get(), ElevatorEntityRenderer::new);
        event.registerEntityRenderer(MineCellsEntities.SHOCKWAVE_PLACER.get(), context -> new net.minecraft.client.renderer.entity.EntityRenderer<>(context) {
            @Override
            public net.minecraft.resources.ResourceLocation getTextureLocation(com.github.mim1q.minecells.entity.nonliving.ShockwavePlacer entity) {
                return net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS;
            }
        });
        event.registerEntityRenderer(MineCellsEntities.LEAPING_ZOMBIE.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new LeapingZombieModel(context.bakeLayer(MineCellsMonsterModelLayers.LEAPING_ZOMBIE)), 0.35F, MineCells.id("textures/entity/leaping_zombie/leaping_zombie.png"), MineCells.id("textures/entity/leaping_zombie/leaping_zombie_glow.png"), () -> MineCellsConfig.CLIENT.leapingZombieGlow.get()));
        event.registerEntityRenderer(MineCellsEntities.GRENADIER.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new GrenadierModel(context.bakeLayer(MineCellsMonsterModelLayers.GRENADIER)), 0.35F, MineCells.id("textures/entity/grenadier/grenadier.png"), MineCells.id("textures/entity/grenadier/grenadier_glow.png"), () -> MineCellsConfig.CLIENT.grenadierGlow.get()));
        event.registerEntityRenderer(MineCellsEntities.SHOCKER.get(), ShockerRenderer::new);
        event.registerEntityRenderer(MineCellsEntities.UNDEAD_ARCHER.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new UndeadArcherModel(context.bakeLayer(MineCellsMonsterModelLayers.UNDEAD_ARCHER)), 0.35F, MineCells.id("textures/entity/undead_archer.png"), null));
        event.registerEntityRenderer(MineCellsEntities.RUNNER.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new RunnerModel(context.bakeLayer(MineCellsMonsterModelLayers.RUNNER)), 0.4F, MineCells.id("textures/entity/runner/runner.png"), MineCells.id("textures/entity/runner/runner_glow.png")));
        event.registerEntityRenderer(MineCellsEntities.PROTECTOR.get(), ProtectorRenderer::new);
        event.registerEntityRenderer(MineCellsEntities.SHIELDBEARER.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new ShieldbearerModel(context.bakeLayer(MineCellsMonsterModelLayers.SHIELDBEARER)), 0.35F, MineCells.id("textures/entity/shieldbearer.png"), null));
        event.registerEntityRenderer(MineCellsEntities.RANCID_RAT.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new RancidRatModel(context.bakeLayer(MineCellsMonsterModelLayers.RANCID_RAT)), 0.35F, MineCells.id("textures/entity/rancid_rat/rancid_rat.png"), MineCells.id("textures/entity/rancid_rat/rancid_rat_glow.png"), () -> MineCellsConfig.CLIENT.rancidRatGlow.get()));
        event.registerEntityRenderer(MineCellsEntities.MUTATED_BAT.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new MutatedBatModel(context.bakeLayer(MineCellsMonsterModelLayers.MUTATED_BAT)), 0.3F, MineCells.id("textures/entity/mutated_bat.png"), null));
        event.registerEntityRenderer(MineCellsEntities.SCORPION.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new ScorpionModel(context.bakeLayer(MineCellsMonsterModelLayers.SCORPION)), 0.75F, MineCells.id("textures/entity/scorpion/scorpion.png"), MineCells.id("textures/entity/scorpion/scorpion_glow.png"), () -> MineCellsConfig.CLIENT.scorpionGlow.get()) {
            @Override
            public void render(com.github.mim1q.minecells.entity.MineCellsMonsterEntity entity, float entityYaw, float partialTick, com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.client.renderer.MultiBufferSource buffers, int packedLight) {
                this.shadowRadius = entity instanceof com.github.mim1q.minecells.entity.ScorpionEntity scorpion && scorpion.isSleeping() ? 0.0F : 0.75F;
                super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
            }
        });
        event.registerEntityRenderer(MineCellsEntities.DISGUSTING_WORM.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new DisgustingWormModel(context.bakeLayer(MineCellsMonsterModelLayers.DISGUSTING_WORM)), 0.75F, MineCells.id("textures/entity/disgusting_worm/disgusting_worm.png"), MineCells.id("textures/entity/disgusting_worm/disgusting_worm_glow.png"), () -> MineCellsConfig.CLIENT.disgustingWormGlow.get()));
        event.registerEntityRenderer(MineCellsEntities.INQUISITOR.get(), InquisitorRenderer::new);
        event.registerEntityRenderer(MineCellsEntities.KAMIKAZE.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new KamikazeModel(context.bakeLayer(MineCellsMonsterModelLayers.KAMIKAZE)), 0.3F, MineCells.id("textures/entity/kamikaze.png"), null));
        event.registerEntityRenderer(MineCellsEntities.SEWERS_TENTACLE.get(), SewersTentacleRenderer::new);
        event.registerEntityRenderer(MineCellsEntities.SWEEPER.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new SweeperModel(context.bakeLayer(MineCellsMonsterModelLayers.SWEEPER)), 0.5F, MineCells.id("textures/entity/sweeper/sweeper.png"), MineCells.id("textures/entity/sweeper/sweeper_glow.png")));
        event.registerEntityRenderer(MineCellsEntities.BUZZCUTTER.get(), context -> new ModelBackedMineCellsMonsterRenderer<>(context, new BuzzcutterModel(context.bakeLayer(MineCellsMonsterModelLayers.BUZZCUTTER)), 0.25F, MineCells.id("textures/entity/fly/buzzcutter.png"), MineCells.id("textures/entity/fly/buzzcutter_glow.png")));
        event.registerEntityRenderer(MineCellsEntities.CONCIERGE.get(), ConciergeEntityRenderer::new);
        event.registerEntityRenderer(MineCellsEntities.CONJUNCTIVIUS.get(), ConjunctiviusEntityRenderer::new);
        event.registerEntityRenderer(MineCellsEntities.CONJUNCTIVIUS_PROJECTILE.get(), ConjunctiviusProjectileRenderer::new);
        event.registerEntityRenderer(MineCellsEntities.MAGIC_ORB.get(), context -> new BillboardProjectileRenderer<>(context, MineCells.id("textures/particle/magic_orb.png"), -0.25F, true));
        event.registerEntityRenderer(MineCellsEntities.SCORPION_SPIT.get(), context -> new BillboardProjectileRenderer<>(context, MineCells.id("textures/entity/scorpion/spit.png"), -0.5F, false));
        event.registerEntityRenderer(MineCellsEntities.CONCIERGE_OBELISK.get(), context -> new ObeliskEntityRenderer(context, "concierge"));
        event.registerEntityRenderer(MineCellsEntities.CONJUNCTIVIUS_OBELISK.get(), context -> new ObeliskEntityRenderer(context, "conjunctivius"));
        event.registerEntityRenderer(MineCellsEntities.ELITE_OBELISK.get(), context -> new ObeliskEntityRenderer(context, "elite"));
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
            .filter(entityType -> entityType != MineCellsEntities.DISGUSTING_WORM)
            .filter(entityType -> entityType != MineCellsEntities.INQUISITOR)
            .filter(entityType -> entityType != MineCellsEntities.KAMIKAZE)
            .filter(entityType -> entityType != MineCellsEntities.SEWERS_TENTACLE)
            .filter(entityType -> entityType != MineCellsEntities.SWEEPER)
            .filter(entityType -> entityType != MineCellsEntities.BUZZCUTTER)
            .forEach(entityType -> event.registerEntityRenderer(entityType.get(), SpriteMineCellsMonsterRenderer::new));
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FlagBlockEntityRenderer.FLAG_LAYER, () -> FlagBlockEntityRenderer.BiomeBannerBlockEntityModel.createLayer(false));
        event.registerLayerDefinition(FlagBlockEntityRenderer.FLAG_LARGE_LAYER, () -> FlagBlockEntityRenderer.BiomeBannerBlockEntityModel.createLayer(true));
        event.registerLayerDefinition(DecorativeStatueBlockEntityRenderer.KING_STATUE_LAYER, KingStatueModel::createLayer);
        event.registerLayerDefinition(ArrowSignBlockEntityRenderer.ARROW_SIGN_LAYER, ArrowSignBlockEntityRenderer::createLayer);
        event.registerLayerDefinition(TeleporterBlockEntityRenderer.TELEPORTER_LAYER, TeleporterBlockEntityRenderer::createLayer);
        event.registerLayerDefinition(BarrierControllerRenderer.BIG_DOOR_LAYER, BarrierControllerRenderer::createBigDoorLayer);
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
        event.registerLayerDefinition(MineCellsMonsterModelLayers.DISGUSTING_WORM, DisgustingWormModel::createLayer);
        event.registerLayerDefinition(MineCellsMonsterModelLayers.INQUISITOR, InquisitorModel::createLayer);
        event.registerLayerDefinition(MineCellsMonsterModelLayers.KAMIKAZE, KamikazeModel::createLayer);
        event.registerLayerDefinition(MineCellsMonsterModelLayers.SEWERS_TENTACLE, SewersTentacleModel::createLayer);
        event.registerLayerDefinition(MineCellsMonsterModelLayers.SWEEPER, SweeperModel::createLayer);
        event.registerLayerDefinition(MineCellsMonsterModelLayers.BUZZCUTTER, BuzzcutterModel::createLayer);
        event.registerLayerDefinition(MineCellsMonsterModelLayers.CONCIERGE, ConciergeEntityModel::createLayer);
        event.registerLayerDefinition(MineCellsMonsterModelLayers.CONJUNCTIVIUS, ConjunctiviusEntityModel::createLayer);
        event.registerLayerDefinition(MineCellsMonsterModelLayers.CONJUNCTIVIUS_EYE, ConjunctiviusEyeRenderer.ConjunctiviusEyeModel::createLayer);
        event.registerLayerDefinition(MineCellsMonsterModelLayers.CONJUNCTIVIUS_SPIKE, ConjunctiviusSpikeRenderer.ConjunctiviusSpikeModel::createLayer);
        event.registerLayerDefinition(MineCellsMonsterModelLayers.CONJUNCTIVIUS_TENTACLE, ConjunctiviusTentacleRenderer.ConjunctiviusTentacleModel::createLayer);
        event.registerLayerDefinition(ObeliskEntityRenderer.LAYER, com.github.mim1q.minecells.client.renderer.obelisk.ObeliskEntityModel::createLayer);
        event.registerLayerDefinition(TentacleWeaponEntityRenderer.LAYER, TentacleWeaponEntityRenderer::createLayer);
        event.registerLayerDefinition(ElevatorEntityRenderer.LAYER, ElevatorEntityRenderer::createLayer);
        event.registerLayerDefinition(GrenadeProjectileRenderer.GRENADE_LAYER, GrenadeProjectileRenderer::createGrenadeLayer);
        event.registerLayerDefinition(GrenadeProjectileRenderer.BIG_GRENADE_LAYER, GrenadeProjectileRenderer::createBigGrenadeLayer);
        event.registerLayerDefinition(GrenadeProjectileRenderer.DISGUSTING_WORM_EGG_LAYER, GrenadeProjectileRenderer::createDisgustingWormEggLayer);
        event.registerLayerDefinition(ConjunctiviusProjectileRenderer.LAYER, ConjunctiviusProjectileRenderer::createLayer);
    }

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(MineCells.id("misc/conjunctivius/chain"));
        event.register(MineCells.id("misc/conjunctivius/dash_chain"));
        CustomArrowType.getAllNames().forEach(name -> event.register(MineCells.id("arrow/" + name)));
    }

    @SubscribeEvent
    public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(MineCells.id("foggy"), new FoggyDimensionSpecialEffects());
        event.register(MineCells.id("promenade"), new PromenadeDimensionSpecialEffects());
    }
}
