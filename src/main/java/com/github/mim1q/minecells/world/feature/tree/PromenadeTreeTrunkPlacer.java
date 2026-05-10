package com.github.mim1q.minecells.world.feature.tree;

import com.github.mim1q.minecells.registry.MineCellsPlacerTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer.FoliageAttachment;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class PromenadeTreeTrunkPlacer extends StraightTrunkPlacer implements PromenadeTreeHelper {
    public static final Codec<PromenadeTreeTrunkPlacer> CODEC = RecordCodecBuilder.create(
        instance -> trunkPlacerParts(instance).apply(instance, PromenadeTreeTrunkPlacer::new)
    );

    public PromenadeTreeTrunkPlacer(int baseHeight, int firstRandom, int secondRandom) {
        super(baseHeight, firstRandom, secondRandom);
    }

    @Override
    public List<FoliageAttachment> placeTrunk(
        LevelSimulatedReader world,
        BiConsumer<BlockPos, BlockState> replacer,
        RandomSource random,
        int height,
        BlockPos startPos,
        TreeConfiguration config
    ) {
        height = height + random.nextInt(10);
        List<FoliageAttachment> nodes = new ArrayList<>();
        boolean broken = random.nextFloat() < 0.1F;
        if (broken) {
            height = height * 2 / 3;
        }
        for (int i = 1; i < height; i++) {
            if (!world.isStateAtPosition(startPos.above(i), BlockBehaviour.BlockStateBase::canBeReplaced)) {
                return nodes;
            }
        }

        setDirtAt(world, replacer, random, startPos.below(), config);
        for (int i = 0; i < height; i++) {
            replacer.accept(startPos.above(i), TRUNK_BLOCK);
        }

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos basePos = startPos.offset(dir.getNormal());
            int baseHeight = random.nextInt(5);
            if (baseHeight > 0) {
                this.placeRoot(world, replacer, basePos.below(), baseHeight);
            }

            if (random.nextFloat() < 0.25F) {
                continue;
            }

            int h = random.nextIntBetweenInclusive(6, height - 3);
            this.placeBranch(world, replacer, random, startPos.above(h), dir, !config.ignoreVines);
            int minH = h + 3;
            while (h < height - 8) {
                h = random.nextIntBetweenInclusive(minH, height - 3);
                this.placeBranch(world, replacer, random, startPos.above(h), dir, !config.ignoreVines);
                if (!broken && h > height - 10) {
                    nodes.add(new FoliageAttachment(startPos.above(h + 1).offset(dir.getNormal().multiply(3)), 2, true));
                }
                minH = h + 3;
            }
        }

        if (!broken) {
            nodes.add(new FoliageAttachment(startPos.above(height), 2, true));
            var dirs = Direction.Plane.HORIZONTAL.shuffledCopy(random);
            if (random.nextFloat() < 0.75F) {
                int branchHeight = 3 + random.nextInt(2);
                BlockPos pos = startPos.above(height - 4 - random.nextInt(10));
                this.generateLongBranch(replacer, pos, dirs.get(0), branchHeight);
                nodes.add(new FoliageAttachment(pos.offset(dirs.get(0).getNormal().multiply(branchHeight)).above(branchHeight + 1), 2, true));
            }
            if (random.nextFloat() < 0.5F) {
                int branchHeight = 3 + random.nextInt(2);
                BlockPos pos = startPos.above(height - 4 - random.nextInt(10));
                this.generateLongBranch(replacer, pos, dirs.get(1), branchHeight);
                nodes.add(new FoliageAttachment(pos.offset(dirs.get(1).getNormal().multiply(branchHeight)).above(branchHeight + 1), 2, true));
            }
        }

        return nodes;
    }

    public void generateLongBranch(BiConsumer<BlockPos, BlockState> replacer, BlockPos startPos, Direction dir, int length) {
        BlockPos pos = startPos;
        for (int i = 0; i < length; i++) {
            pos = pos.offset(dir.getNormal()).above();
            replacer.accept(pos, TRUNK_BLOCK);
        }
        replacer.accept(pos.above(2), TRUNK_BLOCK);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return MineCellsPlacerTypes.PROMENADE_TRUNK.get();
    }
}
