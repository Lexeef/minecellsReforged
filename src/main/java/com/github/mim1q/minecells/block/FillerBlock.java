package com.github.mim1q.minecells.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.function.Predicate;

public abstract class FillerBlock extends Block {
    protected static final int MAX_DEPTH = 7;

    private final Predicate<Block> targetBlockPredicate;
    private final boolean usable;

    public FillerBlock(Properties properties, Predicate<Block> targetBlockPredicate, boolean usable) {
        super(properties);
        this.targetBlockPredicate = targetBlockPredicate;
        this.usable = usable;
    }

    public FillerBlock(Properties properties, Block targetBlock, boolean usable) {
        super(properties);
        this.targetBlockPredicate = block -> block == targetBlock;
        this.usable = usable;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!usable) {
            return InteractionResult.PASS;
        }
        BlockPos targetPos = findTarget(level, pos, 0);
        if (targetPos == null) {
            return InteractionResult.FAIL;
        }
        BlockState targetState = level.getBlockState(targetPos);
        return targetState.getBlock().use(targetState, level, targetPos, player, hand, hit);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (!level.isClientSide()) {
            destroyNeighbors(level, pos, this, targetBlockPredicate);
        }
        super.destroy(level, pos, state);
    }

    private BlockPos findTarget(Level level, BlockPos pos, int depth) {
        if (depth >= MAX_DEPTH) {
            return null;
        }
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.values()) {
            mutable.setWithOffset(pos, direction);
            BlockState neighborState = level.getBlockState(mutable);
            if (targetBlockPredicate.test(neighborState.getBlock())) {
                return mutable.immutable();
            }
            BlockPos newTargetPos = findTarget(level, mutable, depth + 1);
            if (newTargetPos != null) {
                return newTargetPos;
            }
        }
        return null;
    }

    public static void destroyNeighbors(LevelAccessor level, BlockPos pos, Block filler, Predicate<Block> targetPredicate) {
        for (BlockPos neighbor : getNeighbors(pos)) {
            Block block = level.getBlockState(neighbor).getBlock();
            if (block == filler || targetPredicate.test(block)) {
                level.destroyBlock(neighbor, true);
                destroyNeighbors(level, neighbor, filler, targetPredicate);
            }
        }
    }

    private static ArrayList<BlockPos> getNeighbors(BlockPos pos) {
        ArrayList<BlockPos> result = new ArrayList<>();
        result.add(pos.above());
        result.add(pos.below());
        result.add(pos.north());
        result.add(pos.south());
        result.add(pos.east());
        result.add(pos.west());
        return result;
    }
}
