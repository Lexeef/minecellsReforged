package com.github.mim1q.minecells.compat.jei;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.compat.DoorwayRecipeExamples;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.Comparator;
import java.util.List;

@JeiPlugin
public class MineCellsJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID = MineCells.id("jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
            new JeiCellCrafterCategory(guiHelper, getCellCrafterRecipes()),
            new JeiBloodBottleCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(JeiCellCrafterCategory.TYPE, getCellCrafterRecipes());
        registration.addRecipes(JeiBloodBottleCategory.TYPE, List.of(JeiBloodBottleCategory.Recipe.INSTANCE));
        registration.addRecipes(RecipeTypes.CRAFTING, getDoorwayCraftingExamples());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(MineCellsBlocks.CELL_CRAFTER.get()), JeiCellCrafterCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MineCellsBlocks.SPIKES.get()), JeiBloodBottleCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MineCellsItems.BLOOD_BOTTLE.get()), JeiBloodBottleCategory.TYPE);
    }

    private static List<CellForgeRecipe> getCellCrafterRecipes() {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }
        RecipeManager recipeManager = level.getRecipeManager();
        return recipeManager.getAllRecipesFor(MineCellsRecipeTypes.CELL_FORGE_RECIPE_TYPE.get())
            .stream()
            .sorted(Comparator
                .comparingInt((CellForgeRecipe recipe) -> recipe.category().ordinal())
                .thenComparing((a, b) -> b.priority() - a.priority()))
            .toList();
    }

    private static List<CraftingRecipe> getDoorwayCraftingExamples() {
        return DoorwayRecipeExamples.allExamples().stream()
            .map(example -> {
                NonNullList<Ingredient> ingredients = NonNullList.create();
                example.inputs().forEach(stack -> ingredients.add(Ingredient.of(stack)));
                return (CraftingRecipe) new ShapelessRecipe(example.id(), "", CraftingBookCategory.MISC, example.output(), ingredients);
            })
            .toList();
    }
}
