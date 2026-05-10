package com.github.mim1q.minecells.world;

import com.github.mim1q.minecells.block.blockentity.DoorwayPortalBlockEntity;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.world.state.MineCellsData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public final class DoorwayPortalService {
    private static final ResourceLocation OVERWORLD_ID = new ResourceLocation("minecraft", "overworld");

    private DoorwayPortalService() {
    }

    public static InteractionResult useDoorway(ServerPlayer player, ServerLevel level, BlockPos pos, DoorwayPortalBlock block, DoorwayPortalBlockEntity doorway) {
        BlockPos anchor = doorway.getPosOverride() != null ? doorway.getPosOverride() : DoorwayPortalBlockEntity.toPortalAnchor(pos);
        doorway.setPosOverride(anchor);

        ResourceLocation currentDimension = level.dimension().location();
        ResourceLocation targetDimension = block.getType().dimensionId();
        MineCellsDimension targetMineCellsDimension = MineCellsDimension.of(targetDimension);
        MineCellsData.PlayerData playerData = MineCellsData.getPlayerData(player, level, anchor);
        boolean firstVisitToTarget = !playerData.hasVisitedDimension(targetDimension);

        if (!canEnter(currentDimension, targetDimension, playerData)) {
            player.displayClientMessage(Component.literal("This doorway is not unlocked yet."), true);
            return InteractionResult.FAIL;
        }

        ServerLevel targetLevel = resolveLevel(level, targetDimension);
        if (targetLevel == null) {
            player.displayClientMessage(Component.literal("Doorway target dimension is not available yet."), true);
            return InteractionResult.FAIL;
        }

        BlockPos targetPos = resolveTargetPos(level, pos, currentDimension, targetDimension, player, playerData, targetLevel);
        BlockPos sourcePos = pos.relative(level.getBlockState(pos).getValue(DoorwayPortalBlock.FACING));

        playerData.addPortalData(currentDimension, targetDimension, sourcePos, targetPos);
        MineCellsData.syncCurrentPlayerData(player, level);

        Vec3 target = Vec3.atBottomCenterOf(targetPos);
        float targetYaw = targetMineCellsDimension != null ? targetMineCellsDimension.yaw() : player.getYRot();
        player.teleportTo(targetLevel, target.x, target.y, target.z, targetYaw, player.getXRot());
        grantFirstVisitRune(player, targetDimension, firstVisitToTarget);
        level.playSound(null, pos, MineCellsSounds.PORTAL_USE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        targetLevel.playSound(null, targetPos, MineCellsSounds.PORTAL_ACTIVATE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.CONSUME;
    }

    private static boolean canEnter(ResourceLocation currentDimension, ResourceLocation targetDimension, MineCellsData.PlayerData playerData) {
        if (currentDimension.equals(targetDimension)) {
            return false;
        }
        if (targetDimension.equals(OVERWORLD_ID) || targetDimension.equals(DoorwayPortalBlock.DoorwayType.PRISON.dimensionId())) {
            return true;
        }
        return playerData.hasVisitedDimension(targetDimension);
    }

    private static BlockPos resolveTargetPos(
        ServerLevel sourceLevel,
        BlockPos sourcePos,
        ResourceLocation currentDimension,
        ResourceLocation targetDimension,
        ServerPlayer player,
        MineCellsData.PlayerData playerData,
        ServerLevel targetLevel
    ) {
        Optional<MineCellsData.PortalData> existing = playerData.getPortalData(currentDimension, targetDimension);
        if (existing.isPresent()) {
            return existing.get().toPos();
        }
        if (targetDimension.equals(OVERWORLD_ID)) {
            if (player.getRespawnDimension() == net.minecraft.world.level.Level.OVERWORLD && player.getRespawnPosition() != null) {
                return player.getRespawnPosition();
            }
            return targetLevel.getSharedSpawnPos();
        }
        MineCellsDimension target = MineCellsDimension.of(targetDimension);
        if (target != null) {
            return BlockPos.containing(target.getTeleportPosition(sourcePos, sourceLevel));
        }
        return targetLevel.getSharedSpawnPos();
    }

    private static ServerLevel resolveLevel(ServerLevel sourceLevel, ResourceLocation dimensionId) {
        ResourceKey<net.minecraft.world.level.Level> key = ResourceKey.create(Registries.DIMENSION, dimensionId);
        return sourceLevel.getServer().getLevel(key);
    }

    private static void grantFirstVisitRune(ServerPlayer player, ResourceLocation targetDimension, boolean firstVisitToTarget) {
        if (!firstVisitToTarget || targetDimension.equals(OVERWORLD_ID)) {
            return;
        }
        Item rune = MineCellsItems.getDimensionalRune(targetDimension);
        if (rune == null) {
            return;
        }
        ItemStack stack = new ItemStack(rune);
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }
}
