package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.block.setupblocks.SetupBlock;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SetupBlockEntity extends MineCellsBlockEntity {
    private boolean done = false;

    public SetupBlockEntity(BlockPos pos, BlockState state) {
        super(MineCellsBlockEntities.SETUP_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SetupBlockEntity blockEntity) {
        if (blockEntity.done || level == null || level.isClientSide) {
            return;
        }
        if (level.getGameTime() % 20L == 0L && state.getBlock() instanceof SetupBlock setupBlock) {
            blockEntity.done = setupBlock.setup(level, pos, state);
        }
    }
}
