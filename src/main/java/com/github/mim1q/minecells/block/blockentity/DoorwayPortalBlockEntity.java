package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.client.MineCellsClientData;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.world.state.MineCellsData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DoorwayPortalBlockEntity extends MineCellsBlockEntity {
    private boolean clientVisited;
    @Nullable
    private BlockPos posOverride;

    public DoorwayPortalBlockEntity(BlockPos pos, BlockState state) {
        super(MineCellsBlockEntities.DOORWAY.get(), pos, state);
    }

    @Nullable
    public BlockPos getPosOverride() {
        return posOverride;
    }

    public void setPosOverride(@Nullable BlockPos posOverride) {
        this.posOverride = posOverride;
        setChanged();
        sync();
    }

    public void ensureBoundPosition() {
        if (posOverride == null) {
            setPosOverride(toPortalAnchor(worldPosition));
        }
    }

    public boolean hasClientVisited() {
        return clientVisited;
    }

    public void updateClientVisited() {
        if (level == null || !level.isClientSide) {
            return;
        }
        ResourceLocation targetDimension = getTargetDimension();
        if (targetDimension == null) {
            clientVisited = false;
            return;
        }
        BlockPos anchor = posOverride != null ? posOverride : toPortalAnchor(worldPosition);
        clientVisited = MineCellsClientData.getPlayerData().get(anchor).hasVisitedDimension(targetDimension);
    }

    public boolean canPlayerEnter(Player player) {
        ResourceLocation targetDimension = getTargetDimension();
        if (targetDimension == null || level == null) {
            return false;
        }
        ResourceLocation currentDimension = level.dimension().location();
        if (currentDimension.equals(targetDimension)) {
            return false;
        }

        if (level.isClientSide) {
            BlockPos anchor = posOverride != null ? posOverride : toPortalAnchor(worldPosition);
            MineCellsData.PlayerData playerData = MineCellsClientData.getPlayerData().get(anchor);
            return targetDimension.equals(DoorwayPortalServiceAccess.OVERWORLD_ID)
                || targetDimension.equals(DoorwayPortalBlock.DoorwayType.PRISON.dimensionId())
                || playerData.hasVisitedDimension(targetDimension);
        }

        return true;
    }

    public static BlockPos toPortalAnchor(BlockPos pos) {
        int x = Math.round(pos.getX() / 1024.0F) * 1024;
        int z = Math.round(pos.getZ() / 1024.0F) * 1024;
        return new BlockPos(x, pos.getY(), z);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (posOverride != null) {
            tag.putLong("posOverride", posOverride.asLong());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        posOverride = tag.contains("posOverride") ? BlockPos.of(tag.getLong("posOverride")) : null;
        updateClientVisited();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    private void sync() {
        if (level != null) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    @Nullable
    private ResourceLocation getTargetDimension() {
        if (!(getBlockState().getBlock() instanceof DoorwayPortalBlock doorway)) {
            return null;
        }
        return doorway.getType().dimensionId();
    }

    /**
     * Keeps the client-only BE logic decoupled from the common service constants.
     */
    private static final class DoorwayPortalServiceAccess {
        private static final ResourceLocation OVERWORLD_ID = new ResourceLocation("minecraft", "overworld");

        private DoorwayPortalServiceAccess() {
        }
    }
}
