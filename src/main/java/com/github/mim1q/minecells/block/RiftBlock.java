package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.DoorwayPortalBlockEntity;
import com.github.mim1q.minecells.block.blockentity.RiftBlockEntity;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.util.TeleportUtils;
import com.github.mim1q.minecells.world.DoorwayPortalService;
import com.github.mim1q.minecells.world.state.MineCellsData;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

public class RiftBlock extends Block implements EntityBlock {
    public RiftBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RiftBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return context instanceof EntityCollisionContext entityContext
            && entityContext.getEntity() instanceof Player player
            && player.isCreative() ? Shapes.block() : Shapes.empty();
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!(entity instanceof ServerPlayer player)
            || !(level instanceof ServerLevel serverLevel)
            || player.distanceToSqr(Vec3.atBottomCenterOf(pos)) >= 1.1D) {
            return;
        }
        ServerLevel overworld = serverLevel.getServer().overworld();
        BlockPos target = findOverworldEntrance(player, serverLevel, overworld);
        TeleportUtils.teleportToDimension(player, overworld, Vec3.atCenterOf(target), entranceYaw(player, overworld, target));
    }

    private static float entranceYaw(ServerPlayer player, ServerLevel overworld, BlockPos target) {
        if (target.equals(player.getRespawnPosition()) && player.getRespawnDimension() == Level.OVERWORLD) {
            return player.getRespawnAngle();
        }
        if (target.equals(overworld.getSharedSpawnPos())) {
            return overworld.getSharedSpawnAngle();
        }
        return player.getYRot();
    }

    private static BlockPos findOverworldEntrance(ServerPlayer player, ServerLevel level, ServerLevel overworld) {
        MineCellsData.PlayerData data = MineCellsData.getPlayerData(player, level, DoorwayPortalBlockEntity.toPortalAnchor(player.blockPosition()));
        return DoorwayPortalService.findOverworldEntrance(player, data, overworld);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == MineCellsBlockEntities.RIFT.get()
            ? (tickerLevel, tickerPos, tickerState, blockEntity) -> ((RiftBlockEntity) blockEntity).tick(tickerLevel, tickerPos, tickerState)
            : null;
    }
}
