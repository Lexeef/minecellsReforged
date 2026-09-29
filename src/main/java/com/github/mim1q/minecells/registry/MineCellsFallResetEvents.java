package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.config.MineCellsConfig;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.item.MineCellsItemTags;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.GridBasedStructureUtils;
import com.github.mim1q.minecells.structure.grid.SpecialPointIds;
import com.github.mim1q.minecells.util.MathUtils;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Comparator;
import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MineCellsFallResetEvents {
    private static final Map<Entity, FallResetState> STATE = new WeakHashMap<>();

    private MineCellsFallResetEvents() {
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        Entity entity = event.getEntity();
        STATE.put(entity, new FallResetState(
            MineCellsDimension.getFallResetHeight(event.getLevel()),
            entity.blockPosition()
        ));
    }

    @SubscribeEvent
    public static void onDimensionChange(EntityTravelToDimensionEvent event) {
        // Destination height is applied on join; keep spawn anchor for grace window.
        FallResetState state = STATE.get(event.getEntity());
        if (state != null) {
            state.dimensionTpPos = event.getEntity().blockPosition();
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        tickEntity(event.getEntity());
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END
            || event.level.isClientSide()
            || event.level.getGameTime() % 20L != 0L
            || MineCellsDimension.getFallResetHeight(event.level) == null) {
            return;
        }
        for (Entity entity : ((ServerLevel) event.level).getAllEntities()) {
            if (entity instanceof ItemEntity) {
                tickEntity(entity);
            }
        }
    }

    private static void tickEntity(Entity entity) {
        if (entity.level().isClientSide
            || MineCellsConfig.COMMON.disableFallProtection.get()
            || entity.tickCount < 10
            || entity.tickCount % 2 != 0
            || MineCellsDimension.getFallResetHeight(entity.level()) == null) {
            return;
        }

        FallResetState state = STATE.computeIfAbsent(entity, e -> new FallResetState(
            MineCellsDimension.getFallResetHeight(e.level()),
            e.blockPosition()
        ));
        if (state.fallResetY == null) {
            state.fallResetY = MineCellsDimension.getFallResetHeight(entity.level());
        }
        if (state.fallResetY == null) {
            return;
        }

        if (Math.abs(state.dimensionTpPos.getX() - entity.getX()) < 4.0D
            && Math.abs(state.dimensionTpPos.getZ() - entity.getZ()) < 4.0D) {
            return;
        }
        if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return;
        }
        if (entity.getY() >= state.fallResetY) {
            return;
        }

        if (entity.getType() == EntityType.ITEM) {
            if (entity.tickCount % 20 != 0) {
                return;
            }
            ItemEntity itemEntity = (ItemEntity) entity;
            if (itemEntity.getItem().is(MineCellsItemTags.DISCARD_IN_HIGH_DIMENSIONS)) {
                entity.discard();
                return;
            }
            Player nearest = entity.level().getNearestPlayer(entity.getX(), entity.getY(), entity.getZ(), 180.0D, false);
            if (nearest == null) {
                return;
            }
            entity.teleportTo(nearest.getX(), nearest.getY(), nearest.getZ());
            entity.setDeltaMovement(Vec3.ZERO);
            entity.fallDistance = 0.0F;
            return;
        }

        if (entity instanceof Monster) {
            entity.hurt(entity.damageSources().fall(), 100.0F);
            return;
        }

        BlockPos tpPos = getResetToPos(entity);
        entity.stopRiding();
        if (entity instanceof ServerPlayer serverPlayer) {
            MineCells.LOGGER.info(
                "Fall protection mechanic triggered for Player {} at {} from {} in dimension {}",
                serverPlayer.getName().getString(),
                tpPos.toShortString(),
                entity.blockPosition().toShortString(),
                entity.level().dimension().location()
            );
            grantAdvancement(serverPlayer);
        }
        entity.teleportTo(tpPos.getX() + 0.5D, tpPos.getY() + 0.5D, tpPos.getZ() + 0.5D);
        entity.setDeltaMovement(Vec3.ZERO);
        entity.fallDistance = 0.0F;
        entity.hurt(entity.damageSources().fall(), 5.0F);
    }

    private static void grantAdvancement(ServerPlayer player) {
        if (player.serverLevel().getServer() == null) {
            return;
        }
        Advancement advancement = player.serverLevel().getServer().getAdvancements()
            .getAdvancement(MineCells.id("unlock/fall_from_the_ramparts"));
        if (advancement != null) {
            player.getAdvancements().award(advancement, "teleported_up");
        }
    }

    private static BlockPos getResetToPos(Entity entity) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return entity.blockPosition();
        }
        MineCellsDimension dimension = MineCellsDimension.of(entity.level());
        if (dimension == null) {
            return entity.blockPosition();
        }
        BlockPos runCenter = new BlockPos(MathUtils.getClosestMultiplePosition(entity.blockPosition(), 1024));
        var specialPoints = GridBasedStructureUtils.getSpecialPoints(serverLevel, runCenter, dimension);

        return specialPoints.stream()
            .filter(point -> point.id().equals(SpecialPointIds.CHECKPOINT))
            .map(point -> {
                BlockPos result = runCenter.offset(point.offset());
                int topY = entity.level().getHeight(Heightmap.Types.MOTION_BLOCKING, result.getX(), result.getZ());
                return new BlockPos(result.getX(), topY, result.getZ());
            })
            .filter(pos -> pos.getZ() < entity.getZ()
                && entity.level().getBlockState(pos.below()).isFaceSturdy(entity.level(), pos.below(), Direction.UP))
            .min(Comparator.comparingDouble(pos -> pos.distToCenterSqr(entity.getX(), entity.getY(), entity.getZ())))
            .orElseGet(() -> BlockPos.containing(
                dimension.getTeleportPosition(entity.blockPosition(), serverLevel, SpecialPointIds.ENTRANCE)
            ));
    }

    private static final class FallResetState {
        @javax.annotation.Nullable
        Double fallResetY;
        BlockPos dimensionTpPos;

        FallResetState(@javax.annotation.Nullable Double fallResetY, BlockPos dimensionTpPos) {
            this.fallResetY = fallResetY;
            this.dimensionTpPos = dimensionTpPos;
        }
    }
}
