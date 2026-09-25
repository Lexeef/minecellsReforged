package com.github.mim1q.minecells.screen.cellcrafter;

import com.github.mim1q.minecells.registry.MineCellsMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CellCrafterMenu extends AbstractContainerMenu {
    private final BlockPos blockPos;
    private final Level level;

    public CellCrafterMenu(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
        this(containerId, inventory, buffer.readBlockPos());
    }

    public CellCrafterMenu(int containerId, Inventory inventory, BlockPos blockPos) {
        super(MineCellsMenus.CELL_CRAFTER.get(), containerId);
        this.blockPos = blockPos.immutable();
        this.level = inventory.player.level();

        int xOffset = 8;
        int yOffset = 77;

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new ToggleableSlot(inventory, column + row * 9 + 9, xOffset + column * 18, yOffset + row * 18));
            }
        }

        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            addSlot(new ToggleableSlot(inventory, hotbarSlot, xOffset + hotbarSlot * 18, yOffset + 58));
        }
    }

    public void setSlotsActive(boolean active) {
        for (Slot slot : slots) {
            if (slot instanceof ToggleableSlot toggleable) {
                toggleable.active = active;
            }
        }
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public boolean hasValidBlockEntity() {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        return blockEntity != null;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.level() == level && player.distanceToSqr(
            blockPos.getX() + 0.5D,
            blockPos.getY() + 0.5D,
            blockPos.getZ() + 0.5D
        ) <= 64.0D && hasValidBlockEntity();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    private static final class ToggleableSlot extends Slot {
        private boolean active = true;

        private ToggleableSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean isActive() {
            return active;
        }
    }
}
