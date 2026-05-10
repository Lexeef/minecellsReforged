package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;

public class BetterPromenadeGridGenerator extends MultipartGridGenerator {
    private static final ResourceLocation PATH_STRAIGHT = pool("path/straight");
    private static final ResourceLocation PATH_TURN = pool("path/turn");
    private static final ResourceLocation PATH_CROSSROADS = pool("path/crossroads");
    private static final ResourceLocation PATH_BUILDING = pool("path/building");
    private static final ResourceLocation PATH_HALF = pool("path/half");
    private static final ResourceLocation BUILDING_OVERGROUND = pool("overground");
    private static final ResourceLocation BUILDING_OVERGROUND_END = pool("overground_end");
    private static final ResourceLocation BUILDING_OVERGROUND_BASE = pool("overground_base");
    private static final ResourceLocation BUILDING_OVERGROUND_ELEVATOR = pool("overground_elevator");
    private static final ResourceLocation BUILDING_UNDERGROUND = pool("underground");
    private static final ResourceLocation BUILDING_UNDERGROUND_END = pool("underground_end");
    private static final ResourceLocation RAMPARTS_TOWER = pool("ramparts_tower");
    private static final ResourceLocation VINE_RUNE = pool("special/vine_rune");
    private static final ResourceLocation SPAWN = pool("spawn");
    private static final ResourceLocation CROSSROADS_POST = pool("path/crossroads_post");
    private static final ResourceLocation VINE_RUNE_POST = pool("path/post/vine_rune");
    private static final ResourceLocation BEFORE_CROSSROADS_POST = pool("path/post/before_crossroads");
    private static final ResourceLocation AFTER_CROSSROADS_POST = pool("path/post/after_crossroads");

    public BetterPromenadeGridGenerator(int xPart, int zPart) {
        super(xPart, zPart);
    }

    @Override
    protected void addRooms(RandomSource random) {
        this.addRoom(room(32, 0, 32, PATH_HALF).terrainFit().rotation(Rotation.CLOCKWISE_180));
        this.addRoom(room(32, 0, 32, SPAWN).terrainFit().terrainFitOffset(8, 0, 15).offset(0, -7, 0));
        Pair<Vec3i, Vec3i> mainRoad = this.addPath(new Vec3i(32, 0, 33), Rotation.NONE, 23, random, 3, BEFORE_CROSSROADS_POST, AFTER_CROSSROADS_POST);
        Vec3i mainRoadEnd = mainRoad.getFirst();
        Vec3i mainRoadSkipped = mainRoad.getSecond();
        this.addRoom(room(mainRoadSkipped, PATH_CROSSROADS));
        this.addRoom(room(mainRoadSkipped, CROSSROADS_POST).terrainFit().terrainFitOffset(5, 0, 6));
        Pair<Vec3i, Vec3i> sideRoad = this.addPath(mainRoadSkipped.offset(1, 0, 0), Rotation.COUNTERCLOCKWISE_90, 9, random, -1, VINE_RUNE_POST, VINE_RUNE_POST);
        Vec3i sideRoadEnd = sideRoad.getFirst();
        this.addRoom(room(mainRoadEnd.offset(0, 0, 1), RAMPARTS_TOWER).terrainFit(mainRoadEnd).terrainSampleOffset(8, 0, 15));
        this.addRoom(room(sideRoadEnd, VINE_RUNE).terrainFit().terrainSampleOffset(14, 0, 8).offset(0, -21, 0).rotation(Rotation.CLOCKWISE_90));
        this.tryPlaceBuildingsBetween(new Vec3i(1, 0, 32), new Vec3i(63, 0, 58), random, 32, Rotation.CLOCKWISE_180);
        this.tryPlaceBuildingsBetween(new Vec3i(1, 0, 1), new Vec3i(63, 0, 30), random, 48, Rotation.NONE);
    }

    private void addBuilding(Vec3i start, int length, Rotation rotation, RandomSource random) {
        if (length < 1) {
            return;
        }
        boolean underground = length > 3 && random.nextFloat() < 0.66F;
        Vec3i direction = rotation.rotate(Direction.SOUTH).getNormal();
        Vec3i fitPos = start.offset(direction.multiply(length / 2));
        this.addRoom(room(start.offset(direction.multiply(-1)), BUILDING_OVERGROUND_END).rotation(rotation).terrainFit(fitPos));
        int skipped = underground ? random.nextInt(length - 1) : -1;

        for (int i = 0; i < length; ++i) {
            Vec3i current = start.offset(direction.multiply(i));
            if (i == skipped) {
                this.addRoom(room(current.below(2), BUILDING_OVERGROUND_ELEVATOR).rotation(rotation).terrainFit(fitPos));
                continue;
            }
            Rotation randomRotation = random.nextBoolean() ? rotation : Rotation.CLOCKWISE_180.getRotated(rotation);
            this.addRoom(room(current, BUILDING_OVERGROUND).rotation(randomRotation).terrainFit(fitPos));
            this.addRoom(room(current.below(), BUILDING_OVERGROUND_BASE).rotation(rotation).terrainFit(fitPos));
            if (underground) {
                if (i == length - 1 && random.nextFloat() < 0.67F) {
                    this.addRoom(room(current.below(3), BUILDING_UNDERGROUND_END).rotation(rotation).terrainFit(fitPos));
                } else {
                    this.addRoom(room(current.below(2), BUILDING_UNDERGROUND).rotation(rotation).terrainFit(fitPos));
                }
            } else {
                this.addRoom(room(current.below(2), BUILDING_OVERGROUND_BASE).rotation(rotation).terrainFit(fitPos));
            }
        }

        this.addRoom(room(start.offset(direction.multiply(length)), BUILDING_OVERGROUND_END).rotation(Rotation.CLOCKWISE_180.getRotated(rotation)).terrainFit(fitPos));
    }

    private void tryPlaceBuildingsBetween(Vec3i start, Vec3i end, RandomSource random, int count, Rotation excludedRotation) {
        for (int i = 0; i < count; ++i) {
            for (int j = 0; j < 4; ++j) {
                int x = random.nextInt(end.getX() - start.getX() + 1) + start.getX();
                int z = random.nextInt(end.getZ() - start.getZ() + 1) + start.getZ();
                Vec3i pos = new Vec3i(x, start.getY(), z);
                if (this.tryPlaceBuilding(pos, random, excludedRotation)) {
                    break;
                }
            }
        }
    }

    private boolean tryPlaceBuilding(Vec3i start, RandomSource random, Rotation excludedRotation) {
        int maxLength = 3 + random.nextInt(6);
        for (Rotation rotation : Rotation.getShuffled(random)) {
            if (rotation == excludedRotation) {
                continue;
            }

            Vec3i pos = start;
            boolean valid = true;
            Vec3i direction = rotation.rotate(Direction.SOUTH).getNormal();
            for (int i = -1; i < maxLength + 1; ++i) {
                pos = pos.offset(direction);
                if (this.isPositionUsed(pos) || pos.getX() <= 0 || pos.getZ() <= 0 || pos.getX() >= 63 || pos.getZ() >= 56) {
                    valid = false;
                    break;
                }
            }
            if (valid) {
                this.addBuilding(start.offset(direction), maxLength - 2, rotation, random);
                return true;
            }
        }
        return false;
    }

    private Pair<Vec3i, Vec3i> addPath(
        Vec3i start,
        Rotation rotation,
        int length,
        RandomSource random,
        int skippedTurn,
        ResourceLocation beforeCrossroadsPost,
        ResourceLocation afterCrossroadsPost
    ) {
        Vec3i direction = rotation.rotate(Direction.SOUTH).getNormal();
        Vec3i sideDirection = rotation.rotate(Direction.EAST).getNormal();
        Vec3i skippedVec = null;
        Vec3i pos = start;
        int turns = 0;
        int nextLength = 3 + random.nextInt(2);
        boolean nextLeft = random.nextBoolean();
        this.addBuilding(pos.offset(sideDirection).offset(direction.multiply(2)), nextLength - 2, rotation, random);
        this.addBuilding(pos.offset(sideDirection.multiply(-1)).offset(direction.multiply(2)), nextLength - 2, rotation, random);

        for (int i = 0; i < length; ++i) {
            if (nextLength <= 0) {
                if (turns == skippedTurn) {
                    skippedVec = pos;
                } else if (nextLeft) {
                    this.addRoom(room(pos, PATH_TURN).rotation(rotation));
                    pos = pos.offset(sideDirection);
                    this.addRoom(room(pos, PATH_TURN).rotation(rotation.getRotated(Rotation.CLOCKWISE_180)));
                } else {
                    this.addRoom(room(pos, PATH_TURN).rotation(rotation.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                    pos = pos.offset(sideDirection.multiply(-1));
                    this.addRoom(room(pos, PATH_TURN).rotation(rotation.getRotated(Rotation.CLOCKWISE_90)));
                }

                nextLength = Math.min(2 + random.nextInt(3), length - i);
                int leftMultiplier = Math.max(nextLeft ? 0 : 1, skippedTurn == turns ? 1 : 0);
                int rightMultiplier = Math.max(nextLeft ? 1 : 0, skippedTurn == turns ? 1 : 0);
                this.addBuilding(pos.offset(sideDirection).offset(direction.multiply(leftMultiplier)), nextLength, rotation, random);
                this.addBuilding(pos.offset(sideDirection.multiply(-1)).offset(direction.multiply(rightMultiplier)), nextLength, rotation, random);
                turns++;
                nextLeft = random.nextBoolean();
            } else {
                this.addRoom(room(pos, PATH_STRAIGHT).rotation(rotation));
                if (random.nextFloat() < 0.6F) {
                    if (random.nextFloat() < 0.33F) {
                        Vec3i offset = direction.multiply(5).offset(sideDirection.multiply(6));
                        this.addRoom(room(pos, turns < (skippedTurn + 1) ? beforeCrossroadsPost : afterCrossroadsPost)
                            .terrainFitOffset(offset.getX(), offset.getY(), offset.getZ())
                            .rotation(rotation)
                        );
                    } else {
                        this.addRoom(room(pos, PATH_BUILDING).rotation(rotation).terrainFit());
                    }
                }
                nextLength--;
            }

            pos = pos.offset(direction);
        }

        return Pair.of(pos.offset(direction.multiply(-1)), skippedVec);
    }

    private static ResourceLocation pool(String path) {
        return MineCells.id("promenade/" + path);
    }
}
