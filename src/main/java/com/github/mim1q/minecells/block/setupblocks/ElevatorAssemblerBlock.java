package com.github.mim1q.minecells.block.setupblocks;

import com.github.mim1q.minecells.config.MineCellsSyncedConfig;
import com.github.mim1q.minecells.entity.nonliving.ElevatorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class ElevatorAssemblerBlock extends SetupBlock {
    public static final BooleanProperty WAITING = BooleanProperty.create("waiting");
    public static final BooleanProperty UNBREAKABLE = BooleanProperty.create("unbreakable");

    public ElevatorAssemblerBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(WAITING, false).setValue(UNBREAKABLE, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        boolean result = setup(level, pos, state);
        return result ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WAITING, UNBREAKABLE);
    }

    @Override
    public boolean setup(Level level, BlockPos pos, BlockState state) {
        if (state.getValue(WAITING)) {
            return false;
        }

        int maxHeight = MineCellsSyncedConfig.elevatorMaxAssemblyHeight();
        int minHeight = MineCellsSyncedConfig.elevatorMinAssemblyHeight();
        int minY = Math.max(level.getMinBuildHeight(), pos.getY() - maxHeight);
        int maxY = Math.min(level.getMaxBuildHeight(), pos.getY() + maxHeight);
        int second = pos.getY();
        for (int y = minY; y <= maxY; y++) {
            if (y == pos.getY()) {
                continue;
            }
            if (level.getBlockState(new BlockPos(pos.getX(), y, pos.getZ())).getBlock() instanceof ElevatorAssemblerBlock) {
                second = y;
                break;
            }
        }

        if (Math.abs(pos.getY() - second) < minHeight) {
            return false;
        }

        boolean goingUp = second < pos.getY();
        int elevatorMinY = Math.min(pos.getY(), second);
        int elevatorMaxY = Math.max(pos.getY(), second);

        Block north = level.getBlockState(pos.north()).getBlock();
        Block south = level.getBlockState(pos.south()).getBlock();
        boolean rotated = north instanceof ChainBlock && south instanceof ChainBlock;

        if (ElevatorEntity.validateShaft(level, pos.getX(), pos.getZ(), elevatorMinY, elevatorMaxY, rotated)) {
            if (!level.isClientSide) {
                ElevatorEntity elevator = ElevatorEntity.spawn(level, pos.getX(), pos.getZ(), elevatorMinY, elevatorMaxY, rotated, goingUp);
                level.removeBlockEntity(pos);
                level.destroyBlock(pos, false);
                BlockPos secondPos = pos.atY(second);
                level.removeBlockEntity(secondPos);
                level.destroyBlock(secondPos, false);
                if (elevator != null) {
                    elevator.setUnbreakable(state.getValue(UNBREAKABLE));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
