package com.github.mim1q.minecells.recipe;

import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ClearDoorwayRecipe extends CustomRecipe {
    public ClearDoorwayRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        List<ItemStack> stacks = getNonEmptyStacks(container);
        return stacks.size() == 1 && isDoorwayStack(stacks.get(0)) && hasBlockEntityTag(stacks.get(0));
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        List<ItemStack> stacks = getNonEmptyStacks(container);
        if (stacks.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = stacks.get(0).copy();
        result.removeTagKey("BlockEntityTag");
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MineCellsRecipeTypes.CLEAR_DOORWAY_RECIPE_SERIALIZER.get();
    }

    static List<ItemStack> getNonEmptyStacks(CraftingContainer container) {
        List<ItemStack> result = new ArrayList<>();
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (!stack.isEmpty()) {
                result.add(stack);
            }
        }
        return result;
    }

    static boolean isDoorwayStack(ItemStack stack) {
        return Block.byItem(stack.getItem()) instanceof DoorwayPortalBlock;
    }

    static boolean hasBlockEntityTag(ItemStack stack) {
        return stack.getTagElement("BlockEntityTag") != null && !stack.getTagElement("BlockEntityTag").isEmpty();
    }
}
