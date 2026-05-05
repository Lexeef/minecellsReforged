package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.ArrowSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ArrowSignBlock extends Block implements EntityBlock {
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final BooleanProperty MIDDLE = BooleanProperty.create("middle");

    public ArrowSignBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ROTATION, 0).setValue(MIDDLE, true));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace();
        if (direction.getAxis().isHorizontal()) {
            return defaultBlockState().setValue(ROTATION, direction.get2DDataValue() * 4).setValue(MIDDLE, false);
        }
        int rotation = Math.floorMod(Math.round(context.getRotation() / 22.5F) - 8, 16);
        return defaultBlockState().setValue(ROTATION, rotation).setValue(MIDDLE, true);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof ArrowSignBlockEntity arrowSign) {
            if (arrowSign.getItemStack().isEmpty()) {
                ItemStack itemStack = player.getItemInHand(hand);
                if (!itemStack.isEmpty()) {
                    arrowSign.setItemStack(itemStack.copy());
                    return InteractionResult.SUCCESS;
                }
            }
            arrowSign.cycleVerticalRotation(player.isShiftKeyDown() ? -1 : 1);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        updateChainState(level, pos, state);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos sourcePos, boolean moving) {
        super.neighborChanged(state, level, pos, block, sourcePos, moving);
        updateChainState(level, pos, state);
        if (sourcePos.equals(pos.above())) {
            level.updateNeighborsAt(pos.below(), this);
        }
    }

    private static void updateChainState(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide || !state.getValue(MIDDLE) || !(level.getBlockEntity(pos) instanceof ArrowSignBlockEntity arrowSign)) {
            return;
        }
        BlockState stateAbove = level.getBlockState(pos.above());
        if (level.getBlockEntity(pos.above()) instanceof ArrowSignBlockEntity arrowSignAbove) {
            stateAbove = arrowSignAbove.getChainState();
        }
        BlockState stateBelow = level.getBlockState(pos.below());
        boolean chainAbove = stateAbove.getBlock() instanceof ChainBlock && stateAbove.getValue(ChainBlock.AXIS) == Direction.Axis.Y;
        boolean chainBelow = stateBelow.getBlock() instanceof ChainBlock && stateBelow.getValue(ChainBlock.AXIS) == Direction.Axis.Y;
        boolean signBelow = stateBelow.getBlock() instanceof ArrowSignBlock && stateBelow.getValue(MIDDLE);
        if (signBelow || chainBelow) {
            arrowSign.setChainState(chainAbove ? stateAbove : net.minecraft.world.level.block.Blocks.CHAIN.defaultBlockState());
            return;
        }
        arrowSign.setChainState(net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(ROTATION, Math.floorMod(state.getValue(ROTATION) + rotation.ordinal() * 4, 16));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArrowSignBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ROTATION, MIDDLE);
    }
}
