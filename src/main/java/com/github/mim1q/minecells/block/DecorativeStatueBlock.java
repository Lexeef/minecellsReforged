package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.DecorativeStatueBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class DecorativeStatueBlock extends Block implements EntityBlock {
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final EnumProperty<StatuePose> POSE = EnumProperty.create("pose", StatuePose.class);

    public DecorativeStatueBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ROTATION, 0).setValue(POSE, StatuePose.ARM_UP_LEFT));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(ROTATION, Mth.floor(context.getRotation() * 16.0F / 360.0F + 0.5D) & 15);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(ROTATION, rotation.rotate(state.getValue(ROTATION), 16));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(ROTATION, mirror.mirror(state.getValue(ROTATION), 16));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            level.setBlock(pos, state.cycle(POSE), 3);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, POSE);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DecorativeStatueBlockEntity(pos, state);
    }

    public enum StatuePose implements StringRepresentable {
        BASE("base"),
        ARM_UP_RIGHT("arm_up_right"),
        ARM_UP_LEFT("arm_up_left"),
        ARMS_UP("arms_up");

        private final String name;

        StatuePose(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
