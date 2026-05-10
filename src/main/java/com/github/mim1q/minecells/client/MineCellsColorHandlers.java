package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.item.DimensionalRuneItem;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsItems;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class MineCellsColorHandlers {
    private static final int DEFAULT_FOLIAGE_COLOR = 0x80CC80;

    private MineCellsColorHandlers() {
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(
            (state, level, pos, tintIndex) -> level == null || pos == null ? DEFAULT_FOLIAGE_COLOR : BiomeColors.getAverageFoliageColor(level, pos),
            MineCellsBlocks.WILTED_LEAVES.get(),
            MineCellsBlocks.WILTED_HANGING_LEAVES.get(),
            MineCellsBlocks.WILTED_WALL_LEAVES.get()
        );
        event.register(
            (state, level, pos, tintIndex) -> level == null || pos == null ? DEFAULT_FOLIAGE_COLOR : BiomeColors.getAverageGrassColor(level, pos),
            MineCellsBlocks.WILTED_GRASS_BLOCK.get(),
            MineCellsBlocks.BLOOMROCK_WILTED_GRASS_BLOCK.get()
        );
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(
            (stack, tintIndex) -> tintIndex == 1 && stack.getItem() instanceof DimensionalRuneItem runeItem
                ? runeItem.getPortalBlock().getType().getColor()
                : 0xFFFFFF,
            MineCellsItems.PRISON_DIMENSIONAL_RUNE.get(),
            MineCellsItems.PROMENADE_DIMENSIONAL_RUNE.get(),
            MineCellsItems.RAMPARTS_DIMENSIONAL_RUNE.get(),
            MineCellsItems.INSUFFERABLE_CRYPT_DIMENSIONAL_RUNE.get(),
            MineCellsItems.BLACK_BRIDGE_DIMENSIONAL_RUNE.get()
        );
        event.register(
            (stack, tintIndex) -> tintIndex == 1 ? doorwayColor(stack) : 0xFFFFFF,
            MineCellsBlocks.PRISON_DOORWAY.get().asItem(),
            MineCellsBlocks.PROMENADE_DOORWAY.get().asItem(),
            MineCellsBlocks.INSUFFERABLE_CRYPT_DOORWAY.get().asItem(),
            MineCellsBlocks.RAMPARTS_DOORWAY.get().asItem(),
            MineCellsBlocks.BLACK_BRIDGE_DOORWAY.get().asItem()
        );
        event.register(
            (stack, tintIndex) -> DEFAULT_FOLIAGE_COLOR,
            MineCellsBlocks.WILTED_LEAVES.get().asItem(),
            MineCellsBlocks.WILTED_HANGING_LEAVES.get().asItem(),
            MineCellsBlocks.WILTED_WALL_LEAVES.get().asItem(),
            MineCellsBlocks.WILTED_GRASS_BLOCK.get().asItem(),
            MineCellsBlocks.BLOOMROCK_WILTED_GRASS_BLOCK.get().asItem()
        );
    }

    private static int doorwayColor(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof DoorwayPortalBlock doorwayBlock) {
            return doorwayBlock.getType().getColor();
        }
        return 0xFFFFFF;
    }
}
