package com.github.mim1q.minecells.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class HangingLeavesBlock extends HorizontalDirectionalBlock {
    public static final VoxelShape SHAPE = Block.box(2.0D, 6.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public HangingLeavesBlock(Properties properties) {
        super(properties.offsetType(OffsetType.XZ));
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getClickedFace() != Direction.DOWN) {
            return null;
        }
        BlockPos supportPos = context.getClickedPos().above();
        BlockState supportState = context.getLevel().getBlockState(supportPos);
        if (!(supportState.isFaceSturdy(context.getLevel(), supportPos, Direction.DOWN) || supportState.is(BlockTags.LEAVES))) {
            return null;
        }
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (neighborPos.equals(pos.above())) {
            boolean stay = neighborState.getBlock() instanceof LeavesBlock || neighborState.isFaceSturdy(level, neighborPos, Direction.DOWN);
            return stay ? state : Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
