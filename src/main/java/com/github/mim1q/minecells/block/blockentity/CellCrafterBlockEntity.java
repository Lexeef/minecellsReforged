package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CellCrafterBlockEntity extends MineCellsBlockEntity {
    public CellCrafterBlockEntity(BlockPos pos, BlockState state) {
        super(MineCellsBlockEntities.CELL_CRAFTER.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
    }
}
