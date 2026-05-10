package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.GrenadeProjectileEntity;
import com.github.mim1q.minecells.entity.GrenadierEntity;
import com.github.mim1q.minecells.entity.LeapingZombieEntity;
import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;
import com.github.mim1q.minecells.entity.ProtectorEntity;
import com.github.mim1q.minecells.entity.RunnerEntity;
import com.github.mim1q.minecells.entity.ShieldbearerEntity;
import com.github.mim1q.minecells.entity.UndeadArcherEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
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
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> SHOCKER = registerMonster("shocker", 0.9F, 3.0F);
    public static final RegistryObject<EntityType<GrenadierEntity>> GRENADIER = registerMonster("grenadier", GrenadierEntity::new, 0.8F, 1.9F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> DISGUSTING_WORM = registerMonster("disgusting_worm", 0.9F, 0.6F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> INQUISITOR = registerMonster("inquisitor", 0.75F, 1.9F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> KAMIKAZE = registerMonster("kamikaze", 0.75F, 0.75F);
    public static final RegistryObject<EntityType<ProtectorEntity>> PROTECTOR = registerMonster("protector", ProtectorEntity::new, 0.75F, 1.9F);
    public static final RegistryObject<EntityType<UndeadArcherEntity>> UNDEAD_ARCHER = registerMonster("undead_archer", UndeadArcherEntity::new, 0.75F, 1.9F);
    public static final RegistryObject<EntityType<ShieldbearerEntity>> SHIELDBEARER = registerMonster("shieldbearer", ShieldbearerEntity::new, 0.75F, 1.9F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> MUTATED_BAT = registerMonster("mutated_bat", 0.9F, 0.9F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> SEWERS_TENTACLE = registerMonster("sewers_tentacle", 0.75F, 2.25F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> RANCID_RAT = registerMonster("rancid_rat", 0.5F, 0.75F);
    public static final RegistryObject<EntityType<RunnerEntity>> RUNNER = registerMonster("runner", RunnerEntity::new, 0.8F, 2.1F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> SCORPION = registerMonster("scorpion", 0.8F, 1.5F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> BUZZCUTTER = registerMonster("buzzcutter", 0.75F, 0.75F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> SWEEPER = registerMonster("sweeper", 0.9F, 1.6F);
    public static final RegistryObject<EntityType<GrenadeProjectileEntity>> GRENADE = ENTITIES.register(
        "grenade",
        () -> EntityType.Builder.<GrenadeProjectileEntity>of(GrenadeProjectileEntity::new, MobCategory.MISC)
            .sized(0.4F, 0.4F)
            .clientTrackingRange(8)
            .updateInterval(1)
            .build(MineCells.id("grenade").toString())
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
            .forEach(entityType -> event.put(entityType.get(), baseAttributes));
        event.put(PROTECTOR.get(), ProtectorEntity.createAttributes().build());
        event.put(UNDEAD_ARCHER.get(), UndeadArcherEntity.createAttributes().build());
        event.put(LEAPING_ZOMBIE.get(), LeapingZombieEntity.createAttributes().build());
        event.put(SHIELDBEARER.get(), ShieldbearerEntity.createAttributes().build());
        event.put(RUNNER.get(), RunnerEntity.createAttributes().build());
        event.put(GRENADIER.get(), GrenadierEntity.createAttributes().build());
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
