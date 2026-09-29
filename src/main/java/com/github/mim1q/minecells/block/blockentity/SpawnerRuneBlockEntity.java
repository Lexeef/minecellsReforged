package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.data.spawner_runes.SpawnerRuneController;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;

public class SpawnerRuneBlockEntity extends MineCellsBlockEntity {
    private static final String LAST_ACTIVATION_TIME = "last_activation_time";
    private static final String LEGACY_LAST_ACTIVATION_TIME = "lastActivationTime";

    public final SpawnerRuneController controller = new SpawnerRuneController();

    public SpawnerRuneBlockEntity(BlockPos pos, BlockState state) {
        super(MineCellsBlockEntities.SPAWNER_RUNE.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.getGameTime() % 10 == 0 && controller.tick(pos, level)) {
            setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (controller.getDataId() != null) {
            tag.putString("dataId", controller.getDataId().toString());
        }
        tag.putLong(LAST_ACTIVATION_TIME, controller.getLastActivationTime());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("dataId")) {
            controller.setDataId(level, worldPosition, ResourceLocation.tryParse(tag.getString("dataId")));
        }
        if (tag.contains(LAST_ACTIVATION_TIME)) {
            controller.setLastActivationTime(tag.getLong(LAST_ACTIVATION_TIME));
        } else if (tag.contains(LEGACY_LAST_ACTIVATION_TIME)) {
            controller.setLastActivationTime(tag.getLong(LEGACY_LAST_ACTIVATION_TIME));
        }
    }
}
