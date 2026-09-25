package com.github.mim1q.minecells.util;

import com.github.mim1q.minecells.block.WallLeavesBlock;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;

/**
 * Custom block model offsets, set on {@link BlockBehaviour.Properties} before the block is constructed
 * (block states copy the offset function in their constructor).
 */
public final class BlockOffsets {
    private static final Field OFFSET_FUNCTION = findOffsetFunctionField();

    private BlockOffsets() {
    }

    public static BlockBehaviour.Properties withOffset(BlockBehaviour.Properties properties, BlockBehaviour.OffsetFunction function) {
        try {
            OFFSET_FUNCTION.set(properties, Optional.of(function));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not set block offset function", e);
        }
        return properties;
    }

    public static BlockBehaviour.Properties preventZFighting(BlockBehaviour.Properties properties) {
        return withOffset(properties, (state, level, pos) -> {
            int x = pos.getX() % 3;
            int y = pos.getY() % 3;
            int z = pos.getZ() % 3;
            return new Vec3(
                (z * 0.001) + (y * 0.0015),
                (x * 0.001) + (z * 0.0015),
                (y * 0.001) + (x * 0.0015)
            );
        });
    }

    public static BlockBehaviour.Properties wallLeafOffset(BlockBehaviour.Properties properties) {
        return withOffset(properties, (state, level, pos) -> {
            long l = Mth.getSeed(pos.getX(), pos.getY(), pos.getZ());
            float offset = 0.3F;
            float localZScale = 0.2F;
            double x = Mth.clamp((((l & 15L) / 15.0F) - 0.5) * 0.5, -offset, offset);
            double y = Mth.clamp((((l >> 8 & 15L) / 15.0F) - 0.5) * 0.5, -offset, offset);
            double z = Mth.clamp((((l >> 4 & 15L) / 15.0F) - 0.5) * 0.5, -offset, offset);

            Direction direction = state.getValue(WallLeavesBlock.DIRECTION);
            if (direction.getStepX() != 0) x = localZScale * Math.abs(x) * -direction.getStepX();
            if (direction.getStepY() != 0) y = localZScale * Math.abs(y) * -direction.getStepY();
            if (direction.getStepZ() != 0) z = localZScale * Math.abs(z) * -direction.getStepZ();

            return new Vec3(x, y, z);
        });
    }

    private static Field findOffsetFunctionField() {
        for (Field field : BlockBehaviour.Properties.class.getDeclaredFields()) {
            if (field.getType() == Optional.class
                && field.getGenericType() instanceof ParameterizedType type
                && type.getActualTypeArguments()[0] == BlockBehaviour.OffsetFunction.class) {
                field.setAccessible(true);
                return field;
            }
        }
        throw new IllegalStateException("BlockBehaviour.Properties has no offset function field");
    }
}
