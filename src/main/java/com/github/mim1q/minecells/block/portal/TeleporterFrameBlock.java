package com.github.mim1q.minecells.block.portal;

import com.github.mim1q.minecells.block.FillerBlock;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.util.ModelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class TeleporterFrameBlock extends FillerBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape BASE_SHAPE = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    public TeleporterFrameBlock(Properties properties) {
        super(properties, block -> block == MineCellsBlocks.TELEPORTER_CORE.get(), true);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TYPE, Type.MIDDLE));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return ModelUtils.rotateShape(Direction.SOUTH, state.getValue(FACING), state.getValue(TYPE).shape);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(TYPE) == Type.MIDDLE ? Shapes.empty() : getShape(state, level, pos, context);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, TYPE);
    }

    public enum Type implements StringRepresentable {
        BOTTOM("bottom", Shapes.or(
            Block.box(0.0D, 4.0D, 3.0D, 6.0D, 14.0D, 13.0D),
            Block.box(4.0D, 0.0D, 3.0D, 16.0D, 10.0D, 13.0D)
        )),
        TOP("top", Shapes.or(
            Block.box(0.0D, 2.0D, 3.0D, 6.0D, 12.0D, 13.0D),
            Block.box(4.0D, 6.0D, 3.0D, 16.0D, 16.0D, 13.0D)
        )),
        SIDE_LOWER("side_lower", Shapes.or(
            Block.box(0.0D, 4.0D, 3.0D, 10.0D, 16.0D, 13.0D),
            Block.box(4.0D, 0.0D, 3.0D, 14.0D, 6.0D, 13.0D)
        )),
        SIDE_UPPER("side_upper", Shapes.or(
            Block.box(0.0D, 0.0D, 3.0D, 10.0D, 12.0D, 13.0D),
            Block.box(4.0D, 10.0D, 3.0D, 14.0D, 16.0D, 13.0D)
        )),
        CORNER_LOWER("corner_lower", Shapes.or(
            Block.box(4.0D, 10.0D, 3.0D, 14.0D, 16.0D, 13.0D),
            Block.box(10.0D, 4.0D, 3.0D, 16.0D, 14.0D, 13.0D)
        )),
        CORNER_UPPER("corner_upper", Shapes.or(
            Block.box(4.0D, 0.0D, 3.0D, 14.0D, 6.0D, 13.0D),
            Block.box(10.0D, 2.0D, 3.0D, 16.0D, 12.0D, 13.0D)
        )),
        MIDDLE("middle", BASE_SHAPE);

        private final String serializedName;
        private final VoxelShape shape;

        Type(String serializedName, VoxelShape shape) {
            this.serializedName = serializedName;
            this.shape = shape;
        }

        @Override
        public String getSerializedName() {
            return serializedName;
        }
    }
}
