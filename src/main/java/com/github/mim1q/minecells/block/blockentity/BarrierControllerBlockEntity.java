package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.block.BarrierControllerBlock;
import com.github.mim1q.minecells.block.ConditionalBarrierBlock;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BarrierControllerBlockEntity extends MineCellsBlockEntity {
    public BarrierControllerBlockEntity(BlockPos pos, BlockState state) {
        super(MineCellsBlockEntities.BARRIER_CONTROLLER.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide && state.getBlock() instanceof BarrierControllerBlock controller) {
            boolean open = controller.shouldBeOpen(level, pos, state);
            if (state.getValue(ConditionalBarrierBlock.OPEN) != open) {
                level.setBlock(pos, state.setValue(ConditionalBarrierBlock.OPEN, open), 3);
            }
        }
    }
}
