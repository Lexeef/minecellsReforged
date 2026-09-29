package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.effect.MineCellsEffectFlags;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import net.minecraft.world.entity.Entity;

public final class ClientEffectFlags {
    private static final Int2IntOpenHashMap FLAGS = new Int2IntOpenHashMap();

    private ClientEffectFlags() {
    }

    public static void set(int entityId, int flags) {
        if (flags == 0) {
            FLAGS.remove(entityId);
        } else {
            FLAGS.put(entityId, flags);
        }
    }

    public static void remove(int entityId) {
        FLAGS.remove(entityId);
    }

    public static void clear() {
        FLAGS.clear();
    }

    public static boolean has(Entity entity, MineCellsEffectFlags flag) {
        return flag.isSet(FLAGS.get(entity.getId()));
    }
}
