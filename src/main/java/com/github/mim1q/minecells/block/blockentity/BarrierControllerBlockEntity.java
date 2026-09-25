package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.block.BarrierControllerBlock;
import com.github.mim1q.minecells.block.ConditionalBarrierBlock;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class BarrierControllerBlockEntity extends MineCellsBlockEntity {
    private boolean wasOpen = false;
    public final AnimationProperty openProgress = new AnimationProperty(0.0F, MathUtils::easeOutBounce);

    public BarrierControllerBlockEntity(BlockPos pos, BlockState state) {
        super(MineCellsBlockEntities.BARRIER_CONTROLLER.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        boolean open = state.getValue(ConditionalBarrierBlock.OPEN);
        if (level.isClientSide) {
            if (open != wasOpen) {
                wasOpen = open;
                openProgress.setupTransitionTo(open ? 1.0F : 0.0F, open ? 40.0F : 10.0F);
                level.playLocalSound(
                    pos.getX(), pos.getY(), pos.getZ(),
                    open ? SoundEvents.WOODEN_DOOR_OPEN : SoundEvents.WOODEN_DOOR_CLOSE,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F,
                    false
                );
            }
            return;
        }

        if (level.getGameTime() % 20 == 0 && state.getBlock() instanceof BarrierControllerBlock controller) {
            boolean shouldBeOpen = controller.shouldBeOpen(level, pos, state);
            if (open != shouldBeOpen) {
                level.setBlock(pos, state.setValue(ConditionalBarrierBlock.OPEN, shouldBeOpen), 3);
            }
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition).inflate(2.0D, 3.0D, 2.0D);
    }
}
