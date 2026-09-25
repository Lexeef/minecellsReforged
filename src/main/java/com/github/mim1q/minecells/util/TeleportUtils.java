package com.github.mim1q.minecells.util;

import com.github.mim1q.minecells.config.MineCellsConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.ModList;

import java.util.function.Function;

/**
 * Forge replacement for Fabric {@code TeleportUtils} / {@code FabricDimensions.teleport}.
 * Optionally forces the teleport onto the server main thread (needed with some chunk
 * multithreading mods such as C2ME).
 */
public final class TeleportUtils {
    private TeleportUtils() {
    }

    private static boolean shouldRunOnMainThread() {
        MineCellsConfig.ForceServerThreadMode mode = MineCellsConfig.COMMON.teleportForceMainThread.get();
        return switch (mode) {
            case ALWAYS -> true;
            case NEVER -> false;
            case DEFAULT -> ModList.get().isLoaded("c2me");
        };
    }

    public static void teleportToDimension(Entity entity, ServerLevel targetWorld, Vec3 position, float yaw) {
        if (entity == null || targetWorld == null || entity.level().isClientSide) {
            return;
        }

        Runnable teleport = () -> doTeleport(entity, targetWorld, position, yaw);
        if (shouldRunOnMainThread() && !targetWorld.getServer().isSameThread()) {
            targetWorld.getServer().execute(teleport);
            return;
        }
        teleport.run();
    }

    private static void doTeleport(Entity entity, ServerLevel targetWorld, Vec3 position, float yaw) {
        if (entity instanceof ServerPlayer player) {
            player.teleportTo(targetWorld, position.x, position.y, position.z, yaw, player.getXRot());
            return;
        }

        if (entity.level() == targetWorld) {
            entity.moveTo(position.x, position.y, position.z, yaw, entity.getXRot());
            entity.setDeltaMovement(Vec3.ZERO);
            return;
        }

        entity.changeDimension(targetWorld, new ITeleporter() {
            @Override
            public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yawOverride, Function<Boolean, Entity> repositionEntity) {
                Entity relocated = repositionEntity.apply(false);
                relocated.moveTo(position.x, position.y, position.z, yaw, relocated.getXRot());
                relocated.setDeltaMovement(Vec3.ZERO);
                return relocated;
            }
        });
    }
}
