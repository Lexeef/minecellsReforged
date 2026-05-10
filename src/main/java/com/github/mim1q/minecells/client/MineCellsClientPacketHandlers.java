package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.client.screen.CellCrafterScreen;
import com.github.mim1q.minecells.client.toast.CellCrafterRecipeToast;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public final class MineCellsClientPacketHandlers {
    private MineCellsClientPacketHandlers() {
    }

    public static void handleUnlockedCellCrafterRecipes(Map<ResourceLocation, Boolean> unlockedRecipes) {
        Minecraft minecraft = Minecraft.getInstance();
        var newlyUnlocked = MineCellsClientData.updateUnlockedCellCrafterRecipes(unlockedRecipes);
        if (minecraft.screen instanceof CellCrafterScreen cellCrafterScreen) {
            cellCrafterScreen.updateUnlockedRecipes(unlockedRecipes);
        }
        if (minecraft.level != null) {
            for (ResourceLocation recipeId : newlyUnlocked) {
                minecraft.level.getRecipeManager().byKey(recipeId)
                    .filter(CellForgeRecipe.class::isInstance)
                    .map(CellForgeRecipe.class::cast)
                    .ifPresent(recipe -> minecraft.getToasts().addToast(new CellCrafterRecipeToast(recipe)));
            }
        }
    }
}
