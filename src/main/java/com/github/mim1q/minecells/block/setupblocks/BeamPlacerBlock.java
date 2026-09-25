package com.github.mim1q.minecells.block.setupblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BeamPlacerBlock extends SetupBlock {
    public BeamPlacerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean setup(Level level, BlockPos pos, BlockState state) {
        BlockState stateAbove = level.getBlockState(pos.above());
        for (int i = 0; i < 32; i++) {
            if (!tryPlace(level, pos.below(i), stateAbove)) {
                break;
            }
        }
        level.setBlockAndUpdate(pos, stateAbove);
        return true;
    }

    protected boolean tryPlace(Level level, BlockPos pos, BlockState state) {
        BlockPos[] neighbors = {pos.south(), pos.east(), pos.north(), pos.west()};
        for (BlockPos neighbor : neighbors) {
            if (!level.getBlockState(neighbor).isRedstoneConductor(level, pos)) {
                level.setBlockAndUpdate(pos, state);
                return true;
            }
        }
        return false;
    }
}
