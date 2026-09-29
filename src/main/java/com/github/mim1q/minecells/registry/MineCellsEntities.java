package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.entity.boss.ConciergeEntity;
import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.entity.BuzzcutterEntity;
import com.github.mim1q.minecells.entity.DisgustingWormEntity;
import com.github.mim1q.minecells.entity.GrenadeProjectileEntity;
import com.github.mim1q.minecells.entity.GrenadierEntity;
import com.github.mim1q.minecells.entity.InquisitorEntity;
import com.github.mim1q.minecells.entity.KamikazeEntity;
import com.github.mim1q.minecells.entity.LeapingZombieEntity;
import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;
import com.github.mim1q.minecells.entity.MutatedBatEntity;
import com.github.mim1q.minecells.entity.nonliving.ElevatorEntity;
import com.github.mim1q.minecells.entity.nonliving.obelisk.ConciergeObeliskEntity;
import com.github.mim1q.minecells.entity.nonliving.obelisk.ConjunctiviusObeliskEntity;
import com.github.mim1q.minecells.entity.nonliving.obelisk.EliteObeliskEntity;
import com.github.mim1q.minecells.entity.nonliving.projectile.BigGrenadeEntity;
import com.github.mim1q.minecells.entity.nonliving.projectile.ConjunctiviusProjectileEntity;
import com.github.mim1q.minecells.entity.nonliving.projectile.CustomArrowEntity;
import com.github.mim1q.minecells.entity.nonliving.projectile.DisgustingWormEggEntity;
import com.github.mim1q.minecells.entity.nonliving.projectile.MagicOrbEntity;
import com.github.mim1q.minecells.entity.nonliving.projectile.ScorpionSpitEntity;
import com.github.mim1q.minecells.entity.nonliving.ShockwavePlacer;
import com.github.mim1q.minecells.entity.nonliving.SpawnerRuneEntity;
import com.github.mim1q.minecells.entity.nonliving.TentacleWeaponEntity;
import com.github.mim1q.minecells.entity.ProtectorEntity;
import com.github.mim1q.minecells.entity.RancidRatEntity;
import com.github.mim1q.minecells.entity.RunnerEntity;
import com.github.mim1q.minecells.entity.ScorpionEntity;
import com.github.mim1q.minecells.entity.SewersTentacleEntity;
import com.github.mim1q.minecells.entity.ShieldbearerEntity;
import com.github.mim1q.minecells.entity.ShockerEntity;
import com.github.mim1q.minecells.entity.SweeperEntity;
import com.github.mim1q.minecells.entity.UndeadArcherEntity;
import com.github.mim1q.minecells.MineCells;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Monster;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public final class MineCellsEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MineCells.MOD_ID);

    public static final RegistryObject<EntityType<LeapingZombieEntity>> LEAPING_ZOMBIE = registerMonster("leaping_zombie", LeapingZombieEntity::new, 0.75F, 1.9F);
    public static final RegistryObject<EntityType<ShockerEntity>> SHOCKER = registerMonster("shocker", ShockerEntity::new, 0.9F, 3.0F);
    public static final RegistryObject<EntityType<GrenadierEntity>> GRENADIER = registerMonster("grenadier", GrenadierEntity::new, 0.8F, 1.9F);
    public static final RegistryObject<EntityType<DisgustingWormEntity>> DISGUSTING_WORM = registerMonster("disgusting_worm", DisgustingWormEntity::new, 0.9F, 0.6F);
    public static final RegistryObject<EntityType<InquisitorEntity>> INQUISITOR = registerMonster("inquisitor", InquisitorEntity::new, 0.75F, 1.9F);
    public static final RegistryObject<EntityType<KamikazeEntity>> KAMIKAZE = registerMonster("kamikaze", KamikazeEntity::new, 0.75F, 0.75F);
    public static final RegistryObject<EntityType<ProtectorEntity>> PROTECTOR = registerMonster("protector", ProtectorEntity::new, 0.75F, 1.9F);
    public static final RegistryObject<EntityType<UndeadArcherEntity>> UNDEAD_ARCHER = registerMonster("undead_archer", UndeadArcherEntity::new, 0.75F, 1.9F);
    public static final RegistryObject<EntityType<ShieldbearerEntity>> SHIELDBEARER = registerMonster("shieldbearer", ShieldbearerEntity::new, 0.75F, 1.9F);
    public static final RegistryObject<EntityType<MutatedBatEntity>> MUTATED_BAT = registerMonster("mutated_bat", MutatedBatEntity::new, 0.9F, 0.9F);
    public static final RegistryObject<EntityType<SewersTentacleEntity>> SEWERS_TENTACLE = registerMonster("sewers_tentacle", SewersTentacleEntity::new, 0.75F, 2.25F);
    public static final RegistryObject<EntityType<RancidRatEntity>> RANCID_RAT = registerMonster("rancid_rat", RancidRatEntity::new, 0.5F, 0.75F);
    public static final RegistryObject<EntityType<RunnerEntity>> RUNNER = registerMonster("runner", RunnerEntity::new, 0.8F, 2.1F);
    public static final RegistryObject<EntityType<ScorpionEntity>> SCORPION = registerMonster("scorpion", ScorpionEntity::new, 0.8F, 1.5F);
    public static final RegistryObject<EntityType<BuzzcutterEntity>> BUZZCUTTER = registerMonster("buzzcutter", BuzzcutterEntity::new, 0.75F, 0.75F);
    public static final RegistryObject<EntityType<SweeperEntity>> SWEEPER = registerMonster("sweeper", SweeperEntity::new, 0.9F, 1.6F);
    public static final RegistryObject<EntityType<ConciergeEntity>> CONCIERGE = ENTITIES.register(
        "concierge",
        () -> EntityType.Builder.of(ConciergeEntity::new, MobCategory.MONSTER)
            .sized(1.8F, 3.5F)
            .clientTrackingRange(10)
            .build(MineCells.id("concierge").toString())
    );
    public static final RegistryObject<EntityType<ConjunctiviusEntity>> CONJUNCTIVIUS = ENTITIES.register(
        "conjunctivius",
        () -> EntityType.Builder.of(ConjunctiviusEntity::new, MobCategory.MONSTER)
            .sized(5.0F, 5.0F)
            .clientTrackingRange(10)
            .build(MineCells.id("conjunctivius").toString())
    );
    public static final RegistryObject<EntityType<MagicOrbEntity>> MAGIC_ORB = ENTITIES.register(
        "magic_orb",
        () -> EntityType.Builder.<MagicOrbEntity>of(MagicOrbEntity::new, MobCategory.MISC)
            .sized(0.75F, 0.75F)
            .clientTrackingRange(8)
            .updateInterval(1)
            .build(MineCells.id("magic_orb").toString())
    );
    public static final RegistryObject<EntityType<ScorpionSpitEntity>> SCORPION_SPIT = ENTITIES.register(
        "scorpion_spit",
        () -> EntityType.Builder.<ScorpionSpitEntity>of(ScorpionSpitEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(8)
            .updateInterval(1)
            .build(MineCells.id("scorpion_spit").toString())
    );
    public static final RegistryObject<EntityType<ConjunctiviusProjectileEntity>> CONJUNCTIVIUS_PROJECTILE = ENTITIES.register(
        "conjunctivius_projectile",
        () -> EntityType.Builder.<ConjunctiviusProjectileEntity>of(ConjunctiviusProjectileEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(8)
            .updateInterval(1)
            .build(MineCells.id("conjunctivius_projectile").toString())
    );
    public static final RegistryObject<EntityType<ConciergeObeliskEntity>> CONCIERGE_OBELISK = ENTITIES.register(
        "concierge_obelisk",
        () -> EntityType.Builder.<ConciergeObeliskEntity>of(ConciergeObeliskEntity::new, MobCategory.MISC)
            .sized(1.75F, 2.5F)
            .clientTrackingRange(10)
            .build(MineCells.id("concierge_obelisk").toString())
    );
    public static final RegistryObject<EntityType<EliteObeliskEntity>> ELITE_OBELISK = ENTITIES.register(
        "elite_obelisk",
        () -> EntityType.Builder.<EliteObeliskEntity>of(EliteObeliskEntity::new, MobCategory.MISC)
            .sized(1.75F, 2.5F)
            .clientTrackingRange(10)
            .build(MineCells.id("elite_obelisk").toString())
    );
    public static final RegistryObject<EntityType<ConjunctiviusObeliskEntity>> CONJUNCTIVIUS_OBELISK = ENTITIES.register(
        "conjunctivius_obelisk",
        () -> EntityType.Builder.<ConjunctiviusObeliskEntity>of(ConjunctiviusObeliskEntity::new, MobCategory.MISC)
            .sized(1.75F, 2.5F)
            .clientTrackingRange(10)
            .build(MineCells.id("conjunctivius_obelisk").toString())
    );
    public static final RegistryObject<EntityType<GrenadeProjectileEntity>> GRENADE = ENTITIES.register(
        "grenade",
        () -> EntityType.Builder.<GrenadeProjectileEntity>of(GrenadeProjectileEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(8)
            .updateInterval(1)
            .build(MineCells.id("grenade").toString())
    );
    public static final RegistryObject<EntityType<BigGrenadeEntity>> BIG_GRENADE = ENTITIES.register(
        "big_grenade",
        () -> EntityType.Builder.<BigGrenadeEntity>of(BigGrenadeEntity::new, MobCategory.MISC)
            .sized(0.75F, 0.75F)
            .clientTrackingRange(8)
            .updateInterval(1)
            .build(MineCells.id("big_grenade").toString())
    );
    public static final RegistryObject<EntityType<DisgustingWormEggEntity>> DISGUSTING_WORM_EGG = ENTITIES.register(
        "disgusting_worm_egg",
        () -> EntityType.Builder.<DisgustingWormEggEntity>of(DisgustingWormEggEntity::new, MobCategory.MISC)
            .sized(0.375F, 0.375F)
            .clientTrackingRange(8)
            .updateInterval(1)
            .build(MineCells.id("disgusting_worm_egg").toString())
    );
    public static final RegistryObject<EntityType<TentacleWeaponEntity>> TENTACLE_WEAPON = ENTITIES.register(
        "tentacle_weapon",
        () -> EntityType.Builder.<TentacleWeaponEntity>of(TentacleWeaponEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(8)
            .updateInterval(1)
            .build(MineCells.id("tentacle_weapon").toString())
    );
    public static final RegistryObject<EntityType<SpawnerRuneEntity>> SPAWNER_RUNE = ENTITIES.register(
        "spawner_rune",
        () -> EntityType.Builder.<SpawnerRuneEntity>of(SpawnerRuneEntity::new, MobCategory.MISC)
            .sized(1.0F, 1.0F)
            .clientTrackingRange(8)
            .updateInterval(20)
            .build(MineCells.id("spawner_rune").toString())
    );
    public static final RegistryObject<EntityType<ShockwavePlacer>> SHOCKWAVE_PLACER = ENTITIES.register(
        "shockwave_placer",
        () -> EntityType.Builder.<ShockwavePlacer>of(ShockwavePlacer::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(8)
            .updateInterval(1)
            .build(MineCells.id("shockwave_placer").toString())
    );
    public static final RegistryObject<EntityType<ElevatorEntity>> ELEVATOR = ENTITIES.register(
        "elevator",
        () -> EntityType.Builder.<ElevatorEntity>of(ElevatorEntity::new, MobCategory.MISC)
            .sized(2.0F, 0.5F)
            .clientTrackingRange(10)
            .updateInterval(1)
            .build(MineCells.id("elevator").toString())
    );
    public static final RegistryObject<EntityType<CustomArrowEntity>> CUSTOM_ARROW = ENTITIES.register(
        "custom_arrow",
        () -> EntityType.Builder.<CustomArrowEntity>of(CustomArrowEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(8)
            .updateInterval(1)
            .build(MineCells.id("custom_arrow").toString())
    );

    public static final List<RegistryObject<? extends EntityType<? extends MineCellsMonsterEntity>>> MONSTERS = List.of(
        LEAPING_ZOMBIE,
        SHOCKER,
        GRENADIER,
        DISGUSTING_WORM,
        INQUISITOR,
        KAMIKAZE,
        PROTECTOR,
        UNDEAD_ARCHER,
        SHIELDBEARER,
        MUTATED_BAT,
        SEWERS_TENTACLE,
        RANCID_RAT,
        RUNNER,
        SCORPION,
        BUZZCUTTER,
        SWEEPER
    );

    private MineCellsEntities() {
    }

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        AttributeSupplier baseAttributes = MineCellsMonsterEntity.createBaseAttributes().build();
        MONSTERS.stream()
            .filter(entityType -> entityType != PROTECTOR)
            .filter(entityType -> entityType != UNDEAD_ARCHER)
            .filter(entityType -> entityType != LEAPING_ZOMBIE)
            .filter(entityType -> entityType != SHIELDBEARER)
            .filter(entityType -> entityType != RUNNER)
            .filter(entityType -> entityType != GRENADIER)
            .filter(entityType -> entityType != SEWERS_TENTACLE)
            .filter(entityType -> entityType != SHOCKER)
            .filter(entityType -> entityType != INQUISITOR)
            .filter(entityType -> entityType != KAMIKAZE)
            .filter(entityType -> entityType != DISGUSTING_WORM)
            .filter(entityType -> entityType != MUTATED_BAT)
            .filter(entityType -> entityType != RANCID_RAT)
            .filter(entityType -> entityType != SCORPION)
            .filter(entityType -> entityType != BUZZCUTTER)
            .filter(entityType -> entityType != SWEEPER)
            .forEach(entityType -> event.put(entityType.get(), baseAttributes));
        event.put(PROTECTOR.get(), ProtectorEntity.createAttributes().build());
        event.put(UNDEAD_ARCHER.get(), UndeadArcherEntity.createAttributes().build());
        event.put(LEAPING_ZOMBIE.get(), LeapingZombieEntity.createAttributes().build());
        event.put(SHIELDBEARER.get(), ShieldbearerEntity.createAttributes().build());
        event.put(RUNNER.get(), RunnerEntity.createAttributes().build());
        event.put(GRENADIER.get(), GrenadierEntity.createAttributes().build());
        event.put(SEWERS_TENTACLE.get(), SewersTentacleEntity.createSewersTentacleAttributes().build());
        event.put(SHOCKER.get(), ShockerEntity.createAttributes().build());
        event.put(INQUISITOR.get(), InquisitorEntity.createAttributes().build());
        event.put(KAMIKAZE.get(), KamikazeEntity.createAttributes().build());
        event.put(DISGUSTING_WORM.get(), DisgustingWormEntity.createAttributes().build());
        event.put(MUTATED_BAT.get(), MutatedBatEntity.createAttributes().build());
        event.put(RANCID_RAT.get(), RancidRatEntity.createAttributes().build());
        event.put(SCORPION.get(), ScorpionEntity.createAttributes().build());
        event.put(BUZZCUTTER.get(), BuzzcutterEntity.createAttributes().build());
        event.put(SWEEPER.get(), SweeperEntity.createAttributes().build());
        event.put(CONCIERGE.get(), ConciergeEntity.createConciergeAttributes().build());
        event.put(CONJUNCTIVIUS.get(), ConjunctiviusEntity.createConjunctiviusAttributes().build());
    }

    private static RegistryObject<EntityType<MineCellsMonsterEntity>> registerMonster(String name, float width, float height) {
        return registerMonster(name, MineCellsMonsterEntity::new, width, height);
    }

    private static <T extends Monster> RegistryObject<EntityType<T>> registerMonster(String name, EntityType.EntityFactory<T> factory, float width, float height) {
        return ENTITIES.register(
            name,
            () -> EntityType.Builder.of(factory, MobCategory.MONSTER)
                .sized(width, height)
                .clientTrackingRange(8)
                .build(MineCells.id(name).toString())
        );
    }
}
