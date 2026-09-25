package com.github.mim1q.minecells.util;

import com.github.mim1q.minecells.network.MineCellsNetwork;
import com.github.mim1q.minecells.network.s2c.ScreenShakeS2CPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Minimal Forge replacement for gimm1q {@code ScreenShakeUtils}.
 * Sends a client packet that applies a short camera shake; the modifier id selects the client config multiplier.
 */
public final class ScreenShakeUtils {
    private ScreenShakeUtils() {
    }

    public static void shakeAround(
        ServerLevel level,
        Vec3 origin,
        float intensity,
        int durationTicks,
        double minDistance,
        double maxDistance,
        String modifier
    ) {
        AABB box = AABB.ofSize(origin, maxDistance * 2.0D, maxDistance * 2.0D, maxDistance * 2.0D);
        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, box)) {
            double distance = player.position().distanceTo(origin);
            if (distance > maxDistance) {
                continue;
            }
            float falloff = distance <= minDistance
                ? 1.0F
                : (float) (1.0D - (distance - minDistance) / Math.max(0.001D, maxDistance - minDistance));
            float scaled = intensity * Math.max(0.0F, falloff);
            if (scaled <= 0.01F) {
                continue;
            }
            MineCellsNetwork.sendToPlayer(player, new ScreenShakeS2CPacket(scaled, durationTicks, modifier)
            );
        }
    }

    public static void shakePlayer(ServerPlayer player, float intensity, int durationTicks, String modifier) {
        MineCellsNetwork.sendToPlayer(player, new ScreenShakeS2CPacket(intensity, durationTicks, modifier)
        );
    }
}
