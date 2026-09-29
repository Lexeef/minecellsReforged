package com.github.mim1q.minecells.data;

import com.github.mim1q.minecells.data.spawner_runes.SpawnerRuneData;
import com.github.mim1q.minecells.MineCells;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SpawnerRuneReloadListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private Map<ResourceLocation, SpawnerRuneData> entries = Collections.emptyMap();

    public SpawnerRuneReloadListener() {
        super(GSON, "spawner_runes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, SpawnerRuneData> parsed = new HashMap<>();
        objects.forEach((id, json) -> SpawnerRuneData.CODEC.parse(JsonOps.INSTANCE, json)
            .resultOrPartial(error -> MineCells.LOGGER.error("Failed to parse spawner rune {}: {}", id, error))
            .ifPresent(data -> parsed.put(id, data)));
        this.entries = Collections.unmodifiableMap(parsed);
        MineCells.LOGGER.info("Loaded {} Mine Cells spawner rune definitions", this.entries.size());
    }

    public int size() {
        return entries.size();
    }

    public Map<ResourceLocation, SpawnerRuneData> entries() {
        return entries;
    }
}
