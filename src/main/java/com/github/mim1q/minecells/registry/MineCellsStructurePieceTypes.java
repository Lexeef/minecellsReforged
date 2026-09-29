package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.GridPiece;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class MineCellsStructurePieceTypes {
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE_TYPES =
        DeferredRegister.create(Registries.STRUCTURE_PIECE, MineCells.MOD_ID);

    public static final RegistryObject<StructurePieceType> GRID_PIECE =
        STRUCTURE_PIECE_TYPES.register("grid_generator_piece", () -> (StructurePieceType) GridPiece::new);

    private MineCellsStructurePieceTypes() {
    }

    public static void register(IEventBus eventBus) {
        STRUCTURE_PIECE_TYPES.register(eventBus);
    }
}
