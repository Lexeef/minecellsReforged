package com.github.mim1q.minecells.world.processor;

import com.github.mim1q.minecells.registry.MineCellsStructureProcessorTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SwitchBlockStructureProcessor extends StructureProcessor {
    public static final Codec<SwitchBlockStructureProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("default_namespace", "minecraft").forGetter(processor -> processor.defaultNamespace),
        SwitchBlockRule.CODEC.listOf().fieldOf("rules").forGetter(processor -> processor.rules)
    ).apply(instance, SwitchBlockStructureProcessor::new));

    private final List<SwitchBlockRule> rules;
    private final String defaultNamespace;
    private final Map<Block, SwitchBlockRule> rulesByFromBlock;

    public SwitchBlockStructureProcessor(String defaultNamespace, List<SwitchBlockRule> rules) {
        this.rules = rules;
        this.defaultNamespace = defaultNamespace == null ? "minecraft" : defaultNamespace;
        this.rules.forEach(rule -> rule.setup(this.defaultNamespace));
        this.rulesByFromBlock = rules.stream().collect(Collectors.toMap(rule -> rule.fromBlock, rule -> rule));
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(
        LevelReader level,
        BlockPos offset,
        BlockPos pos,
        StructureTemplate.StructureBlockInfo originalBlockInfo,
        StructureTemplate.StructureBlockInfo currentBlockInfo,
        StructurePlaceSettings settings
    ) {
        var oldState = currentBlockInfo.state();
        var rule = rulesByFromBlock.get(oldState.getBlock());
        if (rule == null) {
            return currentBlockInfo;
        }

        var rng = RandomSource.create(currentBlockInfo.pos().hashCode());
        rng.nextInt();
        var newState = rule.selectRandomBlock(rng).defaultBlockState();
        for (var property : newState.getProperties()) {
            newState = copyProperty(oldState, newState, property);
        }
        return new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), newState, currentBlockInfo.nbt());
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState copyFrom, BlockState copyTo, Property<T> property) {
        if (copyFrom.hasProperty(property)) {
            return copyTo.setValue(property, copyFrom.getValue(property));
        }
        return copyTo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return MineCellsStructureProcessorTypes.SWITCH_BLOCK.get();
    }

    public static final class SwitchBlockRule {
        public static final Codec<SwitchBlockRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("from").forGetter(rule -> rule.fromBlockId),
            Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("to").forGetter(rule -> rule.toBlocksIdsWithWeights)
        ).apply(instance, SwitchBlockRule::new));

        private final String fromBlockId;
        private final Map<String, Integer> toBlocksIdsWithWeights;
        private final int totalWeight;
        private Block fromBlock;
        private Map<Block, Integer> toBlocksWithWeights;

        private SwitchBlockRule(String fromBlockId, Map<String, Integer> toBlocksIdsWithWeights) {
            this.fromBlockId = fromBlockId;
            this.toBlocksIdsWithWeights = toBlocksIdsWithWeights;
            this.totalWeight = toBlocksIdsWithWeights.values().stream().reduce(0, Integer::sum);
        }

        private Block selectRandomBlock(RandomSource random) {
            var selected = random.nextInt(totalWeight) + 1;
            var current = 0;
            for (var entry : toBlocksWithWeights.entrySet()) {
                current += entry.getValue();
                if (current >= selected) {
                    return entry.getKey();
                }
            }
            return fromBlock;
        }

        private void setup(String defaultNamespace) {
            this.fromBlock = BuiltInRegistries.BLOCK.get(getBlockId(fromBlockId, defaultNamespace));
            this.toBlocksWithWeights = toBlocksIdsWithWeights.entrySet().stream().collect(Collectors.toMap(
                entry -> BuiltInRegistries.BLOCK.get(getBlockId(entry.getKey(), defaultNamespace)),
                Map.Entry::getValue
            ));
        }

        private static ResourceLocation getBlockId(String id, String defaultNamespace) {
            return new ResourceLocation(id.contains(":") ? id : defaultNamespace + ":" + id);
        }
    }
}
