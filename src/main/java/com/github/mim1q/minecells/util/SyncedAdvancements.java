package com.github.mim1q.minecells.util;

import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Client-side mirror of the advancement completion state sent by {@code AdvancementHintsS2CPacket}.
 * Lives in common code so shared block entity logic can query it on the logical client.
 */
public final class SyncedAdvancements {
    private static final Map<ResourceLocation, Boolean> COMPLETED = new ConcurrentHashMap<>();

    private SyncedAdvancements() {
    }

    public static void apply(Map<ResourceLocation, Boolean> completed, boolean replace) {
        if (replace) {
            COMPLETED.clear();
        }
        COMPLETED.putAll(completed);
    }

    public static void reset() {
        COMPLETED.clear();
    }

    public static boolean isCompleted(ResourceLocation id) {
        return COMPLETED.getOrDefault(id, false);
    }
}
