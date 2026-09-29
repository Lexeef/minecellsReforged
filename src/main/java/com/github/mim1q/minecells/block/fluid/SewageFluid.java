package com.github.mim1q.minecells.block.fluid;

import com.github.mim1q.minecells.registry.MineCellsParticles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import net.minecraftforge.fluids.ForgeFlowingFluid;

public final class SewageFluid {
    public static final int SEWAGE_BUBBLE_COLOR = 0x68DC47;
    public static final int ANCIENT_SEWAGE_BUBBLE_COLOR = 0xEBC331;

    private SewageFluid() {
    }

    private static void spawnBubble(Level level, BlockPos pos, FluidState state, RandomSource random, int color) {
        if (!level.getFluidState(pos.above()).isEmpty() || random.nextFloat() > 0.05F) {
            return;
        }
        level.addParticle(
            MineCellsParticles.RISING_BUBBLE.get().get(color),
            pos.getX() + random.nextDouble(),
            pos.getY() + state.getAmount() * 0.125D - random.nextDouble() * 0.2D,
            pos.getZ() + random.nextDouble(),
            0.0D, 0.05D + random.nextDouble() * 0.02D, 0.0D
        );
    }

    public static class Source extends ForgeFlowingFluid.Source {
        private final int bubbleColor;

        public Source(Properties properties, int bubbleColor) {
            super(properties);
            this.bubbleColor = bubbleColor;
        }

        @Override
        protected void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
            super.animateTick(level, pos, state, random);
            spawnBubble(level, pos, state, random, bubbleColor);
        }

        @Override
        protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
            return false;
        }
    }

    public static class Flowing extends ForgeFlowingFluid.Flowing {
        private final int bubbleColor;

        public Flowing(Properties properties, int bubbleColor) {
            super(properties);
            this.bubbleColor = bubbleColor;
        }

        @Override
        protected void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
            super.animateTick(level, pos, state, random);
            spawnBubble(level, pos, state, random, bubbleColor);
        }

        @Override
        protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
            return false;
        }
    }
}
