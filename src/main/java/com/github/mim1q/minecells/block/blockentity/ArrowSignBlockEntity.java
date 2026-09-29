package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.registry.MineCellsBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

public class ArrowSignBlockEntity extends MineCellsBlockEntity {
    private ItemStack itemStack = ItemStack.EMPTY;
    private BlockState chainState = Blocks.AIR.defaultBlockState();
    private int verticalRotation = 8;

    public ArrowSignBlockEntity(BlockPos pos, BlockState state) {
        super(MineCellsBlockEntities.ARROW_SIGN.get(), pos, state);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        setChanged();
        sync();
    }

    public int getVerticalRotation() {
        return verticalRotation;
    }

    public BlockState getChainState() {
        return chainState;
    }

    public void setChainState(BlockState chainState) {
        this.chainState = chainState;
        setChanged();
        sync();
    }

    public void cycleVerticalRotation(int amount) {
        verticalRotation = Math.floorMod(verticalRotation + amount, 16);
        setChanged();
        sync();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("itemStack", itemStack.save(new CompoundTag()));
        tag.putInt("verticalRotation", verticalRotation);
        BlockState.CODEC.encodeStart(NbtOps.INSTANCE, chainState).result().ifPresent(chainTag -> tag.put("chainState", chainTag));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("itemStack")) {
            itemStack = ItemStack.of(tag.getCompound("itemStack"));
        } else {
            itemStack = tag.contains("Item") ? ItemStack.of(tag.getCompound("Item")) : ItemStack.EMPTY;
        }
        verticalRotation = tag.contains("verticalRotation") ? tag.getInt("verticalRotation") : tag.getInt("VerticalRotation");
        chainState = tag.contains("chainState")
            ? BlockState.CODEC.parse(NbtOps.INSTANCE, tag.get("chainState")).result().orElse(Blocks.AIR.defaultBlockState())
            : Blocks.AIR.defaultBlockState();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    private void sync() {
        if (level != null) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }
}
