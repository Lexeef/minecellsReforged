package com.github.mim1q.minecells.network;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.network.c2s.CellCrafterCraftRequestC2SPacket;
import com.github.mim1q.minecells.network.c2s.RequestSyncMineCellsPlayerDataC2SPacket;
import com.github.mim1q.minecells.network.c2s.RequestUnlockedCellCrafterRecipesC2SPacket;
import com.github.mim1q.minecells.network.c2s.UpdateDoorwayC2SPacket;
import com.github.mim1q.minecells.network.c2s.UseTentacleWeaponC2SPacket;
import com.github.mim1q.minecells.network.s2c.AdvancementHintsS2CPacket;
import com.github.mim1q.minecells.network.s2c.EffectFlagsS2CPacket;
import com.github.mim1q.minecells.network.s2c.ElevatorDestroyedS2CPacket;
import com.github.mim1q.minecells.network.s2c.ExplosionS2CPacket;
import com.github.mim1q.minecells.network.s2c.ObeliskActivationS2CPacket;
import com.github.mim1q.minecells.network.s2c.OpenDoorwayScreenS2CPacket;
import com.github.mim1q.minecells.network.s2c.ScreenShakeS2CPacket;
import com.github.mim1q.minecells.network.s2c.SendUnlockedCellCrafterRecipesS2CPacket;
import com.github.mim1q.minecells.network.s2c.ShockwaveClientEventS2CPacket;
import com.github.mim1q.minecells.network.s2c.SpawnerRuneUpdateS2CPacket;
import com.github.mim1q.minecells.network.s2c.SpawnRuneParticlesS2CPacket;
import com.github.mim1q.minecells.network.s2c.SyncMineCellsPlayerDataS2CPacket;
import com.github.mim1q.minecells.network.s2c.UpdateConjunctiviusBossBarS2CPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

import net.minecraftforge.common.util.FakePlayer;
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
            ScreenShakeS2CPacket.class,
            ScreenShakeS2CPacket::encode,
            ScreenShakeS2CPacket::decode,
            ScreenShakeS2CPacket::handle
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
            RequestSyncMineCellsPlayerDataC2SPacket.class,
            RequestSyncMineCellsPlayerDataC2SPacket::encode,
            RequestSyncMineCellsPlayerDataC2SPacket::decode,
            RequestSyncMineCellsPlayerDataC2SPacket::handle
        );
        CHANNEL.registerMessage(
            nextId++,
            SendUnlockedCellCrafterRecipesS2CPacket.class,
            SendUnlockedCellCrafterRecipesS2CPacket::encode,
            SendUnlockedCellCrafterRecipesS2CPacket::decode,
            SendUnlockedCellCrafterRecipesS2CPacket::handle
        );
        CHANNEL.registerMessage(
            nextId++,
            UseTentacleWeaponC2SPacket.class,
            UseTentacleWeaponC2SPacket::encode,
            UseTentacleWeaponC2SPacket::decode,
            UseTentacleWeaponC2SPacket::handle
        );
        CHANNEL.registerMessage(
            nextId++,
            ObeliskActivationS2CPacket.class,
            ObeliskActivationS2CPacket::encode,
            ObeliskActivationS2CPacket::decode,
            ObeliskActivationS2CPacket::handle
        );
        CHANNEL.registerMessage(
            nextId++,
            ElevatorDestroyedS2CPacket.class,
            ElevatorDestroyedS2CPacket::encode,
            ElevatorDestroyedS2CPacket::decode,
            ElevatorDestroyedS2CPacket::handle
        );
        CHANNEL.registerMessage(
            nextId++,
            UpdateConjunctiviusBossBarS2CPacket.class,
            UpdateConjunctiviusBossBarS2CPacket::encode,
            UpdateConjunctiviusBossBarS2CPacket::decode,
            UpdateConjunctiviusBossBarS2CPacket::handle
        );
        CHANNEL.registerMessage(
            nextId++,
            ExplosionS2CPacket.class,
            ExplosionS2CPacket::encode,
            ExplosionS2CPacket::decode,
            ExplosionS2CPacket::handle
        );
        CHANNEL.registerMessage(
            nextId++,
            SpawnerRuneUpdateS2CPacket.class,
            SpawnerRuneUpdateS2CPacket::encode,
            SpawnerRuneUpdateS2CPacket::decode,
            SpawnerRuneUpdateS2CPacket::handle
        );
        CHANNEL.registerMessage(
            nextId++,
            AdvancementHintsS2CPacket.class,
            AdvancementHintsS2CPacket::encode,
            AdvancementHintsS2CPacket::decode,
            AdvancementHintsS2CPacket::handle
        );
        CHANNEL.registerMessage(
            nextId++,
            OpenDoorwayScreenS2CPacket.class,
            OpenDoorwayScreenS2CPacket::encode,
            OpenDoorwayScreenS2CPacket::decode,
            OpenDoorwayScreenS2CPacket::handle
        );
        CHANNEL.registerMessage(
            nextId++,
            UpdateDoorwayC2SPacket.class,
            UpdateDoorwayC2SPacket::encode,
            UpdateDoorwayC2SPacket::decode,
            UpdateDoorwayC2SPacket::handle
        );
        CHANNEL.registerMessage(
            nextId++,
            EffectFlagsS2CPacket.class,
            EffectFlagsS2CPacket::encode,
            EffectFlagsS2CPacket::decode,
            EffectFlagsS2CPacket::handle
        );
        CHANNEL.registerMessage(
            nextId++,
            com.github.mim1q.minecells.network.s2c.SyncValueCalculatorsS2CPacket.class,
            com.github.mim1q.minecells.network.s2c.SyncValueCalculatorsS2CPacket::encode,
            com.github.mim1q.minecells.network.s2c.SyncValueCalculatorsS2CPacket::decode,
            com.github.mim1q.minecells.network.s2c.SyncValueCalculatorsS2CPacket::handle
        );
        CHANNEL.registerMessage(
            nextId++,
            com.github.mim1q.minecells.network.s2c.SyncCommonConfigS2CPacket.class,
            com.github.mim1q.minecells.network.s2c.SyncCommonConfigS2CPacket::encode,
            com.github.mim1q.minecells.network.s2c.SyncCommonConfigS2CPacket::decode,
            com.github.mim1q.minecells.network.s2c.SyncCommonConfigS2CPacket::handle
        );
    }

    /** Other mods may pass fake players without a real connection (e.g. machines using items); sending to those would crash. */
    public static void sendToPlayer(ServerPlayer player, Object message) {
        if (player == null || player instanceof FakePlayer || player.connection == null) {
            return;
        }
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static boolean canReceivePackets(ServerPlayer player) {
        return player != null && !(player instanceof FakePlayer) && player.connection != null;
    }

    public static void sendSpawnerRuneUpdate(ServerLevel level, BlockPos pos, long lastActivationTime, float cooldown) {
        CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)), new SpawnerRuneUpdateS2CPacket(pos, lastActivationTime, cooldown));
    }

    public static void sendShockwaveClientEvent(ServerLevel level, Block block, BlockPos pos, boolean end) {
        CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)), new ShockwaveClientEventS2CPacket(block, pos, end));
    }

    public static void sendSpawnRuneParticles(ServerLevel level, BlockPos trackingPos, AABB box) {
        CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(trackingPos)), new SpawnRuneParticlesS2CPacket(box));
    }
}
