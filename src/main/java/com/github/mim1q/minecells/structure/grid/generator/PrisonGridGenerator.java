package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.SpecialPointIds;
import com.github.mim1q.minecells.structure.grid.util.Vec3iCursor;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;

import javax.annotation.Nullable;

public class PrisonGridGenerator extends MultipartGridGenerator {
    private static final ResourceLocation SPAWN = id("spawn/spawn");
    private static final ResourceLocation SPAWN_OUTSIDE_NEAR = id("spawn/outside_near");
    private static final ResourceLocation SPAWN_OUTSIDE_FAR = id("spawn/outside_far");

    private static final ResourceLocation STAIRS = id("stairs");
    private static final ResourceLocation STRAIGHT = id("straight");
    private static final ResourceLocation TURN = id("turn");
    private static final ResourceLocation CURVE = id("curve");

    private static final ResourceLocation TERMINAL = id("terminal");
    private static final ResourceLocation PROMENADE = id("promenade");

    public PrisonGridGenerator(int xPart, int zPart) {
        super(xPart, zPart);
    }

    @Override
    protected void addRooms(RandomSource random) {
        Vec3iCursor cursor = new Vec3iCursor(new Vec3i(32, 15, 32), Direction.SOUTH);

        Vec3iCursor spawnCursor = cursor.split();
        this.addRoom(room(immutable(spawnCursor), SPAWN).offset(0, -2, 0).specialPoint(
            SpecialPointIds.ENTRANCE, new Vec3i(12, 1, 1), Rotation.NONE
        ));
        this.addRoom(room(immutable(spawnCursor.stepLeft()), SPAWN_OUTSIDE_NEAR).offset(0, -2, 0));
        this.addRoom(room(immutable(spawnCursor.stepLeft()), SPAWN_OUTSIDE_FAR).offset(0, -2, 0));

        this.addFloor(random, cursor, 3);
    }

    private Vec3iCursor addCorridor(
        RandomSource random,
        Vec3iCursor cursor,
        boolean allowTurns,
        @Nullable ResourceLocation end,
        int length
    ) {
        for (int i = 0; i < length; ++i) {
            if (random.nextFloat() < 0.2F && allowTurns && i < length - 1 && i > 0) {
                boolean left = random.nextBoolean();
                if (left) {
                    this.addRoom(room(immutable(cursor.forward()), CURVE)
                        .rotation(cursor.getRotation().getRotated(Rotation.COUNTERCLOCKWISE_90)));
                    for (int j = 0; j < random.nextInt(3); ++j) {
                        this.addRoom(room(immutable(cursor.stepLeft()), STRAIGHT)
                            .rotation(cursor.getRotation().getRotated(Rotation.CLOCKWISE_90)));
                    }
                    this.addRoom(room(immutable(cursor.stepLeft()), CURVE)
                        .rotation(cursor.getRotation().getRotated(Rotation.CLOCKWISE_90)));
                } else {
                    this.addRoom(room(immutable(cursor.forward()), CURVE)
                        .rotation(cursor.getRotation().getRotated(Rotation.CLOCKWISE_180)));
                    for (int j = 0; j < random.nextInt(3); ++j) {
                        this.addRoom(room(immutable(cursor.stepRight()), STRAIGHT)
                            .rotation(cursor.getRotation().getRotated(Rotation.COUNTERCLOCKWISE_90)));
                    }
                    this.addRoom(room(immutable(cursor.stepRight()), CURVE)
                        .rotation(cursor.getRotation().getRotated(Rotation.NONE)));
                }
                continue;
            }

            boolean turn = allowTurns && random.nextBoolean();
            Rotation rotation = random.nextBoolean() ? Rotation.NONE : Rotation.CLOCKWISE_180;
            this.addRoom(room(immutable(cursor.forward()), turn ? TURN : STRAIGHT)
                .rotation(rotation.getRotated(cursor.getRotation()))
            );

            if (turn) {
                this.addCorridor(
                    random,
                    cursor.split().turn(rotation).turnRight(),
                    false,
                    TERMINAL,
                    randomInclusive(random, 2, 4)
                );
            }
        }
        if (end != null) {
            var endRoom = room(immutable(cursor.forward()), end)
                .rotation(cursor.getRotation().getRotated(Rotation.CLOCKWISE_180));
            if (end.equals(PROMENADE)) {
                endRoom.specialPoint(SpecialPointIds.EXIT, new Vec3i(9, 0, 14), Rotation.CLOCKWISE_180);
            }
            this.addRoom(endRoom);
        }

        return cursor.split();
    }

    private void addFloor(RandomSource random, Vec3iCursor cursor, int index) {
        Vec3iCursor endCursor = this.addCorridor(random, cursor, true, null, randomInclusive(random, 3, 5));
        endCursor.forward();
        if (index > 0) {
            Vec3iCursor newCursor = endCursor.split();
            this.addRoom(room(immutable(newCursor.down()), STAIRS));
            this.addCorridor(random, newCursor.split(), true, TERMINAL, randomInclusive(random, 0, 2));
            this.addFloor(random, newCursor.turn(Rotation.CLOCKWISE_180), index - 1);
            this.addCorridor(random, endCursor, true, TERMINAL, randomInclusive(random, 0, 2));
        } else {
            this.addCorridor(random, endCursor.back(), true, PROMENADE, randomInclusive(random, 0, 2));
        }
    }

    /**
     * Snapshot mutable cursor coords so later cursor moves cannot rewrite placed RoomData.
     */
    private static Vec3i immutable(Vec3i pos) {
        return new Vec3i(pos.getX(), pos.getY(), pos.getZ());
    }

    /** Inclusive range matching Fabric {@code Random.nextBetween}. */
    private static int randomInclusive(RandomSource random, int min, int max) {
        return min >= max ? min : random.nextInt(min, max + 1);
    }

    private static ResourceLocation id(String path) {
        return MineCells.id("better_prison/" + path);
    }
}
