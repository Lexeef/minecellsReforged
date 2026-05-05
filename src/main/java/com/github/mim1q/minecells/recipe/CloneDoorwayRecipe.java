package com.github.mim1q.minecells.recipe;

import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;

public class CloneDoorwayRecipe extends CustomRecipe {
    public CloneDoorwayRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        List<ItemStack> stacks = ClearDoorwayRecipe.getNonEmptyStacks(container);
        if (stacks.size() != 2) {
            return false;
        }
        ItemStack first = stacks.get(0);
        ItemStack second = stacks.get(1);
        return ClearDoorwayRecipe.isDoorwayStack(first)
            && second.is(first.getItem())
            && ClearDoorwayRecipe.hasBlockEntityTag(first) != ClearDoorwayRecipe.hasBlockEntityTag(second);
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        return ClearDoorwayRecipe.getNonEmptyStacks(container)
            .stream()
            .filter(ClearDoorwayRecipe::hasBlockEntityTag)
            .findFirst()
            .map(stack -> stack.copyWithCount(2))
            .orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MineCellsRecipeTypes.CLONE_DOORWAY_RECIPE_SERIALIZER.get();
    }
}
