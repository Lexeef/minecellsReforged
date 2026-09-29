package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsParticles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

public class SkeletonDecorationBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final VoxelShape SHAPE = Block.box(3.0D, 3.0D, 3.0D, 13.0D, 13.0D, 13.0D);

    private final boolean sitting;
    private final Block hangingBlock;

    public SkeletonDecorationBlock(Properties properties) {
        super(properties);
        this.sitting = false;
        this.hangingBlock = null;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public SkeletonDecorationBlock(Properties properties, Block hangingBlock) {
        super(properties);
        this.sitting = true;
        this.hangingBlock = hangingBlock;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getClickedFace() == Direction.DOWN) {
            BlockPos supportPos = context.getClickedPos().above();
            if (hangingBlock != null && context.getLevel().getBlockState(supportPos).getBlock() instanceof ChainBlock) {
                return hangingBlock.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
            }
        } else {
            BlockPos supportPos = context.getClickedPos().below();
            if (context.getLevel().getBlockState(supportPos).isRedstoneConductor(context.getLevel(), supportPos)) {
                return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
            }
        }
        return null;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (sitting) {
            return state;
        }
        if (neighborPos.equals(pos.above())) {
            if (neighborState.getBlock() == Blocks.CHAIN && neighborState.getValue(ChainBlock.AXIS) == Direction.Axis.Y) {
                return state;
            }
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (state.is(MineCellsBlocks.SKELETON.get()) || state.is(MineCellsBlocks.HANGED_SKELETON.get())) {
            return;
        }
        float chance = 0.2F;
        if (state.is(MineCellsBlocks.ROTTING_CORPSE.get()) || state.is(MineCellsBlocks.HANGED_ROTTING_CORPSE.get())) {
            chance = 0.6F;
        }
        if (random.nextFloat() <= chance) {
            Vec3 particlePos = Vec3.atCenterOf(pos).add(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F);
            level.addParticle(MineCellsParticles.FLY.get(), particlePos.x, particlePos.y, particlePos.z, 0.0D, random.nextFloat() * 0.05F, 0.0D);
        }
    }
}
