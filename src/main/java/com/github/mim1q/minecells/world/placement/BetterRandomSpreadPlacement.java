package com.github.mim1q.minecells.world.placement;

import com.github.mim1q.minecells.registry.MineCellsStructurePlacementTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

import java.util.List;
import java.util.Optional;

public class BetterRandomSpreadPlacement extends StructurePlacement {
    public static final Codec<BetterRandomSpreadPlacement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Vec3i.offsetCodec(16).optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(BetterRandomSpreadPlacement::locateOffset),
        Codec.floatRange(0.0F, 1.0F).optionalFieldOf("frequency", 1.0F).forGetter(BetterRandomSpreadPlacement::actualFrequency),
        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter(BetterRandomSpreadPlacement::salt),
        BetterExclusionZone.CODEC.listOf().optionalFieldOf("exclusion_zones", List.of()).forGetter(BetterRandomSpreadPlacement::exclusionZones),
        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("minecells_spacing").forGetter(BetterRandomSpreadPlacement::spacing),
        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("minecells_separation").forGetter(BetterRandomSpreadPlacement::separation),
        RandomSpreadType.CODEC.optionalFieldOf("spread_type", RandomSpreadType.LINEAR).forGetter(BetterRandomSpreadPlacement::spreadType)
    ).apply(instance, BetterRandomSpreadPlacement::new));

    private final float actualFrequency;
    private final List<BetterExclusionZone> exclusionZones;
    private final int spacing;
    private final int separation;
    private final RandomSpreadType spreadType;

    public BetterRandomSpreadPlacement(
        Vec3i locateOffset,
        float frequency,
        int salt,
        List<BetterExclusionZone> exclusionZones,
        int spacing,
        int separation,
        RandomSpreadType spreadType
    ) {
        super(locateOffset, FrequencyReductionMethod.DEFAULT, 1.0F, salt, Optional.empty());
        this.actualFrequency = frequency;
        this.exclusionZones = exclusionZones;
        this.spacing = spacing;
        this.separation = separation;
        this.spreadType = spreadType;
    }

    @Override
    public boolean isStructureChunk(ChunkGeneratorStructureState calculator, int chunkX, int chunkZ) {
        RandomSource random = new WorldgenRandom(new LegacyRandomSource(0L));
        ((WorldgenRandom) random).setLargeFeatureWithSalt(calculator.getLevelSeed(), chunkX, chunkZ, this.salt());
        return super.isStructureChunk(calculator, chunkX, chunkZ)
            && random.nextFloat() <= this.actualFrequency
            && this.exclusionZones.stream().noneMatch(zone -> zone.shouldExclude(calculator, chunkX, chunkZ));
    }

    public ChunkPos getPotentialStructureChunk(long seed, int chunkX, int chunkZ) {
        int regionX = Math.floorDiv(chunkX, this.spacing);
        int regionZ = Math.floorDiv(chunkZ, this.spacing);
        WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0L));
        random.setLargeFeatureWithSalt(seed, regionX, regionZ, this.salt());
        int spread = this.spacing - this.separation;
        int offsetX = this.spreadType.evaluate(random, spread);
        int offsetZ = this.spreadType.evaluate(random, spread);
        return new ChunkPos(regionX * this.spacing + offsetX, regionZ * this.spacing + offsetZ);
    }

    @Override
    protected boolean isPlacementChunk(ChunkGeneratorStructureState calculator, int chunkX, int chunkZ) {
        ChunkPos start = this.getPotentialStructureChunk(calculator.getLevelSeed(), chunkX, chunkZ);
        return start.x == chunkX && start.z == chunkZ;
    }

    @Override
    public StructurePlacementType<?> type() {
        return MineCellsStructurePlacementTypes.BETTER_RANDOM_SPREAD.get();
    }

    private float actualFrequency() {
        return this.actualFrequency;
    }

    private List<BetterExclusionZone> exclusionZones() {
        return this.exclusionZones;
    }

    private int spacing() {
        return this.spacing;
    }

    private int separation() {
        return this.separation;
    }

    private RandomSpreadType spreadType() {
        return this.spreadType;
    }

    private record BetterExclusionZone(Holder<StructureSet> otherSet, int chunkCount) {
        private static final Codec<BetterExclusionZone> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StructureSet.CODEC.fieldOf("other_set").forGetter(BetterExclusionZone::otherSet),
            Codec.intRange(1, 32).fieldOf("chunk_count").forGetter(BetterExclusionZone::chunkCount)
        ).apply(instance, BetterExclusionZone::new));

        private boolean shouldExclude(ChunkGeneratorStructureState calculator, int chunkX, int chunkZ) {
            return calculator.hasStructureChunkInRange(this.otherSet, chunkX, chunkZ, this.chunkCount);
        }
    }
}
