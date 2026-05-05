package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.BarrierControllerBlockEntity;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BarrierControllerBlock extends ConditionalBarrierBlock implements EntityBlock {
    private final BarrierPredicate predicate;

    public BarrierControllerBlock(Properties properties, BarrierPredicate predicate) {
        super(properties);
        this.predicate = predicate;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BarrierControllerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == MineCellsBlockEntities.BARRIER_CONTROLLER.get()
            ? (tickerLevel, pos, tickerState, blockEntity) -> ((BarrierControllerBlockEntity) blockEntity).tick(tickerLevel, pos, tickerState)
            : null;
    }

    public boolean shouldBeOpen(Level level, BlockPos pos, BlockState state) {
        return predicate.test(level, pos, state);
    }

    @FunctionalInterface
    public interface BarrierPredicate {
        boolean test(Level level, BlockPos pos, BlockState state);
    }

    public static boolean bossPredicate(Level level, BlockPos pos, BlockState state) {
        return playerPredicate(level, pos, state);
    }

    public static boolean bossEntryPredicate(Level level, BlockPos pos, BlockState state) {
        return playerPredicate(level, pos, state);
    }

    public static boolean playerPredicate(Level level, BlockPos pos, BlockState state) {
        return level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 4.0D, false) != null;
    }
}
