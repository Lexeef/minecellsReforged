package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public final class MineCellsCreativeTabs {
    public static final ResourceLocation BACKGROUND = MineCells.id("textures/gui/group.png");
    public static final ResourceLocation TABS_IMAGE = MineCells.id("textures/gui/creative_tabs.png");
    public static final ResourceLocation BUTTONS = MineCells.id("textures/gui/tabs.png");
    public static final int TAB_TITLE_COLOR = 0x46D4FF;

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MineCells.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MINECELLS = CREATIVE_TABS.register("minecells", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.minecells.minecells").withStyle(style -> style.withColor(TAB_TITLE_COLOR)))
        .icon(() -> new ItemStack(MineCellsItems.MONSTER_CELL.get()))
        .withBackgroundLocation(BACKGROUND)
        .withTabsImage(TABS_IMAGE)
        .hideTitle()
        .displayItems(MineCellsCreativeTabs::displayItems)
        .build());

    public static final List<SubTab> SUB_TABS = List.of(
        new SubTab("general", () -> new ItemStack(MineCellsBlocks.WILTED_WALL_LEAVES.get())),
        new SubTab("combat", () -> new ItemStack(MineCellsItems.BLOOD_SWORD.get())),
        new SubTab("spawn_eggs", () -> new ItemStack(MineCellsItems.GRENADIER_SPAWN_EGG.get()))
    );

    private static final boolean[] ACTIVE_SUB_TABS = {true, true, true};

    private static final List<String> GENERAL_ORDER = List.of(
        "prison_doorway", "promenade_doorway", "ramparts_doorway", "insufferable_crypt_doorway", "black_bridge_doorway",
        "prison_stone", "prison_stone_stairs", "prison_stone_slab", "prison_stone_wall", "prison_stone_pressure_plate", "prison_stone_button",
        "prison_cobblestone", "prison_cobblestone_stairs", "prison_cobblestone_slab", "prison_cobblestone_wall",
        "prison_bricks", "prison_brick_stairs", "prison_brick_slab", "prison_brick_wall",
        "cracked_prison_bricks", "cracked_prison_brick_stairs", "cracked_prison_brick_slab", "cracked_prison_brick_wall",
        "small_prison_bricks", "small_prison_brick_stairs", "small_prison_brick_slab", "small_prison_brick_wall",
        "wilted_grass_block",
        "bloomrock", "bloomrock_stairs", "bloomrock_slab", "bloomrock_wall",
        "bloomrock_bricks", "bloomrock_brick_stairs", "bloomrock_brick_slab", "bloomrock_brick_wall",
        "cracked_bloomrock_bricks", "cracked_bloomrock_brick_stairs", "cracked_bloomrock_brick_slab", "cracked_bloomrock_brick_wall",
        "bloomrock_tiles", "bloomrock_tile_stairs", "bloomrock_tile_slab", "bloomrock_tile_wall",
        "bloomrock_wilted_grass_block",
        "septite", "septite_stairs", "septite_slab", "septite_wall",
        "cobbled_septite", "cobbled_septite_stairs", "cobbled_septite_slab", "cobbled_septite_wall",
        "polished_septite", "polished_septite_stairs", "polished_septite_slab", "polished_septite_wall",
        "septite_bricks", "septite_brick_stairs", "septite_brick_slab", "septite_brick_wall",
        "small_septite_bricks", "small_septite_brick_stairs", "small_septite_brick_slab", "small_septite_brick_wall",
        "ancient_septite", "ancient_septite_stairs", "ancient_septite_slab", "ancient_septite_wall",
        "cobbled_ancient_septite", "cobbled_ancient_septite_stairs", "cobbled_ancient_septite_slab", "cobbled_ancient_septite_wall",
        "polished_ancient_septite", "polished_ancient_septite_stairs", "polished_ancient_septite_slab", "polished_ancient_septite_wall",
        "ancient_septite_bricks", "ancient_septite_brick_stairs", "ancient_septite_brick_slab", "ancient_septite_brick_wall",
        "small_ancient_septite_bricks", "small_ancient_septite_brick_stairs", "small_ancient_septite_brick_slab", "small_ancient_septite_brick_wall",
        "putrid_planks", "putrid_log", "stripped_putrid_log", "putrid_wood", "stripped_putrid_wood", "putrid_stairs", "putrid_slab",
        "putrid_door", "putrid_trapdoor", "putrid_fence", "putrid_fence_gate", "putrid_button", "putrid_pressure_plate", "putrid_sign",
        "arrow_sign",
        "putrid_boards",
        "putrid_board_block", "putrid_board_stairs", "putrid_board_slab",
        "wilted_leaves", "wilted_wall_leaves", "wilted_hanging_leaves", "putrid_sapling",
        "orange_wilted_leaves", "orange_wilted_wall_leaves", "orange_wilted_hanging_leaves", "orange_putrid_sapling",
        "red_wilted_leaves", "red_wilted_wall_leaves", "red_wilted_hanging_leaves", "red_putrid_sapling",
        "crate", "small_crate", "brittle_barrel", "king_statue", "skeleton", "rotting_corpse", "corpse",
        "elevator_assembler", "cell_crafter", "hardstone", "chain_pile_block", "chain_pile", "big_chain",
        "cage", "broken_cage", "spikes", "flag_pole"
    );

    private static final List<String> GENERAL_ORDER_AFTER_FLAGS = List.of(
        "alchemy_equipment_0", "alchemy_equipment_1", "alchemy_equipment_2",
        "prison_torch", "promenade_torch", "ramparts_torch",
        "sewage_bucket", "ancient_sewage_bucket", "elevator_mechanism", "health_flask",
        "conjunctivius_respawn_rune", "concierge_respawn_rune", "vine_rune",
        "monster_cell", "boss_stem_cell", "cell_holder", "guts", "monsters_eye", "explosive_bulb", "infected_flesh",
        "cell_infused_steel", "metal_shards", "buzzcutter_fang", "molten_chunk", "sewer_calamari", "cooked_sewer_calamari",
        "transposition_core", "blood_bottle", "arcane_goo"
    );

    private static final List<String> COMBAT_ORDER = List.of(
        "assassins_dagger", "blood_sword", "broadsword", "balanced_blade", "crowbar", "nutcracker", "cursed_sword",
        "hattoris_katana", "tentacle", "frost_blast", "spite_sword", "flint", "phaser",
        "multiple_nocks_bow", "bow_and_endless_quiver", "marksmans_bow", "infantry_bow", "quick_bow", "ice_bow", "nerves_of_steel",
        "ice_arrow",
        "heavy_crossbow", "explosive_crossbow",
        "explosive_bolt",
        "electric_whip", "lightning_bolt", "throwing_knife", "firebrands",
        "cudgel", "rampart", "assault_shield", "bloodthirsty_shield", "greed_shield", "ice_shield"
    );

    private static final List<String> SPAWN_EGG_ORDER = List.of(
        "leaping_zombie_spawn_egg", "shocker_spawn_egg", "grenadier_spawn_egg", "disgusting_worm_spawn_egg",
        "inquisitor_spawn_egg", "kamikaze_spawn_egg", "protector_spawn_egg", "undead_archer_spawn_egg",
        "shieldbearer_spawn_egg", "mutated_bat_spawn_egg", "sewers_tentacle_spawn_egg", "rancid_rat_spawn_egg",
        "runner_spawn_egg", "scorpion_spawn_egg", "buzzcutter_spawn_egg", "sweeper_spawn_egg"
    );

    private MineCellsCreativeTabs() {
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }

    public static boolean isSubTabActive(int index) {
        return ACTIVE_SUB_TABS[index];
    }

    public static void selectSingleSubTab(int index) {
        for (int i = 0; i < ACTIVE_SUB_TABS.length; i++) {
            ACTIVE_SUB_TABS[i] = i == index;
        }
    }

    public static void toggleSubTab(int index) {
        if (!ACTIVE_SUB_TABS[index]) {
            ACTIVE_SUB_TABS[index] = true;
            return;
        }
        ACTIVE_SUB_TABS[index] = false;
        for (boolean active : ACTIVE_SUB_TABS) {
            if (active) {
                return;
            }
        }
        for (int i = 0; i < ACTIVE_SUB_TABS.length; i++) {
            ACTIVE_SUB_TABS[i] = true;
        }
    }

    public static Component currentTitle() {
        int single = -1;
        for (int i = 0; i < ACTIVE_SUB_TABS.length; i++) {
            if (!ACTIVE_SUB_TABS[i]) {
                continue;
            }
            if (single != -1) {
                return MINECELLS.get().getDisplayName();
            }
            single = i;
        }
        return single == -1 ? MINECELLS.get().getDisplayName() : SUB_TABS.get(single).title();
    }

    private static void displayItems(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        List<List<Item>> contents = subTabContents();
        for (int i = 0; i < contents.size(); i++) {
            CreativeModeTab.TabVisibility visibility = ACTIVE_SUB_TABS[i]
                ? CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
                : CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY;
            for (Item item : contents.get(i)) {
                output.accept(new ItemStack(item), visibility);
            }
        }
    }

    private static List<List<Item>> subTabContents() {
        Set<Item> used = new LinkedHashSet<>();
        List<Item> general = new ArrayList<>();
        List<Item> combat = new ArrayList<>();
        List<Item> spawnEggs = new ArrayList<>();

        addById(GENERAL_ORDER, general, used);
        for (var flag : MineCellsBlocks.FLAG_BLOCKS) {
            addItem(flag.get().asItem(), general, used);
        }
        addById(GENERAL_ORDER_AFTER_FLAGS, general, used);
        addById(COMBAT_ORDER, combat, used);
        addById(SPAWN_EGG_ORDER, spawnEggs, used);
        return List.of(general, combat, spawnEggs);
    }

    private static void addById(List<String> ids, List<Item> target, Set<Item> used) {
        for (String id : ids) {
            Item item = ForgeRegistries.ITEMS.getValue(MineCells.id(id));
            if (item != null && item != Items.AIR) {
                addItem(item, target, used);
            }
        }
    }

    private static void addItem(Item item, List<Item> target, Set<Item> used) {
        if (used.add(item)) {
            target.add(item);
        }
    }

    public record SubTab(String name, Supplier<ItemStack> icon) {
        public Component title() {
            return Component.translatable("itemGroup.minecells.minecells.tab." + name)
                .withStyle(style -> style.withColor(TAB_TITLE_COLOR));
        }
    }
}
