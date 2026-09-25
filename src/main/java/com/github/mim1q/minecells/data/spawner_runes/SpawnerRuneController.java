package com.github.mim1q.minecells.data.spawner_runes;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;
import com.github.mim1q.minecells.network.MineCellsNetwork;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsReloadListeners;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SpawnerRuneController {
    private static final long NEVER_ACTIVATED = -100000000L;
    private static final double SYNC_DISTANCE_SQR = 64.0D * 64.0D;
    private static final int SYNC_INTERVAL = 4;

    private ResourceLocation dataId;
    private SpawnerRuneData data;
    private boolean visible;
    private long lastActivationTime = NEVER_ACTIVATED;
    private ResourceLocation warnedMissingId;
    private int syncCounter;

    /** @return whether the saved state (last activation time) changed during this tick */
    public boolean tick(BlockPos pos, Level level) {
        if (level instanceof ServerLevel serverLevel) {
            ensureData(serverLevel, pos);
            if (data == null) {
                return false;
            }
            long previousActivation = lastActivationTime;
            tickServer(serverLevel, pos);
            return previousActivation != lastActivationTime;
        }
        tickClient(level, pos);
        return false;
    }

    private void tickServer(ServerLevel level, BlockPos pos) {
        List<ServerPlayer> players = level.players();
        if (players.isEmpty()) {
            return;
        }
        Vec3 center = Vec3.atCenterOf(pos);
        double distance = data.playerDistance();
        AABB range = AABB.ofSize(center, distance, distance, distance);
        boolean playerNearby = false;
        for (ServerPlayer player : players) {
            if (player.distanceToSqr(center) <= SYNC_DISTANCE_SQR) {
                playerNearby = true;
            }
            if (player.isCreative() || player.isSpectator() || !player.getBoundingBox().intersects(range)) {
                continue;
            }
            if (canActivate(level)) {
                spawnEntities(level, data, pos, player);
                return;
            }
        }
        // Periodic resync only for clients that started tracking the rune after its last activation packet.
        if (playerNearby && syncCounter++ % SYNC_INTERVAL == 0) {
            sendUpdatePacket(level, pos);
        }
    }

    private void tickClient(Level level, BlockPos pos) {
        boolean currentlyVisible = canActivate(level);
        int particleAmount = 2;
        if (visible != currentlyVisible) {
            particleAmount = 15;
            visible = currentlyVisible;
        } else if (!visible) {
            particleAmount = 1;
        }
        int color = MineCellsDimension.getColor(level, 0xFF6A00);
        ParticleUtils.addInBox(
            level,
            MineCellsParticles.SPECKLE.get().get(color),
            AABB.ofSize(Vec3.atCenterOf(pos), 0.5D, 0.5D, 0.5D),
            particleAmount,
            Vec3.ZERO.offsetRandom(level.random, 0.1F)
        );
    }

    private boolean canActivate(Level level) {
        if (data == null) {
            return false;
        }
        return level.getGameTime() - lastActivationTime > data.cooldown() * 20.0F;
    }

    private void spawnEntities(ServerLevel level, SpawnerRuneData data, BlockPos pos, ServerPlayer spawningPlayer) {
        long disappearTime = level.getGameTime() + (long) (data.cooldown() * 20.0F);
        for (SpawnerRuneData.EntitySpawnData entityData : data.getSelectedEntities(level.random)) {
            Entity entity = spawnEntity(level, entityData, findPos(level, pos, data.spawnDistance()), pos, spawned -> {
                if (spawned instanceof MineCellsMonsterEntity monster && !monster.isElite()) {
                    monster.setDisappearTime(disappearTime);
                }
            });
            if (entity instanceof Monster monster && monster.hasLineOfSight(spawningPlayer)) {
                monster.setTarget(spawningPlayer);
            }
        }
        lastActivationTime = level.getGameTime();
        sendUpdatePacket(level, pos);
    }

    public static List<Entity> spawnEntities(ServerLevel level, ResourceLocation dataId, BlockPos pos, Consumer<Entity> entityConsumer) {
        SpawnerRuneData data = MineCellsReloadListeners.spawnerRunes().entries().get(dataId);
        if (data == null) {
            return List.of();
        }

        List<Entity> result = new ArrayList<>();
        for (SpawnerRuneData.EntitySpawnData entityData : data.getSelectedEntities(level.random)) {
            Entity entity = spawnEntity(level, entityData, findPos(level, pos, data.spawnDistance()), pos, entityConsumer);
            if (entity != null) {
                result.add(entity);
            }
        }
        return result;
    }

    private static Entity spawnEntity(ServerLevel level, SpawnerRuneData.EntitySpawnData entityData, BlockPos pos, BlockPos runePos, Consumer<Entity> entityConsumer) {
        if (entityData.entityType() == null) {
            return null;
        }

        Entity spawnedEntity = entityData.entityType().create(level);
        if (spawnedEntity == null) {
            return null;
        }
        spawnedEntity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, level.random.nextFloat() * 360.0F, 0.0F);

        if (spawnedEntity instanceof LivingEntity living) {
            if (living instanceof Mob mob) {
                mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.NATURAL, null, null);
            }
            MineCellsNetwork.sendSpawnRuneParticles(level, runePos, living.getBoundingBox().inflate(0.5D));
            for (Map.Entry<Attribute, Double> entry : entityData.attributeOverrides().entrySet()) {
                AttributeInstance instance = living.getAttribute(entry.getKey());
                if (instance != null) {
                    instance.setBaseValue(entry.getValue());
                }
            }
            mergeNbt(living, entityData.nbt());
            entityConsumer.accept(living);
            living.heal(living.getMaxHealth());
            living.push(
                (level.random.nextDouble() - 0.5D) * 0.1D,
                0.05D + level.random.nextDouble() * 0.05D,
                (level.random.nextDouble() - 0.5D) * 0.1D
            );
        } else {
            mergeNbt(spawnedEntity, entityData.nbt());
        }

        level.addFreshEntity(spawnedEntity);
        return spawnedEntity;
    }

    private static void mergeNbt(Entity entity, CompoundTag overrides) {
        if (overrides.isEmpty()) {
            return;
        }
        CompoundTag merged = entity.saveWithoutId(new CompoundTag());
        for (String key : overrides.getAllKeys()) {
            merged.put(key, overrides.get(key).copy());
        }
        entity.load(merged);
    }

    private static BlockPos findPos(Level level, BlockPos pos, float radius) {
        int x = pos.getX() + (int) (radius * (level.random.nextFloat() - 0.5F));
        int z = pos.getZ() + (int) (radius * (level.random.nextFloat() - 0.5F));
        int y = pos.getY();
        for (int i = 0; i < 4; i++) {
            BlockPos candidate = new BlockPos(x, y, z);
            BlockState state = level.getBlockState(candidate);
            BlockState below = level.getBlockState(candidate.below());
            BlockState above = level.getBlockState(candidate.above());
            boolean solidBelow = below.isFaceSturdy(level, candidate.below(), Direction.UP);
            boolean empty = state.getCollisionShape(level, candidate).isEmpty();
            boolean emptyAbove = above.getCollisionShape(level, candidate.above()).isEmpty();
            if (solidBelow && empty && emptyAbove) {
                return candidate;
            }
            y++;
        }
        return pos;
    }

    private void sendUpdatePacket(ServerLevel level, BlockPos pos) {
        if (data != null) {
            MineCellsNetwork.sendSpawnerRuneUpdate(level, pos, lastActivationTime, data.cooldown());
        }
    }

    /** Client-side: the server data pack isn't available, so only the cooldown needed for visibility is kept. */
    public void applyClientUpdate(long lastActivationTime, float cooldown) {
        this.lastActivationTime = lastActivationTime;
        this.data = new SpawnerRuneData(cooldown, 0.0F, 0.0F, List.of());
    }

    public void setDataId(Level level, BlockPos pos, ResourceLocation id) {
        this.dataId = id;
        if (level != null && level.isClientSide) {
            return;
        }
        this.data = id == null ? null : MineCellsReloadListeners.spawnerRunes().entries().get(id);
        if (level != null && id != null && data == null && !id.equals(warnedMissingId)) {
            warnedMissingId = id;
            MineCells.LOGGER.warn("Unknown spawner rune data id {} at {} in {}", id, pos.toShortString(), level.dimension().location());
        }
    }

    private void ensureData(ServerLevel level, BlockPos pos) {
        if (dataId != null && data == null) {
            setDataId(level, pos, dataId);
        }
    }

    public void resetActivation(ServerLevel level, BlockPos pos) {
        lastActivationTime = NEVER_ACTIVATED;
        sendUpdatePacket(level, pos);
    }

    public ResourceLocation getDataId() {
        return dataId;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public long getLastActivationTime() {
        return lastActivationTime;
    }

    public void setLastActivationTime(long lastActivationTime) {
        this.lastActivationTime = lastActivationTime;
    }
}
