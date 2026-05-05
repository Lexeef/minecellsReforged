package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.item.CellHolderItem;
import com.github.mim1q.minecells.item.HealthFlaskItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class MineCellsItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MineCells.MOD_ID);

    public static final RegistryObject<Item> ELEVATOR_MECHANISM = registerSimple("elevator_mechanism");
    public static final RegistryObject<Item> CONJUNCTIVIUS_RESPAWN_RUNE = register("conjunctivius_respawn_rune", new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> CONCIERGE_RESPAWN_RUNE = register("concierge_respawn_rune", new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> VINE_RUNE = register("vine_rune", new Item.Properties().stacksTo(1).durability(8));
    public static final RegistryObject<Item> RESET_RUNE = ITEMS.register("reset_rune", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SEWAGE_BUCKET = ITEMS.register("sewage_bucket", () -> new BucketItem(MineCellsFluids.STILL_SEWAGE, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));
    public static final RegistryObject<Item> ANCIENT_SEWAGE_BUCKET = ITEMS.register("ancient_sewage_bucket", () -> new BucketItem(MineCellsFluids.STILL_ANCIENT_SEWAGE, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));
    public static final RegistryObject<Item> CELL_HOLDER = ITEMS.register("cell_holder", () -> new CellHolderItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GUTS = register("guts", new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).meat().build()));
    public static final RegistryObject<Item> MONSTERS_EYE = register("monsters_eye", new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(0.8F).meat().build()));
    public static final RegistryObject<Item> MONSTER_CELL = registerSimple("monster_cell");
    public static final RegistryObject<Item> BOSS_STEM_CELL = registerSimple("boss_stem_cell");
    public static final RegistryObject<Item> EXPLOSIVE_BULB = registerSimple("explosive_bulb");
    public static final RegistryObject<Item> INFECTED_FLESH = register("infected_flesh", new Item.Properties().food(
        new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.3F)
            .meat()
            .effect(new MobEffectInstance(MobEffects.POISON, 100, 0), 1)
            .build()
    ));
    public static final RegistryObject<Item> CELL_INFUSED_STEEL = registerSimple("cell_infused_steel");
    public static final RegistryObject<Item> METAL_SHARDS = registerSimple("metal_shards");
    public static final RegistryObject<Item> BUZZCUTTER_FANG = registerSimple("buzzcutter_fang");
    public static final RegistryObject<Item> MOLTEN_CHUNK = registerSimple("molten_chunk");
    public static final RegistryObject<Item> SEWER_CALAMARI = register("sewer_calamari", new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).meat().build()));
    public static final RegistryObject<Item> COOKED_SEWER_CALAMARI = register("cooked_sewer_calamari", new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(0.8F).meat().build()));
    public static final RegistryObject<Item> TRANSPOSITION_CORE = registerSimple("transposition_core");
    public static final RegistryObject<Item> BLOOD_BOTTLE = registerSimple("blood_bottle");
    public static final RegistryObject<Item> ARCANE_GOO = registerSimple("arcane_goo");
    public static final RegistryObject<Item> ICE_ARROW = registerSimple("ice_arrow");
    public static final RegistryObject<Item> EXPLOSIVE_BOLT = registerSimple("explosive_bolt");
    public static final RegistryObject<Item> ASSASSINS_DAGGER = registerDurable("assassins_dagger", 1200);
    public static final RegistryObject<Item> BLOOD_SWORD = registerDurable("blood_sword", 1200);
    public static final RegistryObject<Item> CURSED_SWORD = registerDurable("cursed_sword", 600);
    public static final RegistryObject<Item> TENTACLE = register("tentacle", new Item.Properties().stacksTo(1).durability(800).rarity(Rarity.EPIC));
    public static final RegistryObject<Item> HATTORIS_KATANA = registerDurable("hattoris_katana", 1200);
    public static final RegistryObject<Item> BROADSWORD = registerDurable("broadsword", 1000);
    public static final RegistryObject<Item> BALANCED_BLADE = registerDurable("balanced_blade", 1200);
    public static final RegistryObject<Item> CROWBAR = registerDurable("crowbar", 1100);
    public static final RegistryObject<Item> NUTCRACKER = registerDurable("nutcracker", 1000);
    public static final RegistryObject<Item> FROST_BLAST = registerDurable("frost_blast", 32);
    public static final RegistryObject<Item> FLINT = register("flint", new Item.Properties().stacksTo(1).durability(1000).rarity(Rarity.EPIC));
    public static final RegistryObject<Item> SPITE_SWORD = registerDurable("spite_sword", 1200);
    public static final RegistryObject<Item> PHASER = registerDurable("phaser", 32);
    public static final RegistryObject<Item> HEALTH_FLASK = ITEMS.register("health_flask", () -> new HealthFlaskItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> MULTIPLE_NOCKS_BOW = registerDurable("multiple_nocks_bow", 500);
    public static final RegistryObject<Item> BOW_AND_ENDLESS_QUIVER = registerDurable("bow_and_endless_quiver", 400);
    public static final RegistryObject<Item> MARKSMANS_BOW = registerDurable("marksmans_bow", 450);
    public static final RegistryObject<Item> INFANTRY_BOW = registerDurable("infantry_bow", 450);
    public static final RegistryObject<Item> QUICK_BOW = registerDurable("quick_bow", 800);
    public static final RegistryObject<Item> ICE_BOW = registerDurable("ice_bow", 400);
    public static final RegistryObject<Item> NERVES_OF_STEEL = registerDurable("nerves_of_steel", 450);
    public static final RegistryObject<Item> HEAVY_CROSSBOW = register("heavy_crossbow", new Item.Properties().stacksTo(1).durability(600).rarity(Rarity.EPIC));
    public static final RegistryObject<Item> EXPLOSIVE_CROSSBOW = registerDurable("explosive_crossbow", 500);
    public static final RegistryObject<Item> CUDGEL = registerDurable("cudgel", 500);
    public static final RegistryObject<Item> RAMPART = registerDurable("rampart", 400);
    public static final RegistryObject<Item> ASSAULT_SHIELD = registerDurable("assault_shield", 600);
    public static final RegistryObject<Item> BLOODTHIRSTY_SHIELD = registerDurable("bloodthirsty_shield", 500);
    public static final RegistryObject<Item> GREED_SHIELD = registerDurable("greed_shield", 300);
    public static final RegistryObject<Item> ICE_SHIELD = registerDurable("ice_shield", 360);
    public static final RegistryObject<Item> ELECTRIC_WHIP = registerDurable("electric_whip", 450);
    public static final RegistryObject<Item> LIGHTNING_BOLT = registerDurable("lightning_bolt", 600);
    public static final RegistryObject<Item> THROWING_KNIFE = registerSimple("throwing_knife");
    public static final RegistryObject<Item> FIREBRANDS = registerSimple("firebrands");
    public static final RegistryObject<Item> PRISON_DIMENSIONAL_RUNE = registerSimple("prison_dimensional_rune");
    public static final RegistryObject<Item> PROMENADE_DIMENSIONAL_RUNE = registerSimple("promenade_dimensional_rune");
    public static final RegistryObject<Item> RAMPARTS_DIMENSIONAL_RUNE = registerSimple("ramparts_dimensional_rune");
    public static final RegistryObject<Item> INSUFFERABLE_CRYPT_DIMENSIONAL_RUNE = registerSimple("insufferable_crypt_dimensional_rune");
    public static final RegistryObject<Item> BLACK_BRIDGE_DIMENSIONAL_RUNE = registerSimple("black_bridge_dimensional_rune");
    public static final RegistryObject<ForgeSpawnEggItem> LEAPING_ZOMBIE_SPAWN_EGG = registerSpawnEgg("leaping_zombie_spawn_egg", MineCellsEntities.LEAPING_ZOMBIE, 0x5B7B53, 0x8DBB4E);
    public static final RegistryObject<ForgeSpawnEggItem> SHOCKER_SPAWN_EGG = registerSpawnEgg("shocker_spawn_egg", MineCellsEntities.SHOCKER, 0x2B5369, 0x5FBED1);
    public static final RegistryObject<ForgeSpawnEggItem> GRENADIER_SPAWN_EGG = registerSpawnEgg("grenadier_spawn_egg", MineCellsEntities.GRENADIER, 0x8B3D56, 0xDB7CDB);
    public static final RegistryObject<ForgeSpawnEggItem> DISGUSTING_WORM_SPAWN_EGG = registerSpawnEgg("disgusting_worm_spawn_egg", MineCellsEntities.DISGUSTING_WORM, 0x67DFCF, 0xFF44C6);
    public static final RegistryObject<ForgeSpawnEggItem> INQUISITOR_SPAWN_EGG = registerSpawnEgg("inquisitor_spawn_egg", MineCellsEntities.INQUISITOR, 0xFFFFFF, 0xE52806);
    public static final RegistryObject<ForgeSpawnEggItem> KAMIKAZE_SPAWN_EGG = registerSpawnEgg("kamikaze_spawn_egg", MineCellsEntities.KAMIKAZE, 0x0A6F47, 0x15FF4E);
    public static final RegistryObject<ForgeSpawnEggItem> PROTECTOR_SPAWN_EGG = registerSpawnEgg("protector_spawn_egg", MineCellsEntities.PROTECTOR, 0xC0861D, 0x5FBED1);
    public static final RegistryObject<ForgeSpawnEggItem> UNDEAD_ARCHER_SPAWN_EGG = registerSpawnEgg("undead_archer_spawn_egg", MineCellsEntities.UNDEAD_ARCHER, 0x4C854A, 0x755240);
    public static final RegistryObject<ForgeSpawnEggItem> SHIELDBEARER_SPAWN_EGG = registerSpawnEgg("shieldbearer_spawn_egg", MineCellsEntities.SHIELDBEARER, 0x8459AA, 0xA2A9B6);
    public static final RegistryObject<ForgeSpawnEggItem> MUTATED_BAT_SPAWN_EGG = registerSpawnEgg("mutated_bat_spawn_egg", MineCellsEntities.MUTATED_BAT, 0xD279D2, 0xD33D3D);
    public static final RegistryObject<ForgeSpawnEggItem> SEWERS_TENTACLE_SPAWN_EGG = registerSpawnEgg("sewers_tentacle_spawn_egg", MineCellsEntities.SEWERS_TENTACLE, 0x3983B9, 0xFFF0C6);
    public static final RegistryObject<ForgeSpawnEggItem> RANCID_RAT_SPAWN_EGG = registerSpawnEgg("rancid_rat_spawn_egg", MineCellsEntities.RANCID_RAT, 0x68607C, 0xF17E5D);
    public static final RegistryObject<ForgeSpawnEggItem> RUNNER_SPAWN_EGG = registerSpawnEgg("runner_spawn_egg", MineCellsEntities.RUNNER, 0xE43E2C, 0xF9F9F9);
    public static final RegistryObject<ForgeSpawnEggItem> SCORPION_SPAWN_EGG = registerSpawnEgg("scorpion_spawn_egg", MineCellsEntities.SCORPION, 0x6DBCD5, 0x4B3A5B);
    public static final RegistryObject<ForgeSpawnEggItem> BUZZCUTTER_SPAWN_EGG = registerSpawnEgg("buzzcutter_spawn_egg", MineCellsEntities.BUZZCUTTER, 0xEB1F51, 0xFFCC00);
    public static final RegistryObject<ForgeSpawnEggItem> SWEEPER_SPAWN_EGG = registerSpawnEgg("sweeper_spawn_egg", MineCellsEntities.SWEEPER, 0x5C73BF, 0xFFCC00);

    private MineCellsItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    private static RegistryObject<Item> registerSimple(String name) {
        return register(name, new Item.Properties());
    }

    private static RegistryObject<Item> registerDurable(String name, int durability) {
        return register(name, new Item.Properties().stacksTo(1).durability(durability));
    }

    private static RegistryObject<ForgeSpawnEggItem> registerSpawnEgg(
        String name,
        net.minecraftforge.registries.RegistryObject<? extends net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.Mob>> entityType,
        int primaryColor,
        int secondaryColor
    ) {
        return ITEMS.register(name, () -> new ForgeSpawnEggItem(entityType, primaryColor, secondaryColor, new Item.Properties()));
    }

    private static RegistryObject<Item> register(String name, Item.Properties properties) {
        return ITEMS.register(name, () -> new Item(properties));
    }
}
