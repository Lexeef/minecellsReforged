package com.github.mim1q.minecells.world.feature.tree;

import com.github.mim1q.minecells.block.CageBlock;
import com.github.mim1q.minecells.block.FlagBlock;
import com.github.mim1q.minecells.block.FlagPoleBlock;
import com.github.mim1q.minecells.block.MineCellsBlockTags;
import com.github.mim1q.minecells.block.SkeletonDecorationBlock;
import com.github.mim1q.minecells.registry.MineCellsBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelSimulatedReader;

import java.util.function.BiConsumer;
import java.util.List;

public interface PromenadeTreeHelper {
    BlockState TRUNK_BLOCK = MineCellsBlocks.PUTRID_WOOD.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);

    default void placeBranch(LevelSimulatedReader world, BiConsumer<BlockPos, BlockState> replacer, RandomSource random, BlockPos origin, Direction direction, boolean decorations) {
        if (decorations && random.nextFloat() < 0.025F) {
            this.placeFlag(replacer, origin, direction, random);
            return;
        }
        boolean big = random.nextFloat() < 0.75F;
        Vec3i offset = direction.getNormal();
        origin = origin.offset(offset);
        replacer.accept(origin, TRUNK_BLOCK);
        if (big) {
            origin = origin.offset(offset).above();
            replacer.accept(origin, TRUNK_BLOCK);
            replacer.accept(origin.above(), TRUNK_BLOCK);
        }
        if (random.nextFloat() < 0.5F) {
            int length = 3 + random.nextInt(8);
            if (decorations && this.canPlaceChain(world, origin.below(), length + 2)) {
                Block chain = random.nextFloat() < 0.33F ? MineCellsBlocks.BIG_CHAIN.get() : Blocks.CHAIN;
                this.placeChain(replacer, random, origin.below(), length, chain.defaultBlockState());
                if (big) {
                    BlockPos pos = origin.offset(direction.getOpposite().getNormal());
                    BlockState state = chain.defaultBlockState().setValue(ChainBlock.AXIS, direction.getAxis());
                    replacer.accept(pos, state);
                }
            }
        }
    }

    default void placeRoot(LevelSimulatedReader world, BiConsumer<BlockPos, BlockState> replacer, BlockPos origin, int height) {
        origin = origin.above(height);
        for (int i = height; i > -3; i--) {
            if (!world.isStateAtPosition(origin, state -> state.is(MineCellsBlockTags.TREE_ROOT_REPLACEABLE))) {
                continue;
            }
            BlockPos[] positions = {origin.north(), origin.south(), origin.east(), origin.west()};
            boolean shouldPlace = false;
            for (BlockPos pos : positions) {
                if (world.isStateAtPosition(pos, state -> state.getCollisionShape((BlockGetter) world, pos).isEmpty())) {
                    shouldPlace = true;
                }
            }
            if (!shouldPlace) {
                return;
            }
            replacer.accept(origin, TRUNK_BLOCK);
            origin = origin.below();
        }
    }

    default void placeFlag(BiConsumer<BlockPos, BlockState> replacer, BlockPos origin, Direction direction, RandomSource random) {
        var flagBlock = random.nextBoolean()
            ? MineCellsBlocks.PROMENADE_OF_THE_CONDEMNED_FLAG.get()
            : MineCellsBlocks.RIBBON_FLAGS.get("red").get();
        var pole = MineCellsBlocks.FLAG_POLE.get().defaultBlockState().setValue(FlagPoleBlock.FACING, direction);
        var offset = direction.getNormal();
        origin = origin.offset(offset);
        replacer.accept(origin, pole.setValue(FlagPoleBlock.CONNECTING, true));
        origin = origin.offset(offset);
        replacer.accept(origin, pole.setValue(FlagPoleBlock.CONNECTING, false));
        replacer.accept(
            origin.below(),
            flagBlock.defaultBlockState()
                .setValue(FlagBlock.FACING, direction.getClockWise())
                .setValue(FlagBlock.PLACEMENT, FlagBlock.Placement.CENTERED)
        );
    }

    default boolean canPlaceChain(LevelSimulatedReader world, BlockPos origin, int length) {
        for (int i = 0; i < length; i++) {
            boolean obstructed = world.isStateAtPosition(origin.below(i), state -> !state.isAir());
            if (obstructed) {
                return false;
            }
        }
        return true;
    }

    default void placeChain(BiConsumer<BlockPos, BlockState> replacer, RandomSource random, BlockPos origin, int length, BlockState chain) {
        for (int i = 0; i < length; i++) {
            replacer.accept(origin.below(i), chain);
        }
        if (random.nextFloat() < 0.5F) {
            this.placeChainDecoration(replacer, random, origin.below(length));
        }
    }

    default void placeChainDecoration(BiConsumer<BlockPos, BlockState> replacer, RandomSource random, BlockPos origin) {
        if (random.nextFloat() < 0.25F) {
            List<Block> corpses = List.of(
                MineCellsBlocks.HANGED_CORPSE.get(),
                MineCellsBlocks.HANGED_SKELETON.get(),
                MineCellsBlocks.HANGED_ROTTING_CORPSE.get()
            );
            Block corpse = corpses.get(random.nextInt(3));
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            replacer.accept(origin, corpse.defaultBlockState().setValue(SkeletonDecorationBlock.FACING, direction));
            return;
        }
        boolean broken = random.nextBoolean();
        if (broken) {
            replacer.accept(origin, MineCellsBlocks.BROKEN_CAGE.get().defaultBlockState().setValue(CageBlock.FLIPPED, true));
            return;
        }
        replacer.accept(origin, MineCellsBlocks.CAGE.get().defaultBlockState().setValue(CageBlock.FLIPPED, true));
        replacer.accept(origin.below(), MineCellsBlocks.CAGE.get().defaultBlockState());
    }
}
