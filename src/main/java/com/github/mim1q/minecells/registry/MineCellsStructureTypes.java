package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.MineCellsBigJigsawStructure;
import com.github.mim1q.minecells.structure.grid.GridBasedStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class MineCellsStructureTypes {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
        DeferredRegister.create(Registries.STRUCTURE_TYPE, MineCells.MOD_ID);

    public static final RegistryObject<StructureType<MineCellsBigJigsawStructure>> BIG_JIGSAW =
        register("big_jigsaw", MineCellsBigJigsawStructure.CODEC);

    public static final RegistryObject<StructureType<GridBasedStructure>> PRISON =
        register("prison", GridBasedStructure.PRISON_CODEC);
    public static final RegistryObject<StructureType<GridBasedStructure>> PROMENADE =
        register("promenade", GridBasedStructure.PROMENADE_CODEC);
    public static final RegistryObject<StructureType<GridBasedStructure>> PROMENADE_WALL_X =
        register("promenade_wall_x", GridBasedStructure.PROMENADE_WALL_X_CODEC);
    public static final RegistryObject<StructureType<GridBasedStructure>> PROMENADE_WALL_Z =
        register("promenade_wall_z", GridBasedStructure.PROMENADE_WALL_Z_CODEC);
    public static final RegistryObject<StructureType<GridBasedStructure>> RAMPARTS =
        register("ramparts", GridBasedStructure.RAMPARTS_CODEC);
    public static final RegistryObject<StructureType<GridBasedStructure>> BLACK_BRIDGE =
        register("black_bridge", GridBasedStructure.BLACK_BRIDGE_CODEC);

    private MineCellsStructureTypes() {
    }

    public static void register(IEventBus eventBus) {
        STRUCTURE_TYPES.register(eventBus);
    }

    private static <S extends Structure> RegistryObject<StructureType<S>> register(String id, com.mojang.serialization.Codec<S> codec) {
        return STRUCTURE_TYPES.register(id, () -> () -> codec);
    }
}
