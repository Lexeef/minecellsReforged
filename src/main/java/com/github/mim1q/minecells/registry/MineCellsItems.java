package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.item.CellHolderItem;
import com.github.mim1q.minecells.item.DimensionalRuneItem;
import com.github.mim1q.minecells.item.HealthFlaskItem;
import com.github.mim1q.minecells.item.ResetRuneItem;
import com.github.mim1q.minecells.item.weapon.BasicBowWeaponItem;
import com.github.mim1q.minecells.item.weapon.BasicCrossbowWeaponItem;
import com.github.mim1q.minecells.item.weapon.BasicMeleeWeaponItem;
import com.github.mim1q.minecells.item.weapon.BasicShieldWeaponItem;
import com.github.mim1q.minecells.item.weapon.ElectricWhipItem;
import com.github.mim1q.minecells.item.weapon.FirebrandsItem;
import com.github.mim1q.minecells.item.weapon.FrostBlastItem;
import com.github.mim1q.minecells.item.weapon.LightningBoltItem;
import com.github.mim1q.minecells.item.weapon.MultipleNocksBowItem;
import com.github.mim1q.minecells.item.weapon.PhaserItem;
import com.github.mim1q.minecells.item.weapon.QuickBowWeaponItem;
import com.github.mim1q.minecells.item.weapon.ThrowingKnifeItem;
import net.minecraft.resources.ResourceLocation;
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
import org.jetbrains.annotations.Nullable;

public final class MineCellsItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MineCells.MOD_ID);

    public static final RegistryObject<Item> ELEVATOR_MECHANISM = registerSimple("elevator_mechanism");
    public static final RegistryObject<Item> CONJUNCTIVIUS_RESPAWN_RUNE = register("conjunctivius_respawn_rune", new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> CONCIERGE_RESPAWN_RUNE = register("concierge_respawn_rune", new Item.Properties().stacksTo(1));
    public static final RegistryObject<Item> VINE_RUNE = register("vine_rune", new Item.Properties().stacksTo(1).durability(8));
    public static final RegistryObject<Item> RESET_RUNE = ITEMS.register("reset_rune", () -> new ResetRuneItem(new Item.Properties().stacksTo(1)));
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
    public static final RegistryObject<Item> ASSASSINS_DAGGER = ITEMS.register("assassins_dagger", () -> new BasicMeleeWeaponItem(2, -1.6F, new Item.Properties().stacksTo(1).durability(1200)));
    public static final RegistryObject<Item> BLOOD_SWORD = ITEMS.register("blood_sword", () -> new BasicMeleeWeaponItem(4, -2.2F, new Item.Properties().stacksTo(1).durability(1200)));
    public static final RegistryObject<Item> CURSED_SWORD = ITEMS.register("cursed_sword", () -> new BasicMeleeWeaponItem(7, -2.8F, new Item.Properties().stacksTo(1).durability(600)));
    public static final RegistryObject<Item> TENTACLE = ITEMS.register("tentacle", () -> new BasicMeleeWeaponItem(3, -2.0F, new Item.Properties().stacksTo(1).durability(800).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> HATTORIS_KATANA = ITEMS.register("hattoris_katana", () -> new BasicMeleeWeaponItem(3, -1.8F, new Item.Properties().stacksTo(1).durability(1200)));
    public static final RegistryObject<Item> BROADSWORD = ITEMS.register("broadsword", () -> new BasicMeleeWeaponItem(5, -2.6F, new Item.Properties().stacksTo(1).durability(1000)));
    public static final RegistryObject<Item> BALANCED_BLADE = ITEMS.register("balanced_blade", () -> new BasicMeleeWeaponItem(3, -2.0F, new Item.Properties().stacksTo(1).durability(1200)));
    public static final RegistryObject<Item> CROWBAR = ITEMS.register("crowbar", () -> new BasicMeleeWeaponItem(4, -2.3F, new Item.Properties().stacksTo(1).durability(1100)));
    public static final RegistryObject<Item> NUTCRACKER = ITEMS.register("nutcracker", () -> new BasicMeleeWeaponItem(6, -3.0F, new Item.Properties().stacksTo(1).durability(1000)));
    public static final RegistryObject<Item> FROST_BLAST = ITEMS.register("frost_blast", () -> new FrostBlastItem(new Item.Properties().stacksTo(1).durability(32)));
    public static final RegistryObject<Item> FLINT = register("flint", new Item.Properties().stacksTo(1).durability(1000).rarity(Rarity.EPIC));
    public static final RegistryObject<Item> SPITE_SWORD = ITEMS.register("spite_sword", () -> new BasicMeleeWeaponItem(4, -2.1F, new Item.Properties().stacksTo(1).durability(1200)));
    public static final RegistryObject<Item> PHASER = ITEMS.register("phaser", () -> new PhaserItem(new Item.Properties().stacksTo(1).durability(32)));
    public static final RegistryObject<Item> HEALTH_FLASK = ITEMS.register("health_flask", () -> new HealthFlaskItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> MULTIPLE_NOCKS_BOW = ITEMS.register("multiple_nocks_bow", () -> new MultipleNocksBowItem(new Item.Properties().stacksTo(1).durability(500)));
    public static final RegistryObject<Item> BOW_AND_ENDLESS_QUIVER = ITEMS.register("bow_and_endless_quiver", () -> new BasicBowWeaponItem(new Item.Properties().stacksTo(1).durability(400)));
    public static final RegistryObject<Item> MARKSMANS_BOW = ITEMS.register("marksmans_bow", () -> new BasicBowWeaponItem(new Item.Properties().stacksTo(1).durability(450)));
    public static final RegistryObject<Item> INFANTRY_BOW = ITEMS.register("infantry_bow", () -> new BasicBowWeaponItem(new Item.Properties().stacksTo(1).durability(450)));
    public static final RegistryObject<Item> QUICK_BOW = ITEMS.register("quick_bow", () -> new QuickBowWeaponItem(new Item.Properties().stacksTo(1).durability(800)));
    public static final RegistryObject<Item> ICE_BOW = ITEMS.register("ice_bow", () -> new BasicBowWeaponItem(new Item.Properties().stacksTo(1).durability(400)));
    public static final RegistryObject<Item> NERVES_OF_STEEL = ITEMS.register("nerves_of_steel", () -> new BasicBowWeaponItem(new Item.Properties().stacksTo(1).durability(450)));
    public static final RegistryObject<Item> HEAVY_CROSSBOW = ITEMS.register("heavy_crossbow", () -> new BasicCrossbowWeaponItem(new Item.Properties().stacksTo(1).durability(600).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> EXPLOSIVE_CROSSBOW = ITEMS.register("explosive_crossbow", () -> new BasicCrossbowWeaponItem(new Item.Properties().stacksTo(1).durability(500)));
    public static final RegistryObject<Item> CUDGEL = ITEMS.register("cudgel", () -> new BasicShieldWeaponItem(new Item.Properties().stacksTo(1).durability(500)));
    public static final RegistryObject<Item> RAMPART = ITEMS.register("rampart", () -> new BasicShieldWeaponItem(new Item.Properties().stacksTo(1).durability(400)));
    public static final RegistryObject<Item> ASSAULT_SHIELD = ITEMS.register("assault_shield", () -> new BasicShieldWeaponItem(new Item.Properties().stacksTo(1).durability(600)));
    public static final RegistryObject<Item> BLOODTHIRSTY_SHIELD = ITEMS.register("bloodthirsty_shield", () -> new BasicShieldWeaponItem(new Item.Properties().stacksTo(1).durability(500)));
    public static final RegistryObject<Item> GREED_SHIELD = ITEMS.register("greed_shield", () -> new BasicShieldWeaponItem(new Item.Properties().stacksTo(1).durability(300)));
    public static final RegistryObject<Item> ICE_SHIELD = ITEMS.register("ice_shield", () -> new BasicShieldWeaponItem(new Item.Properties().stacksTo(1).durability(360)));
    public static final RegistryObject<Item> ELECTRIC_WHIP = ITEMS.register("electric_whip", () -> new ElectricWhipItem(new Item.Properties().stacksTo(1).durability(450)));
    public static final RegistryObject<Item> LIGHTNING_BOLT = ITEMS.register("lightning_bolt", () -> new LightningBoltItem(new Item.Properties().stacksTo(1).durability(600)));
    public static final RegistryObject<Item> THROWING_KNIFE = ITEMS.register("throwing_knife", () -> new ThrowingKnifeItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> FIREBRANDS = ITEMS.register("firebrands", () -> new FirebrandsItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> PRISON_DIMENSIONAL_RUNE = ITEMS.register("prison_dimensional_rune", () -> new DimensionalRuneItem(new Item.Properties().stacksTo(1), MineCellsBlocks.PRISON_DOORWAY.get()));
    public static final RegistryObject<Item> PROMENADE_DIMENSIONAL_RUNE = ITEMS.register("promenade_dimensional_rune", () -> new DimensionalRuneItem(new Item.Properties().stacksTo(1), MineCellsBlocks.PROMENADE_DOORWAY.get()));
    public static final RegistryObject<Item> RAMPARTS_DIMENSIONAL_RUNE = ITEMS.register("ramparts_dimensional_rune", () -> new DimensionalRuneItem(new Item.Properties().stacksTo(1), MineCellsBlocks.RAMPARTS_DOORWAY.get()));
    public static final RegistryObject<Item> INSUFFERABLE_CRYPT_DIMENSIONAL_RUNE = ITEMS.register("insufferable_crypt_dimensional_rune", () -> new DimensionalRuneItem(new Item.Properties().stacksTo(1), MineCellsBlocks.INSUFFERABLE_CRYPT_DOORWAY.get()));
    public static final RegistryObject<Item> BLACK_BRIDGE_DIMENSIONAL_RUNE = ITEMS.register("black_bridge_dimensional_rune", () -> new DimensionalRuneItem(new Item.Properties().stacksTo(1), MineCellsBlocks.BLACK_BRIDGE_DOORWAY.get()));
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

    @Nullable
    public static Item getDimensionalRune(ResourceLocation dimensionId) {
        if (dimensionId.equals(MineCellsBlocks.PRISON_DOORWAY.get().getType().dimensionId())) {
            return PRISON_DIMENSIONAL_RUNE.get();
        }
        if (dimensionId.equals(MineCellsBlocks.PROMENADE_DOORWAY.get().getType().dimensionId())) {
            return PROMENADE_DIMENSIONAL_RUNE.get();
        }
        if (dimensionId.equals(MineCellsBlocks.INSUFFERABLE_CRYPT_DOORWAY.get().getType().dimensionId())) {
            return INSUFFERABLE_CRYPT_DIMENSIONAL_RUNE.get();
        }
        if (dimensionId.equals(MineCellsBlocks.RAMPARTS_DOORWAY.get().getType().dimensionId())) {
            return RAMPARTS_DIMENSIONAL_RUNE.get();
        }
        if (dimensionId.equals(MineCellsBlocks.BLACK_BRIDGE_DOORWAY.get().getType().dimensionId())) {
            return BLACK_BRIDGE_DIMENSIONAL_RUNE.get();
        }
        return null;
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
