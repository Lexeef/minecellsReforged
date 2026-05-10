package com.github.mim1q.minecells.world.placement;

import com.github.mim1q.minecells.registry.MineCellsPlacementModifierTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class ExcludeChunkMultiplesPlacementModifier extends PlacementModifier {
    public static final Codec<ExcludeChunkMultiplesPlacementModifier> CODEC = RecordCodecBuilder
        .<ExcludeChunkMultiplesPlacementModifier>mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("multiple").forGetter(it -> it.multiple),
            Codec.INT.optionalFieldOf("x_offset", 0).forGetter(it -> it.xOffset),
            Codec.INT.optionalFieldOf("z_offset", 0).forGetter(it -> it.zOffset)
        ).apply(instance, ExcludeChunkMultiplesPlacementModifier::new))
        .codec();

    private final int multiple;
    private final int xOffset;
    private final int zOffset;

    private ExcludeChunkMultiplesPlacementModifier(int multiple, int xOffset, int zOffset) {
        this.multiple = multiple;
        this.xOffset = xOffset;
        this.zOffset = zOffset;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos.offset(this.xOffset, 0, this.zOffset));
        return chunkPos.x % this.multiple == 0 && chunkPos.z % this.multiple == 0 ? Stream.empty() : Stream.of(pos);
    }

    @Override
    public PlacementModifierType<?> type() {
        return MineCellsPlacementModifierTypes.EXCLUDE_CHUNK_MULTIPLES.get();
    }
}
