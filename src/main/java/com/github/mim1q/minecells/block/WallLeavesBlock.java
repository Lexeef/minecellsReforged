package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.util.ModelUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;

public class WallLeavesBlock extends Block {
    public static final DirectionProperty DIRECTION = BlockStateProperties.FACING;
    public static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D);
    public static final VoxelShape SHAPE_TOP = Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    public static final VoxelShape SHAPE_BOTTOM = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);

    public WallLeavesBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(DIRECTION, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DIRECTION);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos supportPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
        BlockState supportState = context.getLevel().getBlockState(supportPos);
        if (!(supportState.isFaceSturdy(context.getLevel(), supportPos, context.getClickedFace()) || supportState.is(BlockTags.LEAVES))) {
            return null;
        }
        return defaultBlockState().setValue(DIRECTION, context.getClickedFace());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == state.getValue(DIRECTION).getOpposite()) {
            boolean stay = neighborState.getBlock() instanceof LeavesBlock || neighborState.isFaceSturdy(level, neighborPos, direction);
            return stay ? state : Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(DIRECTION, rotation.rotate(state.getValue(DIRECTION)));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(DIRECTION);
        if (direction == Direction.DOWN) {
            return SHAPE_TOP;
        }
        if (direction == Direction.UP) {
            return SHAPE_BOTTOM;
        }
        return ModelUtils.rotateShape(Direction.NORTH, direction, SHAPE);
    }

    @Override
    public float getMaxHorizontalOffset() {
        return 0.125F;
    }
}
