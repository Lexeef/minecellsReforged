package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.item.CellHolderItem;
import com.github.mim1q.minecells.item.DimensionalRuneItem;
import com.github.mim1q.minecells.item.HealthFlaskItem;
import com.github.mim1q.minecells.item.ResetRuneItem;
import com.github.mim1q.minecells.item.weapon.AssassinsDaggerItem;
import com.github.mim1q.minecells.item.weapon.BalancedBladeItem;
import com.github.mim1q.minecells.item.weapon.BloodSwordItem;
import com.github.mim1q.minecells.item.weapon.bow.CustomArrowType;
import com.github.mim1q.minecells.item.weapon.bow.CustomBowItem;
import com.github.mim1q.minecells.item.weapon.bow.CustomCrossbowItem;
import com.github.mim1q.minecells.item.weapon.bow.HeavyCrossbowItem;
import com.github.mim1q.minecells.item.weapon.bow.MultipleNocksBowItem;
import com.github.mim1q.minecells.item.weapon.bow.NervesOfSteelItem;
import com.github.mim1q.minecells.item.weapon.bow.QuickBowItem;
import com.github.mim1q.minecells.item.weapon.bow.SingleUseProjectileItem;
import com.github.mim1q.minecells.item.weapon.CrowbarItem;
import com.github.mim1q.minecells.item.weapon.CursedSwordItem;
import com.github.mim1q.minecells.item.weapon.CustomMeleeWeaponItem;
import com.github.mim1q.minecells.item.weapon.ElectricWhipItem;
import com.github.mim1q.minecells.item.weapon.FlintItem;
import com.github.mim1q.minecells.item.weapon.FrostBlastItem;
import com.github.mim1q.minecells.item.weapon.HattorisKatanaItem;
import com.github.mim1q.minecells.item.weapon.LightningBoltItem;
import com.github.mim1q.minecells.item.weapon.NutcrackerItem;
import com.github.mim1q.minecells.item.weapon.PhaserItem;
import com.github.mim1q.minecells.item.weapon.shield.CustomShieldItem;
import com.github.mim1q.minecells.item.weapon.shield.CustomShieldType;
import com.github.mim1q.minecells.item.weapon.SpiteSwordItem;
import com.github.mim1q.minecells.item.weapon.TentacleItem;
import com.github.mim1q.minecells.MineCells;

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
    public static final RegistryObject<Item> ASSASSINS_DAGGER = ITEMS.register("assassins_dagger", () -> new AssassinsDaggerItem(new Item.Properties().stacksTo(1).durability(1200)));
    public static final RegistryObject<Item> BLOOD_SWORD = ITEMS.register("blood_sword", () -> new BloodSwordItem(new Item.Properties().stacksTo(1).durability(1200)));
    public static final RegistryObject<Item> CURSED_SWORD = ITEMS.register("cursed_sword", () -> new CursedSwordItem(new Item.Properties().stacksTo(1).durability(600)));
    public static final RegistryObject<Item> TENTACLE = ITEMS.register("tentacle", () -> new TentacleItem(new Item.Properties().stacksTo(1).durability(800).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> HATTORIS_KATANA = ITEMS.register("hattoris_katana", () -> new HattorisKatanaItem(new Item.Properties().stacksTo(1).durability(1200)));
    public static final RegistryObject<Item> BROADSWORD = ITEMS.register("broadsword", () -> new CustomMeleeWeaponItem("broadsword", 10.0D, 1.1D, new Item.Properties().stacksTo(1).durability(1000)));
    public static final RegistryObject<Item> BALANCED_BLADE = ITEMS.register("balanced_blade", () -> new BalancedBladeItem(new Item.Properties().stacksTo(1).durability(1200)));
    public static final RegistryObject<Item> CROWBAR = ITEMS.register("crowbar", () -> new CrowbarItem(new Item.Properties().stacksTo(1).durability(1100)));
    public static final RegistryObject<Item> NUTCRACKER = ITEMS.register("nutcracker", () -> new NutcrackerItem(new Item.Properties().stacksTo(1).durability(1000)));
    public static final RegistryObject<Item> FROST_BLAST = ITEMS.register("frost_blast", () -> new FrostBlastItem(new Item.Properties().stacksTo(1).durability(32)));
    public static final RegistryObject<Item> FLINT = ITEMS.register("flint", () -> new FlintItem(new Item.Properties().stacksTo(1).durability(1000).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> SPITE_SWORD = ITEMS.register("spite_sword", () -> new SpiteSwordItem(new Item.Properties().stacksTo(1).durability(1200)));
    public static final RegistryObject<Item> PHASER = ITEMS.register("phaser", () -> new PhaserItem(new Item.Properties().stacksTo(1).durability(32)));
    public static final RegistryObject<Item> HEALTH_FLASK = ITEMS.register("health_flask", () -> new HealthFlaskItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> MULTIPLE_NOCKS_BOW = ITEMS.register("multiple_nocks_bow", () -> new MultipleNocksBowItem(new Item.Properties().stacksTo(1).durability(500)));
    public static final RegistryObject<Item> BOW_AND_ENDLESS_QUIVER = ITEMS.register("bow_and_endless_quiver", () -> new CustomBowItem(new Item.Properties().stacksTo(1).durability(400), CustomArrowType.ENDLESS));
    public static final RegistryObject<Item> MARKSMANS_BOW = ITEMS.register("marksmans_bow", () -> new CustomBowItem(new Item.Properties().stacksTo(1).durability(450), CustomArrowType.MARKSMAN));
    public static final RegistryObject<Item> INFANTRY_BOW = ITEMS.register("infantry_bow", () -> new CustomBowItem(new Item.Properties().stacksTo(1).durability(450), CustomArrowType.INFANTRY));
    public static final RegistryObject<Item> QUICK_BOW = ITEMS.register("quick_bow", () -> new QuickBowItem(new Item.Properties().stacksTo(1).durability(800)));
    public static final RegistryObject<Item> ICE_BOW = ITEMS.register("ice_bow", () -> new CustomBowItem(new Item.Properties().stacksTo(1).durability(400), CustomArrowType.ICE));
    public static final RegistryObject<Item> NERVES_OF_STEEL = ITEMS.register("nerves_of_steel", () -> new NervesOfSteelItem(new Item.Properties().stacksTo(1).durability(450)));
    public static final RegistryObject<Item> HEAVY_CROSSBOW = ITEMS.register("heavy_crossbow", () -> new HeavyCrossbowItem(new Item.Properties().stacksTo(1).durability(600).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> EXPLOSIVE_CROSSBOW = ITEMS.register("explosive_crossbow", () -> new CustomCrossbowItem(new Item.Properties().stacksTo(1).durability(500), CustomArrowType.EXPLOSIVE_BOLT));
    public static final RegistryObject<Item> CUDGEL = ITEMS.register("cudgel", () -> new CustomShieldItem(new Item.Properties().stacksTo(1).durability(500), CustomShieldType.CUDGEL));
    public static final RegistryObject<Item> RAMPART = ITEMS.register("rampart", () -> new CustomShieldItem(new Item.Properties().stacksTo(1).durability(400), CustomShieldType.RAMPART));
    public static final RegistryObject<Item> ASSAULT_SHIELD = ITEMS.register("assault_shield", () -> new CustomShieldItem(new Item.Properties().stacksTo(1).durability(600), CustomShieldType.ASSAULT));
    public static final RegistryObject<Item> BLOODTHIRSTY_SHIELD = ITEMS.register("bloodthirsty_shield", () -> new CustomShieldItem(new Item.Properties().stacksTo(1).durability(500), CustomShieldType.BLOOD));
    public static final RegistryObject<Item> GREED_SHIELD = ITEMS.register("greed_shield", () -> new CustomShieldItem(new Item.Properties().stacksTo(1).durability(300), CustomShieldType.GREED));
    public static final RegistryObject<Item> ICE_SHIELD = ITEMS.register("ice_shield", () -> new CustomShieldItem(new Item.Properties().stacksTo(1).durability(360), CustomShieldType.ICE));
    public static final RegistryObject<Item> ELECTRIC_WHIP = ITEMS.register("electric_whip", () -> new ElectricWhipItem(new Item.Properties().stacksTo(1).durability(450)));
    public static final RegistryObject<Item> LIGHTNING_BOLT = ITEMS.register("lightning_bolt", () -> new LightningBoltItem(new Item.Properties().stacksTo(1).durability(600)));
    public static final RegistryObject<Item> THROWING_KNIFE = ITEMS.register("throwing_knife", () -> new SingleUseProjectileItem(new Item.Properties(), CustomArrowType.THROWING_KNIFE));
    public static final RegistryObject<Item> FIREBRANDS = ITEMS.register("firebrands", () -> new SingleUseProjectileItem(new Item.Properties(), CustomArrowType.FIREBRANDS));
    public static final RegistryObject<Item> PRISON_DIMENSIONAL_RUNE = ITEMS.register("prison_dimensional_rune", () -> new DimensionalRuneItem(new Item.Properties().stacksTo(1), MineCellsBlocks.PRISON_DOORWAY.get()));
    public static final RegistryObject<Item> PROMENADE_DIMENSIONAL_RUNE = ITEMS.register("promenade_dimensional_rune", () -> new DimensionalRuneItem(new Item.Properties().stacksTo(1), MineCellsBlocks.PROMENADE_DOORWAY.get()));
    public static final RegistryObject<Item> RAMPARTS_DIMENSIONAL_RUNE = ITEMS.register("ramparts_dimensional_rune", () -> new DimensionalRuneItem(new Item.Properties().stacksTo(1), MineCellsBlocks.RAMPARTS_DOORWAY.get()));
    public static final RegistryObject<Item> INSUFFERABLE_CRYPT_DIMENSIONAL_RUNE = ITEMS.register("insufferable_crypt_dimensional_rune", () -> new DimensionalRuneItem(new Item.Properties().stacksTo(1), MineCellsBlocks.INSUFFERABLE_CRYPT_DOORWAY.get()));
    public static final RegistryObject<Item> BLACK_BRIDGE_DIMENSIONAL_RUNE = ITEMS.register("black_bridge_dimensional_rune", () -> new DimensionalRuneItem(new Item.Properties().stacksTo(1), MineCellsBlocks.BLACK_BRIDGE_DOORWAY.get()));
    public static final RegistryObject<ForgeSpawnEggItem> LEAPING_ZOMBIE_SPAWN_EGG = registerSpawnEgg("leaping_zombie_spawn_egg", MineCellsEntities.LEAPING_ZOMBIE);
    public static final RegistryObject<ForgeSpawnEggItem> SHOCKER_SPAWN_EGG = registerSpawnEgg("shocker_spawn_egg", MineCellsEntities.SHOCKER);
    public static final RegistryObject<ForgeSpawnEggItem> GRENADIER_SPAWN_EGG = registerSpawnEgg("grenadier_spawn_egg", MineCellsEntities.GRENADIER);
    public static final RegistryObject<ForgeSpawnEggItem> DISGUSTING_WORM_SPAWN_EGG = registerSpawnEgg("disgusting_worm_spawn_egg", MineCellsEntities.DISGUSTING_WORM);
    public static final RegistryObject<ForgeSpawnEggItem> INQUISITOR_SPAWN_EGG = registerSpawnEgg("inquisitor_spawn_egg", MineCellsEntities.INQUISITOR);
    public static final RegistryObject<ForgeSpawnEggItem> KAMIKAZE_SPAWN_EGG = registerSpawnEgg("kamikaze_spawn_egg", MineCellsEntities.KAMIKAZE);
    public static final RegistryObject<ForgeSpawnEggItem> PROTECTOR_SPAWN_EGG = registerSpawnEgg("protector_spawn_egg", MineCellsEntities.PROTECTOR);
    public static final RegistryObject<ForgeSpawnEggItem> UNDEAD_ARCHER_SPAWN_EGG = registerSpawnEgg("undead_archer_spawn_egg", MineCellsEntities.UNDEAD_ARCHER);
    public static final RegistryObject<ForgeSpawnEggItem> SHIELDBEARER_SPAWN_EGG = registerSpawnEgg("shieldbearer_spawn_egg", MineCellsEntities.SHIELDBEARER);
    public static final RegistryObject<ForgeSpawnEggItem> MUTATED_BAT_SPAWN_EGG = registerSpawnEgg("mutated_bat_spawn_egg", MineCellsEntities.MUTATED_BAT);
    public static final RegistryObject<ForgeSpawnEggItem> SEWERS_TENTACLE_SPAWN_EGG = registerSpawnEgg("sewers_tentacle_spawn_egg", MineCellsEntities.SEWERS_TENTACLE);
    public static final RegistryObject<ForgeSpawnEggItem> RANCID_RAT_SPAWN_EGG = registerSpawnEgg("rancid_rat_spawn_egg", MineCellsEntities.RANCID_RAT);
    public static final RegistryObject<ForgeSpawnEggItem> RUNNER_SPAWN_EGG = registerSpawnEgg("runner_spawn_egg", MineCellsEntities.RUNNER);
    public static final RegistryObject<ForgeSpawnEggItem> SCORPION_SPAWN_EGG = registerSpawnEgg("scorpion_spawn_egg", MineCellsEntities.SCORPION);
    public static final RegistryObject<ForgeSpawnEggItem> BUZZCUTTER_SPAWN_EGG = registerSpawnEgg("buzzcutter_spawn_egg", MineCellsEntities.BUZZCUTTER);
    public static final RegistryObject<ForgeSpawnEggItem> SWEEPER_SPAWN_EGG = registerSpawnEgg("sweeper_spawn_egg", MineCellsEntities.SWEEPER);
    public static final RegistryObject<ForgeSpawnEggItem> CONCIERGE_SPAWN_EGG = registerSpawnEgg("concierge_spawn_egg", MineCellsEntities.CONCIERGE, 0x3A2A1A, 0xF8AF0C);
    public static final RegistryObject<ForgeSpawnEggItem> CONJUNCTIVIUS_SPAWN_EGG = registerSpawnEgg("conjunctivius_spawn_egg", MineCellsEntities.CONJUNCTIVIUS, 0x2A0A2A, 0xDF5FE2);

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

    private static RegistryObject<ForgeSpawnEggItem> registerSpawnEgg(
        String name,
        net.minecraftforge.registries.RegistryObject<? extends net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.Mob>> entityType
    ) {
        return registerSpawnEgg(name, entityType, 0xFFFFFF, 0xFFFFFF);
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
