package com.github.mim1q.minecells.data.spawner_runes;

import com.github.mim1q.minecells.MineCells;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record SpawnerRuneData(
    float cooldown,
    float spawnDistance,
    float playerDistance,
    List<Pool> pools
) {
    public static final Codec<SpawnerRuneData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.FLOAT.optionalFieldOf("cooldown", 0.0F).forGetter(SpawnerRuneData::cooldown),
            Codec.FLOAT.optionalFieldOf("spawnDistance", 0.0F).forGetter(SpawnerRuneData::spawnDistance),
            Codec.FLOAT.fieldOf("playerDistance").forGetter(SpawnerRuneData::playerDistance),
            Pool.CODEC.listOf().fieldOf("pools").forGetter(SpawnerRuneData::pools)
        ).apply(instance, SpawnerRuneData::new)
    );

    public List<EntitySpawnData> getSelectedEntities(RandomSource random) {
        List<EntitySpawnData> entities = new ArrayList<>();
        for (Pool pool : pools) {
            entities.addAll(pool.getSelectedEntities(random));
        }
        return entities;
    }

    public record Pool(
        IntProvider rolls,
        List<Entry> entries
    ) {
        public static final Codec<Pool> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                IntProvider.NON_NEGATIVE_CODEC.fieldOf("rolls").forGetter(Pool::rolls),
                Entry.CODEC.listOf().fieldOf("entries").forGetter(Pool::entries)
            ).apply(instance, Pool::new)
        );

        public List<EntitySpawnData> getSelectedEntities(RandomSource random) {
            int weightSum = entries.stream().mapToInt(Entry::weight).sum();
            int count = rolls.sample(random);
            List<EntitySpawnData> entities = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                EntitySpawnData selected = chooseEntity(weightSum, random);
                if (selected != null) {
                    entities.add(selected);
                }
            }
            return entities;
        }

        private EntitySpawnData chooseEntity(int weightSum, RandomSource random) {
            int randomWeight = random.nextInt(Math.max(1, weightSum));
            for (Entry entry : entries) {
                randomWeight -= entry.weight;
                if (randomWeight < 0) {
                    return new EntitySpawnData(entry.entityType, entry.attributeOverrides, entry.nbt.copy());
                }
            }
            return null;
        }
    }

    public static final class Entry {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.INT.optionalFieldOf("weight", 1).forGetter(Entry::weight),
                Codec.STRING.fieldOf("entity").forGetter(Entry::entityId),
                Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).optionalFieldOf("attributes", Map.of()).forGetter(Entry::attributeMap),
                CompoundTag.CODEC.optionalFieldOf("nbt", new CompoundTag()).forGetter(Entry::nbt)
            ).apply(instance, Entry::new)
        );

        private final int weight;
        private final String entityId;
        private final EntityType<?> entityType;
        private final Map<String, Double> attributeMap;
        private final Map<Attribute, Double> attributeOverrides;
        private final CompoundTag nbt;

        private Entry(int weight, String entityId, Map<String, Double> attributeMap, CompoundTag nbt) {
            this.weight = weight;
            this.entityId = entityId;
            this.attributeMap = attributeMap;
            this.nbt = nbt.copy();
            this.entityType = ForgeRegistries.ENTITY_TYPES.getValue(net.minecraft.resources.ResourceLocation.tryParse(entityId));
            this.attributeOverrides = attributeMap.entrySet().stream()
                .map(entry -> {
                    Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(net.minecraft.resources.ResourceLocation.tryParse(entry.getKey()));
                    if (attribute == null) {
                        MineCells.LOGGER.warn("Unknown attribute in Spawner Rune data: {}", entry.getKey());
                        return null;
                    }
                    return Map.entry(attribute, entry.getValue());
                })
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        public int weight() {
            return weight;
        }

        public String entityId() {
            return entityId;
        }

        public EntityType<?> entityType() {
            return entityType;
        }

        public Map<String, Double> attributeMap() {
            return attributeMap;
        }

        public Map<Attribute, Double> attributeOverrides() {
            return attributeOverrides;
        }

        public CompoundTag nbt() {
            return nbt;
        }
    }

    public record EntitySpawnData(
        EntityType<?> entityType,
        Map<Attribute, Double> attributeOverrides,
        CompoundTag nbt
    ) {
    }
}
