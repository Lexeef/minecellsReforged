package com.github.mim1q.minecells.world.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;

public record RingDensityFunction(
    int gridSize,
    int minRadius,
    int maxRadius,
    int blendRadius,
    double fromValue,
    double toValue,
    int offsetX,
    int offsetZ,
    boolean offsetGrid
) implements DensityFunction {
    public static final KeyDispatchDataCodec<RingDensityFunction> CODEC = KeyDispatchDataCodec.of(
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("grid_size", 1024).forGetter(RingDensityFunction::gridSize),
            Codec.INT.fieldOf("min_radius").forGetter(RingDensityFunction::minRadius),
            Codec.INT.optionalFieldOf("max_radius", 2048).forGetter(RingDensityFunction::maxRadius),
            Codec.INT.optionalFieldOf("blend_radius", 16).forGetter(RingDensityFunction::blendRadius),
            Codec.DOUBLE.optionalFieldOf("from_value", 0.0).forGetter(RingDensityFunction::fromValue),
            Codec.DOUBLE.optionalFieldOf("to_value", 1.0).forGetter(RingDensityFunction::toValue),
            Codec.INT.optionalFieldOf("offset_x", 0).forGetter(RingDensityFunction::offsetX),
            Codec.INT.optionalFieldOf("offset_z", 0).forGetter(RingDensityFunction::offsetZ),
            Codec.BOOL.optionalFieldOf("offset_grid", false).forGetter(RingDensityFunction::offsetGrid)
        ).apply(instance, RingDensityFunction::new))
    );

    @Override
    public double compute(FunctionContext pos) {
        int centerX = DensityFunctionUtils.getClosestMultiple(pos.blockX() - (this.offsetGrid ? this.offsetX : 0), this.gridSize);
        int centerZ = DensityFunctionUtils.getClosestMultiple(pos.blockZ() - (this.offsetGrid ? this.offsetZ : 0), this.gridSize);
        int distX = pos.blockX() - centerX - (this.offsetGrid ? 0 : this.offsetX);
        int distZ = pos.blockZ() - centerZ - (this.offsetGrid ? 0 : this.offsetZ);
        int distSquared = distX * distX + distZ * distZ;
        if (distSquared < this.minRadius * this.minRadius || distSquared > this.maxRadius * this.maxRadius) {
            return this.fromValue;
        }
        if (this.blendRadius == 0) {
            return this.toValue;
        }
        double dist = Math.sqrt(distSquared);
        double distToEdge = Math.min(dist - this.minRadius, this.maxRadius - dist);
        double delta = Math.min(distToEdge / (double) this.blendRadius, 1.0);
        return Mth.lerp(delta, this.fromValue, this.toValue);
    }

    @Override
    public void fillArray(double[] densities, ContextProvider applier) {
        applier.fillAllDirectly(densities, this);
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        return visitor.apply(this);
    }

    @Override
    public double minValue() {
        return Math.min(this.fromValue, this.toValue);
    }

    @Override
    public double maxValue() {
        return Math.max(this.fromValue, this.toValue);
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
