package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomGridGenerator;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;

public class PromenadeWallGenerator extends RoomGridGenerator {
    private static final ResourceLocation TOP = MineCells.id("promenade/border_wall/top");
    private static final ResourceLocation MIDDLE = MineCells.id("promenade/border_wall/middle");
    private static final ResourceLocation BOTTOM = MineCells.id("promenade/border_wall/bottom");
    private static final ResourceLocation UNDERGROUND = MineCells.id("promenade/border_wall/underground");

    private final boolean zAxis;

    public PromenadeWallGenerator(boolean zAxis) {
        this.zAxis = zAxis;
    }

    @Override
    protected void addRooms(RandomSource random) {
        Rotation rotation = this.zAxis ? Rotation.CLOCKWISE_90 : Rotation.NONE;
        Vec3i offset = this.zAxis ? new Vec3i(-7, -4, 0) : new Vec3i(0, -4, -7);
        for (int i = -7; i <= 8; i++) {
            int x = this.zAxis ? 0 : i;
            int z = this.zAxis ? i : 0;
            Vec3i pos = new Vec3i(x, this.zAxis ? 3 : 8, z);
            this.addColumn(pos, rotation, offset);
        }
    }

    private void addColumn(Vec3i pos, Rotation rotation, Vec3i blockOffset) {
        for (int i = 0; i < 3; ++i) {
            this.addRoom(pos.below(i), rotation, UNDERGROUND, blockOffset);
        }
        this.addRoom(pos, rotation, BOTTOM, blockOffset);
        for (int i = 1; i < 7; ++i) {
            this.addRoom(pos.above(i), rotation, MIDDLE, blockOffset);
        }
        this.addRoom(pos.above(7), rotation, TOP, blockOffset);
    }
}
