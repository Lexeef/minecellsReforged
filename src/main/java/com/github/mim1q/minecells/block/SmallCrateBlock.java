package com.github.mim1q.minecells.block;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;

import org.jetbrains.annotations.Nullable;

public class SmallCrateBlock extends GroundDecorationBlock {
    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 3);

    public SmallCrateBlock(Properties properties) {
        super(properties, Shape.BLOCK_12);
        registerDefaultState(stateDefinition.any().setValue(ROTATION, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ROTATION);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var rotation = 3 - Math.floorMod((int) ((context.getRotation() + 360.0F - 11.25F) / 22.5F), 4);
        return defaultBlockState().setValue(ROTATION, rotation);
    }
}
