package com.github.mim1q.minecells.world.feature.tree;

import com.github.mim1q.minecells.registry.MineCellsPlacerTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer.FoliageAttachment;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class PromenadeShrubTrunkPlacer extends TrunkPlacer {
    public static final Codec<PromenadeShrubTrunkPlacer> CODEC = RecordCodecBuilder.create(
        instance -> trunkPlacerParts(instance).apply(instance, PromenadeShrubTrunkPlacer::new)
    );

    public PromenadeShrubTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
        super(baseHeight, firstRandomHeight, secondRandomHeight);
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
        if (world.isStateAtPosition(startPos, BlockState::isAir)) {
            replacer.accept(startPos, config.trunkProvider.getState(random, startPos));
        }
        return List.of(new FoliageAttachment(startPos.above(), 0, false));
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return MineCellsPlacerTypes.PROMENADE_SHRUB.get();
    }
}
