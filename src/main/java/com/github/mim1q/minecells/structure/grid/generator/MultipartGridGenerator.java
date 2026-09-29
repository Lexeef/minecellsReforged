package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomData;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomGridGenerator;
import com.github.mim1q.minecells.util.MathUtils;

import net.minecraft.core.Vec3i;

public abstract class MultipartGridGenerator extends RoomGridGenerator {
    private final int xPart;
    private final int zPart;

    protected MultipartGridGenerator(int xPart, int zPart) {
        this.xPart = xPart;
        this.zPart = zPart;
    }

    @Override
    protected void addRoom(RoomData data) {
        this.usedPositions.add(data.pos);

        int x = data.pos.getX() - 16 * this.xPart;
        int z = data.pos.getZ() - 16 * this.zPart;
        int terrainSampleX = data.terrainSamplePos.getX() - 16 * this.xPart;
        int terrainSampleZ = data.terrainSamplePos.getZ() - 16 * this.zPart;

        RoomData newData = RoomData.create(x - 8, data.pos.getY(), z - 8, data.poolId)
            .rotation(data.rotation)
            .offset(data.offset);

        boolean outOfBounds = x < 0 || z < 0 || x >= 16 || z >= 16;

        if (data.specialPoint != null) {
            newData.specialPoint(data.specialPoint.id(), data.specialPoint.offset(), data.specialPoint.facing());
            this.specialPoints.add(new SpecialPoint(
                newData.specialPoint.id(),
                newData.pos.offset(-24, 0, -24).multiply(16)
                    .offset(MathUtils.getRotatedOffsetWithinChunk(newData.specialPoint.offset(), newData.rotation)),
                newData.specialPoint.facing().getRotated(newData.rotation)
            ));
        }

        if (data.terrainFit) {
            newData.terrainFit();
            newData.terrainSamplePos = new Vec3i(terrainSampleX - 8, data.terrainSamplePos.getY(), terrainSampleZ - 8);
        }

        if (outOfBounds) {
            return;
        }

        this.rooms.add(newData);
    }
}
