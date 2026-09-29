package com.github.mim1q.minecells.world.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.Optional;

public record CliffDensityFunction(
    int gridSize,
    boolean zAxis,
    int width,
    int offset,
    int fromY,
    int toY,
    double taperWidth,
    Optional<DensityFunction> offsetNoise
) implements DensityFunction {
    public static final KeyDispatchDataCodec<CliffDensityFunction> CODEC = KeyDispatchDataCodec.of(
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("grid_size", 1024).forGetter(CliffDensityFunction::gridSize),
            Codec.BOOL.optionalFieldOf("z_axis", false).forGetter(CliffDensityFunction::zAxis),
            Codec.INT.fieldOf("width").forGetter(CliffDensityFunction::width),
            Codec.INT.fieldOf("offset").forGetter(CliffDensityFunction::offset),
            Codec.INT.fieldOf("from_y").forGetter(CliffDensityFunction::fromY),
            Codec.INT.fieldOf("to_y").forGetter(CliffDensityFunction::toY),
            Codec.DOUBLE.fieldOf("taper_width").forGetter(CliffDensityFunction::taperWidth),
            DensityFunction.HOLDER_HELPER_CODEC.optionalFieldOf("offset_noise").forGetter(CliffDensityFunction::offsetNoise)
        ).apply(instance, CliffDensityFunction::new))
    );

    @Override
    public double compute(FunctionContext pos) {
        int coord = (this.zAxis ? pos.blockZ() : pos.blockX()) - this.offset;
        int y = pos.blockY();
        int center = DensityFunctionUtils.getClosestMultiple(coord, this.gridSize);
        double dist = Math.abs(coord - center);
        double maxDist = this.width / 2.0D;
        if (dist > maxDist) {
            return 0.0D;
        }
        if (this.offsetNoise.isPresent()) {
            double sample = this.offsetNoise.get().compute(pos) * 0.8D;
            dist += (int) (this.taperWidth * sample);
        }
        if (y <= this.fromY || y >= this.toY) {
            return 1.0D;
        }
        double heightFraction = (y - this.fromY) / (double) (this.toY - this.fromY);
        heightFraction = 2.0D * (heightFraction - 0.5D);
        heightFraction = 1.0D - Math.abs(heightFraction);
        if (heightFraction > 0.5D) {
            heightFraction = 1.0D;
        } else {
            heightFraction = 2.0D * heightFraction;
        }
        double distanceToEdge = maxDist - dist;
        double maxDistanceToEdge = this.taperWidth * heightFraction;
        return DensityFunctionUtils.easeInQuad(0.0D, 1.0D, Mth.clamp(distanceToEdge / maxDistanceToEdge, 0.0D, 1.0D));
    }

    @Override
    public void fillArray(double[] densities, ContextProvider applier) {
        applier.fillAllDirectly(densities, this);
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        Optional<DensityFunction> mappedOffsetNoise = this.offsetNoise.map(densityFunction -> densityFunction.mapAll(visitor));
        return visitor.apply(new CliffDensityFunction(
            this.gridSize,
            this.zAxis,
            this.width,
            this.offset,
            this.fromY,
            this.toY,
            this.taperWidth,
            mappedOffsetNoise
        ));
    }

    @Override
    public double minValue() {
        return 0.0D;
    }

    @Override
    public double maxValue() {
        return 1.0D;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
