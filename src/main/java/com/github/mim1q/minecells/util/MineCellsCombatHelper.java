package com.github.mim1q.minecells.util;

import com.github.mim1q.minecells.registry.MineCellsStatusEffects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class MineCellsCombatHelper {
    private static final String BALANCED_BLADE_STACKS = "MineCellsBalancedBladeStacks";
    private static final String BALANCED_BLADE_TIMER = "MineCellsBalancedBladeTimer";
    private static final String LAST_DAMAGE_TIME = "MineCellsLastDamageTime";
    private static final String TEMP_INVULNERABILITY = "MineCellsTempInvulnerability";
    private static final String CURSED_PROCESSING = "MineCellsCursedProcessing";

    private MineCellsCombatHelper() {
    }

    public static void addBalancedBladeStack(Player player) {
        CompoundTag data = player.getPersistentData();
        int stacks = Math.min(9, data.getInt(BALANCED_BLADE_STACKS) + 1);
        data.putInt(BALANCED_BLADE_STACKS, stacks);
        data.putInt(BALANCED_BLADE_TIMER, 20 * 5);
    }

    public static int getBalancedBladeStacks(Player player) {
        return player.getPersistentData().getInt(BALANCED_BLADE_STACKS);
    }

    public static void resetBalancedBlade(Player player) {
        CompoundTag data = player.getPersistentData();
        data.putInt(BALANCED_BLADE_STACKS, 0);
        data.putInt(BALANCED_BLADE_TIMER, 0);
    }

    public static void tickBalancedBlade(Player player) {
        CompoundTag data = player.getPersistentData();
        int timer = data.getInt(BALANCED_BLADE_TIMER);
        if (timer > 0) {
            timer--;
            data.putInt(BALANCED_BLADE_TIMER, timer);
            if (timer == 0) {
                data.putInt(BALANCED_BLADE_STACKS, 0);
            }
        }
    }

    public static void recordDamageTime(LivingEntity entity) {
        entity.getPersistentData().putLong(LAST_DAMAGE_TIME, entity.level().getGameTime());
    }

    public static long getLastDamageTime(LivingEntity entity) {
        return entity.getPersistentData().getLong(LAST_DAMAGE_TIME);
    }

    public static void setTemporaryInvulnerability(Player player, int ticks) {
        player.getPersistentData().putInt(TEMP_INVULNERABILITY, Math.max(ticks, 0));
    }

    public static void tickTemporaryInvulnerability(Player player) {
        CompoundTag data = player.getPersistentData();
        int ticks = data.getInt(TEMP_INVULNERABILITY);
        if (ticks > 0) {
            data.putInt(TEMP_INVULNERABILITY, ticks - 1);
        }
    }

    public static boolean hasTemporaryInvulnerability(Player player) {
        return player.getPersistentData().getInt(TEMP_INVULNERABILITY) > 0;
    }

    public static boolean isProcessingCursedDamage(LivingEntity entity) {
        return entity.getPersistentData().getBoolean(CURSED_PROCESSING);
    }

    public static void setProcessingCursedDamage(LivingEntity entity, boolean value) {
        entity.getPersistentData().putBoolean(CURSED_PROCESSING, value);
    }

    public static boolean isFrozenOrStunned(LivingEntity entity) {
        return entity.hasEffect(MineCellsStatusEffects.FROZEN.get()) || entity.hasEffect(MineCellsStatusEffects.STUNNED.get());
    }
}
