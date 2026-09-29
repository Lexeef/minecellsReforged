package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.world.processor.RandomizePropertyStructureProcessor;
import com.github.mim1q.minecells.world.processor.SwitchBlockStructureProcessor;

import com.mojang.serialization.Codec;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class MineCellsStructureProcessorTypes {
    public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSOR_TYPES = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, MineCells.MOD_ID);

    public static final RegistryObject<StructureProcessorType<SwitchBlockStructureProcessor>> SWITCH_BLOCK = register("switch_block", SwitchBlockStructureProcessor.CODEC);
    public static final RegistryObject<StructureProcessorType<RandomizePropertyStructureProcessor>> RANDOMIZE_PROPERTY = register("randomize_property", RandomizePropertyStructureProcessor.CODEC);

    private MineCellsStructureProcessorTypes() {
    }

    public static void register(IEventBus eventBus) {
        STRUCTURE_PROCESSOR_TYPES.register(eventBus);
    }

    private static <P extends StructureProcessor> RegistryObject<StructureProcessorType<P>> register(String id, Codec<P> codec) {
        return STRUCTURE_PROCESSOR_TYPES.register(id, () -> () -> codec);
    }
}
