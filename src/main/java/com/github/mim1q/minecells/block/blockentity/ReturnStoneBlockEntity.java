package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.block.MineCellsBlockTags;
import com.github.mim1q.minecells.block.ReturnStoneBlock;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class ReturnStoneBlockEntity extends MineCellsBlockEntity {
    @Nullable
    private ResourceLocation structure = null;
    private int windup = 0;
    @Nullable
    private Player player = null;

    public ReturnStoneBlockEntity(BlockPos pos, BlockState state) {
        super(MineCellsBlockEntities.RETURN_STONE.get(), pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        String structureKey = tag.getString("structure");
        structure = structureKey.isBlank() ? null : new ResourceLocation(structureKey);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (structure != null) {
            tag.putString("structure", structure.toString());
        }
    }

    public InteractionResult setPlayer(Player player) {
        if (this.player != null || windup > 0 || level == null) {
            return InteractionResult.FAIL;
        }
        this.player = player;
        windup = 25;
        level.playSound(null, worldPosition, MineCellsSounds.TELEPORT_CHARGE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.SUCCESS;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (windup > 0) {
            windup--;
            serverLevel.sendParticles(ReturnStoneBlock.particle(), pos.getX() + 0.5D, pos.getY() + 1.25D, pos.getZ() + 0.5D, 5, 0.25D, 0.25D, 0.25D, 0.01D);
            if (player != null) {
                serverLevel.sendParticles(ReturnStoneBlock.particle(), player.getX(), player.getY() + 1.0D, player.getZ(), 5, 0.5D, 1.0D, 0.5D, 0.01D);
            }
        }

        if (windup == 0 && player != null) {
            teleportPlayer(serverLevel, pos, player);
            player = null;
        }
    }

    private void teleportPlayer(ServerLevel level, BlockPos pos, Player player) {
        if (structure != null && !structure.getPath().isBlank()) {
            teleportToStructure(level, pos, player, structure);
            return;
        }

        BlockPos targetPos = null;
        for (BlockPos offset : BlockPos.withinManhattan(pos, 30, 0, 30)) {
            int topY = level.getHeight(Heightmap.Types.WORLD_SURFACE, offset.getX(), offset.getZ());
            BlockPos topPos = new BlockPos(offset.getX(), topY, offset.getZ());
            if (level.getBlockState(topPos.below()).is(MineCellsBlockTags.RETURN_STONE_TARGETS)) {
                targetPos = topPos;
                break;
            }
        }

        if (targetPos == null) {
            int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ());
            targetPos = new BlockPos(pos.getX(), y, pos.getZ());
        }

        Vec3 target = Vec3.atBottomCenterOf(targetPos);
        player.teleportTo(target.x, target.y, target.z);
        playTeleportEffects(level, player);
    }

    private static void teleportToStructure(ServerLevel level, BlockPos pos, Player player, ResourceLocation structureId) {
        Optional<Holder.Reference<Structure>> holder = level.registryAccess()
            .registryOrThrow(Registries.STRUCTURE)
            .getHolder(ResourceKey.create(Registries.STRUCTURE, structureId));
        if (holder.isEmpty()) {
            return;
        }
        Pair<BlockPos, Holder<Structure>> found = level.getChunkSource().getGenerator()
            .findNearestMapStructure(level, HolderSet.direct(holder.get()), pos, 128, false);
        if (found == null) {
            return;
        }
        BlockPos structurePos = found.getFirst();
        level.getChunk(structurePos);
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, structurePos.getX(), structurePos.getZ());
        player.teleportTo(structurePos.getX() + 0.5D, y, structurePos.getZ() + 0.5D);
        playTeleportEffects(level, player);
    }

    private static void playTeleportEffects(ServerLevel level, Player player) {
        level.playSound(null, player.blockPosition(), MineCellsSounds.TELEPORT_RELEASE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        level.sendParticles(ReturnStoneBlock.particle(), player.getX(), player.getY() + 1.0D, player.getZ(), 30, 0.5D, 1.0D, 0.5D, 0.025D);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
