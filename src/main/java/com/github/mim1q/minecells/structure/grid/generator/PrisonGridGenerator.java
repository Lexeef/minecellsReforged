package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomGridGenerator;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;

public class PrisonGridGenerator extends RoomGridGenerator {
    private static final ResourceLocation SPAWN = MineCells.id("prison/spawn");
    private static final ResourceLocation MAIN_CORRIDOR = MineCells.id("prison/main_corridor");
    private static final ResourceLocation MAIN_CORRIDOR_END = MineCells.id("prison/main_corridor_end");
    private static final ResourceLocation CORRIDOR = MineCells.id("prison/corridor");
    private static final ResourceLocation CORRIDOR_END = MineCells.id("prison/corridor_end");
    private static final ResourceLocation CHAIN_UPPER = MineCells.id("prison/chain_upper");
    private static final ResourceLocation CHAIN_LOWER = MineCells.id("prison/chain_lower");
    private static final ResourceLocation END = MineCells.id("prison/end");
    private static final ResourceLocation END_SEWERS = MineCells.id("prison/end_sewers");

    @Override
    protected void addRooms(RandomSource random) {
        Vec3i end1 = this.generateFloor(Vec3i.ZERO, Rotation.NONE, SPAWN, CHAIN_UPPER, random, random.nextBoolean(), true);
        this.generateFloor(end1.offset(0, -1, 0), Rotation.CLOCKWISE_180, CHAIN_LOWER, END, random, random.nextBoolean(), false);
    }

    protected Vec3i generateFloor(
        Vec3i pos,
        Rotation rotation,
        ResourceLocation startPool,
        ResourceLocation endPool,
        RandomSource random,
        boolean specialLeft,
        boolean sewersExit
    ) {
        this.addRoom(pos, rotation, startPool);
        Vec3i unit = rotation.rotate(Direction.SOUTH).getNormal();
        Vec3i side = rotation.rotate(Direction.EAST).getNormal();
        Vec3i endPos = pos.offset(0, -1, 0);
        int specialCorridor = random.nextInt(4) + 1;

        for (int i = 1; i < 5; i++) {
            this.addRoom(pos.offset(unit.multiply(i)), rotation, MAIN_CORRIDOR);
            int leftLength = random.nextInt(2) + 1;
            for (int j = 1; j <= leftLength; j++) {
                this.addRoom(pos.offset(unit.multiply(i)).offset(side.multiply(j)), Rotation.COUNTERCLOCKWISE_90.getRotated(rotation), CORRIDOR);
            }
            if (i == specialCorridor && specialLeft) {
                this.addRoom(pos.offset(unit.multiply(i)).offset(side.multiply(leftLength + 1)), Rotation.COUNTERCLOCKWISE_90.getRotated(rotation), endPool);
                endPos = pos.offset(unit.multiply(i)).offset(side.multiply(leftLength + 1));
            } else {
                this.addRoom(pos.offset(unit.multiply(i)).offset(side.multiply(leftLength + 1)), Rotation.COUNTERCLOCKWISE_90.getRotated(rotation), CORRIDOR_END);
            }

            int rightLength = random.nextInt(2) + 1;
            for (int j = 1; j <= rightLength; j++) {
                this.addRoom(pos.offset(unit.multiply(i)).offset(side.multiply(-j)), Rotation.CLOCKWISE_90.getRotated(rotation), CORRIDOR);
            }
            if (i == specialCorridor && !specialLeft) {
                this.addRoom(pos.offset(unit.multiply(i)).offset(side.multiply(-rightLength - 1)), Rotation.CLOCKWISE_90.getRotated(rotation), endPool);
                endPos = pos.offset(unit.multiply(i)).offset(side.multiply(-rightLength - 1));
            } else {
                this.addRoom(pos.offset(unit.multiply(i)).offset(side.multiply(-rightLength - 1)), Rotation.CLOCKWISE_90.getRotated(rotation), CORRIDOR_END);
            }
        }

        this.addRoom(pos.offset(unit.multiply(5)), rotation, sewersExit ? END_SEWERS : MAIN_CORRIDOR_END);
        return endPos;
    }
}
