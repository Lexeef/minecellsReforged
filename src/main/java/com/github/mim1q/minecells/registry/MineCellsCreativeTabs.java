package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class MineCellsCreativeTabs {
    private static final ResourceLocation GROUP_BACKGROUND = MineCells.id("textures/gui/group.png");
    private static final ResourceLocation TABS_TEXTURE = MineCells.id("textures/gui/tabs.png");
    private static final int TAB_LABEL_COLOR = 0x46D4FF;

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MineCells.MOD_ID);

    public static final RegistryObject<CreativeModeTab> GENERAL = CREATIVE_TABS.register("minecells_general", () -> baseBuilder("general")
        .icon(() -> new ItemStack(MineCellsBlocks.WILTED_LEAVES.get()))
        .withSearchBar(89)
        .displayItems((parameters, output) -> addGeneralItems(output))
        .build());

    public static final RegistryObject<CreativeModeTab> COMBAT = CREATIVE_TABS.register("minecells_combat", () -> baseBuilder("combat")
        .icon(() -> new ItemStack(MineCellsItems.BLOOD_SWORD.get()))
        .displayItems((parameters, output) -> addCombatItems(output))
        .build());

    public static final RegistryObject<CreativeModeTab> SPAWN_EGGS = CREATIVE_TABS.register("minecells_spawn_eggs", () -> baseBuilder("spawn_eggs")
        .icon(() -> new ItemStack(MineCellsItems.GRENADIER_SPAWN_EGG.get()))
        .displayItems((parameters, output) -> addSpawnEggItems(output))
        .build());

    private MineCellsCreativeTabs() {
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }

    private static CreativeModeTab.Builder baseBuilder(String titleKeySuffix) {
        return CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.minecells.minecells.tab." + titleKeySuffix))
            .withBackgroundLocation(GROUP_BACKGROUND)
            .withTabsImage(TABS_TEXTURE)
            .withLabelColor(TAB_LABEL_COLOR);
    }

    private static void addGeneralItems(CreativeModeTab.Output output) {
        add(output,
            MineCellsBlocks.PRISON_DOORWAY.get(),
            MineCellsBlocks.PROMENADE_DOORWAY.get(),
            MineCellsBlocks.RAMPARTS_DOORWAY.get(),
            MineCellsBlocks.INSUFFERABLE_CRYPT_DOORWAY.get(),
            MineCellsBlocks.BLACK_BRIDGE_DOORWAY.get(),
            MineCellsItems.PRISON_DIMENSIONAL_RUNE.get(),
            MineCellsItems.PROMENADE_DIMENSIONAL_RUNE.get(),
            MineCellsItems.RAMPARTS_DIMENSIONAL_RUNE.get(),
            MineCellsItems.INSUFFERABLE_CRYPT_DIMENSIONAL_RUNE.get(),
            MineCellsItems.BLACK_BRIDGE_DIMENSIONAL_RUNE.get(),
            MineCellsBlocks.PRISON_STONE.get(),
            MineCellsBlocks.PRISON_STONE_STAIRS.get(),
            MineCellsBlocks.PRISON_STONE_SLAB.get(),
            MineCellsBlocks.PRISON_STONE_WALL.get(),
            MineCellsBlocks.PRISON_STONE_PRESSURE_PLATE.get(),
            MineCellsBlocks.PRISON_STONE_BUTTON.get(),
            MineCellsBlocks.PRISON_COBBLESTONE.get(),
            MineCellsBlocks.PRISON_COBBLESTONE_STAIRS.get(),
            MineCellsBlocks.PRISON_COBBLESTONE_SLAB.get(),
            MineCellsBlocks.PRISON_COBBLESTONE_WALL.get(),
            MineCellsBlocks.PRISON_BRICKS.get(),
            MineCellsBlocks.PRISON_BRICK_STAIRS.get(),
            MineCellsBlocks.PRISON_BRICK_SLAB.get(),
            MineCellsBlocks.PRISON_BRICK_WALL.get(),
            MineCellsBlocks.CRACKED_PRISON_BRICKS.get(),
            MineCellsBlocks.CRACKED_PRISON_BRICK_STAIRS.get(),
            MineCellsBlocks.CRACKED_PRISON_BRICK_SLAB.get(),
            MineCellsBlocks.CRACKED_PRISON_BRICK_WALL.get(),
            MineCellsBlocks.SMALL_PRISON_BRICKS.get(),
            MineCellsBlocks.SMALL_PRISON_BRICK_STAIRS.get(),
            MineCellsBlocks.SMALL_PRISON_BRICK_SLAB.get(),
            MineCellsBlocks.SMALL_PRISON_BRICK_WALL.get(),
            MineCellsBlocks.WILTED_GRASS_BLOCK.get(),
            MineCellsBlocks.BLOOMROCK.get(),
            MineCellsBlocks.BLOOMROCK_STAIRS.get(),
            MineCellsBlocks.BLOOMROCK_SLAB.get(),
            MineCellsBlocks.BLOOMROCK_WALL.get(),
            MineCellsBlocks.BLOOMROCK_BRICKS.get(),
            MineCellsBlocks.BLOOMROCK_BRICK_STAIRS.get(),
            MineCellsBlocks.BLOOMROCK_BRICK_SLAB.get(),
            MineCellsBlocks.BLOOMROCK_BRICK_WALL.get(),
            MineCellsBlocks.CRACKED_BLOOMROCK_BRICKS.get(),
            MineCellsBlocks.CRACKED_BLOOMROCK_BRICK_STAIRS.get(),
            MineCellsBlocks.CRACKED_BLOOMROCK_BRICK_SLAB.get(),
            MineCellsBlocks.CRACKED_BLOOMROCK_BRICK_WALL.get(),
            MineCellsBlocks.BLOOMROCK_TILES.get(),
            MineCellsBlocks.BLOOMROCK_TILE_STAIRS.get(),
            MineCellsBlocks.BLOOMROCK_TILE_SLAB.get(),
            MineCellsBlocks.BLOOMROCK_TILE_WALL.get(),
            MineCellsBlocks.BLOOMROCK_WILTED_GRASS_BLOCK.get(),
            MineCellsBlocks.PUTRID_PLANKS.get(),
            MineCellsBlocks.PUTRID_LOG.get(),
            MineCellsBlocks.STRIPPED_PUTRID_LOG.get(),
            MineCellsBlocks.PUTRID_WOOD.get(),
            MineCellsBlocks.STRIPPED_PUTRID_WOOD.get(),
            MineCellsBlocks.PUTRID_STAIRS.get(),
            MineCellsBlocks.PUTRID_SLAB.get(),
            MineCellsBlocks.PUTRID_FENCE.get(),
            MineCellsBlocks.PUTRID_FENCE_GATE.get(),
            MineCellsBlocks.PUTRID_DOOR.get(),
            MineCellsBlocks.PUTRID_TRAPDOOR.get(),
            MineCellsBlocks.PUTRID_BUTTON.get(),
            MineCellsBlocks.PUTRID_PRESSURE_PLATE.get(),
            MineCellsBlocks.ARROW_SIGN.get(),
            MineCellsBlocks.PUTRID_BOARDS.get(),
            MineCellsBlocks.PUTRID_BOARD_BLOCK.get(),
            MineCellsBlocks.PUTRID_BOARD_STAIRS.get(),
            MineCellsBlocks.PUTRID_BOARD_SLAB.get(),
            MineCellsBlocks.WILTED_LEAVES.get(),
            MineCellsBlocks.WILTED_WALL_LEAVES.get(),
            MineCellsBlocks.WILTED_HANGING_LEAVES.get(),
            MineCellsBlocks.PUTRID_SAPLING.get(),
            MineCellsBlocks.ORANGE_WILTED_LEAVES.get(),
            MineCellsBlocks.ORANGE_WILTED_WALL_LEAVES.get(),
            MineCellsBlocks.ORANGE_WILTED_HANGING_LEAVES.get(),
            MineCellsBlocks.ORANGE_PUTRID_SAPLING.get(),
            MineCellsBlocks.RED_WILTED_LEAVES.get(),
            MineCellsBlocks.RED_WILTED_WALL_LEAVES.get(),
            MineCellsBlocks.RED_WILTED_HANGING_LEAVES.get(),
            MineCellsBlocks.RED_PUTRID_SAPLING.get(),
            MineCellsBlocks.CRATE.get(),
            MineCellsBlocks.SMALL_CRATE.get(),
            MineCellsBlocks.BRITTLE_BARREL.get(),
            MineCellsBlocks.KING_STATUE.get(),
            MineCellsBlocks.SKELETON.get(),
            MineCellsBlocks.ROTTING_CORPSE.get(),
            MineCellsBlocks.CORPSE.get(),
            MineCellsBlocks.ELEVATOR_ASSEMBLER.get(),
            MineCellsBlocks.CELL_CRAFTER.get(),
            MineCellsBlocks.HARDSTONE.get(),
            MineCellsBlocks.CHAIN_PILE_BLOCK.get(),
            MineCellsBlocks.CHAIN_PILE.get(),
            MineCellsBlocks.BIG_CHAIN.get(),
            MineCellsBlocks.CAGE.get(),
            MineCellsBlocks.BROKEN_CAGE.get(),
            MineCellsBlocks.SPIKES.get(),
            MineCellsBlocks.FLAG_POLE.get()
        );
        MineCellsBlocks.FLAG_BLOCKS.forEach(flag -> output.accept(flag.get()));
        add(output,
            MineCellsBlocks.ALCHEMY_EQUIPMENT_0.get(),
            MineCellsBlocks.ALCHEMY_EQUIPMENT_1.get(),
            MineCellsBlocks.ALCHEMY_EQUIPMENT_2.get(),
            MineCellsBlocks.PRISON_TORCH.get(),
            MineCellsBlocks.PROMENADE_TORCH.get(),
            MineCellsBlocks.RAMPARTS_TORCH.get(),
            MineCellsItems.SEWAGE_BUCKET.get(),
            MineCellsItems.ANCIENT_SEWAGE_BUCKET.get(),
            MineCellsItems.ELEVATOR_MECHANISM.get(),
            MineCellsItems.HEALTH_FLASK.get(),
            MineCellsItems.RESET_RUNE.get(),
            MineCellsItems.CONJUNCTIVIUS_RESPAWN_RUNE.get(),
            MineCellsItems.CONCIERGE_RESPAWN_RUNE.get(),
            MineCellsItems.VINE_RUNE.get(),
            MineCellsItems.MONSTER_CELL.get(),
            MineCellsItems.BOSS_STEM_CELL.get(),
            MineCellsItems.CELL_HOLDER.get(),
            MineCellsItems.GUTS.get(),
            MineCellsItems.MONSTERS_EYE.get(),
            MineCellsItems.EXPLOSIVE_BULB.get(),
            MineCellsItems.INFECTED_FLESH.get(),
            MineCellsItems.CELL_INFUSED_STEEL.get(),
            MineCellsItems.METAL_SHARDS.get(),
            MineCellsItems.BUZZCUTTER_FANG.get(),
            MineCellsItems.MOLTEN_CHUNK.get(),
            MineCellsItems.SEWER_CALAMARI.get(),
            MineCellsItems.COOKED_SEWER_CALAMARI.get(),
            MineCellsItems.TRANSPOSITION_CORE.get(),
            MineCellsItems.BLOOD_BOTTLE.get(),
            MineCellsItems.ARCANE_GOO.get()
        );
    }

    private static void addCombatItems(CreativeModeTab.Output output) {
        add(output,
            MineCellsItems.ASSASSINS_DAGGER.get(),
            MineCellsItems.BLOOD_SWORD.get(),
            MineCellsItems.BROADSWORD.get(),
            MineCellsItems.BALANCED_BLADE.get(),
            MineCellsItems.CROWBAR.get(),
            MineCellsItems.NUTCRACKER.get(),
            MineCellsItems.CURSED_SWORD.get(),
            MineCellsItems.HATTORIS_KATANA.get(),
            MineCellsItems.TENTACLE.get(),
            MineCellsItems.FROST_BLAST.get(),
            MineCellsItems.SPITE_SWORD.get(),
            MineCellsItems.FLINT.get(),
            MineCellsItems.PHASER.get(),
            MineCellsItems.MULTIPLE_NOCKS_BOW.get(),
            MineCellsItems.BOW_AND_ENDLESS_QUIVER.get(),
            MineCellsItems.MARKSMANS_BOW.get(),
            MineCellsItems.INFANTRY_BOW.get(),
            MineCellsItems.QUICK_BOW.get(),
            MineCellsItems.ICE_BOW.get(),
            MineCellsItems.NERVES_OF_STEEL.get(),
            MineCellsItems.ICE_ARROW.get(),
            MineCellsItems.HEAVY_CROSSBOW.get(),
            MineCellsItems.EXPLOSIVE_CROSSBOW.get(),
            MineCellsItems.EXPLOSIVE_BOLT.get(),
            MineCellsItems.ELECTRIC_WHIP.get(),
            MineCellsItems.LIGHTNING_BOLT.get(),
            MineCellsItems.THROWING_KNIFE.get(),
            MineCellsItems.FIREBRANDS.get(),
            MineCellsItems.CUDGEL.get(),
            MineCellsItems.RAMPART.get(),
            MineCellsItems.ASSAULT_SHIELD.get(),
            MineCellsItems.BLOODTHIRSTY_SHIELD.get(),
            MineCellsItems.GREED_SHIELD.get(),
            MineCellsItems.ICE_SHIELD.get()
        );
    }

    private static void addSpawnEggItems(CreativeModeTab.Output output) {
        add(output,
            MineCellsItems.LEAPING_ZOMBIE_SPAWN_EGG.get(),
            MineCellsItems.SHOCKER_SPAWN_EGG.get(),
            MineCellsItems.GRENADIER_SPAWN_EGG.get(),
            MineCellsItems.DISGUSTING_WORM_SPAWN_EGG.get(),
            MineCellsItems.INQUISITOR_SPAWN_EGG.get(),
            MineCellsItems.KAMIKAZE_SPAWN_EGG.get(),
            MineCellsItems.PROTECTOR_SPAWN_EGG.get(),
            MineCellsItems.UNDEAD_ARCHER_SPAWN_EGG.get(),
            MineCellsItems.SHIELDBEARER_SPAWN_EGG.get(),
            MineCellsItems.MUTATED_BAT_SPAWN_EGG.get(),
            MineCellsItems.SEWERS_TENTACLE_SPAWN_EGG.get(),
            MineCellsItems.RANCID_RAT_SPAWN_EGG.get(),
            MineCellsItems.RUNNER_SPAWN_EGG.get(),
            MineCellsItems.SCORPION_SPAWN_EGG.get(),
            MineCellsItems.BUZZCUTTER_SPAWN_EGG.get(),
            MineCellsItems.SWEEPER_SPAWN_EGG.get()
        );
    }

    private static void add(CreativeModeTab.Output output, ItemLike... entries) {
        for (ItemLike entry : entries) {
            output.accept(entry);
        }
    }
}
