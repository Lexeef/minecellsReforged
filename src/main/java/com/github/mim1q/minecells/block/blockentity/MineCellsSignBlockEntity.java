package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.registry.MineCellsBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MineCellsSignBlockEntity extends SignBlockEntity {
    public MineCellsSignBlockEntity(BlockPos pos, BlockState state) {
        super(MineCellsBlockEntities.SIGN.get(), pos, state);
    }
}
