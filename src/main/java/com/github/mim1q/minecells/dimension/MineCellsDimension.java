package com.github.mim1q.minecells.dimension;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.structure.grid.GridBasedStructureUtils;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomGridGenerator.SpecialPoint;
import com.github.mim1q.minecells.structure.grid.SpecialPointIds;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.LongSupplier;

public enum MineCellsDimension {
    OVERWORLD(new ResourceLocation("minecraft", "overworld"), new BlockPos(0, 0, 0), 0.0D, 0.0F),
    PRISONERS_QUARTERS(
        MineCells.id("prison"),
        new BlockPos(2, 44, 3),
        1024.0D,
        -90.0F,
        Map.of(
            MineCells.id("insufferable_crypt"), new BlockPos(7, 50, 83)
        )
    ),
    INSUFFERABLE_CRYPT(MineCells.id("insufferable_crypt"), new BlockPos(6, 43, 2), 1024.0D, 90.0F),
    PROMENADE_OF_THE_CONDEMNED(MineCells.id("promenade"), new BlockPos(6, -5, 6), 1024.0D, 0.0F),
    RAMPARTS(MineCells.id("ramparts"), new BlockPos(-54, 213, -265), 384.0D, 180.0F),
    BLACK_BRIDGE(MineCells.id("black_bridge"), new BlockPos(32, 72, 11), 384.0D, 0.0F);

    private static final Set<MineCellsDimension> DIMENSIONS_WITH_SURFACE = Set.of(PROMENADE_OF_THE_CONDEMNED);

    private final ResourceLocation id;
    private final BlockPos defaultSpawnOffset;
    private final Map<ResourceLocation, BlockPos> routeSpawnOffsets;
    private final double borderSize;
    private final float yaw;

    MineCellsDimension(ResourceLocation id, BlockPos defaultSpawnOffset, double borderSize, float yaw) {
        this(id, defaultSpawnOffset, borderSize, yaw, Map.of());
    }

    MineCellsDimension(
        ResourceLocation id,
        BlockPos defaultSpawnOffset,
        double borderSize,
        float yaw,
        Map<ResourceLocation, BlockPos> routeSpawnOffsets
    ) {
        this.id = id;
        this.defaultSpawnOffset = defaultSpawnOffset;
        this.routeSpawnOffsets = routeSpawnOffsets;
        this.borderSize = borderSize;
        this.yaw = yaw;
    }

    public ResourceLocation id() {
        return id;
    }

    public BlockPos spawnOffset() {
        return defaultSpawnOffset;
    }

    public double borderSize() {
        return borderSize;
    }

    public float yaw() {
        return yaw;
    }

    public int getDimensionLevel() {
        return switch (this) {
            case PROMENADE_OF_THE_CONDEMNED -> 1;
            case RAMPARTS -> 2;
            default -> 0;
        };
    }

    public Vec3 getTeleportPosition(BlockPos pos, ServerLevel originLevel) {
        return getTeleportPosition(pos, originLevel, SpecialPointIds.ENTRANCE);
    }

    public Vec3 getTeleportPosition(BlockPos pos, ServerLevel originLevel, ResourceLocation specialPointId) {
        ServerLevel destination = getLevel(originLevel);
        if (destination == null) {
            return Vec3.atBottomCenterOf(pos);
        }
        Optional<Vec3> specialPointPos = getSpecialPointTeleportPosition(pos, originLevel, specialPointId);
        if (specialPointPos.isPresent()) {
            return specialPointPos.get();
        }
        BlockPos tpPos = getPreferredSpawnPos(originLevel.dimension().location(), pos, originLevel, specialPointId);
        if (DIMENSIONS_WITH_SURFACE.contains(this)) {
            int y = destination.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, tpPos.getX(), tpPos.getZ());
            return Vec3.atBottomCenterOf(new BlockPos(tpPos.getX(), y, tpPos.getZ()));
        }
        return Vec3.atBottomCenterOf(findSafeInteriorPosition(
            destination,
            tpPos,
            originLevel.dimension().location(),
            getDoorwaySearchRadius(originLevel.dimension().location())
        ));
    }

    public Optional<Vec3> getSpecialPointTeleportPosition(BlockPos pos, ServerLevel originLevel, ResourceLocation specialPointId) {
        ServerLevel destination = getLevel(originLevel);
        if (destination == null) {
            return Optional.empty();
        }
        return findSpecialPoint(originLevel.dimension().location(), pos, destination, specialPointId).map(point -> {
            Vec3 tpPos = Vec3.atBottomCenterOf(getRunCenter(pos).offset(point.offset()));
            Vec3i facing = point.facing().rotate(Direction.NORTH).getNormal();
            return tpPos.add(facing.getX() * -0.5D, 0.0D, facing.getZ() * -0.5D);
        });
    }

    public boolean isValidStoredTeleportTarget(BlockPos sourcePos, ServerLevel originLevel, BlockPos candidatePos) {
        return isValidStoredTeleportTarget(originLevel.dimension().location(), sourcePos, originLevel, candidatePos);
    }

    public boolean isValidStoredTeleportTarget(
        ResourceLocation sourceDimension,
        BlockPos sourcePos,
        ServerLevel originLevel,
        BlockPos candidatePos
    ) {
        ServerLevel destination = getLevel(originLevel);
        if (destination == null) {
            return true;
        }

        BlockPos expectedPos = getPreferredSpawnPos(sourceDimension, sourcePos, originLevel);
        if (DIMENSIONS_WITH_SURFACE.contains(this)) {
            int expectedY = destination.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, expectedPos.getX(), expectedPos.getZ());
            return horizontalDistanceSquared(candidatePos, expectedPos.getX(), expectedPos.getZ()) <= 64 * 64
                && Math.abs(candidatePos.getY() - expectedY) <= 24;
        }

        int validationRadius = Math.min(24, getDoorwaySearchRadius(sourceDimension));
        if (searchDoorways(destination, candidatePos, null, validationRadius, 12, true).found()) {
            return true;
        }

        return squaredDistance(candidatePos, expectedPos) <= 24 * 24
            && isWalkable(destination, candidatePos)
            && !isHardstoneFloor(destination, candidatePos);
    }

    @Nullable
    public ServerLevel getLevel(ServerLevel referenceLevel) {
        ResourceKey<Level> key = ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, id);
        return referenceLevel.getServer().getLevel(key);
    }

    @Nullable
    public static MineCellsDimension of(ResourceLocation id) {
        return Arrays.stream(values()).filter(value -> value.id.equals(id)).findFirst().orElse(null);
    }

    @Nullable
    public static MineCellsDimension of(Level level) {
        return of(level.dimension().location());
    }

    public int getColor() {
        return switch (this) {
            case OVERWORLD -> 0x8EF96D;
            case PRISONERS_QUARTERS -> 0x54EF88;
            case PROMENADE_OF_THE_CONDEMNED -> 0x93FFF7;
            case INSUFFERABLE_CRYPT -> 0xFF4CF4;
            case RAMPARTS -> 0xFFC540;
            case BLACK_BRIDGE -> 0x623CC9;
        };
    }

    public static int getColor(@Nullable Level level, int defaultColor) {
        if (level == null) {
            return defaultColor;
        }
        MineCellsDimension dimension = of(level);
        return dimension == null ? defaultColor : dimension.getColor();
    }

    public static boolean isMineCellsDimension(Level level) {
        MineCellsDimension dimension = of(level);
        return dimension != null && dimension != OVERWORLD;
    }

    @Nullable
    public static Double getFallResetHeight(Level level) {
        if (!isMineCellsDimension(level)) {
            return null;
        }
        MineCellsDimension dimension = of(level);
        if (dimension == RAMPARTS) {
            return 180.0D;
        }
        return null;
    }

    private BlockPos getPreferredSpawnPos(@Nullable ResourceLocation sourceDimension, BlockPos pos) {
        return getPreferredSpawnPos(sourceDimension, pos, null, SpecialPointIds.ENTRANCE);
    }

    private BlockPos getPreferredSpawnPos(@Nullable ResourceLocation sourceDimension, BlockPos pos, @Nullable ServerLevel originLevel) {
        return getPreferredSpawnPos(sourceDimension, pos, originLevel, SpecialPointIds.ENTRANCE);
    }

    private BlockPos getPreferredSpawnPos(
        @Nullable ResourceLocation sourceDimension,
        BlockPos pos,
        @Nullable ServerLevel originLevel,
        ResourceLocation specialPointId
    ) {
        BlockPos runCenter = getRunCenter(pos);
        if (originLevel != null) {
            ServerLevel destination = getLevel(originLevel);
            if (destination != null) {
                Optional<SpecialPoint> point = findSpecialPoint(sourceDimension, pos, destination, specialPointId);
                if (point.isPresent()) {
                    return runCenter.offset(point.get().offset());
                }
            }
        }
        return runCenter.offset(routeSpawnOffsets.getOrDefault(sourceDimension, defaultSpawnOffset));
    }

    public float getTeleportYaw(BlockPos pos, ServerLevel originLevel, ResourceLocation specialPointId) {
        ServerLevel destination = getLevel(originLevel);
        if (destination == null) {
            return yaw;
        }
        return findSpecialPoint(originLevel.dimension().location(), pos, destination, specialPointId)
            .map(point -> (float) point.facing().rotate(0, 360))
            .orElse(yaw);
    }

    private Optional<SpecialPoint> findSpecialPoint(
        @Nullable ResourceLocation sourceDimension,
        BlockPos pos,
        ServerLevel destination,
        @Nullable ResourceLocation specialPointId
    ) {
        ResourceLocation effectiveSpecialPoint = specialPointId != null ? specialPointId : SpecialPointIds.ENTRANCE;
        if (this == RAMPARTS && MineCells.id("black_bridge").equals(sourceDimension)
            && SpecialPointIds.ENTRANCE.equals(effectiveSpecialPoint)) {
            effectiveSpecialPoint = SpecialPointIds.EXIT;
        }
        try {
            return GridBasedStructureUtils.getSpecialPoint(destination, getRunCenter(pos), this, effectiveSpecialPoint);
        } catch (Exception e) {
            MineCells.LOGGER.error("Failed to get Mine Cells special point {} in {}", effectiveSpecialPoint, id, e);
            return Optional.empty();
        }
    }

    private static BlockPos getRunCenter(BlockPos pos) {
        return new BlockPos(MathUtils.getClosestMultiplePosition(pos, 1024));
    }

    private int getDoorwaySearchRadius(@Nullable ResourceLocation sourceDimension) {
        if (this == PRISONERS_QUARTERS && sourceDimension != null && !sourceDimension.equals(new ResourceLocation("minecraft", "overworld"))) {
            return 192;
        }
        if (this == RAMPARTS && MineCells.id("black_bridge").equals(sourceDimension)) {
            return 128;
        }
        return 96;
    }

    private static BlockPos findSafeInteriorPosition(
        ServerLevel level,
        BlockPos preferredPos,
        @Nullable ResourceLocation sourceDimension,
        int doorwaySearchRadius
    ) {
        // A doorway leading back into the destination itself never exists (fall reset, same-dimension teleports).
        ResourceLocation targetDimension = level.dimension().location().equals(sourceDimension) ? null : sourceDimension;
        DoorwaySearch doorways = searchDoorways(level, preferredPos, targetDimension, doorwaySearchRadius, 24, false);
        if (doorways.targeted != null) {
            return doorways.targeted;
        }
        if (doorways.any != null) {
            return doorways.any;
        }
        if (isWalkable(level, preferredPos) && !isHardstoneFloor(level, preferredPos)) {
            return preferredPos;
        }

        int minY = Math.max(level.getMinBuildHeight() + 1, preferredPos.getY() - 24);
        int maxY = Math.min(level.getMaxBuildHeight() - 2, preferredPos.getY() + 24);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        BlockPos[] bestPos = {null};
        long[] bestScore = {Long.MAX_VALUE};
        visitChunksByDistance(level, preferredPos, doorwaySearchRadius, () -> bestScore[0], chunk -> {
            int fromX = Math.max(chunk.getPos().getMinBlockX(), preferredPos.getX() - doorwaySearchRadius);
            int toX = Math.min(chunk.getPos().getMaxBlockX(), preferredPos.getX() + doorwaySearchRadius);
            int fromZ = Math.max(chunk.getPos().getMinBlockZ(), preferredPos.getZ() - doorwaySearchRadius);
            int toZ = Math.min(chunk.getPos().getMaxBlockZ(), preferredPos.getZ() + doorwaySearchRadius);
            for (int x = fromX; x <= toX; x++) {
                for (int z = fromZ; z <= toZ; z++) {
                    for (int y = minY; y <= maxY; y++) {
                        cursor.set(x, y, z);
                        if (!isWalkable(level, cursor)) {
                            continue;
                        }
                        long score = squaredDistance(cursor, preferredPos);
                        if (isHardstoneFloor(level, cursor)) {
                            score += 4096;
                        }
                        if (score < bestScore[0]) {
                            bestScore[0] = score;
                            bestPos[0] = cursor.immutable();
                        }
                    }
                }
            }
        });
        return bestPos[0] != null ? bestPos[0] : preferredPos;
    }

    private static final class DoorwaySearch {
        @Nullable
        BlockPos targeted;
        long targetedDistance = Long.MAX_VALUE;
        @Nullable
        BlockPos any;
        long anyDistance = Long.MAX_VALUE;

        boolean found() {
            return targeted != null || any != null;
        }
    }

    /**
     * Finds the closest walkable doorway entry, preferring doorways leading to {@code targetDimension}.
     * Doorways are looked up through chunk block entities, ring by ring, stopping once no farther chunk can win.
     */
    private static DoorwaySearch searchDoorways(
        ServerLevel level,
        BlockPos preferredPos,
        @Nullable ResourceLocation targetDimension,
        int horizontalRadius,
        int verticalRadius,
        boolean stopAtFirst
    ) {
        DoorwaySearch search = new DoorwaySearch();
        visitChunksByDistance(level, preferredPos, horizontalRadius, () -> {
            if (stopAtFirst && search.found()) {
                return -1L;
            }
            return targetDimension != null ? search.targetedDistance : search.anyDistance;
        }, chunk -> {
            for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                BlockPos doorwayPos = blockEntity.getBlockPos();
                if (Math.abs(doorwayPos.getX() - preferredPos.getX()) > horizontalRadius
                    || Math.abs(doorwayPos.getZ() - preferredPos.getZ()) > horizontalRadius
                    || Math.abs(doorwayPos.getY() - preferredPos.getY()) > verticalRadius) {
                    continue;
                }
                BlockState state = chunk.getBlockState(doorwayPos);
                if (!(state.getBlock() instanceof DoorwayPortalBlock doorwayBlock)) {
                    continue;
                }
                boolean targeted = targetDimension != null && doorwayBlock.getType().dimensionId().equals(targetDimension);
                long distance = squaredDistance(doorwayPos, preferredPos);
                if (distance >= search.anyDistance && (!targeted || distance >= search.targetedDistance)) {
                    continue;
                }
                Direction facing = state.getValue(DoorwayPortalBlock.FACING);
                BlockPos entryPos = doorwayPos.relative(facing);
                if (!isWalkable(level, entryPos)) {
                    entryPos = doorwayPos.relative(facing.getOpposite());
                    if (!isWalkable(level, entryPos)) {
                        continue;
                    }
                }
                if (distance < search.anyDistance) {
                    search.anyDistance = distance;
                    search.any = entryPos;
                }
                if (targeted && distance < search.targetedDistance) {
                    search.targetedDistance = distance;
                    search.targeted = entryPos;
                }
            }
        });
        return search;
    }

    /**
     * Visits chunks within {@code horizontalRadius} of {@code center} in square rings of increasing distance.
     * Stops before a ring whose closest possible block is farther than {@code bestSquaredDistance}.
     */
    private static void visitChunksByDistance(
        ServerLevel level,
        BlockPos center,
        int horizontalRadius,
        LongSupplier bestSquaredDistance,
        Consumer<LevelChunk> visitor
    ) {
        int centerX = SectionPos.blockToSectionCoord(center.getX());
        int centerZ = SectionPos.blockToSectionCoord(center.getZ());
        int minX = SectionPos.blockToSectionCoord(center.getX() - horizontalRadius);
        int maxX = SectionPos.blockToSectionCoord(center.getX() + horizontalRadius);
        int minZ = SectionPos.blockToSectionCoord(center.getZ() - horizontalRadius);
        int maxZ = SectionPos.blockToSectionCoord(center.getZ() + horizontalRadius);
        int maxRing = Math.max(Math.max(centerX - minX, maxX - centerX), Math.max(centerZ - minZ, maxZ - centerZ));
        for (int ring = 0; ring <= maxRing; ring++) {
            long ringDistance = Math.max(0, (ring - 1) * 16L);
            if (bestSquaredDistance.getAsLong() < ringDistance * ringDistance) {
                return;
            }
            for (int x = centerX - ring; x <= centerX + ring; x++) {
                for (int z = centerZ - ring; z <= centerZ + ring; z++) {
                    if (Math.max(Math.abs(x - centerX), Math.abs(z - centerZ)) != ring
                        || x < minX || x > maxX || z < minZ || z > maxZ) {
                        continue;
                    }
                    visitor.accept(level.getChunk(x, z));
                }
            }
        }
    }

    private static boolean isWalkable(ServerLevel level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState floor = level.getBlockState(below);
        return floor.isFaceSturdy(level, below, Direction.UP)
            && level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
            && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty();
    }

    private static boolean isHardstoneFloor(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(MineCellsBlocks.HARDSTONE.get());
    }

    private static long squaredDistance(BlockPos first, BlockPos second) {
        long dx = (long) first.getX() - second.getX();
        long dy = (long) first.getY() - second.getY();
        long dz = (long) first.getZ() - second.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    private static long horizontalDistanceSquared(BlockPos pos, int x, int z) {
        long dx = (long) pos.getX() - x;
        long dz = (long) pos.getZ() - z;
        return dx * dx + dz * dz;
    }
}
