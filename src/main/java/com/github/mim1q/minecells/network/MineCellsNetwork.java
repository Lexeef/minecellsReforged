package com.github.mim1q.minecells.network;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.network.c2s.CellCrafterCraftRequestC2SPacket;
import com.github.mim1q.minecells.network.c2s.RequestUnlockedCellCrafterRecipesC2SPacket;
import com.github.mim1q.minecells.network.s2c.SendUnlockedCellCrafterRecipesS2CPacket;
import com.github.mim1q.minecells.network.s2c.ShockwaveClientEventS2CPacket;
import com.github.mim1q.minecells.network.s2c.SpawnRuneParticlesS2CPacket;
import com.github.mim1q.minecells.network.s2c.SyncMineCellsPlayerDataS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class MineCellsNetwork {
    private static final String PROTOCOL_VERSION = "1";
    private static int nextId = 0;

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        MineCells.id("main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    private MineCellsNetwork() {
    }

    public static void init() {
        CHANNEL.registerMessage(
            nextId++,
            ShockwaveClientEventS2CPacket.class,
            ShockwaveClientEventS2CPacket::encode,
            ShockwaveClientEventS2CPacket::decode,
            ShockwaveClientEventS2CPacket::handle
        );
        CHANNEL.registerMessage(
            nextId++,
            SpawnRuneParticlesS2CPacket.class,
            SpawnRuneParticlesS2CPacket::encode,
            SpawnRuneParticlesS2CPacket::decode,
            SpawnRuneParticlesS2CPacket::handle
        );
        CHANNEL.registerMessage(
            nextId++,
            SyncMineCellsPlayerDataS2CPacket.class,
            SyncMineCellsPlayerDataS2CPacket::encode,
            SyncMineCellsPlayerDataS2CPacket::decode,
            SyncMineCellsPlayerDataS2CPacket::handle
        );
        CHANNEL.registerMessage(
            nextId++,
            CellCrafterCraftRequestC2SPacket.class,
            CellCrafterCraftRequestC2SPacket::encode,
            CellCrafterCraftRequestC2SPacket::decode,
            CellCrafterCraftRequestC2SPacket::handle
        );
        CHANNEL.registerMessage(
            nextId++,
            RequestUnlockedCellCrafterRecipesC2SPacket.class,
            RequestUnlockedCellCrafterRecipesC2SPacket::encode,
            RequestUnlockedCellCrafterRecipesC2SPacket::decode,
            RequestUnlockedCellCrafterRecipesC2SPacket::handle
        );
        CHANNEL.registerMessage(
            nextId++,
            SendUnlockedCellCrafterRecipesS2CPacket.class,
            SendUnlockedCellCrafterRecipesS2CPacket::encode,
            SendUnlockedCellCrafterRecipesS2CPacket::decode,
            SendUnlockedCellCrafterRecipesS2CPacket::handle
        );
    }

    public static void sendShockwaveClientEvent(ServerLevel level, Block block, BlockPos pos, boolean end) {
        CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)), new ShockwaveClientEventS2CPacket(block, pos, end));
    }

    public static void sendSpawnRuneParticles(ServerLevel level, BlockPos trackingPos, AABB box) {
        CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(trackingPos)), new SpawnRuneParticlesS2CPacket(box));
    }
}
