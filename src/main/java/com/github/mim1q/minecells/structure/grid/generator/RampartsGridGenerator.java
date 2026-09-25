package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomData;
import com.github.mim1q.minecells.structure.grid.SpecialPointIds;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;

public class RampartsGridGenerator extends MultipartGridGenerator {
    private static final ResourceLocation BASE = MineCells.id("ramparts/base");
    private static final ResourceLocation BOTTOM = MineCells.id("ramparts/bottom");
    private static final ResourceLocation TOP = MineCells.id("ramparts/top");
    private static final ResourceLocation TOP_ENTRY = MineCells.id("ramparts/top_entry");
    private static final ResourceLocation ROOM_ENTRY = MineCells.id("ramparts/room_entry");
    private static final ResourceLocation ROOM = MineCells.id("ramparts/room");
    private static final ResourceLocation ROOM_END = MineCells.id("ramparts/room_end");
    private static final ResourceLocation ROOM_EXIT = MineCells.id("ramparts/room_exit");
    private static final ResourceLocation END = MineCells.id("ramparts/end");
    private static final ResourceLocation BOTTOM_END = MineCells.id("ramparts/bottom_end");
    private static final ResourceLocation SPAWN = MineCells.id("ramparts/spawn");
    private static final ResourceLocation SPAWN_END = MineCells.id("ramparts/spawn_end");

    private static final ResourceLocation TOWER_BOTTOM = MineCells.id("ramparts/tower/bottom");
    private static final ResourceLocation TOWER_BASE = MineCells.id("ramparts/tower/base");
    private static final ResourceLocation TOWER_ROOM = MineCells.id("ramparts/tower/room");
    private static final ResourceLocation TOWER_ENTRY_ROOM = MineCells.id("ramparts/tower/entry_room");
    private static final ResourceLocation TOWER_TOP = MineCells.id("ramparts/tower/top");
    private static final ResourceLocation END_TOWER_ENTRANCE = MineCells.id("ramparts/end_tower/entrance");
    private static final ResourceLocation END_TOWER_ELEVATOR_SHAFT = MineCells.id("ramparts/end_tower/elevator_shaft");
    private static final ResourceLocation END_TOWER_EXIT = MineCells.id("ramparts/end_tower/exit");

    private static final ResourceLocation PLATFORM = MineCells.id("ramparts/platform");
    private static final ResourceLocation PLATFORM_UP = MineCells.id("ramparts/platform_up");

    private static final int LOWER_BASE_HEIGHT = 6;
    private static final int BASE_HEIGHT = 14;

    public RampartsGridGenerator(int xPart, int zPart) {
        super(xPart, zPart);
    }

    @Override
    protected void addRooms(RandomSource random) {
        boolean[] turns = new boolean[]{random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean()};
        if ((turns[0] && turns[1] && turns[2] && turns[3]) || (!turns[0] && !turns[1] && !turns[2] && !turns[3])) {
            turns[random.nextInt(4)] ^= true;
        }
        this.addWall(40, 24, BASE_HEIGHT, 3, true);

        int x = 40;
        int z = 26;
        int height = BASE_HEIGHT;
        for (int i = 0; i < 4; ++i) {
            boolean up = random.nextBoolean();

            x += turns[i] ? 2 : -2;
            Rotation rot = turns[i] ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90;
            int length = 3 + random.nextInt(4);
            this.addRoom(new Vec3i(x - rot.rotate(Direction.SOUTH).getStepX(), height - 1, z + 1), rot, up ? PLATFORM_UP : PLATFORM);

            if (up) {
                height += 1;
            }
            this.addWall(x, z, height, length, false);

            boolean[] sideTowers = new boolean[]{random.nextFloat() < 0.75F, random.nextFloat() < 0.75F};
            if (!sideTowers[0] && !sideTowers[1]) {
                sideTowers[random.nextInt(2)] = true;
            }

            if (sideTowers[0]) {
                boolean prevRight = turns[i];
                int startZ = z + (prevRight ? 2 : 1);
                this.addTower(x - 2, height, startZ + random.nextInt(length - 2), random, Rotation.CLOCKWISE_90);
            }

            if (sideTowers[1]) {
                boolean prevLeft = !turns[i];
                int startZ = z + (prevLeft ? 2 : 1);
                this.addTower(x + 2, height, startZ + random.nextInt(length - 2), random, Rotation.COUNTERCLOCKWISE_90);
            }
            z += length - 1;
        }

        this.addRoom(new Vec3i(x, height - 1, z + 2), Rotation.NONE, PLATFORM_UP);
        this.addEndTower(x, height + 1, z + 3);
    }

    private void addWall(int x, int z, int height, int length, boolean spawn) {
        boolean rooms = length >= 4 && !spawn;
        this.addColumn(x, z, 0, height, 0, BOTTOM_END, null, spawn ? SPAWN_END : END, Rotation.NONE, Rotation.NONE);
        if (spawn) {
            this.addColumn(x, z, 1, height, 0, BOTTOM, null, SPAWN, Rotation.NONE, Rotation.NONE);
        }
        for (int i = spawn ? 2 : 1; i <= length; ++i) {
            this.addColumn(
                x, z, i, height,
                rooms && i > 1 && i < length ? 1 : 0,
                BOTTOM,
                rooms && i == 1 ? TOP_ENTRY : TOP,
                rooms && i > 1 && i < length ? null : BOTTOM,
                Rotation.NONE,
                Rotation.NONE
            );
        }
        if (rooms) {
            this.addDungeonRooms(x, height - 1, z + 1, length, 2, Rotation.NONE);
        }
        this.addColumn(x, z, length + 1, height, 0, BOTTOM_END, null, END, Rotation.CLOCKWISE_180, Rotation.NONE);
    }

    private void addDungeonRooms(int startX, int startY, int startZ, int length, int height, Rotation offsetRotation) {
        for (int y = startY; y > startY - height; y--) {
            for (int offset = 0; offset < length; offset++) {
                int x = startX + offset * offsetRotation.rotate(Direction.SOUTH).getStepX();
                int z = startZ + offset * offsetRotation.rotate(Direction.SOUTH).getStepZ();

                boolean reversed = (y % 2 == 1) ^ (startY % 2 == 1);
                ResourceLocation roomType = ROOM;
                if ((reversed && offset == length - 1) || (!reversed && offset == 0)) {
                    roomType = ROOM_ENTRY;
                }
                if ((reversed && offset == 0) || (!reversed && offset == length - 1)) {
                    roomType = y == startY - height + 1 ? ROOM_EXIT : ROOM_END;
                }
                this.addRoom(new Vec3i(x, y, z), reversed ? Rotation.CLOCKWISE_180 : Rotation.NONE, roomType);
            }
        }
    }

    private void addTower(int x, int y, int z, RandomSource random, Rotation rotation) {
        if (this.isPositionUsed(new Vec3i(x, 0, z))) {
            return;
        }

        boolean floating = random.nextBoolean();
        int height = 2 + random.nextInt(3);

        this.addRoom(new Vec3i(x - rotation.rotate(Direction.SOUTH).getStepX(), y - 1, z), rotation, PLATFORM);

        if (floating) {
            int underY = random.nextInt(1) + 2;
            this.addRoom(new Vec3i(x, y - underY, z), rotation, TOWER_BOTTOM);
            for (int i = y - underY + 1; i < y; i++) {
                this.addRoom(new Vec3i(x, i, z), rotation, TOWER_BASE);
            }
        } else {
            for (int i = 0; i < y; i++) {
                this.addRoom(new Vec3i(x, i, z), rotation, TOWER_BASE);
            }
        }
        for (int i = y; i < y + height; i++) {
            this.addRoom(new Vec3i(x, i, z), rotation, i == y ? TOWER_ENTRY_ROOM : TOWER_ROOM);
        }
        this.addRoom(new Vec3i(x, y + height, z), rotation, TOWER_TOP);
    }

    private void addEndTower(int x, int y, int z) {
        int height = 13;

        for (int i = 0; i < height; i++) {
            this.addRoom(new Vec3i(x, i, z), Rotation.NONE, TOWER_BASE);
        }
        this.addRoom(RoomData.create(new Vec3i(x, height, z), END_TOWER_EXIT)
            .specialPoint(SpecialPointIds.EXIT, new Vec3i(4, 0, 4), Rotation.NONE));
        for (int i = height + 1; i < y; i++) {
            this.addRoom(new Vec3i(x, i, z), Rotation.NONE, END_TOWER_ELEVATOR_SHAFT);
        }
        this.addRoom(new Vec3i(x, y, z), Rotation.NONE, END_TOWER_ENTRANCE);
        this.addRoom(new Vec3i(x, y + 1, z), Rotation.NONE, TOWER_TOP);
    }

    private void addColumn(
        int startX,
        int startZ,
        int offset,
        int height,
        int freeY,
        ResourceLocation bottom,
        ResourceLocation top,
        ResourceLocation second,
        Rotation rotation,
        Rotation offsetRotation
    ) {
        int x = startX + offset * offsetRotation.rotate(Direction.SOUTH).getStepX();
        int z = startZ + offset * offsetRotation.rotate(Direction.SOUTH).getStepZ();
        for (int i = 0; i < LOWER_BASE_HEIGHT; i++) {
            this.addRoom(new Vec3i(x, i, z), rotation, BASE);
        }
        for (int i = LOWER_BASE_HEIGHT; i <= height - 2 - freeY; i++) {
            this.addRoom(new Vec3i(x, i, z), rotation, bottom);
        }
        if (second != null) {
            RoomData room = RoomData.create(new Vec3i(x, height - 1, z), second).rotation(rotation);
            if (second.equals(SPAWN)) {
                room.specialPoint(SpecialPointIds.ENTRANCE, new Vec3i(10, 3, 8), Rotation.CLOCKWISE_180);
            }
            this.addRoom(room);
        }
        if (top != null) {
            this.addRoom(room(new Vec3i(x, height, z), top)
                .rotation(rotation)
                .specialPoint(SpecialPointIds.CHECKPOINT, new Vec3i(8, 0, 4), Rotation.NONE));
        }
    }
}
