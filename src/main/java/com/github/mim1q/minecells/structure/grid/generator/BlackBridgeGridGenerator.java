package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.structure.grid.SpecialPointIds;

import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;

/**
 * Forge port of Fabric {@code BlackBridgeGridGenerator}.
 */
public class BlackBridgeGridGenerator extends MultipartGridGenerator {
    public BlackBridgeGridGenerator(int xPart, int zPart) {
        super(xPart, zPart);
    }

    @Override
    protected void addRooms(RandomSource random) {
        addRoom(room(new Vec3i(34, 4, 32), Pools.EMPTY.location())
            .specialPoint(SpecialPointIds.ENTRANCE, new Vec3i(1, 6, 11), Rotation.CLOCKWISE_90));

        for (int x = 0; x < 64; ++x) {
            if ((x >= 40 && x <= 42) || (x >= 21 && x <= 26)) {
                continue;
            }

            int[] zCoords = {31, 38, 29, 40};
            int[] heightModifiers = {8, 8, 11, 11};

            for (int i = 0; i < zCoords.length; i++) {
                int z = zCoords[i];
                int maxY = heightModifiers[i] + random.nextInt(2);
                for (int y = 0; y < maxY; ++y) {
                    ResourceLocation id = PromenadeWallGenerator.UNDERGROUND;
                    if (y == 4) {
                        id = PromenadeWallGenerator.BOTTOM;
                    }
                    if (y >= 5 && y < maxY - 1) {
                        id = PromenadeWallGenerator.MIDDLE;
                    }
                    if (y == maxY - 1) {
                        id = PromenadeWallGenerator.TOP;
                    }
                    addRoom(room(new Vec3i(x, y, z), id));
                }
            }
        }
    }
}
