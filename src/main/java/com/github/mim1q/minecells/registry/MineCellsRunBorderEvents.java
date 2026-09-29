package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.world.MineCellsRunBorder;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Forge replacement for Fabric's {@code MineCellsBorderEntityMixin} / {@code BorderEntityCollisionMixin}
 * and the run-border part of {@code ServerPlayerEntityMixin#requestTeleport}.
 * Only teleports that Forge exposes through {@link EntityTeleportEvent} (commands, ender pearls, chorus fruit) are blocked.
 */
@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MineCellsRunBorderEvents {
    private static final double DISMOUNT_DISTANCE = 2.0D;
    private static final double TELEPORT_MIN_DISTANCE = 2.0D;

    private MineCellsRunBorderEvents() {
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!MineCellsDimension.isMineCellsDimension(entity.level())) {
            return;
        }
        if (MineCellsRunBorder.clamp(entity) && entity instanceof ServerPlayer player) {
            player.connection.teleport(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        }
        if (!entity.level().isClientSide && entity.isPassenger() && MineCellsRunBorder.distanceInside(entity) <= DISMOUNT_DISTANCE) {
            entity.stopRiding();
        }
    }

    @SubscribeEvent
    public static void onTeleport(EntityTeleportEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !MineCellsDimension.isMineCellsDimension(player.level())) {
            return;
        }
        double centerX = MineCellsRunBorder.centerX(player.getX(), player.getZ());
        double centerZ = MineCellsRunBorder.centerZ(player.getX(), player.getZ());
        if (MineCellsRunBorder.distanceInside(centerX, centerZ, event.getTargetX(), event.getTargetZ()) < TELEPORT_MIN_DISTANCE) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onMount(EntityMountEvent event) {
        Entity mount = event.getEntityBeingMounted();
        if (event.isMounting() && mount != null
            && MineCellsDimension.isMineCellsDimension(mount.level())
            && MineCellsRunBorder.distanceInside(mount) < DISMOUNT_DISTANCE) {
            event.setCanceled(true);
        }
    }
}
