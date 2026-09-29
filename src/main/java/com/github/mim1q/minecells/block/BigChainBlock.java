package com.github.mim1q.minecells.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;

public class BigChainBlock extends ChainBlock {
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    public BigChainBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(CONNECTED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTED);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return getHangingState(super.updateShape(state, direction, neighborState, level, pos, neighborPos), level, pos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var state = super.getStateForPlacement(context);
        return state == null ? null : getHangingState(state, context.getLevel(), context.getClickedPos());
    }

    private BlockState getHangingState(BlockState state, LevelAccessor level, BlockPos pos) {
        if (state.getValue(AXIS).isHorizontal()) {
            return state;
        }
        BlockState stateBelow = level.getBlockState(pos.below());
        boolean cageBelow = stateBelow.getBlock() instanceof CageBlock && stateBelow.getValue(CageBlock.FLIPPED);
        boolean solidBelow = stateBelow.isFaceSturdy(level, pos.below(), Direction.UP);
        return state.setValue(CONNECTED, cageBelow || solidBelow);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(AXIS) == Direction.Axis.Y ? Shapes.empty() : super.getCollisionShape(state, level, pos, context);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(AXIS)) {
            case X -> Block.box(0.0D, 5.0D, 5.0D, 16.0D, 11.0D, 11.0D);
            case Z -> Block.box(5.0D, 5.0D, 0.0D, 11.0D, 11.0D, 16.0D);
            default -> Block.box(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D);
        };
    }
}
