package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.client.MineCellsClientData;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.structure.grid.SpecialPointIds;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import com.github.mim1q.minecells.util.MineCellsText;
import com.github.mim1q.minecells.world.state.MineCellsData;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.Util;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DoorwayPortalBlockEntity extends MineCellsBlockEntity {
    public final AnimationProperty closedBarsAnimation;
    private boolean clientVisited;
    @Nullable
    private BlockPos posOverride;
    private ResourceLocation specialPointTarget = SpecialPointIds.ENTRANCE;
    @Nullable
    private UUID ownerId;
    private String ownerName = "";
    private boolean onlyOwnerCanEnter;

    public DoorwayPortalBlockEntity(BlockPos pos, BlockState state) {
        super(MineCellsBlockEntities.DOORWAY.get(), pos, state);
        boolean closed = state.hasProperty(DoorwayPortalBlock.CLOSED) && state.getValue(DoorwayPortalBlock.CLOSED);
        closedBarsAnimation = new AnimationProperty(closed ? 1.0F : 0.0F);
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

    public ResourceLocation getSpecialPointTarget() {
        return specialPointTarget != null ? specialPointTarget : SpecialPointIds.ENTRANCE;
    }

    public void setSpecialPointTarget(ResourceLocation specialPointTarget) {
        this.specialPointTarget = specialPointTarget != null ? specialPointTarget : SpecialPointIds.ENTRANCE;
        setChanged();
        sync();
    }

    public void ensureBoundPosition() {
        if (posOverride == null) {
            setPosOverride(toPortalAnchor(worldPosition));
        }
    }

    public BlockPos getAnchor() {
        return posOverride != null ? posOverride : toPortalAnchor(worldPosition);
    }

    public void update(Player owner, BlockPos anchor, boolean onlyOwner) {
        this.ownerId = owner.getUUID();
        this.ownerName = owner.getGameProfile().getName();
        this.onlyOwnerCanEnter = onlyOwner;
        this.posOverride = anchor;
        setChanged();
        sync();
    }

    @Nullable
    public UUID getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public List<MutableComponent> getLabel() {
        List<MutableComponent> list = new ArrayList<>();
        if (!ownerName.isEmpty()) {
            list.add(Component.literal(ownerName).withStyle(ChatFormatting.GRAY));
        }
        if (getBlockState().getBlock() instanceof DoorwayPortalBlock doorway) {
            DoorwayPortalBlock.DoorwayType type = doorway.getType();
            MutableComponent dimensionName = Component.translatable(Util.makeDescriptionId("dimension", type.dimensionId()))
                .withStyle(style -> style.withColor(type.getColor()));
            list.addAll(MineCellsText.splitIfExceeds(dimensionName, 20));
        }
        return list;
    }

    public boolean isOnlyOwnerCanEnter() {
        return onlyOwnerCanEnter;
    }

    public boolean isOwnerAllowed(Player player) {
        return !onlyOwnerCanEnter || ownerId == null || ownerId.equals(player.getUUID());
    }

    public boolean canEdit(Player player) {
        if (level == null || level.dimension() != Level.OVERWORLD) {
            return false;
        }
        return ownerId == null || ownerId.equals(player.getUUID());
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
        clientVisited = MineCellsClientData.getPlayerData().get(getAnchor()).hasVisitedDimension(targetDimension);
    }

    public boolean canPlayerEnter(Player player) {
        ResourceLocation targetDimension = getTargetDimension();
        if (targetDimension == null || level == null || !isOwnerAllowed(player)) {
            return false;
        }
        ResourceLocation currentDimension = level.dimension().location();
        if (currentDimension.equals(targetDimension)) {
            return false;
        }

        if (level.isClientSide) {
            MineCellsData.PlayerData playerData = MineCellsClientData.getPlayerData().get(getAnchor());
            return DoorwayPortalServiceAccess.canEnter(currentDimension, targetDimension, playerData, player);
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        MineCellsData.PlayerData playerData = MineCellsData.getPlayerData(serverPlayer, serverPlayer.serverLevel(), getAnchor());
        return DoorwayPortalServiceAccess.canEnter(currentDimension, targetDimension, playerData, player);
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
        tag.putString("special_point_target", getSpecialPointTarget().toString());
        if (ownerId != null) {
            tag.putUUID("owner_id", ownerId);
            tag.putString("owner_name", ownerName);
        }
        tag.putBoolean("only_owner_can_enter", onlyOwnerCanEnter);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        posOverride = tag.contains("posOverride") ? BlockPos.of(tag.getLong("posOverride")) : null;
        if (tag.contains("special_point_target")) {
            ResourceLocation parsed = ResourceLocation.tryParse(tag.getString("special_point_target"));
            specialPointTarget = parsed != null ? parsed : SpecialPointIds.ENTRANCE;
        } else if (tag.contains("upstream")) {
            specialPointTarget = tag.getBoolean("upstream") ? SpecialPointIds.EXIT : SpecialPointIds.ENTRANCE;
        } else {
            specialPointTarget = SpecialPointIds.ENTRANCE;
        }
        ownerId = tag.hasUUID("owner_id") ? tag.getUUID("owner_id") : null;
        ownerName = tag.getString("owner_name");
        onlyOwnerCanEnter = tag.getBoolean("only_owner_can_enter");
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
        private DoorwayPortalServiceAccess() {
        }

        private static boolean canEnter(ResourceLocation currentDimension, ResourceLocation targetDimension, MineCellsData.PlayerData playerData, Player player) {
            return com.github.mim1q.minecells.world.DoorwayPortalService.canEnter(currentDimension, targetDimension, playerData, player);
        }
    }
}
