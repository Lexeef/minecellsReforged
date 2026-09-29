package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.FlagBlockEntity;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.util.ModelUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;

public class FlagBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WAVING = BooleanProperty.create("waving");
    public static final EnumProperty<Placement> PLACEMENT = EnumProperty.create("placement", Placement.class);

    public static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
    public static final VoxelShape CENTERED_SHAPE = Block.box(0.0D, 0.0D, 7.0D, 16.0D, 16.0D, 9.0D);
    public static final VoxelShape HORIZONTAL_SHAPE = Block.box(7.0D, 0.0D, 0.0D, 9.0D, 16.0D, 16.0D);

    public final ResourceLocation texture;
    public final boolean large;

    public FlagBlock(Properties properties, String name, boolean large) {
        super(properties);
        this.texture = MineCells.id("textures/blockentity/banner/" + name + ".png");
        this.large = large;
        registerDefaultState(stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(WAVING, true)
            .setValue(PLACEMENT, Placement.SIDE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WAVING, PLACEMENT);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getClickedFace() == Direction.UP) {
            return null;
        }
        if (context.getClickedFace() == Direction.DOWN) {
            return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(PLACEMENT, Placement.CENTERED);
        }
        if (context.getLevel().getBlockState(BlockPos.containing(context.getClickLocation())).is(BlockTags.FENCES)) {
            return defaultBlockState()
                .setValue(FACING, context.getClickedFace())
                .setValue(WAVING, true)
                .setValue(PLACEMENT, Placement.HORIZONTAL);
        }
        BlockPos offsetPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
        BlockState state0 = context.getLevel().getBlockState(offsetPos.below());
        BlockState state1 = context.getLevel().getBlockState(offsetPos.below(2));
        return defaultBlockState()
            .setValue(FACING, context.getClickedFace())
            .setValue(WAVING, state0.isAir() && state1.isAir());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        level.setBlock(pos, state.cycle(WAVING), 3);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = switch (state.getValue(PLACEMENT)) {
            case SIDE -> SHAPE;
            case CENTERED -> CENTERED_SHAPE;
            case HORIZONTAL -> HORIZONTAL_SHAPE;
        };
        return ModelUtils.rotateShape(Direction.NORTH, state.getValue(FACING), shape);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FlagBlockEntity(pos, state);
    }

    public enum Placement implements StringRepresentable {
        SIDE("side"),
        CENTERED("centered"),
        HORIZONTAL("horizontal");

        private final String name;

        Placement(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
