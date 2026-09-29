package com.github.mim1q.minecells.structure.grid;

import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.structure.grid.generator.BetterPromenadeGridGenerator;
import com.github.mim1q.minecells.structure.grid.generator.BlackBridgeGridGenerator;
import com.github.mim1q.minecells.structure.grid.generator.InsufferableCryptGridGenerator;
import com.github.mim1q.minecells.structure.grid.generator.PrisonGridGenerator;
import com.github.mim1q.minecells.structure.grid.generator.RampartsGridGenerator;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomGridGenerator;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomGridGenerator.SpecialPoint;
import com.github.mim1q.minecells.util.MathUtils;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

public final class GridBasedStructureUtils {
    private static long prevSeed = 0L;
    private static final HashMap<Pair<String, Vec3i>, List<SpecialPoint>> SPECIAL_POINTS_CACHE = new HashMap<>();

    private GridBasedStructureUtils() {
    }

    public static List<SpecialPoint> getSpecialPoints(
        ServerLevel level,
        Vec3i structureOrigin,
        RoomGridGenerator generator,
        String cacheKey
    ) {
        long seed = level.getSeed();
        if (prevSeed != seed) {
            SPECIAL_POINTS_CACHE.clear();
            prevSeed = seed;
        }

        Vec3i pos = MathUtils.getClosestMultiplePosition(structureOrigin, 1024);
        return SPECIAL_POINTS_CACHE.computeIfAbsent(Pair.of(cacheKey, pos), ignored -> {
            ChunkGenerator chunkGenerator = level.getChunkSource().getGenerator();
            Structure.GenerationContext context = new Structure.GenerationContext(
                level.registryAccess(),
                chunkGenerator,
                chunkGenerator.getBiomeSource(),
                level.getChunkSource().randomState(),
                level.getStructureManager(),
                seed,
                new net.minecraft.world.level.ChunkPos(new BlockPos(structureOrigin)),
                level,
                biome -> true
            );
            return generator.generateSpecialPoints(context);
        });
    }

    public static Optional<SpecialPoint> getSpecialPoint(
        ServerLevel level,
        Vec3i structureOrigin,
        RoomGridGenerator generator,
        String cacheKey,
        ResourceLocation id
    ) {
        for (SpecialPoint point : getSpecialPoints(level, structureOrigin, generator, cacheKey)) {
            if (point.id().equals(id)) {
                return Optional.of(point);
            }
        }
        return Optional.empty();
    }

    public static Optional<SpecialPoint> getSpecialPoint(
        ServerLevel level,
        Vec3i structureOrigin,
        MineCellsDimension dimension,
        ResourceLocation id
    ) {
        RoomGridGenerator generator = createBaseGenerator(dimension);
        if (generator == null) {
            return Optional.empty();
        }
        return getSpecialPoint(level, structureOrigin, generator, dimension.id().toString(), id);
    }

    public static List<SpecialPoint> getSpecialPoints(
        ServerLevel level,
        Vec3i structureOrigin,
        MineCellsDimension dimension
    ) {
        RoomGridGenerator generator = createBaseGenerator(dimension);
        if (generator == null) {
            return List.of();
        }
        return getSpecialPoints(level, structureOrigin, generator, dimension.id().toString());
    }

    public static Optional<SpecialPoint> getSpecialPoint(
        ServerLevel level,
        Vec3i structureOrigin,
        ResourceLocation id
    ) {
        MineCellsDimension dimension = MineCellsDimension.of(level);
        if (dimension == null) {
            return Optional.empty();
        }
        return getSpecialPoint(level, structureOrigin, dimension, id);
    }

    @Nullable
    public static RoomGridGenerator createBaseGenerator(MineCellsDimension dimension) {
        return switch (dimension) {
            case PRISONERS_QUARTERS -> new PrisonGridGenerator(0, 0);
            case INSUFFERABLE_CRYPT -> new InsufferableCryptGridGenerator(0, 0);
            case PROMENADE_OF_THE_CONDEMNED -> new BetterPromenadeGridGenerator(0, 0);
            case RAMPARTS -> new RampartsGridGenerator(0, 0);
            case BLACK_BRIDGE -> new BlackBridgeGridGenerator(0, 0);
            default -> null;
        };
    }

    public static void clearSpecialPointsCache() {
        SPECIAL_POINTS_CACHE.clear();
    }
}
