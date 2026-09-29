package com.github.mim1q.minecells.recipe;

import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Optional;

public record CellForgeRecipe(
    ResourceLocation id,
    Map<Item, Integer> ingredients,
    ItemStack output,
    Optional<ResourceLocation> requiredAdvancement,
    int priority,
    Category category
) implements Recipe<Container> {
    @Override
    public boolean matches(Container container, Level level) {
        for (Map.Entry<Item, Integer> ingredient : ingredients.entrySet()) {
            int count = 0;
            for (int slot = 0; slot < container.getContainerSize(); slot++) {
                ItemStack stack = container.getItem(slot);
                if (stack.is(ingredient.getKey())) {
                    count += stack.getCount();
                }
            }
            if (count < ingredient.getValue()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> result = NonNullList.create();
        ingredients.forEach((item, count) -> {
            ItemStack stack = new ItemStack(item, count);
            result.add(Ingredient.of(stack));
        });
        return result;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MineCellsRecipeTypes.CELL_FORGE_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return MineCellsRecipeTypes.CELL_FORGE_RECIPE_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public boolean matchesInventory(Inventory inventory, Level level) {
        return matches(inventory, level);
    }

    public void consumeIngredients(Inventory inventory) {
        for (Map.Entry<Item, Integer> ingredient : ingredients.entrySet()) {
            int remaining = ingredient.getValue();
            for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
                ItemStack stack = inventory.getItem(slot);
                if (!stack.is(ingredient.getKey())) {
                    continue;
                }

                int taken = Math.min(stack.getCount(), remaining);
                stack.shrink(taken);
                remaining -= taken;
            }
        }

        inventory.setChanged();
    }

    public boolean isUnlockedFor(ServerPlayer player) {
        if (requiredAdvancement.isEmpty()) {
            return true;
        }

        Advancement advancement = player.server.getAdvancements().getAdvancement(requiredAdvancement.get());
        if (advancement == null) {
            return true;
        }

        return player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    public enum Category implements StringRepresentable {
        GEAR("gear", Component.translatable("block.minecells.cell_crafter.category.gear")),
        DECORATION("decoration", Component.translatable("block.minecells.cell_crafter.category.decoration")),
        OTHER("other", Component.translatable("block.minecells.cell_crafter.category.other"));

        private final String serializedName;
        private final Component name;

        Category(String serializedName, Component name) {
            this.serializedName = serializedName;
            this.name = name;
        }

        public static Category byName(String name) {
            for (Category category : values()) {
                if (category.serializedName.equals(name)) {
                    return category;
                }
            }
            return OTHER;
        }

        public ItemStack getDisplayStack() {
            return switch (this) {
                case GEAR -> new ItemStack(MineCellsItems.BLOOD_SWORD.get());
                case DECORATION -> new ItemStack(MineCellsBlocks.KINGS_CREST_FLAG.get().asItem());
                case OTHER -> new ItemStack(MineCellsItems.CONCIERGE_RESPAWN_RUNE.get());
            };
        }

        public Component getName() {
            return name;
        }

        @Override
        public String getSerializedName() {
            return serializedName;
        }
    }
}
