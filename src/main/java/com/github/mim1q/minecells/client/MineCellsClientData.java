package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.world.state.PlayerSpecificMineCellsData;

public final class MineCellsClientData {
    private static PlayerSpecificMineCellsData playerData = new PlayerSpecificMineCellsData(new net.minecraft.nbt.CompoundTag());

    private MineCellsClientData() {
    }

    public static PlayerSpecificMineCellsData getPlayerData() {
        return playerData;
    }

    public static void setPlayerData(PlayerSpecificMineCellsData data) {
        playerData = data;
    }
}
