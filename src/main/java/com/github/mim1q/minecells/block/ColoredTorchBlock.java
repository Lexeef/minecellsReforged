package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.util.ModelUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

public class ColoredTorchBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty STANDING = BooleanProperty.create("standing");
    public static final VoxelShape SHAPE = Block.box(6.0D, 3.0D, 0.0D, 10.0D, 14.0D, 4.0D);
    public static final VoxelShape STANDING_SHAPE = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 11.0D, 10.0D);

    public ColoredTorchBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(STANDING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, STANDING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getClickedFace() == Direction.DOWN) {
            return null;
        }
        if (context.getClickedFace() == Direction.UP) {
            return defaultBlockState().setValue(STANDING, true);
        }
        BlockPos supportPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
        if (context.getLevel().getBlockState(supportPos).isFaceSturdy(context.getLevel(), supportPos, context.getClickedFace())) {
            return defaultBlockState().setValue(FACING, context.getClickedFace());
        }
        return null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(STANDING)) {
            return direction == Direction.DOWN ? Blocks.AIR.defaultBlockState() : state;
        }
        Direction facing = state.getValue(FACING);
        if (direction == facing.getOpposite() && !Block.isFaceFull(neighborState.getCollisionShape(level, pos), facing)) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(STANDING)) {
            return STANDING_SHAPE;
        }
        return ModelUtils.rotateShape(Direction.SOUTH, state.getValue(FACING), SHAPE);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() > 0.5F) {
            return;
        }
        Vec3 particlePos = getOffsetPos(state.getValue(STANDING), pos, state.getValue(FACING));
        level.addParticle(ParticleTypes.SMOKE, particlePos.x(), particlePos.y(), particlePos.z(), 0.0D, 0.02D, 0.0D);
    }

    public static Vec3 getOffsetPos(boolean onGround, BlockPos pos, Direction direction) {
        if (onGround) {
            return Vec3.atBottomCenterOf(pos).add(0.0D, 0.75D, 0.0D);
        }
        double x = 0.5D - 0.4D * direction.getStepX();
        double z = 0.5D - 0.4D * direction.getStepZ();
        return new Vec3(x, 1.0D, z).add(pos.getX(), pos.getY(), pos.getZ());
    }
}
