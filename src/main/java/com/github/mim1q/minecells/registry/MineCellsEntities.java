package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public final class MineCellsEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MineCells.MOD_ID);

    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> LEAPING_ZOMBIE = registerMonster("leaping_zombie", 0.75F, 1.9F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> SHOCKER = registerMonster("shocker", 0.9F, 3.0F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> GRENADIER = registerMonster("grenadier", 0.8F, 1.9F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> DISGUSTING_WORM = registerMonster("disgusting_worm", 0.9F, 0.6F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> INQUISITOR = registerMonster("inquisitor", 0.75F, 1.9F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> KAMIKAZE = registerMonster("kamikaze", 0.75F, 0.75F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> PROTECTOR = registerMonster("protector", 0.75F, 1.9F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> UNDEAD_ARCHER = registerMonster("undead_archer", 0.75F, 1.9F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> SHIELDBEARER = registerMonster("shieldbearer", 0.75F, 1.9F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> MUTATED_BAT = registerMonster("mutated_bat", 0.9F, 0.9F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> SEWERS_TENTACLE = registerMonster("sewers_tentacle", 0.75F, 2.25F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> RANCID_RAT = registerMonster("rancid_rat", 0.5F, 0.75F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> RUNNER = registerMonster("runner", 0.8F, 2.1F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> SCORPION = registerMonster("scorpion", 0.8F, 1.5F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> BUZZCUTTER = registerMonster("buzzcutter", 0.75F, 0.75F);
    public static final RegistryObject<EntityType<MineCellsMonsterEntity>> SWEEPER = registerMonster("sweeper", 0.9F, 1.6F);

    public static final List<RegistryObject<EntityType<MineCellsMonsterEntity>>> MONSTERS = List.of(
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
        AttributeSupplier attributes = Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.25D)
            .add(Attributes.ATTACK_DAMAGE, 3.0D)
            .add(Attributes.FOLLOW_RANGE, 24.0D)
            .build();
        MONSTERS.forEach(entityType -> event.put(entityType.get(), attributes));
    }

    private static RegistryObject<EntityType<MineCellsMonsterEntity>> registerMonster(String name, float width, float height) {
        return ENTITIES.register(
            name,
            () -> EntityType.Builder.of(MineCellsMonsterEntity::new, MobCategory.MONSTER)
                .sized(width, height)
                .clientTrackingRange(8)
                .build(MineCells.id(name).toString())
        );
    }
}
