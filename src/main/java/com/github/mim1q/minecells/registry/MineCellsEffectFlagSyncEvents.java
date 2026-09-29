package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.effect.MineCellsEffectFlags;
import com.github.mim1q.minecells.effect.MineCellsMobEffect;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.network.MineCellsNetwork;
import com.github.mim1q.minecells.network.s2c.EffectFlagsS2CPacket;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Forge replacement for Fabric's synced Mine Cells effect flags (LivingEntity data tracker mixin).
 * Changed entities are flushed once per server tick so the effect map is already updated.
 */
@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MineCellsEffectFlagSyncEvents {
    private static final Set<LivingEntity> PENDING = Collections.newSetFromMap(new WeakHashMap<>());

    private MineCellsEffectFlagSyncEvents() {
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        markIfTracked(event.getEntity(), event.getEffectInstance());
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        markIfTracked(event.getEntity(), event.getEffectInstance());
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        markIfTracked(event.getEntity(), event.getEffectInstance());
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer player && event.getTarget() instanceof LivingEntity living) {
            int flags = MineCellsEffectFlags.compute(living);
            if (flags != 0) {
                MineCellsNetwork.sendToPlayer(player, new EffectFlagsS2CPacket(living.getId(), flags));
            }
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PENDING.add(event.getEntity());
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        PENDING.add(event.getEntity());
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        PENDING.add(event.getEntity());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || PENDING.isEmpty()) {
            return;
        }
        for (LivingEntity entity : PENDING.toArray(LivingEntity[]::new)) {
            if (!entity.isRemoved()) {
                MineCellsNetwork.CHANNEL.send(
                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                    new EffectFlagsS2CPacket(entity.getId(), MineCellsEffectFlags.compute(entity))
                );
            }
        }
        PENDING.clear();
    }

    private static void markIfTracked(LivingEntity entity, MobEffectInstance instance) {
        if (entity.level().isClientSide || instance == null || !(instance.getEffect() instanceof MineCellsMobEffect effect) || effect.flag() == null) {
            return;
        }
        PENDING.add(entity);
    }
}
