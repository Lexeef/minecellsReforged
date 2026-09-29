package com.github.mim1q.minecells.block.setupblocks;

import com.github.mim1q.minecells.data.spawner_runes.SpawnerRuneController;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class MonsterBoxBlock extends SetupBlock {
    private final ResourceLocation spawnerRuneDataId;

    public MonsterBoxBlock(ResourceLocation spawnerRuneDataId) {
        super(Properties.copy(Blocks.BEDROCK).noLootTable());
        this.spawnerRuneDataId = spawnerRuneDataId;
    }

    @Override
    public boolean setup(Level level, BlockPos pos, BlockState state) {
        level.removeBlock(pos, false);
        if (level.isClientSide) {
            return false;
        }
        SpawnerRuneController.spawnEntities((ServerLevel) level, spawnerRuneDataId, pos, entity -> {
            if (entity instanceof Mob mob) {
                mob.setPersistenceRequired();
                mob.moveTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, mob.getYRot(), mob.getXRot());
                mob.finalizeSpawn(
                    (ServerLevelAccessor) level,
                    level.getCurrentDifficultyAt(pos),
                    MobSpawnType.EVENT,
                    null,
                    null
                );
                mob.setOldPosAndRot();
            }
        });
        return false;
    }
}
