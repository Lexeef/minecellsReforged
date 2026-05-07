package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.data.spawner_runes.SpawnerRuneController;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnerRuneBlockEntity extends MineCellsBlockEntity {
    public final SpawnerRuneController controller = new SpawnerRuneController();

    public SpawnerRuneBlockEntity(BlockPos pos, BlockState state) {
        super(MineCellsBlockEntities.SPAWNER_RUNE.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.getGameTime() % 10 == 0) {
            controller.tick(pos, level);
            setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (controller.getDataId() != null) {
            tag.putString("dataId", controller.getDataId().toString());
        }
        tag.putBoolean("visible", controller.isVisible());
        tag.putLong("lastActivationTime", controller.getLastActivationTime());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ResourceLocation id = ResourceLocation.tryParse(tag.getString("dataId"));
        controller.setDataId(level, worldPosition, id);
        controller.setVisible(tag.getBoolean("visible"));
        controller.setLastActivationTime(tag.getLong("lastActivationTime"));
    }
}
