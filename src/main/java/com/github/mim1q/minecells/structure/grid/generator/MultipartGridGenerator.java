package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomData;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomGridGenerator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.structure.Structure;

public abstract class MultipartGridGenerator extends RoomGridGenerator {
    private final int xPart;
    private final int zPart;

    protected MultipartGridGenerator(int xPart, int zPart) {
        this.xPart = xPart;
        this.zPart = zPart;
    }

    @Override
    public List<RoomData> generate(Structure.GenerationContext context) {
        BlockPos origin = context.chunkPos().getWorldPosition();
        int x = Math.round(origin.getX() / 1024.0F) * 1024;
        int z = Math.round(origin.getZ() / 1024.0F) * 1024;
        context.random().setSeed(new Vec3i(x, 0, z).hashCode() + context.seed());
        return super.generate(context);
    }

    @Override
    protected void addRoom(RoomData data) {
        this.usedPositions.add(data.pos);

        int x = data.pos.getX() - 16 * this.xPart;
        int z = data.pos.getZ() - 16 * this.zPart;
        int terrainSampleX = data.terrainSamplePos.getX() - 16 * this.xPart;
        int terrainSampleZ = data.terrainSamplePos.getZ() - 16 * this.zPart;
        if (x < 0 || z < 0 || x >= 16 || z >= 16) {
            return;
        }

        RoomData newData = RoomData.create(x - 8, data.pos.getY(), z - 8, data.poolId)
            .rotation(data.rotation)
            .offset(data.offset);

        if (data.terrainFit) {
            newData.terrainFit();
            newData.terrainSamplePos = new Vec3i(terrainSampleX - 8, data.terrainSamplePos.getY(), terrainSampleZ - 8);
        }

        this.rooms.add(newData);
    }
}
