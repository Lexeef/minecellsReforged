package com.github.mim1q.minecells.data.spawner_runes;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.MineCellsClientData;
import com.github.mim1q.minecells.network.MineCellsNetwork;
import com.github.mim1q.minecells.registry.MineCellsReloadListeners;
import com.github.mim1q.minecells.world.state.MineCellsData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SpawnerRuneController {
    private ResourceLocation dataId;
    private SpawnerRuneData data;
    private boolean visible;
    private long lastActivationTime;

    public void tick(BlockPos pos, Level level) {
        ensureData(level, pos);
        if (data == null) {
            return;
        }

        if (level.isClientSide && level instanceof ClientLevel clientLevel) {
            tickClient(clientLevel, pos);
            return;
        }

        if (level instanceof ServerLevel serverLevel) {
            tickServer(serverLevel, pos);
        }
    }

    private void tickServer(ServerLevel level, BlockPos pos) {
        double distance = data.playerDistance();
        AABB range = AABB.ofSize(Vec3.atCenterOf(pos), distance * 2.0D, distance * 2.0D, distance * 2.0D);
        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, range, candidate -> !candidate.isCreative() && !candidate.isSpectator())) {
            if (canPlayerActivate(player, level, pos)) {
                MineCellsData.PlayerData playerData = MineCellsData.getPlayerData(player, level, null);
                playerData.addActivatedSpawnerRune(level.dimension().location(), pos);
                MineCellsData.syncCurrentPlayerData(player, level);
                spawnEntities(data, pos, player);
                break;
            }
        }
    }

    private void tickClient(ClientLevel level, BlockPos pos) {
        boolean currentlyVisible = canClientPlayerActivate(level, pos);
        if (currentlyVisible != visible) {
            for (int i = 0; i < 15; i++) {
                Vec3 particlePos = Vec3.atCenterOf(pos).add(
                    (level.random.nextDouble() - 0.5D) * 0.5D,
                    (level.random.nextDouble() - 0.5D) * 0.5D,
                    (level.random.nextDouble() - 0.5D) * 0.5D
                );
                level.addParticle(
                    com.github.mim1q.minecells.registry.MineCellsParticles.SPECKLE.get().get(0xFF6A00),
                    particlePos.x,
                    particlePos.y,
                    particlePos.z,
                    (level.random.nextDouble() - 0.5D) * 0.1D,
                    (level.random.nextDouble() - 0.5D) * 0.1D,
                    (level.random.nextDouble() - 0.5D) * 0.1D
                );
            }
            visible = currentlyVisible;
        }
    }

    private void spawnEntities(SpawnerRuneData data, BlockPos pos, ServerPlayer spawningPlayer) {
        Level level = spawningPlayer.level();
        for (SpawnerRuneData.EntitySpawnData entityData : data.getSelectedEntities(level.random)) {
            Entity entity = spawnEntity((ServerLevel) level, entityData, findPos(level, pos, data.spawnDistance()), pos, spawned -> {
                if (spawned instanceof Mob mob) {
                    mob.setTarget(spawningPlayer);
                }
            });
            if (entity != null) {
                MineCellsNetwork.sendSpawnRuneParticles((ServerLevel) level, pos, entity.getBoundingBox().expandTowards(0.5D, 0.5D, 0.5D));
            }
        }
        lastActivationTime = level.getGameTime();
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

    private boolean canPlayerActivate(ServerPlayer player, ServerLevel level, BlockPos pos) {
        if (data.cooldown() != 0 && level.getGameTime() - lastActivationTime < data.cooldown() * 20.0F) {
            return false;
        }
        return !MineCellsData.getPlayerData(player, level, null).hasActivatedSpawnerRune(level.dimension().location(), pos);
    }

    private boolean canClientPlayerActivate(ClientLevel level, BlockPos pos) {
        return !MineCellsClientData.getPlayerData().get(pos).hasActivatedSpawnerRune(level.dimension().location(), pos);
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

        if (spawnedEntity instanceof Mob mob) {
            if (!NaturalSpawner.isSpawnPositionOk(SpawnPlacements.getPlacementType(mob.getType()), level, pos, mob.getType())) {
                return null;
            }
            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.NATURAL, null, null);
            for (Map.Entry<net.minecraft.world.entity.ai.attributes.Attribute, Double> entry : entityData.attributeOverrides().entrySet()) {
                AttributeInstance instance = mob.getAttribute(entry.getKey());
                if (instance != null) {
                    instance.setBaseValue(entry.getValue());
                }
            }
            if (!entityData.nbt().isEmpty()) {
                CompoundTag merged = mob.saveWithoutId(new CompoundTag());
                for (String key : entityData.nbt().getAllKeys()) {
                    merged.put(key, entityData.nbt().get(key).copy());
                }
                mob.load(merged);
            }
            entityConsumer.accept(mob);
            mob.setPersistenceRequired();
            mob.setHealth(mob.getMaxHealth());
        } else if (!entityData.nbt().isEmpty()) {
            CompoundTag merged = spawnedEntity.saveWithoutId(new CompoundTag());
            for (String key : entityData.nbt().getAllKeys()) {
                merged.put(key, entityData.nbt().get(key).copy());
            }
            spawnedEntity.load(merged);
        }

        level.addFreshEntity(spawnedEntity);
        return spawnedEntity;
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
            boolean solidBelow = below.isFaceSturdy(level, candidate.below(), net.minecraft.core.Direction.UP);
            boolean empty = state.getCollisionShape(level, candidate).isEmpty();
            boolean emptyAbove = above.getCollisionShape(level, candidate.above()).isEmpty();
            if (solidBelow && empty && emptyAbove) {
                return candidate;
            }
            y++;
        }
        return pos;
    }

    public void setDataId(Level level, BlockPos pos, ResourceLocation id) {
        this.dataId = id;
        this.data = id == null ? null : MineCellsReloadListeners.spawnerRunes().entries().get(id);
        if (level != null && !level.isClientSide && id != null && data == null) {
            MineCells.LOGGER.warn("Unknown spawner rune data id {} at {} in {}", id, pos.toShortString(), level.dimension().location());
        }
    }

    public void ensureData(Level level, BlockPos pos) {
        if (dataId == null) {
            setDataId(level, pos, MineCells.id("prison"));
        } else if (data == null) {
            setDataId(level, pos, dataId);
        }
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
