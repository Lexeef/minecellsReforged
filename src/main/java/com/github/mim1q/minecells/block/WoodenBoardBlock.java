package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.util.ModelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WoodenBoardBlock extends Block {
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);

    public WoodenBoardBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(TYPE, Type.MIDDLE).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TYPE, FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockState stateBelow = context.getLevel().getBlockState(context.getClickedPos().below());
        if (context.getClickedFace() == Direction.UP && stateBelow.is(this)) {
            facing = stateBelow.getValue(FACING);
        }
        BlockState stateAbove = context.getLevel().getBlockState(context.getClickedPos().above());
        if (context.getClickedFace() == Direction.DOWN && stateAbove.is(this)) {
            facing = stateAbove.getValue(FACING);
        }
        return defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.getItemInHand(hand).getItem() instanceof AxeItem) {
            level.setBlock(pos, state.cycle(TYPE), 3);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return ModelUtils.rotateShape(Direction.SOUTH, state.getValue(FACING), SHAPE);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public enum Type implements StringRepresentable {
        MIDDLE("middle"),
        BOARDED("boarded"),
        TOP("top"),
        TOP_INVERTED("top_inverted"),
        BOTTOM("bottom"),
        BOTTOM_INVERTED("bottom_inverted");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
