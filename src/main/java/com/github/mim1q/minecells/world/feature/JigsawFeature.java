package com.github.mim1q.minecells.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class JigsawFeature extends Feature<JigsawFeature.JigsawFeatureConfig> {
    public JigsawFeature(Codec<JigsawFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<JigsawFeatureConfig> context) {
        WorldGenLevel level = context.level();
        Registry<StructureTemplatePool> poolRegistry = level.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        ResourceKey<StructureTemplatePool> poolKey = ResourceKey.create(Registries.TEMPLATE_POOL, context.config().templatePool());
        Holder.Reference<StructureTemplatePool> pool = poolRegistry.getHolder(poolKey).orElse(null);
        if (pool == null) {
            return false;
        }

        BlockPos pos = context.origin().offset(getOffset(context.origin(), context.random()));
        Rotation rotation = Rotation.getRandom(context.random());
        pos = pos.offset(getRotationOffset(rotation));
        return JigsawPlacement.generateJigsaw(level.getLevel(), pool, context.config().start(), 8, pos, false);
    }

    protected BlockPos getOffset(BlockPos start, RandomSource random) {
        return BlockPos.ZERO;
    }

    private static BlockPos getRotationOffset(Rotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_90 -> new BlockPos(0, 0, -1);
            case CLOCKWISE_180 -> new BlockPos(-1, 0, -1);
            case COUNTERCLOCKWISE_90 -> new BlockPos(-1, 0, 0);
            default -> BlockPos.ZERO;
        };
    }

    public record JigsawFeatureConfig(ResourceLocation templatePool, ResourceLocation start) implements FeatureConfiguration {
        public static final Codec<JigsawFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("template_pool").forGetter(JigsawFeatureConfig::templatePool),
            ResourceLocation.CODEC.fieldOf("start").forGetter(JigsawFeatureConfig::start)
        ).apply(instance, JigsawFeatureConfig::new));
    }
}
