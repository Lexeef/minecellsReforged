package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.world.state.PlayerSpecificMineCellsData;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MineCellsClientData {
    private static PlayerSpecificMineCellsData playerData = new PlayerSpecificMineCellsData(new net.minecraft.nbt.CompoundTag());
    private static Map<ResourceLocation, Boolean> unlockedCellCrafterRecipes = Map.of();

    private MineCellsClientData() {
    }

    public static PlayerSpecificMineCellsData getPlayerData() {
        return playerData;
    }

    public static void setPlayerData(PlayerSpecificMineCellsData data) {
        playerData = data;
    }

    public static List<ResourceLocation> updateUnlockedCellCrafterRecipes(Map<ResourceLocation, Boolean> unlockedRecipes) {
        Map<ResourceLocation, Boolean> previous = unlockedCellCrafterRecipes;
        unlockedCellCrafterRecipes = Map.copyOf(unlockedRecipes);
        if (previous.isEmpty()) {
            return List.of();
        }

        List<ResourceLocation> newlyUnlocked = new java.util.ArrayList<>();
        for (Map.Entry<ResourceLocation, Boolean> entry : unlockedRecipes.entrySet()) {
            if (entry.getValue() && !previous.getOrDefault(entry.getKey(), false)) {
                newlyUnlocked.add(entry.getKey());
            }
        }
        return List.copyOf(newlyUnlocked);
    }

    public static Map<ResourceLocation, Boolean> getUnlockedCellCrafterRecipes() {
        return new LinkedHashMap<>(unlockedCellCrafterRecipes);
    }

    public static void clearUnlockedCellCrafterRecipes() {
        unlockedCellCrafterRecipes = Map.of();
    }

    public static void resetSessionData() {
        playerData = new PlayerSpecificMineCellsData(new net.minecraft.nbt.CompoundTag());
        unlockedCellCrafterRecipes = Map.of();
        com.github.mim1q.minecells.util.SyncedAdvancements.reset();
    }
}
