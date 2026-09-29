package com.github.mim1q.minecells.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;

public class CageBlock extends Block {
    public static final BooleanProperty FLIPPED = BooleanProperty.create("flipped");

    private static final VoxelShape SIDES_SHAPE = Shapes.or(
        Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 2.0D),
        Block.box(1.0D, 0.0D, 14.0D, 15.0D, 16.0D, 15.0D),
        Block.box(1.0D, 0.0D, 2.0D, 2.0D, 16.0D, 14.0D),
        Block.box(14.0D, 0.0D, 2.0D, 15.0D, 16.0D, 14.0D)
    );
    private static final VoxelShape BOTTOM_SHAPE = Shapes.or(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), SIDES_SHAPE);
    private static final VoxelShape TOP_SHAPE = Shapes.or(Block.box(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D), SIDES_SHAPE);

    private final boolean broken;

    public CageBlock(Properties properties, boolean broken) {
        super(properties);
        this.broken = broken;
        registerDefaultState(stateDefinition.any().setValue(FLIPPED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FLIPPED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getPlayer() == null) {
            return defaultBlockState();
        }
        boolean flipped = context.getClickedFace() == Direction.DOWN;
        BlockPos secondPos = flipped ? context.getClickedPos().below() : context.getClickedPos().above();
        BlockState secondState = context.getLevel().getBlockState(secondPos);
        BlockState newState = defaultBlockState().setValue(FLIPPED, flipped);
        if (broken || secondState.isAir()) {
            return newState;
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!broken) {
            BlockPos secondPos = state.getValue(FLIPPED) ? pos.below() : pos.above();
            level.setBlock(secondPos, state.setValue(FLIPPED, !state.getValue(FLIPPED)), 3);
        }
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        super.destroy(level, pos, state);
        if (broken) {
            return;
        }
        BlockPos secondPos = state.getValue(FLIPPED) ? pos.below() : pos.above();
        BlockState secondState = level.getBlockState(secondPos);
        if (secondState.getBlock() instanceof CageBlock && secondState.getValue(FLIPPED) != state.getValue(FLIPPED)) {
            level.destroyBlock(secondPos, true);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FLIPPED) ? TOP_SHAPE : BOTTOM_SHAPE;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }
}
