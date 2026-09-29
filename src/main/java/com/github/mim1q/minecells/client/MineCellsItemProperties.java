package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.item.CellHolderItem;
import com.github.mim1q.minecells.item.weapon.bow.CustomBowItem;
import com.github.mim1q.minecells.item.weapon.bow.CustomCrossbowItem;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.registry.MineCellsItems;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class MineCellsItemProperties {
    private static final ResourceLocation BLOCKING = new ResourceLocation("minecraft", "blocking");
    private static final ResourceLocation MINECELLS_BLOCKING = MineCells.id("blocking");
    private static final ResourceLocation MINECELLS_CELLS = MineCells.id("cells");
    private static final ResourceLocation MINECELLS_PULLING = MineCells.id("pulling");
    private static final ResourceLocation MINECELLS_PULL = MineCells.id("pull");
    private static final ResourceLocation MINECELLS_CHARGED = MineCells.id("charged");

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

        registerBowProperties(MineCellsItems.BOW_AND_ENDLESS_QUIVER.get());
        registerBowProperties(MineCellsItems.MARKSMANS_BOW.get());
        registerBowProperties(MineCellsItems.INFANTRY_BOW.get());
        registerBowProperties(MineCellsItems.QUICK_BOW.get());
        registerBowProperties(MineCellsItems.ICE_BOW.get());
        registerBowProperties(MineCellsItems.NERVES_OF_STEEL.get());
        registerBowProperties(MineCellsItems.MULTIPLE_NOCKS_BOW.get());
        registerCrossbowProperties(MineCellsItems.HEAVY_CROSSBOW.get());
        registerCrossbowProperties(MineCellsItems.EXPLOSIVE_CROSSBOW.get());
    }

    private static void registerBlocking(Item item, ResourceLocation property) {
        ItemProperties.register(item, property, (stack, level, entity, seed) -> isBlocking(entity, stack) ? 1.0F : 0.0F);
    }

    private static void registerBowProperties(Item item) {
        ItemProperties.register(item, MINECELLS_PULLING, (stack, level, entity, seed) -> isUsing(entity, stack) ? 1.0F : 0.0F);
        ItemProperties.register(item, MINECELLS_PULL, (stack, level, entity, seed) -> {
            if (entity == null || entity.getUseItem() != stack || !(stack.getItem() instanceof CustomBowItem bow)) {
                return 0.0F;
            }
            int drawTime = Math.max(1, bow.getDrawTime(entity, stack));
            return (float) entity.getTicksUsingItem() / (float) drawTime;
        });
    }

    private static void registerCrossbowProperties(Item item) {
        ItemProperties.register(item, MINECELLS_PULLING, (stack, level, entity, seed) -> isUsing(entity, stack) && !CrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
        ItemProperties.register(item, MINECELLS_PULL, (stack, level, entity, seed) -> {
            if (entity == null || entity.getUseItem() != stack) {
                return 0.0F;
            }
            if (CrossbowItem.isCharged(stack)) {
                return 0.0F;
            }
            if (stack.getItem() instanceof CustomCrossbowItem crossbow) {
                int drawTime = Math.max(1, crossbow.getDrawTime(entity, stack));
                return (float) entity.getTicksUsingItem() / (float) drawTime;
            }
            int duration = CrossbowItem.getChargeDuration(stack);
            return duration <= 0 ? 0.0F : (float) (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / (float) duration;
        });
        ItemProperties.register(item, MINECELLS_CHARGED, (stack, level, entity, seed) -> CrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
    }

    private static boolean isBlocking(LivingEntity entity, ItemStack stack) {
        return entity != null && entity.isUsingItem() && entity.getUseItem() == stack;
    }

    private static boolean isUsing(LivingEntity entity, ItemStack stack) {
        return entity != null && entity.isUsingItem() && entity.getUseItem() == stack;
    }
}
