package com.github.mim1q.minecells.dimension;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Set;

public enum MineCellsDimension {
    OVERWORLD(new ResourceLocation("minecraft", "overworld"), 0, 0, 0, 0.0D, 0.0F),
    PRISONERS_QUARTERS(MineCells.id("prison"), 2, 43, 3, 1024.0D, -90.0F),
    INSUFFERABLE_CRYPT(MineCells.id("insufferable_crypt"), 6, 41, 2, 1024.0D, 90.0F),
    PROMENADE_OF_THE_CONDEMNED(MineCells.id("promenade"), 6, -5, 6, 1024.0D, 0.0F),
    RAMPARTS(MineCells.id("ramparts"), -54, 212, -265, 384.0D, 180.0F),
    BLACK_BRIDGE(MineCells.id("black_bridge"), 32, 70, 11, 384.0D, 0.0F);

    private static final Set<MineCellsDimension> DIMENSIONS_WITH_SURFACE = Set.of(PROMENADE_OF_THE_CONDEMNED);

    private final ResourceLocation id;
    private final BlockPos spawnOffset;
    private final double borderSize;
    private final float yaw;

    MineCellsDimension(ResourceLocation id, int offsetX, int offsetY, int offsetZ, double borderSize, float yaw) {
        this.id = id;
        this.spawnOffset = new BlockPos(offsetX, offsetY, offsetZ);
        this.borderSize = borderSize;
        this.yaw = yaw;
    }

    public ResourceLocation id() {
        return id;
    }

    public BlockPos spawnOffset() {
        return spawnOffset;
    }

    public double borderSize() {
        return borderSize;
    }

    public float yaw() {
        return yaw;
    }

    public Vec3 getTeleportPosition(BlockPos pos, ServerLevel originLevel) {
        ServerLevel destination = getLevel(originLevel);
        if (destination == null) {
            return Vec3.atBottomCenterOf(pos);
        }
        BlockPos runCenter = new BlockPos(
            Math.round(pos.getX() / 1024.0F) * 1024,
            pos.getY(),
            Math.round(pos.getZ() / 1024.0F) * 1024
        );
        BlockPos tpPos = runCenter.offset(spawnOffset);
        if (DIMENSIONS_WITH_SURFACE.contains(this)) {
            int y = destination.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, tpPos.getX(), tpPos.getZ());
            return Vec3.atBottomCenterOf(new BlockPos(tpPos.getX(), y, tpPos.getZ()));
        }
        return Vec3.atCenterOf(tpPos);
    }

    @Nullable
    public ServerLevel getLevel(ServerLevel referenceLevel) {
        ResourceKey<Level> key = ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, id);
        return referenceLevel.getServer().getLevel(key);
    }

    @Nullable
    public static MineCellsDimension of(ResourceLocation id) {
        return Arrays.stream(values()).filter(value -> value.id.equals(id)).findFirst().orElse(null);
    }

    @Nullable
    public static MineCellsDimension of(Level level) {
        return of(level.dimension().location());
    }

    public static boolean isMineCellsDimension(Level level) {
        MineCellsDimension dimension = of(level);
        return dimension != null && dimension != OVERWORLD;
    }
}
