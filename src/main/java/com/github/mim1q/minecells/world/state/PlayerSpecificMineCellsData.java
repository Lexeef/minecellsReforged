package com.github.mim1q.minecells.world.state;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class PlayerSpecificMineCellsData {
    public final ImmutableMap<String, MineCellsData.PlayerData> map;

    public PlayerSpecificMineCellsData(MineCellsData data, ServerPlayer player) {
        ImmutableMap.Builder<String, MineCellsData.PlayerData> builder = ImmutableMap.builder();
        data.runs.forEach((ignored, run) -> builder.put(run.x + "," + run.z, run.getPlayerData(player)));
        this.map = builder.build();
    }

    public PlayerSpecificMineCellsData(CompoundTag tag) {
        ImmutableMap.Builder<String, MineCellsData.PlayerData> builder = ImmutableMap.builder();
        for (String key : tag.getAllKeys()) {
            builder.put(key, new MineCellsData.PlayerData(tag.getCompound(key), null));
        }
        this.map = builder.build();
    }

    public MineCellsData.PlayerData get(BlockPos pos) {
        return get(Math.round(pos.getX() / 1024F), Math.round(pos.getZ() / 1024F));
    }

    public MineCellsData.PlayerData get(int x, int z) {
        return map.getOrDefault(x + "," + z, MineCellsData.PlayerData.EMPTY);
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        map.forEach((key, value) -> tag.put(key, value.save(new CompoundTag())));
        return tag;
    }
}
