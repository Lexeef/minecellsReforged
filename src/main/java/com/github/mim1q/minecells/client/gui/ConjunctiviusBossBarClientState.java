package com.github.mim1q.minecells.client.gui;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side tentacle counts for Conjunctivius custom boss bars.
 * Fabric subclassed {@code ClientBossBar}; Forge uses a UUID-keyed cache instead.
 */
public final class ConjunctiviusBossBarClientState {
    public record TentacleCounts(int count, int maxCount) {
    }

    private static final Map<UUID, TentacleCounts> TENTACLES = new ConcurrentHashMap<>();

    private ConjunctiviusBossBarClientState() {
    }

    public static void setTentacleCount(UUID bossBarId, int count, int maxCount) {
        if (bossBarId == null) {
            return;
        }
        if (maxCount <= 0 && count <= 0) {
            TENTACLES.remove(bossBarId);
            return;
        }
        TENTACLES.put(bossBarId, new TentacleCounts(count, maxCount));
    }

    public static TentacleCounts get(UUID bossBarId) {
        return bossBarId == null ? null : TENTACLES.get(bossBarId);
    }

    public static void clear() {
        TENTACLES.clear();
    }
}
