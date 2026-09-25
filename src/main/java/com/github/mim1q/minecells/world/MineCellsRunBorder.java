package com.github.mim1q.minecells.world;

import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Per-run square border of Mine Cells dimensions (Fabric's per-entity {@code WorldBorder}, size 1023,
 * centered on the nearest 1024 grid point). Only blocks movement that crosses it from the inside.
 */
public final class MineCellsRunBorder {
    public static final double HALF_SIZE = 1023.0D / 2.0D;

    private MineCellsRunBorder() {
    }

    public static double centerX(double x, double z) {
        return MathUtils.getClosestMultiplePosition(BlockPos.containing(x, 0, z), 1024).getX() + 0.5D;
    }

    public static double centerZ(double x, double z) {
        return MathUtils.getClosestMultiplePosition(BlockPos.containing(x, 0, z), 1024).getZ() + 0.5D;
    }

    public static double distanceInside(double centerX, double centerZ, double x, double z) {
        double dx = HALF_SIZE - Math.abs(x - centerX);
        double dz = HALF_SIZE - Math.abs(z - centerZ);
        return Math.min(dx, dz);
    }

    public static double distanceInside(Entity entity) {
        Vec3i center = MathUtils.getClosestMultiplePosition(entity.blockPosition(), 1024);
        return distanceInside(center.getX() + 0.5D, center.getZ() + 0.5D, entity.getX(), entity.getZ());
    }

    /**
     * Pushes the entity back if it crossed the border of the cell it was in on the previous tick.
     * @return true if the position was corrected
     */
    public static boolean clamp(Entity entity) {
        if (!MineCellsDimension.isMineCellsDimension(entity.level())) {
            return false;
        }
        double cx = centerX(entity.xo, entity.zo);
        double cz = centerZ(entity.xo, entity.zo);
        double minX = cx - HALF_SIZE;
        double maxX = cx + HALF_SIZE;
        double minZ = cz - HALF_SIZE;
        double maxZ = cz + HALF_SIZE;
        AABB box = entity.getBoundingBox();
        double halfWidth = box.getXsize() / 2.0D;
        double halfDepth = box.getZsize() / 2.0D;
        boolean wasInside = entity.xo - halfWidth >= minX && entity.xo + halfWidth <= maxX
            && entity.zo - halfDepth >= minZ && entity.zo + halfDepth <= maxZ;
        if (!wasInside) {
            return false;
        }
        double x = entity.getX();
        double z = entity.getZ();
        double clampedX = Math.max(minX + halfWidth, Math.min(maxX - halfWidth, x));
        double clampedZ = Math.max(minZ + halfDepth, Math.min(maxZ - halfDepth, z));
        if (clampedX == x && clampedZ == z) {
            return false;
        }
        Vec3 motion = entity.getDeltaMovement();
        entity.setDeltaMovement(clampedX != x ? 0.0D : motion.x, motion.y, clampedZ != z ? 0.0D : motion.z);
        entity.setPos(clampedX, entity.getY(), clampedZ);
        return true;
    }
}
