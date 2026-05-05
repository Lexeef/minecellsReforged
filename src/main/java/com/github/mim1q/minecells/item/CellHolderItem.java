package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.registry.MineCellsItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static java.lang.Math.min;

public class CellHolderItem extends Item {
    private static final String EMPTY_KEY = "item.minecells.cell_holder.empty";
    private static final String CELL_COUNT_KEY = "item.minecells.cell_holder.cell_count";
    private static final String FULL_KEY = "item.minecells.cell_holder.full";

    public CellHolderItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack otherStack, Slot slot, ClickAction action, Player player, SlotAccess access) {
        int maxToDeposit = action == ClickAction.PRIMARY ? 64 : 1;
        if (otherStack.is(MineCellsItems.MONSTER_CELL.get())) {
            int count = min(maxToDeposit, otherStack.getCount());
            otherStack.shrink(count);
            setCellCount(stack, getCellCount(stack) + count);
            return true;
        }
        if (otherStack.isEmpty() && action == ClickAction.SECONDARY) {
            int maxToWithdraw = min(64, getCellCount(stack));
            if (maxToWithdraw == 0) {
                return false;
            }
            setCellCount(stack, getCellCount(stack) - maxToWithdraw);
            access.set(new ItemStack(MineCellsItems.MONSTER_CELL.get(), maxToWithdraw));
            return true;
        }
        return super.overrideOtherStackedOnMe(stack, otherStack, slot, action, player, access);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        int count = getCellCount(stack);
        if (count == 0) {
            tooltip.add(Component.translatable(EMPTY_KEY).withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable(CELL_COUNT_KEY, count));
            tooltip.add(Component.translatable(FULL_KEY).withStyle(ChatFormatting.GRAY));
        }
    }

    public static int getCellCount(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag == null ? 0 : tag.getInt("Cells");
    }

    public static void setCellCount(ItemStack stack, int count) {
        stack.getOrCreateTag().putInt("Cells", count);
    }
}
