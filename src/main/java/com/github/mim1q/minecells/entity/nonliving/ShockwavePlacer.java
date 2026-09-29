package com.github.mim1q.minecells.entity.nonliving;

import com.github.mim1q.minecells.network.MineCellsNetwork;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsEntities;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.network.NetworkHooks;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.UUID;

public class ShockwavePlacer extends Entity {
    private static final double STEPS_PER_POS = 2.0D;

    private final Map<Integer, Set<BlockPos>> positions;
    private int maxAge;
    private BlockState block;
    @Nullable
    private UUID ownerUuid;
    private float damage;
    private int blockAge;

    private ShockwavePlacer(
        EntityType<?> type,
        Level level,
        Map<Integer, Set<BlockPos>> positions,
        BlockState block,
        @Nullable UUID ownerUuid,
        float damage,
        int blockAge
    ) {
        super(type, level);
        this.positions = positions;
        this.maxAge = positions.keySet().stream().max(Integer::compareTo).orElse(5);
        this.block = block;
        this.ownerUuid = ownerUuid;
        this.damage = damage;
        this.blockAge = blockAge;
    }

    public ShockwavePlacer(EntityType<?> type, Level level) {
        this(type, level, new HashMap<>(), Blocks.FIRE.defaultBlockState(), null, 0.0F, 20);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            return;
        }

        Set<BlockPos> posList = positions.get(tickCount);
        if (posList != null) {
            for (BlockPos pos : posList) {
                BlockPos placedPos = tryPlace(pos);
                if (placedPos != null && level() instanceof ServerLevel serverLevel) {
                    level().scheduleTick(placedPos, block.getBlock(), blockAge);
                    MineCellsNetwork.sendShockwaveClientEvent(serverLevel, block.getBlock(), placedPos, false);
                    damageEntities(placedPos);
                }
            }
        }

        if (tickCount > maxAge) {
            discard();
        }
    }

    private BlockPos tryPlace(BlockPos position) {
        BlockPos[] candidates = new BlockPos[]{position, position.above(), position.below()};
        for (BlockPos pos : candidates) {
            if (block.canSurvive(level(), pos)) {
                level().setBlock(pos, block, 3);
                return pos;
            }
        }
        return null;
    }

    private AABB getDamageBox(BlockPos position) {
        return new AABB(position)
            .inflate(0.1D, 0.0D, 0.1D)
            .move(0.0D, -0.25D, 0.0D);
    }

    private void damageEntities(BlockPos position) {
        boolean isPlayer = ownerUuid != null && level().getPlayerByUUID(ownerUuid) != null;
        Predicate<LivingEntity> predicate = isPlayer
            ? e -> !e.getUUID().equals(ownerUuid)
            : e -> !(e instanceof Monster);
        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, getDamageBox(position), predicate)) {
            entity.hurt(level().damageSources().onFire(), damage);
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return false;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        CompoundTag nbtPositions = tag.getCompound("positions");
        positions.clear();
        for (String key : nbtPositions.getAllKeys()) {
            long[] values = nbtPositions.getLongArray(key);
            positions.put(Integer.parseInt(key), Arrays.stream(values).mapToObj(BlockPos::of).collect(Collectors.toSet()));
        }
        maxAge = tag.getInt("maxAge");
        if (tag.contains("block")) {
            CompoundTag blockNbt = tag.getCompound("block");
            block = BlockState.CODEC
                .decode(NbtOps.INSTANCE, blockNbt)
                .result()
                .orElse(new Pair<>(MineCellsBlocks.SHOCKWAVE_FLAME.get().defaultBlockState(), null))
                .getFirst();
        }
        if (tag.hasUUID("ownerUuid")) {
            ownerUuid = tag.getUUID("ownerUuid");
        }
        damage = tag.getFloat("damage");
        blockAge = tag.getInt("blockAge");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        CompoundTag nbtPositions = new CompoundTag();
        positions.forEach((key, value) -> nbtPositions.putLongArray(key.toString(), value.stream().map(BlockPos::asLong).toList()));
        tag.put("positions", nbtPositions);
        tag.putInt("maxAge", maxAge);
        BlockState.CODEC
            .encodeStart(NbtOps.INSTANCE, block)
            .result()
            .ifPresent(blockNbt -> tag.put("block", blockNbt));
        if (ownerUuid != null) {
            tag.putUUID("ownerUuid", ownerUuid);
        }
        tag.putFloat("damage", damage);
        tag.putInt("blockAge", blockAge);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static ShockwavePlacer createLine(
        Level level,
        Vec3 startPos,
        Vec3 endPos,
        float interval,
        BlockState block,
        @Nullable UUID ownerUuid,
        float damage
    ) {
        Map<Integer, Set<BlockPos>> map = new HashMap<>();
        Vec3 diff = endPos.subtract(startPos);
        double stepLength = 1.0D / STEPS_PER_POS;
        Vec3 step = diff.normalize().scale(stepLength);
        Vec3 pos = startPos;

        double accumulatedLength = 0.0D;
        for (int i = 1; accumulatedLength <= diff.length() + 2.0D; i++) {
            Set<BlockPos> set = new HashSet<>();
            for (int j = 0; j < STEPS_PER_POS; j++) {
                set.add(BlockPos.containing(pos));
                pos = pos.add(step);
            }
            int index = (int) (i * interval);
            map.computeIfAbsent(index, ignored -> new HashSet<>()).addAll(set);
            accumulatedLength += 1.0D;
        }

        ShockwavePlacer placer = new ShockwavePlacer(
            MineCellsEntities.SHOCKWAVE_PLACER.get(),
            level,
            map,
            block,
            ownerUuid,
            damage,
            (int) interval + 10
        );
        placer.setPos(startPos);
        return placer;
    }

    public static ShockwavePlacer createCircle(
        Level level,
        Vec3 origin,
        int radius,
        float interval,
        BlockState block,
        @Nullable UUID ownerUuid,
        float damage
    ) {
        Map<Integer, Set<BlockPos>> map = new HashMap<>();
        for (int i = 1; i < radius; i++) {
            Set<BlockPos> set = new HashSet<>();
            for (int angle = 0; angle < i * 8; angle++) {
                float x = (float) Math.cos(angle * Math.PI / (i * 4.0D)) * i;
                float z = (float) Math.sin(angle * Math.PI / (i * 4.0D)) * i;
                set.add(BlockPos.containing(origin).offset(Math.round(x), 0, Math.round(z)));
            }
            int index = (int) (i * interval);
            map.computeIfAbsent(index, ignored -> new HashSet<>()).addAll(set);
        }
        ShockwavePlacer placer = new ShockwavePlacer(
            MineCellsEntities.SHOCKWAVE_PLACER.get(),
            level,
            map,
            block,
            ownerUuid,
            damage,
            (int) interval + 2
        );
        placer.setPos(origin);
        return placer;
    }
}
