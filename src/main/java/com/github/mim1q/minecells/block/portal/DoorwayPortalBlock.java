package com.github.mim1q.minecells.block.portal;

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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DoorwayPortalBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape COLLISION_SHAPE = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);

    private final DoorwayType type;

    public DoorwayPortalBlock(Properties properties, DoorwayType type) {
        super(properties);
        this.type = type;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public DoorwayType getType() {
        return type;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return ModelUtils.rotateShape(Direction.NORTH, state.getValue(FACING), SHAPE);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return ModelUtils.rotateShape(Direction.NORTH, state.getValue(FACING), COLLISION_SHAPE);
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
        builder.add(FACING);
    }

    public enum DoorwayType implements StringRepresentable {
        OVERWORLD("overworld", 0x8EF96D),
        PRISON("prison", 0x54EF88),
        PROMENADE("promenade", 0x93FFF7),
        INSUFFERABLE_CRYPT("insufferable_crypt", 0xFF4CF4),
        RAMPARTS("ramparts", 0xFFC540),
        BLACK_BRIDGE("black_bridge", 0x623CC9);

        private final String serializedName;
        private final int color;

        DoorwayType(String serializedName, int color) {
            this.serializedName = serializedName;
            this.color = color;
        }

        public int getColor() {
            return color;
        }

        @Override
        public String getSerializedName() {
            return serializedName;
        }
    }
}
