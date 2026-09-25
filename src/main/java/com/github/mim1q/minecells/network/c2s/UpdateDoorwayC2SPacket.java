package com.github.mim1q.minecells.network.c2s;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.DoorwayPortalBlockEntity;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.world.DoorwayRequirements;
import com.github.mim1q.minecells.world.state.MineCellsData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record UpdateDoorwayC2SPacket(BlockPos doorwayPos, DoorwayPortalBlock.DoorwayType type, boolean onlyOwnerCanEnter) {
    private static final double MAX_DISTANCE_SQR = 10.0D * 10.0D;

    public static void encode(UpdateDoorwayC2SPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.doorwayPos);
        buf.writeEnum(packet.type);
        buf.writeBoolean(packet.onlyOwnerCanEnter);
    }

    public static UpdateDoorwayC2SPacket decode(FriendlyByteBuf buf) {
        return new UpdateDoorwayC2SPacket(buf.readBlockPos(), buf.readEnum(DoorwayPortalBlock.DoorwayType.class), buf.readBoolean());
    }

    public static void handle(UpdateDoorwayC2SPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            ServerLevel level = player.serverLevel();
            BlockPos pos = packet.doorwayPos;
            if (packet.type == DoorwayPortalBlock.DoorwayType.OVERWORLD
                || !level.isLoaded(pos)
                || player.distanceToSqr(Vec3.atCenterOf(pos)) > MAX_DISTANCE_SQR) {
                MineCells.LOGGER.warn("Invalid doorway update packet from player {}", player.getName().getString());
                return;
            }
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof DoorwayPortalBlock) || !(level.getBlockEntity(pos) instanceof DoorwayPortalBlockEntity doorway) || !doorway.canEdit(player)) {
                return;
            }
            BlockPos anchor = MineCellsData.get(level).getOrCreatePlayerRunCenter(player);
            MineCellsData.PlayerData playerData = MineCellsData.getPlayerData(player, level, anchor);
            if (!DoorwayRequirements.isMet(packet.type, player, playerData)) {
                return;
            }
            ResourceLocation specialPointTarget = doorway.getSpecialPointTarget();
            DoorwayPortalBlock target = MineCellsBlocks.getDoorway(packet.type);
            if (state.getBlock() != target) {
                level.setBlock(pos, target.defaultBlockState().setValue(DoorwayPortalBlock.FACING, state.getValue(DoorwayPortalBlock.FACING)), 3);
            }
            if (level.getBlockEntity(pos) instanceof DoorwayPortalBlockEntity updated) {
                updated.setSpecialPointTarget(specialPointTarget);
                updated.update(player, anchor, packet.onlyOwnerCanEnter);
            }
        });
        context.setPacketHandled(true);
    }
}
