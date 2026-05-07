package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.block.CellCrafterBlock;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.screen.cellcrafter.CellCrafterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Queue;

public class CellCrafterBlockEntity extends MineCellsBlockEntity implements MenuProvider {
    private static final String CRAFTED_ITEMS_TAG = "CraftedItems";
    private static final String COOLDOWN_TAG = "Cooldown";

    private final Queue<ItemStack> craftedItems = new LinkedList<>();
    private int cooldown = 0;

    public CellCrafterBlockEntity(BlockPos pos, BlockState state) {
        super(MineCellsBlockEntities.CELL_CRAFTER.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(CellCrafterBlock.CELL_FORGE_TITLE_KEY);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new CellCrafterMenu(containerId, inventory, worldPosition);
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
        setChanged();
    }

    public void addStack(ItemStack stack) {
        craftedItems.add(stack.copy());
        setChanged();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) {
            return;
        }

        if (cooldown > 0) {
            if (cooldown % 10 == 0) {
                level.playSound(null, pos, MineCellsSounds.BUZZ.get(), SoundSource.BLOCKS, 0.2f, 0.8f + level.random.nextFloat() * 0.4f);
            }
            ((ServerLevel) level).sendParticles(
                MineCellsParticles.ELECTRICITY.get().get(Vec3.ZERO.add(level.random.nextGaussian(), level.random.nextGaussian(), level.random.nextGaussian()), 1, 0xFFFFFF, 0.5f),
                pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 1,
                0, 0, 0, 0
            );
            ((ServerLevel) level).sendParticles(
                MineCellsParticles.SPECKLE.get().get(0x00FFFF),
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 1,
                0.5, 0, 0.5, 0.01
            );
            setCooldown(cooldown - 1);
            updateStatus(level, pos, state, CellCrafterBlock.Status.CRAFTING);
            return;
        }

        if (!craftedItems.isEmpty()) {
            ItemStack next = craftedItems.poll();
            Block.popResource(level, pos.above(), next);
            setCooldown(10);
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5f, 0.8f + level.random.nextFloat() * 0.4f);
            ((ServerLevel) level).sendParticles(
                MineCellsParticles.SPECKLE.get().get(0xEE4460),
                pos.getX() + 0.5, pos.getY() + 1.75, pos.getZ() + 0.5, 15,
                0.2, 0.2, 0.2, 0.02
            );
            setChanged();
            updateStatus(level, pos, state, CellCrafterBlock.Status.CRAFTING);
            return;
        }

        updateStatus(level, pos, state, CellCrafterBlock.Status.IDLE);
    }

    private void updateStatus(Level level, BlockPos pos, BlockState state, CellCrafterBlock.Status status) {
        if (state.getValue(CellCrafterBlock.STATUS) != status) {
            level.setBlock(pos, state.setValue(CellCrafterBlock.STATUS, status), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag craftedItemsTag = new ListTag();
        for (ItemStack itemStack : craftedItems) {
            craftedItemsTag.add(itemStack.save(new CompoundTag()));
        }
        tag.put(CRAFTED_ITEMS_TAG, craftedItemsTag);
        tag.putInt(COOLDOWN_TAG, cooldown);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        craftedItems.clear();
        for (Tag itemTag : tag.getList(CRAFTED_ITEMS_TAG, Tag.TAG_COMPOUND)) {
            craftedItems.add(ItemStack.of((CompoundTag) itemTag));
        }
        cooldown = tag.getInt(COOLDOWN_TAG);
    }
}
