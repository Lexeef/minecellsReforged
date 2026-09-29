package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.SpawnerRuneBlockEntity;
import com.github.mim1q.minecells.entity.nonliving.SpawnerRuneEntity;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

public class SpawnerRuneBlock extends BaseEntityBlock implements EntityBlock {
    public SpawnerRuneBlock(Properties properties) {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!newState.isAir() && !newState.is(this) && !level.isClientSide
            && level.getBlockEntity(pos) instanceof SpawnerRuneBlockEntity blockEntity) {
            SpawnerRuneEntity entity = MineCellsEntities.SPAWNER_RUNE.get().create(level);
            if (entity != null) {
                entity.setPos(Vec3.atBottomCenterOf(pos));
                entity.controller.setDataId(level, pos, blockEntity.controller.getDataId());
                entity.controller.setVisible(blockEntity.controller.isVisible());
                entity.controller.setLastActivationTime(blockEntity.controller.getLastActivationTime());
                level.addFreshEntity(entity);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpawnerRuneBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == MineCellsBlockEntities.SPAWNER_RUNE.get()
            ? (tickerLevel, tickerPos, tickerState, blockEntity) -> ((SpawnerRuneBlockEntity) blockEntity).tick(tickerLevel, tickerPos, tickerState)
            : null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityCtx
            && entityCtx.getEntity() instanceof Player player
            && !player.isCreative()) {
            return Shapes.empty();
        }
        return super.getShape(state, level, pos, context);
    }
}
