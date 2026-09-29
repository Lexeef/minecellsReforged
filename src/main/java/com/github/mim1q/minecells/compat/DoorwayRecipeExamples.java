package com.github.mim1q.minecells.compat;

import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.MineCells;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Example inputs/outputs for the special doorway crafting recipes (clearing and cloning a bound doorway),
 * which recipe viewers cannot derive on their own because the recipes depend on item NBT.
 */
public final class DoorwayRecipeExamples {
    private DoorwayRecipeExamples() {
    }

    public record Example(ResourceLocation id, List<ItemStack> inputs, ItemStack output) {
    }

    public static List<Item> doorwayItems() {
        List<Item> result = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
            if (MineCells.MOD_ID.equals(key.getNamespace()) && Block.byItem(item) instanceof DoorwayPortalBlock) {
                result.add(item);
            }
        }
        return result;
    }

    public static ItemStack bound(Item item) {
        ItemStack stack = new ItemStack(item);
        CompoundTag blockEntityTag = new CompoundTag();
        blockEntityTag.putLong("posOverride", BlockPos.ZERO.asLong());
        stack.addTagElement("BlockEntityTag", blockEntityTag);
        return stack;
    }

    public static List<Example> clearExamples() {
        return doorwayItems().stream()
            .map(item -> new Example(
                MineCells.id("clear_doorway/" + BuiltInRegistries.ITEM.getKey(item).getPath()),
                List.of(bound(item)),
                new ItemStack(item)
            ))
            .toList();
    }

    public static List<Example> cloneExamples() {
        return doorwayItems().stream()
            .map(item -> new Example(
                MineCells.id("clone_doorway/" + BuiltInRegistries.ITEM.getKey(item).getPath()),
                List.of(bound(item), new ItemStack(item)),
                bound(item).copyWithCount(2)
            ))
            .toList();
    }

    public static List<Example> allExamples() {
        List<Example> result = new ArrayList<>(clearExamples());
        result.addAll(cloneExamples());
        return result;
    }
}
