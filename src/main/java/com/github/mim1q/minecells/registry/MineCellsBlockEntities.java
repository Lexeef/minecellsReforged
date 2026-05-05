package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.ArrowSignBlockEntity;
import com.github.mim1q.minecells.block.blockentity.BarrierControllerBlockEntity;
import com.github.mim1q.minecells.block.blockentity.CellCrafterBlockEntity;
import com.github.mim1q.minecells.block.blockentity.DecorativeStatueBlockEntity;
import com.github.mim1q.minecells.block.blockentity.FlagBlockEntity;
import com.github.mim1q.minecells.block.blockentity.ReturnStoneBlockEntity;
import com.github.mim1q.minecells.block.blockentity.RiftBlockEntity;
import com.github.mim1q.minecells.block.blockentity.RunicVinePlantBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class MineCellsBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MineCells.MOD_ID);

    public static final RegistryObject<BlockEntityType<FlagBlockEntity>> FLAG_BLOCK_ENTITY = BLOCK_ENTITIES.register(
        "biome_banner",
        () -> BlockEntityType.Builder.of(FlagBlockEntity::new, MineCellsBlocks.FLAG_BLOCKS.stream().map(Supplier::get).toArray(Block[]::new)).build(null)
    );
    public static final RegistryObject<BlockEntityType<DecorativeStatueBlockEntity>> DECORATIVE_STATUE_BLOCK_ENTITY = register(
        "decorative_statue",
        DecorativeStatueBlockEntity::new,
        MineCellsBlocks.KING_STATUE
    );
    public static final RegistryObject<BlockEntityType<ReturnStoneBlockEntity>> RETURN_STONE = register(
        "return_stone",
        ReturnStoneBlockEntity::new,
        MineCellsBlocks.RETURN_STONE
    );
    public static final RegistryObject<BlockEntityType<ArrowSignBlockEntity>> ARROW_SIGN = register(
        "arrow_sign",
        ArrowSignBlockEntity::new,
        MineCellsBlocks.ARROW_SIGN
    );
    public static final RegistryObject<BlockEntityType<CellCrafterBlockEntity>> CELL_CRAFTER = BLOCK_ENTITIES.register(
        "cell_crafter",
        () -> BlockEntityType.Builder.of(CellCrafterBlockEntity::new, MineCellsBlocks.CELL_CRAFTER.get(), MineCellsBlocks.UNBREAKABLE_CELL_CRAFTER.get()).build(null)
    );
    public static final RegistryObject<BlockEntityType<BarrierControllerBlockEntity>> BARRIER_CONTROLLER = BLOCK_ENTITIES.register(
        "barrier_controller",
        () -> BlockEntityType.Builder.of(
            BarrierControllerBlockEntity::new,
            MineCellsBlocks.BOSS_BARRIER_CONTROLLER.get(),
            MineCellsBlocks.BOSS_ENTRY_BARRIER_CONTROLLER.get(),
            MineCellsBlocks.PLAYER_BARRIER_CONTROLLER.get()
        ).build(null)
    );
    public static final RegistryObject<BlockEntityType<RunicVinePlantBlockEntity>> RUNIC_VINE_PLANT = register(
        "runic_vine_plant",
        RunicVinePlantBlockEntity::new,
        MineCellsBlocks.RUNIC_VINE_PLANT
    );
    public static final RegistryObject<BlockEntityType<RiftBlockEntity>> RIFT = register(
        "rift",
        RiftBlockEntity::new,
        MineCellsBlocks.RIFT
    );

    private MineCellsBlockEntities() {
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(
        String name,
        BlockEntityType.BlockEntitySupplier<T> factory,
        RegistryObject<? extends Block> block
    ) {
        return BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(factory, block.get()).build(null));
    }
}
