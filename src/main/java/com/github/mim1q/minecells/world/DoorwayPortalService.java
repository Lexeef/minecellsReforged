package com.github.mim1q.minecells.world;

import com.github.mim1q.minecells.block.blockentity.DoorwayPortalBlockEntity;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.dimension.MineCellsDimensionGraph;
import com.github.mim1q.minecells.util.TeleportUtils;
import com.github.mim1q.minecells.world.state.MineCellsData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public final class DoorwayPortalService {
    private static final ResourceLocation OVERWORLD_ID = new ResourceLocation("minecraft", "overworld");
    private static final MineCellsDimensionGraph DIMENSION_GRAPH = new MineCellsDimensionGraph();

    private DoorwayPortalService() {
    }

    public static InteractionResult useDoorway(ServerPlayer player, ServerLevel level, BlockPos pos, DoorwayPortalBlock block, DoorwayPortalBlockEntity doorway) {
        BlockPos anchor = doorway.getPosOverride() != null ? doorway.getPosOverride() : DoorwayPortalBlockEntity.toPortalAnchor(pos);
        doorway.setPosOverride(anchor);

        ResourceLocation currentDimension = level.dimension().location();
        ResourceLocation targetDimension = block.getType().dimensionId();
        MineCellsDimension targetMineCellsDimension = MineCellsDimension.of(targetDimension);
        MineCellsData.PlayerData playerData = MineCellsData.getPlayerData(player, level, anchor);

        if (!doorway.isOwnerAllowed(player)) {
            player.displayClientMessage(Component.literal("Only " + doorway.getOwnerName() + " can use this doorway."), true);
            return InteractionResult.FAIL;
        }
        if (!canEnter(currentDimension, targetDimension, playerData, player)) {
            player.displayClientMessage(Component.literal("This doorway is not unlocked yet."), true);
            return InteractionResult.FAIL;
        }

        ServerLevel targetLevel = resolveLevel(level, targetDimension);
        if (targetLevel == null) {
            player.displayClientMessage(Component.literal("Doorway target dimension is not available yet."), true);
            return InteractionResult.FAIL;
        }

        BlockPos runSourcePos = currentDimension.equals(OVERWORLD_ID) ? anchor.atY(pos.getY()) : pos;
        Vec3 target = resolveTarget(level, runSourcePos, currentDimension, targetDimension, player, playerData, targetLevel, doorway.getSpecialPointTarget());
        BlockPos targetPos = BlockPos.containing(target);
        BlockPos sourcePos = pos.relative(level.getBlockState(pos).getValue(DoorwayPortalBlock.FACING));

        playerData.addPortalData(currentDimension, targetDimension, sourcePos, targetPos);
        MineCellsData.syncCurrentPlayerData(player, level);

        float targetYaw = targetMineCellsDimension != null
            ? targetMineCellsDimension.getTeleportYaw(runSourcePos, level, doorway.getSpecialPointTarget())
            : player.getYRot();
        // Prefer TeleportUtils so C2ME / config-forced main-thread teleports stay safe.
        TeleportUtils.teleportToDimension(player, targetLevel, target, targetYaw);
        return InteractionResult.CONSUME;
    }

    public static boolean canEnter(ResourceLocation currentDimension, ResourceLocation targetDimension, MineCellsData.PlayerData playerData) {
        if (currentDimension.equals(targetDimension)) {
            return false;
        }
        if (targetDimension.equals(OVERWORLD_ID) || targetDimension.equals(DoorwayPortalBlock.DoorwayType.PRISON.dimensionId())) {
            return true;
        }
        MineCellsDimension current = MineCellsDimension.of(currentDimension);
        MineCellsDimension target = MineCellsDimension.of(targetDimension);
        if (current != null && target != null && DIMENSION_GRAPH.areAdjacent(current, target)) {
            return true;
        }
        return playerData.hasVisitedDimension(targetDimension);
    }

    public static boolean canEnter(ResourceLocation currentDimension, ResourceLocation targetDimension, MineCellsData.PlayerData playerData, Player player) {
        if (canEnter(currentDimension, targetDimension, playerData)) {
            return true;
        }
        return !currentDimension.equals(targetDimension)
            && targetDimension.equals(DoorwayPortalBlock.DoorwayType.INSUFFERABLE_CRYPT.dimensionId())
            && DoorwayRequirements.hasVineRune(player);
    }

    private static Vec3 resolveTarget(
        ServerLevel sourceLevel,
        BlockPos sourcePos,
        ResourceLocation currentDimension,
        ResourceLocation targetDimension,
        ServerPlayer player,
        MineCellsData.PlayerData playerData,
        ServerLevel targetLevel,
        ResourceLocation specialPointTarget
    ) {
        if (targetDimension.equals(OVERWORLD_ID)) {
            return Vec3.atBottomCenterOf(findOverworldEntrance(player, playerData, targetLevel));
        }
        MineCellsDimension target = MineCellsDimension.of(targetDimension);
        if (target != null) {
            // Like Fabric: the exact special point position (with the half-block offset), not rounded to a block.
            Optional<Vec3> specialPoint = target.getSpecialPointTeleportPosition(sourcePos, sourceLevel, specialPointTarget);
            if (specialPoint.isPresent()) {
                return specialPoint.get();
            }
        }
        Optional<MineCellsData.PortalData> existing = playerData.getPortalData(currentDimension, targetDimension);
        if (existing.isPresent()) {
            BlockPos existingTargetPos = existing.get().toPos();
            if (target == null || target.isValidStoredTeleportTarget(currentDimension, sourcePos, sourceLevel, existingTargetPos)) {
                return Vec3.atBottomCenterOf(existingTargetPos);
            }
        }
        if (target != null) {
            return target.getTeleportPosition(sourcePos, sourceLevel, specialPointTarget);
        }
        return Vec3.atBottomCenterOf(targetLevel.getSharedSpawnPos());
    }

    /**
     * The Overworld doorway the player used to enter this run, else their Overworld respawn point, else world spawn.
     */
    public static BlockPos findOverworldEntrance(ServerPlayer player, MineCellsData.PlayerData playerData, ServerLevel overworld) {
        for (int i = playerData.portals.size() - 1; i >= 0; i--) {
            MineCellsData.PortalData portal = playerData.portals.get(i);
            if (portal.fromDimension().equals(OVERWORLD_ID)) {
                return portal.fromPos();
            }
            if (portal.toDimension().equals(OVERWORLD_ID)) {
                return portal.toPos();
            }
        }
        if (player.getRespawnDimension() == net.minecraft.world.level.Level.OVERWORLD && player.getRespawnPosition() != null) {
            return player.getRespawnPosition();
        }
        return overworld.getSharedSpawnPos();
    }

    private static ServerLevel resolveLevel(ServerLevel sourceLevel, ResourceLocation dimensionId) {
        ResourceKey<net.minecraft.world.level.Level> key = ResourceKey.create(Registries.DIMENSION, dimensionId);
        return sourceLevel.getServer().getLevel(key);
    }
}
