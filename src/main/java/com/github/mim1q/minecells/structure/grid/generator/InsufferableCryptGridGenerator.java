package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.structure.grid.SpecialPointIds;

import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;

/**
 * Fabric-parity crypt base generator: marks the entrance cell for run routing.
 * The playable dungeon itself remains {@code minecells:big_jigsaw} via datapack.
 */
public class InsufferableCryptGridGenerator extends MultipartGridGenerator {
    public InsufferableCryptGridGenerator(int xPart, int zPart) {
        super(xPart, zPart);
    }

    @Override
    protected void addRooms(RandomSource random) {
        addRoom(room(new Vec3i(32, 2, 32), Pools.EMPTY.location())
            .specialPoint(SpecialPointIds.ENTRANCE, new Vec3i(6, 9, 1), Rotation.NONE));
    }
}
