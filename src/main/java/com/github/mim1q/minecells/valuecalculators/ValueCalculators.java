package com.github.mim1q.minecells.valuecalculators;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.network.MineCellsNetwork;
import com.github.mim1q.minecells.network.s2c.SyncValueCalculatorsS2CPacket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.ToDoubleFunction;

public final class ValueCalculators {
    private static final Gson GSON = new GsonBuilder().create();
    private static final List<Runnable> RELOAD_CALLBACKS = new CopyOnWriteArrayList<>();

    private static volatile Map<ResourceLocation, ValueCalculatorDefinition> definitions = Map.of();
    private static volatile Map<ResourceLocation, String> sources = Map.of();

    private ValueCalculators() {
    }

    public static ValueCalculator of(String path, String name, double fallback) {
        return new ValueCalculator(MineCells.id(path), name, context -> fallback);
    }

    public static ValueCalculator of(String path, String name, ToDoubleFunction<ValueCalculatorContext> fallback) {
        return new ValueCalculator(MineCells.id(path), name, fallback);
    }

    public static boolean isLoaded() {
        return !definitions.isEmpty();
    }

    @Nullable
    static ValueCalculatorDefinition definition(ResourceLocation id) {
        return definitions.get(id);
    }

    public static void onReload(Runnable callback) {
        RELOAD_CALLBACKS.add(callback);
    }

    public static Map<ResourceLocation, String> sources() {
        return sources;
    }

    public static void applySynced(Map<ResourceLocation, String> synced) {
        Map<ResourceLocation, JsonElement> parsed = new HashMap<>();
        synced.forEach((id, json) -> {
            try {
                parsed.put(id, JsonParser.parseString(json));
            } catch (RuntimeException e) {
                MineCells.LOGGER.error("Failed to read synced value calculator {}: {}", id, e.getMessage());
            }
        });
        load(parsed);
    }

    private static void load(Map<ResourceLocation, JsonElement> objects) {
        Map<ResourceLocation, ValueCalculatorDefinition> parsed = new HashMap<>();
        Map<ResourceLocation, String> raw = new HashMap<>();
        objects.forEach((id, json) -> {
            try {
                if (!json.isJsonObject()) {
                    throw new IllegalArgumentException("root must be an object");
                }
                parsed.put(id, ValueCalculatorDefinition.parse(json.getAsJsonObject()));
                raw.put(id, GSON.toJson(json));
            } catch (RuntimeException e) {
                MineCells.LOGGER.error("Failed to parse value calculator {}: {}", id, e.getMessage());
            }
        });
        definitions = Map.copyOf(parsed);
        sources = Map.copyOf(raw);
        RELOAD_CALLBACKS.forEach(Runnable::run);
    }

    public static void onDatapackSync(OnDatapackSyncEvent event) {
        SyncValueCalculatorsS2CPacket packet = new SyncValueCalculatorsS2CPacket(sources);
        if (event.getPlayer() != null) {
            MineCellsNetwork.sendToPlayer(event.getPlayer(), packet);
        } else {
            MineCellsNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
        }
    }

    public static final class ReloadListener extends SimpleJsonResourceReloadListener {
        public ReloadListener() {
            super(GSON, "value_calculators");
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager, ProfilerFiller profiler) {
            load(objects);
            MineCells.LOGGER.info("Loaded {} Mine Cells value calculators", definitions.size());
        }
    }
}
