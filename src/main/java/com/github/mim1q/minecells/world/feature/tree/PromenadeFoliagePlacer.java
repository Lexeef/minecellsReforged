package com.github.mim1q.minecells.world.feature.tree;

import com.github.mim1q.minecells.registry.MineCellsPlacerTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class PromenadeFoliagePlacer extends FoliagePlacer {
    private static final Set<BlockPos> OFFSETS = Set.of(
        new BlockPos(0, 0, 0),
        new BlockPos(1, 0, 0),
        new BlockPos(-1, 0, 0),
        new BlockPos(0, 0, 1),
        new BlockPos(0, 0, -1)
    );

    public static final Codec<PromenadeFoliagePlacer> CODEC = RecordCodecBuilder.create(
        instance -> foliagePlacerParts(instance).apply(instance, PromenadeFoliagePlacer::new)
    );

    public PromenadeFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return MineCellsPlacerTypes.PROMENADE_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(
        LevelSimulatedReader world,
        FoliageSetter placer,
        RandomSource random,
        TreeConfiguration config,
        int trunkHeight,
        FoliageAttachment treeNode,
        int foliageHeight,
        int radius,
        int offset
    ) {
        BlockPos blockPos = treeNode.pos().above(offset);
        boolean giantTrunk = treeNode.doubleTrunk();
        BlockState foliageBlock = config.foliageProvider.getState(random, blockPos);
        if (random.nextBoolean()) {
            this.generateSquare(world, placer, random, foliageBlock, blockPos, radius, -2, giantTrunk);
        }
        this.generateSquare(world, placer, random, foliageBlock, blockPos, radius + 2, -1, giantTrunk);
        this.generateSquare(world, placer, random, foliageBlock, blockPos, radius + 3, 0, giantTrunk);
        this.generateSquare(world, placer, random, foliageBlock, blockPos, radius + 2, 1, giantTrunk);
        if (random.nextBoolean()) {
            this.generateSquare(world, placer, random, foliageBlock, blockPos, radius, 2, giantTrunk);
        }

        BlockState trunk = config.trunkProvider.getState(random, blockPos);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (BlockPos offsetPos : OFFSETS) {
            mutablePos.set(blockPos).move(offsetPos);
            placer.set(mutablePos, trunk);
        }
    }

    @Override
    public int foliageHeight(RandomSource random, int trunkHeight, TreeConfiguration config) {
        return 4;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        int dist = Math.abs(dx) + Math.abs(dz);
        if (dist > radius * 2 - 2) {
            return true;
        }
        if (dist > radius * 2 - 3) {
            return random.nextBoolean();
        }
        return false;
    }

    protected void generateSquare(LevelSimulatedReader world, FoliageSetter placer, RandomSource random, BlockState leavesState, BlockPos centerPos, int radius, int y, boolean giantTrunk) {
        int extra = giantTrunk ? 1 : 0;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int j = -radius; j <= radius + extra; ++j) {
            for (int k = -radius; k <= radius + extra; ++k) {
                if (!this.shouldSkipLocationSigned(random, j, y, k, radius, giantTrunk)) {
                    mutable.set(centerPos).move(j, y, k);
                    if (TreeFeature.validTreePos(world, mutable)) {
                        placer.set(mutable, leavesState);
                    }
                }
            }
        }
    }
}
