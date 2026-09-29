package com.github.mim1q.minecells.block.portal;

import com.github.mim1q.minecells.util.ModelUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DoorwayFrameBlock extends Block {
    public static final EnumProperty<FillerType> TYPE = EnumProperty.create("type", FillerType.class);

    public DoorwayFrameBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(DoorwayPortalBlock.FACING, Direction.NORTH).setValue(TYPE, FillerType.MIDDLE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DoorwayPortalBlock.FACING, TYPE);
    }

    public BlockState getState(FillerType type, Direction direction) {
        return defaultBlockState().setValue(DoorwayPortalBlock.FACING, direction).setValue(TYPE, type);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return ModelUtils.rotateShape(Direction.NORTH, state.getValue(DoorwayPortalBlock.FACING), state.getValue(TYPE).outlineShape);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return ModelUtils.rotateShape(Direction.NORTH, state.getValue(DoorwayPortalBlock.FACING), state.getValue(TYPE).collisionShape);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(DoorwayPortalBlock.FACING, rotation.rotate(state.getValue(DoorwayPortalBlock.FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(DoorwayPortalBlock.FACING)));
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (level.isClientSide || level.dimension() != Level.OVERWORLD) {
            return;
        }
        Direction facing = state.getValue(DoorwayPortalBlock.FACING);
        if (fromPos.equals(pos.relative(facing.getOpposite())) && !level.getBlockState(fromPos).isFaceSturdy(level, fromPos, facing)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
        if (level.isClientSide || state.is(newState.getBlock())) {
            return;
        }
        BlockPos breakPos = getBreakPos(state, pos);
        Block target = level.getBlockState(breakPos).getBlock();
        if (target instanceof DoorwayPortalBlock || target instanceof DoorwayFrameBlock) {
            level.destroyBlock(breakPos, true);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockPos targetPos = getBreakPos(state, pos);
        BlockState target = level.getBlockState(targetPos);
        if (target.getBlock() instanceof DoorwayPortalBlock || target.getBlock() instanceof DoorwayFrameBlock) {
            return target.use(level, player, hand, hit.withPosition(targetPos));
        }
        return InteractionResult.PASS;
    }

    private static BlockPos getBreakPos(BlockState state, BlockPos pos) {
        Direction side = state.getValue(DoorwayPortalBlock.FACING).getCounterClockWise();
        Vec3i offset = state.getValue(TYPE).breakOffset;
        return pos.offset(offset.getX() * side.getStepX(), offset.getY(), offset.getX() * side.getStepZ());
    }

    public enum FillerType implements StringRepresentable {
        MIDDLE(
            Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D),
            Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D),
            "middle",
            new Vec3i(0, 1, 0)
        ),
        RIGHT(
            Block.box(4.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D),
            Block.box(4.0D, 0.0D, 8.0D, 12.0D, 16.0D, 16.0D),
            "right",
            new Vec3i(-1, 0, 0)
        ),
        LEFT(
            Block.box(0.0D, 0.0D, 8.0D, 12.0D, 16.0D, 16.0D),
            Block.box(4.0D, 0.0D, 8.0D, 12.0D, 16.0D, 16.0D),
            "left",
            new Vec3i(1, 0, 0)
        ),
        TOP_RIGHT(
            Block.box(4.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D),
            Shapes.or(
                Block.box(4.0D, 0.0D, 8.0D, 12.0D, 16.0D, 16.0D),
                Block.box(12.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D)
            ),
            "top_right",
            new Vec3i(-1, 0, 0)
        ),
        TOP_LEFT(
            Block.box(0.0D, 0.0D, 8.0D, 12.0D, 16.0D, 16.0D),
            Shapes.or(
                Block.box(4.0D, 0.0D, 8.0D, 12.0D, 16.0D, 16.0D),
                Block.box(0.0D, 8.0D, 8.0D, 4.0D, 16.0D, 16.0D)
            ),
            "top_left",
            new Vec3i(1, 0, 0)
        ),
        TOP(
            Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D),
            Block.box(0.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D),
            "top",
            new Vec3i(0, -1, 0)
        );

        public final VoxelShape outlineShape;
        public final VoxelShape collisionShape;
        private final String name;
        private final Vec3i breakOffset;

        FillerType(VoxelShape outlineShape, VoxelShape collisionShape, String name, Vec3i breakOffset) {
            this.outlineShape = outlineShape;
            this.collisionShape = collisionShape;
            this.name = name;
            this.breakOffset = breakOffset;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
