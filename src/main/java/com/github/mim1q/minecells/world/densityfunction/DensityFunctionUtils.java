package com.github.mim1q.minecells.world.densityfunction;

final class DensityFunctionUtils {
    private DensityFunctionUtils() {
    }

    static int getClosestMultiple(int value, int multiple) {
        return Math.round(value / (float) multiple) * multiple;
    }

    static double easeInQuad(double a, double b, double delta) {
        return a + (b - a) * delta * delta;
    }
}
