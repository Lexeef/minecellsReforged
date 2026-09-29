package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.registry.MineCellsBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class DecorativeStatueBlockEntity extends MineCellsBlockEntity {
    public DecorativeStatueBlockEntity(BlockPos pos, BlockState state) {
        super(MineCellsBlockEntities.DECORATIVE_STATUE_BLOCK_ENTITY.get(), pos, state);
    }
}
