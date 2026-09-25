package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.MineCellsSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public final class MineCellsSignBlocks {
    private MineCellsSignBlocks() {
    }

    private static <T extends BlockEntity> BlockEntityTicker<T> signTicker() {
        return (level, pos, state, blockEntity) -> {
            if (blockEntity instanceof SignBlockEntity sign) {
                SignBlockEntity.tick(level, pos, state, sign);
            }
        };
    }

    public static class Standing extends StandingSignBlock {
        public Standing(Properties properties, WoodType woodType) {
            super(properties, woodType);
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new MineCellsSignBlockEntity(pos, state);
        }

        @Override
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
            return signTicker();
        }
    }

    public static class Wall extends WallSignBlock {
        public Wall(Properties properties, WoodType woodType) {
            super(properties, woodType);
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new MineCellsSignBlockEntity(pos, state);
        }

        @Override
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
            return signTicker();
        }
    }
}
