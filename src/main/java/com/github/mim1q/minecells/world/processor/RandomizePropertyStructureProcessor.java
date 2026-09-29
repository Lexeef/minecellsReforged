package com.github.mim1q.minecells.world.processor;

import com.github.mim1q.minecells.registry.MineCellsStructureProcessorTypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.LevelReader;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RandomizePropertyStructureProcessor extends StructureProcessor {
    public static final Codec<RandomizePropertyStructureProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.BLOCK.byNameCodec().listOf().fieldOf("blocks").forGetter(processor -> processor.blocks),
        Codec.STRING.fieldOf("property_name").forGetter(processor -> processor.propertyName)
    ).apply(instance, RandomizePropertyStructureProcessor::new));

    private final List<Block> blocks;
    private final String propertyName;

    public RandomizePropertyStructureProcessor(List<Block> blocks, String propertyName) {
        this.blocks = blocks;
        this.propertyName = propertyName;
    }

    @Nullable
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public StructureTemplate.StructureBlockInfo processBlock(
        LevelReader level,
        BlockPos offset,
        BlockPos pos,
        StructureTemplate.StructureBlockInfo originalBlockInfo,
        StructureTemplate.StructureBlockInfo currentBlockInfo,
        StructurePlaceSettings settings
    ) {
        if (!blocks.contains(currentBlockInfo.state().getBlock())) {
            return currentBlockInfo;
        }

        var property = (Property) currentBlockInfo.state().getProperties().stream()
            .filter(it -> it.getName().equals(propertyName))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Property " + propertyName + " not found in block: " + currentBlockInfo.state().getBlock()));

        var random = settings.getRandom(currentBlockInfo.pos());
        var propertyValue = (Comparable) property.getPossibleValues().stream()
            .skip(random.nextInt(property.getPossibleValues().size()))
            .findFirst()
            .orElseThrow();

        var newState = currentBlockInfo.state().setValue(property, propertyValue);
        return new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), newState, currentBlockInfo.nbt());
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return MineCellsStructureProcessorTypes.RANDOMIZE_PROPERTY.get();
    }
}
