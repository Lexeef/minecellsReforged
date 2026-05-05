package com.github.mim1q.minecells.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CellForgeRecipeSerializer implements RecipeSerializer<CellForgeRecipe> {
    @Override
    public CellForgeRecipe fromJson(ResourceLocation id, JsonObject json) {
        Map<Item, Integer> ingredients = new LinkedHashMap<>();
        JsonObject input = GsonHelper.getAsJsonObject(json, "input");
        for (Map.Entry<String, com.google.gson.JsonElement> entry : input.entrySet()) {
            ResourceLocation itemId = new ResourceLocation(entry.getKey());
            Item item = BuiltInRegistries.ITEM.getOptional(itemId)
                .orElseThrow(() -> new JsonParseException("Unknown item in cell forge recipe input: " + itemId));
            ingredients.put(item, GsonHelper.convertToInt(entry.getValue(), "input." + entry.getKey()));
        }

        ItemStack output = readOutput(GsonHelper.getAsJsonObject(json, "output"));
        Optional<ResourceLocation> advancement = json.has("advancement")
            ? Optional.of(new ResourceLocation(GsonHelper.getAsString(json, "advancement")))
            : Optional.empty();
        int priority = GsonHelper.getAsInt(json, "priority", 0);
        CellForgeRecipe.Category category = CellForgeRecipe.Category.byName(GsonHelper.getAsString(json, "category", "other"));
        return new CellForgeRecipe(id, ingredients, output, advancement, priority, category);
    }

    @Override
    public CellForgeRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        Map<Item, Integer> ingredients = buffer.readMap(
            byteBuf -> BuiltInRegistries.ITEM.get(byteBuf.readResourceLocation()),
            FriendlyByteBuf::readVarInt
        );
        ItemStack output = buffer.readItem();
        Optional<ResourceLocation> advancement = buffer.readOptional(FriendlyByteBuf::readResourceLocation);
        int priority = buffer.readVarInt();
        CellForgeRecipe.Category category = buffer.readEnum(CellForgeRecipe.Category.class);
        return new CellForgeRecipe(id, ingredients, output, advancement, priority, category);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, CellForgeRecipe recipe) {
        buffer.writeMap(
            recipe.ingredients(),
            (byteBuf, item) -> byteBuf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(item)),
            FriendlyByteBuf::writeVarInt
        );
        buffer.writeItem(recipe.output());
        buffer.writeOptional(recipe.requiredAdvancement(), FriendlyByteBuf::writeResourceLocation);
        buffer.writeVarInt(recipe.priority());
        buffer.writeEnum(recipe.category());
    }

    private static ItemStack readOutput(JsonObject output) {
        ResourceLocation itemId = new ResourceLocation(GsonHelper.getAsString(output, "id"));
        Item item = BuiltInRegistries.ITEM.getOptional(itemId)
            .orElseThrow(() -> new JsonParseException("Unknown item in cell forge recipe output: " + itemId));
        int count = output.has("Count")
            ? GsonHelper.getAsInt(output, "Count")
            : GsonHelper.getAsInt(output, "count", 1);
        return new ItemStack(item, count);
    }
}
