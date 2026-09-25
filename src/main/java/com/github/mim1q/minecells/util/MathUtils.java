package com.github.mim1q.minecells.util;

import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class MathUtils {
    private MathUtils() {
    }

    public static Vec3 vectorRotateY(Vec3 vector, float theta) {
        double z = vector.z * Mth.sin(theta) + vector.x * Mth.cos(theta);
        double x = vector.z * Mth.cos(theta) - vector.x * Mth.sin(theta);
        return new Vec3(x, vector.y, z);
    }

    public static Vector3f vectorRotateY(Vector3f vector, float theta) {
        float z = vector.z() * Mth.sin(theta) + vector.x() * Mth.cos(theta);
        float x = vector.z() * Mth.cos(theta) - vector.x() * Mth.sin(theta);
        return new Vector3f(x, vector.y(), z);
    }

    public static float lerp(float a, float b, float delta) {
        return Mth.lerp(delta, a, b);
    }

    public static float easeInOutQuad(float a, float b, float delta) {
        float t = delta < 0.5F
            ? 2.0F * delta * delta
            : 1.0F - (-2.0F * delta + 2.0F) * (-2.0F * delta + 2.0F) * 0.5F;
        return Mth.lerp(t, a, b);
    }

    public static float easeOutBounce(float a, float b, float delta) {
        float n1 = 7.5625F;
        float d1 = 2.75F;

        if (delta < 1.0F / d1) {
            return lerp(a, b, n1 * delta * delta);
        } else if (delta < 2.0F / d1) {
            return lerp(a, b, n1 * (delta -= 1.5F / d1) * delta + 0.75F);
        } else if (delta < 2.5F / d1) {
            return lerp(a, b, n1 * (delta -= 2.25F / d1) * delta + 0.9375F);
        }
        return lerp(a, b, n1 * (delta -= 2.625F / d1) * delta + 0.984375F);
    }

    public static float easeOutBack(float a, float b, float delta) {
        float c1 = 1.70158F;
        float c3 = c1 + 1.0F;
        float t = 1.0F + c3 * (float) Math.pow(delta - 1.0F, 3.0D) + c1 * (float) Math.pow(delta - 1.0F, 2.0D);
        return Mth.lerp(t, a, b);
    }

    public static float getClosestMultiple(float value, float multiple) {
        return Math.round(value / multiple) * multiple;
    }

    public static int getClosestMultiple(int value, int multiple) {
        return Math.round(value / (float) multiple) * multiple;
    }

    public static Vec3i getClosestMultiplePosition(Vec3i pos, int multiple) {
        int x = Math.round(pos.getX() / (float) multiple) * multiple;
        int z = Math.round(pos.getZ() / (float) multiple) * multiple;
        return new Vec3i(x, 0, z);
    }

    public static Vec3i getSpiralPosition(int index) {
        int i = index + 1;
        int k = (int) Math.ceil((Math.sqrt(i) - 1) / 2);
        int t = 2 * k + 1;
        int m = t * t;
        t -= 1;
        if (i >= m - t) {
            return new Vec3i(k - (m - i), 0, -k);
        }
        m -= t;
        if (i >= m - t) {
            return new Vec3i(-k, 0, -k + (m - i));
        }
        m -= t;
        if (i >= m - t) {
            return new Vec3i(-k + (m - i), 0, k);
        }
        return new Vec3i(k, 0, k - (m - i - t));
    }

    public static Vec3i getRotatedOffsetWithinChunk(Vec3i offset, Rotation rotation) {
        int x = offset.getX();
        int y = offset.getY();
        int z = offset.getZ();
        return switch (rotation) {
            case NONE -> new Vec3i(x, y, z);
            case CLOCKWISE_90 -> new Vec3i(15 - z, y, x);
            case CLOCKWISE_180 -> new Vec3i(15 - x, y, 15 - z);
            case COUNTERCLOCKWISE_90 -> new Vec3i(z, y, 15 - x);
        };
    }

    public static float easeOutQuad(float a, float b, float delta) {
        return lerp(a, b, 1.0F - (1.0F - delta) * (1.0F - delta));
    }

    public static float easeOutCubic(float a, float b, float delta) {
        float t = 1.0F - delta;
        return lerp(a, b, 1.0F - t * t * t);
    }

    public static float easeInQuad(float a, float b, float delta) {
        return lerp(a, b, delta * delta);
    }

    public static float easeInOutCubic(float a, float b, float delta) {
        float t = delta < 0.5F
            ? 4.0F * delta * delta * delta
            : 1.0F - (float) Math.pow(-2.0F * delta + 2.0F, 3.0D) / 2.0F;
        return lerp(a, b, t);
    }

    public static Vec3 interpolateVec(Vec3 start, Vec3 end, float delta, AnimationEasing easing) {
        return new Vec3(
            easing.ease((float) start.x, (float) end.x, delta),
            easing.ease((float) start.y, (float) end.y, delta),
            easing.ease((float) start.z, (float) end.z, delta)
        );
    }

    public static float radians(float degrees) {
        return degrees * Mth.DEG_TO_RAD;
    }

    @FunctionalInterface
    public interface AnimationEasing {
        float ease(float start, float end, float delta);
    }

    public static final class PosRotScale {
        private final Vector3f pos;
        private final Vector3f rot;
        private final Vector3f scale;

        private PosRotScale(Vector3f pos, Vector3f rot, Vector3f scale) {
            this.pos = pos;
            this.rot = rot;
            this.scale = scale;
        }

        public static PosRotScale ofRadians(Vector3f pos, Vector3f rot, Vector3f scale) {
            return new PosRotScale(pos, rot, scale);
        }

        public static PosRotScale ofDegrees(float px, float py, float pz, float rx, float ry, float rz, float sx, float sy, float sz) {
            return ofRadians(
                new Vector3f(px, py, pz),
                new Vector3f(radians(rx), radians(ry), radians(rz)),
                new Vector3f(sx, sy, sz)
            );
        }

        public void apply(com.mojang.blaze3d.vertex.PoseStack poseStack) {
            poseStack.translate(pos.x(), pos.y(), pos.z());
            poseStack.mulPose(new org.joml.Quaternionf().rotationZYX(rot.z(), rot.y(), rot.x()));
            poseStack.scale(scale.x(), scale.y(), scale.z());
        }

        public Vector3f getPos() {
            return pos;
        }
    }
}
