package com.github.mim1q.minecells.world.feature;

import com.github.mim1q.minecells.structure.grid.MineCellsStructurePoolBasedGenerator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.WorldGenLevel;

public class JigsawFeature extends Feature<JigsawFeature.JigsawFeatureConfig> {
    public JigsawFeature(Codec<JigsawFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<JigsawFeatureConfig> context) {
        WorldGenLevel level = context.level();
        Holder<StructureTemplatePool> pool = level.registryAccess()
            .registryOrThrow(Registries.TEMPLATE_POOL)
            .getHolder(ResourceKey.create(Registries.TEMPLATE_POOL, context.config().templatePool()))
            .orElse(null);
        if (pool == null) {
            return false;
        }
        BlockPos pos = context.origin().offset(this.getOffset(context.origin()));
        Rotation rotation = Rotation.getRandom(context.random());
        MineCellsStructurePoolBasedGenerator.placeFeature(level, context.chunkGenerator(), context.random(), pool, 8, pos, rotation);
        return true;
    }

    public Vec3i getOffset(Vec3i start) {
        return Vec3i.ZERO;
    }

    public record JigsawFeatureConfig(ResourceLocation templatePool, ResourceLocation start) implements FeatureConfiguration {
        public static final Codec<JigsawFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("template_pool").forGetter(JigsawFeatureConfig::templatePool),
            ResourceLocation.CODEC.fieldOf("start").forGetter(JigsawFeatureConfig::start)
        ).apply(instance, JigsawFeatureConfig::new));
    }
}
