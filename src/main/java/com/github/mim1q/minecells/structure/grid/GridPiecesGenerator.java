package com.github.mim1q.minecells.structure.grid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class GridPiecesGenerator {
    private GridPiecesGenerator() {
    }

    public static List<RoomData> generateRoomData(Structure.GenerationContext context, RoomGridGenerator generator) {
        return generator.generate(context);
    }

    public static BlockPos getTerrainFitStart(
        RoomData data,
        BlockPos startPos,
        Optional<Heightmap.Types> projectStartToHeightmap,
        Structure.GenerationContext context,
        int size
    ) {
        BlockPos pos = startPos.offset(data.terrainSamplePos.multiply(size));
        BlockPos heightmapPos = pos.offset(data.terrainSampleOffset);
        int heightmapY = projectStartToHeightmap.map(
            type -> context.chunkGenerator().getFirstFreeHeight(
                heightmapPos.getX(),
                heightmapPos.getZ(),
                type,
                context.heightAccessor(),
                context.randomState()
            )
        ).orElse(startPos.getY());
        int heightDiff = heightmapY - startPos.getY();
        return startPos.offset(data.pos.multiply(size)).offset(data.offset).offset(0, heightDiff, 0);
    }

    public static final class RoomData {
        public final Vec3i pos;
        public final ResourceLocation poolId;
        public Rotation rotation = Rotation.NONE;
        public Vec3i offset = Vec3i.ZERO;
        public boolean terrainFit = false;
        public Vec3i terrainSamplePos;
        public Vec3i terrainSampleOffset = new Vec3i(8, 0, 8);

        public RoomData(Vec3i pos, ResourceLocation poolId) {
            this.pos = pos;
            this.poolId = poolId;
            this.terrainSamplePos = pos;
        }

        public static RoomData create(Vec3i pos, ResourceLocation poolId) {
            return new RoomData(pos, poolId);
        }

        public static RoomData create(int x, int y, int z, ResourceLocation poolId) {
            return create(new Vec3i(x, y, z), poolId);
        }

        public RoomData rotation(Rotation rotation) {
            this.rotation = rotation;
            return this;
        }

        public RoomData offset(Vec3i offset) {
            this.offset = offset;
            return this;
        }

        public RoomData offset(int x, int y, int z) {
            return this.offset(new Vec3i(x, y, z));
        }

        public RoomData terrainFitOffset(int x, int y, int z) {
            this.offset(x, y, z);
            this.terrainSampleOffset = this.offset;
            return this.terrainFit();
        }

        public RoomData terrainFit() {
            this.terrainFit = true;
            return this;
        }

        public RoomData terrainFit(Vec3i pos) {
            this.terrainFit = true;
            this.terrainSamplePos = pos;
            return this;
        }

        public RoomData terrainFit(int x, int y, int z) {
            return this.terrainFit(new Vec3i(x, y, z));
        }

        public RoomData terrainSampleOffset(int x, int y, int z) {
            this.terrainSampleOffset = new Vec3i(x, y, z);
            return this;
        }
    }

    public abstract static class RoomGridGenerator {
        protected final List<RoomData> rooms = new ArrayList<>();
        protected final Set<Vec3i> usedPositions = new HashSet<>();

        protected abstract void addRooms(RandomSource random);

        public List<RoomData> generate(Structure.GenerationContext context) {
            this.rooms.clear();
            this.usedPositions.clear();
            this.addRooms(context.random());
            return this.rooms;
        }

        protected final void addRoom(Vec3i pos, Rotation rotation, ResourceLocation poolId, Vec3i offset, boolean terrainFit) {
            RoomData builder = RoomData.create(pos, poolId).rotation(rotation).offset(offset);
            if (terrainFit) {
                builder.terrainFit();
            }
            this.addRoom(builder);
        }

        protected final void addRoom(Vec3i pos, Rotation rotation, ResourceLocation poolId, Vec3i offset) {
            this.addRoom(pos, rotation, poolId, offset, false);
        }

        protected final void addRoom(Vec3i pos, Rotation rotation, ResourceLocation poolId) {
            this.addRoom(pos, rotation, poolId, Vec3i.ZERO);
        }

        protected final void addTerrainFitRoom(Vec3i pos, Rotation rotation, ResourceLocation poolId, Vec3i offset) {
            this.addRoom(pos, rotation, poolId, offset, true);
        }

        protected final void addTerrainFitRoom(Vec3i pos, Rotation rotation, ResourceLocation poolId) {
            this.addTerrainFitRoom(pos, rotation, poolId, Vec3i.ZERO);
        }

        protected void addRoom(RoomData roomData) {
            this.rooms.add(roomData);
            this.usedPositions.add(roomData.pos);
        }

        protected boolean isPositionUsed(Vec3i pos) {
            return this.usedPositions.contains(pos);
        }

        protected static RoomData room(int x, int y, int z, ResourceLocation poolId) {
            return RoomData.create(x, y, z, poolId);
        }

        protected static RoomData room(Vec3i pos, ResourceLocation poolId) {
            return RoomData.create(pos, poolId);
        }
    }
}
