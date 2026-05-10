package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.item.CellHolderItem;
import com.github.mim1q.minecells.registry.MineCellsItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class MineCellsItemProperties {
    private static final ResourceLocation BLOCKING = new ResourceLocation("minecraft", "blocking");
    private static final ResourceLocation MINECELLS_BLOCKING = MineCells.id("blocking");
    private static final ResourceLocation MINECELLS_CELLS = MineCells.id("cells");

    private MineCellsItemProperties() {
    }

    public static void register() {
        ItemProperties.register(MineCellsItems.CELL_HOLDER.get(), MINECELLS_CELLS, (stack, level, entity, seed) -> {
            int cells = CellHolderItem.getCellCount(stack);
            if (cells > 128) return 1.0F;
            if (cells > 64) return 0.75F;
            if (cells > 0) return 0.5F;
            return 0.0F;
        });

        registerBlocking(MineCellsItems.HATTORIS_KATANA.get(), BLOCKING);
        registerBlocking(MineCellsItems.CUDGEL.get(), MINECELLS_BLOCKING);
        registerBlocking(MineCellsItems.RAMPART.get(), MINECELLS_BLOCKING);
        registerBlocking(MineCellsItems.ASSAULT_SHIELD.get(), MINECELLS_BLOCKING);
        registerBlocking(MineCellsItems.BLOODTHIRSTY_SHIELD.get(), MINECELLS_BLOCKING);
        registerBlocking(MineCellsItems.GREED_SHIELD.get(), MINECELLS_BLOCKING);
        registerBlocking(MineCellsItems.ICE_SHIELD.get(), MINECELLS_BLOCKING);
    }

    private static void registerBlocking(Item item, ResourceLocation property) {
        ItemProperties.register(item, property, (stack, level, entity, seed) -> isBlocking(entity, stack) ? 1.0F : 0.0F);
    }

    private static boolean isBlocking(LivingEntity entity, ItemStack stack) {
        return entity != null && entity.isUsingItem() && entity.getUseItem() == stack;
    }
}
