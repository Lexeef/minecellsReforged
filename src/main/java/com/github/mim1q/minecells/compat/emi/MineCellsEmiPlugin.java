package com.github.mim1q.minecells.compat.emi;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.compat.BlockStateIcon;
import com.github.mim1q.minecells.compat.DoorwayRecipeExamples;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Comparator;

@EmiEntrypoint
public class MineCellsEmiPlugin implements EmiPlugin {
    public static final ResourceLocation CELL_CRAFTER_ID = MineCells.id("cell_crafter");

    private static EmiRecipeCategory cellCrafterCategory;

    public static EmiRecipeCategory cellCrafterCategory() {
        return cellCrafterCategory;
    }

    @Override
    public void register(EmiRegistry registry) {
        cellCrafterCategory = new EmiRecipeCategory(CELL_CRAFTER_ID, EmiStack.of(MineCellsBlocks.CELL_CRAFTER.get()));
        registry.addCategory(cellCrafterCategory);
        registry.addWorkstation(cellCrafterCategory, EmiIngredient.of(Ingredient.of(MineCellsBlocks.CELL_CRAFTER.get())));

        registry.getRecipeManager()
            .getAllRecipesFor(MineCellsRecipeTypes.CELL_FORGE_RECIPE_TYPE.get())
            .stream()
            .sorted(Comparator
                .comparingInt((CellForgeRecipe recipe) -> recipe.category().ordinal())
                .thenComparing((a, b) -> b.priority() - a.priority()))
            .forEach(recipe -> registry.addRecipe(new EmiCellCrafterRecipeDisplay(recipe)));

        registry.addRecipe(
            EmiWorldInteractionRecipe.builder()
                .id(MineCells.id("blood_bottle"))
                .leftInput(new EmiBlockIngredient(BlockStateIcon.bloodySpikes()))
                .rightInput(EmiIngredient.of(Ingredient.of(Items.GLASS_BOTTLE)), false)
                .output(EmiStack.of(MineCellsItems.BLOOD_BOTTLE.get()))
                .build()
        );

        for (DoorwayRecipeExamples.Example example : DoorwayRecipeExamples.allExamples()) {
            registry.addRecipe(new EmiCraftingRecipe(
                example.inputs().stream().map(stack -> (EmiIngredient) EmiStack.of(stack)).toList(),
                EmiStack.of(example.output()),
                example.id(),
                true
            ));
        }
    }
}
