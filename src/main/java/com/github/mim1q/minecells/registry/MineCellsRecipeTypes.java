package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.recipe.CellForgeRecipeSerializer;
import com.github.mim1q.minecells.recipe.ClearDoorwayRecipe;
import com.github.mim1q.minecells.recipe.CloneDoorwayRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class MineCellsRecipeTypes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MineCells.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MineCells.MOD_ID);

    public static final RegistryObject<RecipeType<CellForgeRecipe>> CELL_FORGE_RECIPE_TYPE = RECIPE_TYPES.register(
        "cell_forge_recipe",
        () -> new RecipeType<>() {
            @Override
            public String toString() {
                return MineCells.id("cell_forge_recipe").toString();
            }
        }
    );
    public static final RegistryObject<RecipeSerializer<CellForgeRecipe>> CELL_FORGE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(
        "cell_forge_recipe",
        CellForgeRecipeSerializer::new
    );
    public static final RegistryObject<RecipeSerializer<ClearDoorwayRecipe>> CLEAR_DOORWAY_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(
        "clear_doorway_recipe",
        () -> new SimpleCraftingRecipeSerializer<>(ClearDoorwayRecipe::new)
    );
    public static final RegistryObject<RecipeSerializer<CloneDoorwayRecipe>> CLONE_DOORWAY_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(
        "clone_doorway_recipe",
        () -> new SimpleCraftingRecipeSerializer<>(CloneDoorwayRecipe::new)
    );

    private MineCellsRecipeTypes() {
    }

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
    }
}
