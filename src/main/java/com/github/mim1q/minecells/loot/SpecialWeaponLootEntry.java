package com.github.mim1q.minecells.loot;

import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.registry.MineCellsLootPoolEntryTypes;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SpecialWeaponLootEntry extends LootPoolEntryContainer {
    private final List<Entry> entries;

    public SpecialWeaponLootEntry(List<Entry> entries, LootItemCondition[] conditions) {
        super(conditions);
        this.entries = entries;
    }

    @Override
    public LootPoolEntryType getType() {
        return MineCellsLootPoolEntryTypes.SPECIAL_WEAPON.get();
    }

    @Override
    public boolean expand(LootContext context, Consumer<LootPoolEntry> entryConsumer) {
        if (!canRun(context)) {
            return false;
        }
        entryConsumer.accept(new LootPoolEntry() {
            @Override
            public int getWeight(float luck) {
                return 1;
            }

            @Override
            public void createItemStack(Consumer<ItemStack> stackConsumer, LootContext lootContext) {
                Entry selected = selectEntry(lootContext);
                if (selected == null) {
                    return;
                }
                List<LootPoolEntry> generatedEntries = new ArrayList<>();
                if (!selected.loot().expand(lootContext, generatedEntries::add)) {
                    return;
                }
                generatedEntries.forEach(entry -> entry.createItemStack(stackConsumer, lootContext));
            }
        });
        return true;
    }

    @Override
    public void validate(ValidationContext validationContext) {
        super.validate(validationContext);
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).loot().validate(validationContext.forChild(".entries[" + i + "].loot"));
        }
    }

    @Nullable
    private Entry selectEntry(LootContext context) {
        ServerPlayer player = resolvePlayer(context);
        MineCellsDimension dimension = MineCellsDimension.of(context.getLevel());
        int currentDimensionLevel = dimension == null ? 0 : dimension.getDimensionLevel();
        List<WeightedEntry> eligible = new ArrayList<>();
        int totalWeight = 0;

        for (Entry entry : entries) {
            if (entry.dimensionLevel() > currentDimensionLevel) {
                continue;
            }
            if (player != null && !entry.isUnlockedFor(player)) {
                continue;
            }
            int weight = entry.weight();
            if (entry.dimensionLevel() == currentDimensionLevel) {
                weight *= 2;
            }
            if (weight <= 0) {
                continue;
            }
            eligible.add(new WeightedEntry(entry, weight));
            totalWeight += weight;
        }

        if (totalWeight <= 0) {
            return null;
        }

        int randomWeight = context.getRandom().nextInt(totalWeight);
        for (WeightedEntry entry : eligible) {
            randomWeight -= entry.weight();
            if (randomWeight < 0) {
                return entry.entry();
            }
        }
        return eligible.get(eligible.size() - 1).entry();
    }

    @Nullable
    private static ServerPlayer resolvePlayer(LootContext context) {
        Entity thisEntity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (thisEntity instanceof ServerPlayer player) {
            return player;
        }
        Player lastDamagePlayer = context.getParamOrNull(LootContextParams.LAST_DAMAGE_PLAYER);
        return lastDamagePlayer instanceof ServerPlayer player ? player : null;
    }

    private record WeightedEntry(Entry entry, int weight) {
    }

    private record Entry(LootPoolEntryContainer loot, int weight, int dimensionLevel, @Nullable ResourceLocation advancementId) {
        private boolean isUnlockedFor(ServerPlayer player) {
            if (advancementId == null) {
                return true;
            }
            Advancement advancement = player.server.getAdvancements().getAdvancement(advancementId);
            return advancement == null || player.getAdvancements().getOrStartProgress(advancement).isDone();
        }
    }

    public static class Serializer extends LootPoolEntryContainer.Serializer<SpecialWeaponLootEntry> {
        @Override
        public void serializeCustom(JsonObject json, SpecialWeaponLootEntry object, JsonSerializationContext context) {
            JsonArray entries = new JsonArray();
            json.add("entries", entries);
            for (Entry entry : object.entries) {
                JsonObject entryJson = new JsonObject();
                entryJson.add("loot", context.serialize(entry.loot()));
                entryJson.addProperty("weight", entry.weight());
                entryJson.addProperty("dimension_level", entry.dimensionLevel());
                if (entry.advancementId() != null) {
                    entryJson.addProperty("advancement", entry.advancementId().toString());
                }
                entries.add(entryJson);
            }
        }

        @Override
        public SpecialWeaponLootEntry deserializeCustom(JsonObject json, JsonDeserializationContext context, LootItemCondition[] conditions) {
            List<Entry> entries = new ArrayList<>();
            for (var element : json.getAsJsonArray("entries")) {
                JsonObject entryJson = element.getAsJsonObject();
                ResourceLocation advancementId = entryJson.has("advancement")
                    ? ResourceLocation.tryParse(entryJson.get("advancement").getAsString())
                    : null;
                entries.add(new Entry(
                    context.deserialize(entryJson.get("loot"), LootPoolEntryContainer.class),
                    entryJson.get("weight").getAsInt(),
                    entryJson.get("dimension_level").getAsInt(),
                    advancementId
                ));
            }
            return new SpecialWeaponLootEntry(entries, conditions);
        }
    }
}
