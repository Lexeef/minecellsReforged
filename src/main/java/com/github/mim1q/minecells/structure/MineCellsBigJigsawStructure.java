package com.github.mim1q.minecells.structure;

import com.github.mim1q.minecells.registry.MineCellsStructureTypes;
import com.github.mim1q.minecells.structure.grid.MineCellsStructurePoolBasedGenerator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class MineCellsBigJigsawStructure extends Structure {
    public static final Codec<MineCellsBigJigsawStructure> CODEC = RecordCodecBuilder.<MineCellsBigJigsawStructure>mapCodec(instance ->
        instance.group(
            Structure.settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
            Codec.intRange(0, 100).fieldOf("size").forGetter(structure -> structure.size),
            HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
            Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
            Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
        ).apply(instance, MineCellsBigJigsawStructure::new)
    ).codec();

    private final Holder<StructureTemplatePool> startPool;
    private final int size;
    private final HeightProvider startHeight;
    private final int maxDistanceFromCenter;
    private final Optional<Heightmap.Types> projectStartToHeightmap;

    protected MineCellsBigJigsawStructure(
        StructureSettings settings,
        Holder<StructureTemplatePool> startPool,
        int size,
        HeightProvider startHeight,
        Optional<Heightmap.Types> projectStartToHeightmap,
        int maxDistanceFromCenter
    ) {
        super(settings);
        this.startPool = startPool;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        BlockPos pos = context.chunkPos().getWorldPosition().atY(this.startHeight.sample(context.random(), null));
        // Match Fabric: custom generator (vanilla JigsawPlacement diverged and contributed to cut rooms).
        return MineCellsStructurePoolBasedGenerator.generate(
            context,
            this.startPool,
            this.size,
            pos,
            Rotation.NONE
        );
    }

    @Override
    public StructureType<?> type() {
        return MineCellsStructureTypes.BIG_JIGSAW.get();
    }
}
