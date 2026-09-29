package com.github.mim1q.minecells.entity.nonliving;

import com.github.mim1q.minecells.block.blockentity.SpawnerRuneBlockEntity;
import com.github.mim1q.minecells.data.spawner_runes.SpawnerRuneController;
import com.github.mim1q.minecells.registry.MineCellsBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;

import net.minecraftforge.network.NetworkHooks;

/**
 * Keeps a spawner rune alive while its block position is occupied by another block,
 * and turns back into a {@link SpawnerRuneBlockEntity} once the position is air again.
 */
public class SpawnerRuneEntity extends Entity {
    public final SpawnerRuneController controller = new SpawnerRuneController();

    public SpawnerRuneEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    public void tick() {
        if (tickCount % 10 != 0) {
            return;
        }
        BlockPos pos = blockPosition();
        controller.tick(pos, level());
        if (level().isClientSide) {
            return;
        }

        BlockState state = level().getBlockState(pos);
        if (state.isAir()) {
            level().setBlockAndUpdate(pos, MineCellsBlocks.SPAWNER_RUNE.get().defaultBlockState());
            discard();
            if (level().getBlockEntity(pos) instanceof SpawnerRuneBlockEntity blockEntity) {
                blockEntity.controller.setDataId(level(), pos, controller.getDataId());
                blockEntity.controller.setVisible(controller.isVisible());
                blockEntity.controller.setLastActivationTime(controller.getLastActivationTime());
                blockEntity.setChanged();
            }
        } else if (state.is(MineCellsBlocks.SPAWNER_RUNE.get())) {
            discard();
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("dataId")) {
            controller.setDataId(level(), blockPosition(), ResourceLocation.tryParse(tag.getString("dataId")));
        }
        if (tag.contains("last_activation_time")) {
            controller.setLastActivationTime(tag.getLong("last_activation_time"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (controller.getDataId() != null) {
            tag.putString("dataId", controller.getDataId().toString());
        }
        tag.putLong("last_activation_time", controller.getLastActivationTime());
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
