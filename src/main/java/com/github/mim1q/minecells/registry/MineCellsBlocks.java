package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.AlchemyEquipmentBlock;
import com.github.mim1q.minecells.block.ArrowSignBlock;
import com.github.mim1q.minecells.block.BarrierRuneBlock;
import com.github.mim1q.minecells.block.BarrierControllerBlock;
import com.github.mim1q.minecells.block.BigChainBlock;
import com.github.mim1q.minecells.block.CageBlock;
import com.github.mim1q.minecells.block.CellCrafterBlock;
import com.github.mim1q.minecells.block.ColoredTorchBlock;
import com.github.mim1q.minecells.block.ConditionalBarrierBlock;
import com.github.mim1q.minecells.block.DecorativeStatueBlock;
import com.github.mim1q.minecells.block.FlagBlock;
import com.github.mim1q.minecells.block.FlagPoleBlock;
import com.github.mim1q.minecells.block.GroundDecorationBlock;
import com.github.mim1q.minecells.block.HangingLeavesBlock;
import com.github.mim1q.minecells.block.RunicVineBlock;
import com.github.mim1q.minecells.block.RunicVinePlantBlock;
import com.github.mim1q.minecells.block.ReturnStoneBlock;
import com.github.mim1q.minecells.block.RiftBlock;
import com.github.mim1q.minecells.block.SkeletonDecorationBlock;
import com.github.mim1q.minecells.block.ShockwaveFlameBlock;
import com.github.mim1q.minecells.block.SmallCrateBlock;
import com.github.mim1q.minecells.block.SpikesBlock;
import com.github.mim1q.minecells.block.WallLeavesBlock;
import com.github.mim1q.minecells.block.WoodenBoardBlock;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.block.portal.TeleporterFrameBlock;
import com.github.mim1q.minecells.item.FlagBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class MineCellsBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MineCells.MOD_ID);
    public static final List<RegistryObject<FlagBlock>> FLAG_BLOCKS = new ArrayList<>();
    private static final List<String> ORDERED_COLORS = List.of(
        "white", "light_gray", "gray", "black",
        "brown", "red", "orange", "yellow",
        "lime", "green", "cyan", "light_blue",
        "blue", "purple", "magenta", "pink"
    );

    public static final RegistryObject<Block> ELEVATOR_ASSEMBLER = registerBlockWithItem("elevator_assembler", () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(0.5F)));
    public static final RegistryObject<Block> HARDSTONE = registerBlockWithItem("hardstone", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BEDROCK)));
    public static final RegistryObject<Block> WILTED_GRASS_BLOCK = registerBlockWithItem("wilted_grass_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK).mapColor(MapColor.TERRACOTTA_CYAN)));
    public static final RegistryObject<Block> BLOOMROCK_WILTED_GRASS_BLOCK = registerBlockWithItem("bloomrock_wilted_grass_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK).mapColor(MapColor.TERRACOTTA_CYAN)));
    public static final RegistryObject<BigChainBlock> BIG_CHAIN = registerBlockWithItem("big_chain", () -> new BigChainBlock(BlockBehaviour.Properties.copy(Blocks.CHAIN)));
    public static final RegistryObject<ChainBlock> UNBREAKABLE_CHAIN = registerBlockWithItem("unbreakable_chain", () -> new ChainBlock(BlockBehaviour.Properties.copy(Blocks.CHAIN).strength(-1.0F, 3600000.0F)));
    public static final RegistryObject<Block> CRATE = registerBlockWithItem("crate", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(1.0F)));
    public static final RegistryObject<Block> CHAIN_PILE_BLOCK = registerBlockWithItem("chain_pile_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.CHAIN)));
    public static final RegistryObject<GroundDecorationBlock> CHAIN_PILE = registerBlockWithItem("chain_pile", () -> new GroundDecorationBlock(BlockBehaviour.Properties.copy(Blocks.CHAIN), GroundDecorationBlock.Shape.PILE));
    public static final RegistryObject<RunicVineBlock> RUNIC_VINE = registerBlock("runic_vine", () -> new RunicVineBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).noCollission()));
    public static final RegistryObject<RunicVinePlantBlock> RUNIC_VINE_PLANT = registerBlock("runic_vine_plant", () -> new RunicVinePlantBlock(BlockBehaviour.Properties.copy(Blocks.BEDROCK).sound(net.minecraft.world.level.block.SoundType.WET_GRASS).lightLevel(state -> 8).noOcclusion().randomTicks()));
    public static final RegistryObject<Block> RUNIC_VINE_STONE = registerBlock("runic_vine_stone", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BEDROCK)));
    public static final RegistryObject<Block> KINGDOM_PORTAL_CORE = registerBlock("kingdom_portal_core", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIRT)));
    public static final RegistryObject<LiquidBlock> SEWAGE = registerBlock("sewage", () -> new LiquidBlock(MineCellsFluids.STILL_SEWAGE, BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable()));
    public static final RegistryObject<LiquidBlock> ANCIENT_SEWAGE = registerBlock("ancient_sewage", () -> new LiquidBlock(MineCellsFluids.STILL_ANCIENT_SEWAGE, BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable()));
    public static final RegistryObject<ShockwaveFlameBlock> SHOCKWAVE_FLAME = registerBlock("shockwave_flame", () -> new ShockwaveFlameBlock(BlockBehaviour.Properties.copy(Blocks.FIRE).noCollission().noLootTable().lightLevel(state -> 8), false));
    public static final RegistryObject<ShockwaveFlameBlock> SHOCKWAVE_FLAME_PLAYER = registerBlock("shockwave_flame_player", () -> new ShockwaveFlameBlock(BlockBehaviour.Properties.copy(Blocks.FIRE).noCollission().noLootTable().lightLevel(state -> 8), true));
    public static final RegistryObject<Block> PUTRID_PLANKS = registerBlockWithItem("putrid_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<RotatedPillarBlock> PUTRID_LOG = registerBlockWithItem("putrid_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
    public static final RegistryObject<RotatedPillarBlock> STRIPPED_PUTRID_LOG = registerBlockWithItem("stripped_putrid_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)));
    public static final RegistryObject<RotatedPillarBlock> PUTRID_WOOD = registerBlockWithItem("putrid_wood", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)));
    public static final RegistryObject<RotatedPillarBlock> STRIPPED_PUTRID_WOOD = registerBlockWithItem("stripped_putrid_wood", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD)));
    public static final RegistryObject<StairBlock> PUTRID_STAIRS = registerBlockWithItem("putrid_stairs", () -> new StairBlock(PUTRID_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS)));
    public static final RegistryObject<SlabBlock> PUTRID_SLAB = registerBlockWithItem("putrid_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<DoorBlock> PUTRID_DOOR = registerBlockWithItem("putrid_door", () -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_DOOR).noOcclusion(), BlockSetType.OAK));
    public static final RegistryObject<TrapDoorBlock> PUTRID_TRAPDOOR = registerBlockWithItem("putrid_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR).noOcclusion(), BlockSetType.OAK));
    public static final RegistryObject<FenceBlock> PUTRID_FENCE = registerBlockWithItem("putrid_fence", () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE)));
    public static final RegistryObject<FenceGateBlock> PUTRID_FENCE_GATE = registerBlockWithItem("putrid_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE_GATE), WoodType.OAK));
    public static final RegistryObject<ButtonBlock> PUTRID_BUTTON = registerBlockWithItem("putrid_button", () -> new ButtonBlock(BlockBehaviour.Properties.copy(Blocks.OAK_BUTTON).noCollission(), BlockSetType.OAK, 20, true));
    public static final RegistryObject<PressurePlateBlock> PUTRID_PRESSURE_PLATE = registerBlockWithItem("putrid_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.OAK_PRESSURE_PLATE).noCollission(), BlockSetType.OAK));
    public static final RegistryObject<Block> PUTRID_BOARD_BLOCK = registerBlockWithItem("putrid_board_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<StairBlock> PUTRID_BOARD_STAIRS = registerBlockWithItem("putrid_board_stairs", () -> new StairBlock(PUTRID_BOARD_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS)));
    public static final RegistryObject<SlabBlock> PUTRID_BOARD_SLAB = registerBlockWithItem("putrid_board_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SLAB).noOcclusion()));
    public static final RegistryObject<WoodenBoardBlock> PUTRID_BOARDS = registerBlockWithItem("putrid_boards", () -> new WoodenBoardBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion()));
    public static final RegistryObject<ArrowSignBlock> ARROW_SIGN = registerBlockWithItem("arrow_sign", () -> new ArrowSignBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion().noCollission()));
    public static final RegistryObject<CellCrafterBlock> CELL_CRAFTER = registerBlockWithItem("cell_crafter", () -> new CellCrafterBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));
    public static final RegistryObject<CellCrafterBlock> UNBREAKABLE_CELL_CRAFTER = registerBlock("unbreakable_cell_crafter", () -> new CellCrafterBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().strength(-1.0F, 3600000.0F)));
    public static final RegistryObject<LeavesBlock> WILTED_LEAVES = registerBlockWithItem("wilted_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).mapColor(MapColor.TERRACOTTA_CYAN)));
    public static final RegistryObject<LeavesBlock> ORANGE_WILTED_LEAVES = registerBlockWithItem("orange_wilted_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).mapColor(MapColor.COLOR_ORANGE)));
    public static final RegistryObject<LeavesBlock> RED_WILTED_LEAVES = registerBlockWithItem("red_wilted_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).mapColor(MapColor.COLOR_RED)));
    public static final RegistryObject<WallLeavesBlock> WILTED_WALL_LEAVES = registerBlockWithItem("wilted_wall_leaves", () -> new WallLeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).noCollission().replaceable().instabreak()));
    public static final RegistryObject<WallLeavesBlock> ORANGE_WILTED_WALL_LEAVES = registerBlockWithItem("orange_wilted_wall_leaves", () -> new WallLeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).noCollission().replaceable().instabreak().mapColor(MapColor.COLOR_ORANGE)));
    public static final RegistryObject<WallLeavesBlock> RED_WILTED_WALL_LEAVES = registerBlockWithItem("red_wilted_wall_leaves", () -> new WallLeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).noCollission().replaceable().instabreak().mapColor(MapColor.COLOR_RED)));
    public static final RegistryObject<HangingLeavesBlock> WILTED_HANGING_LEAVES = registerBlockWithItem("wilted_hanging_leaves", () -> new HangingLeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).noCollission().instabreak()));
    public static final RegistryObject<HangingLeavesBlock> ORANGE_WILTED_HANGING_LEAVES = registerBlockWithItem("orange_wilted_hanging_leaves", () -> new HangingLeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).noCollission().instabreak().mapColor(MapColor.COLOR_ORANGE)));
    public static final RegistryObject<HangingLeavesBlock> RED_WILTED_HANGING_LEAVES = registerBlockWithItem("red_wilted_hanging_leaves", () -> new HangingLeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).noCollission().instabreak().mapColor(MapColor.COLOR_RED)));
    public static final RegistryObject<SaplingBlock> PUTRID_SAPLING = registerBlockWithItem("putrid_sapling", () -> new SaplingBlock(NoOpTreeGrower.INSTANCE, BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)));
    public static final RegistryObject<SaplingBlock> ORANGE_PUTRID_SAPLING = registerBlockWithItem("orange_putrid_sapling", () -> new SaplingBlock(NoOpTreeGrower.INSTANCE, BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)));
    public static final RegistryObject<SaplingBlock> RED_PUTRID_SAPLING = registerBlockWithItem("red_putrid_sapling", () -> new SaplingBlock(NoOpTreeGrower.INSTANCE, BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)));
    public static final RegistryObject<SmallCrateBlock> SMALL_CRATE = registerBlockWithItem("small_crate", () -> new SmallCrateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(1.0F)));
    public static final RegistryObject<GroundDecorationBlock> BRITTLE_BARREL = registerBlockWithItem("brittle_barrel", () -> new GroundDecorationBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(1.0F), GroundDecorationBlock.Shape.BARREL));
    public static final RegistryObject<CageBlock> CAGE = registerBlockWithItem("cage", () -> new CageBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS), false));
    public static final RegistryObject<CageBlock> BROKEN_CAGE = registerBlockWithItem("broken_cage", () -> new CageBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS), true));
    public static final RegistryObject<SpikesBlock> SPIKES = registerBlockWithItem("spikes", () -> new SpikesBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS)));
    public static final RegistryObject<AlchemyEquipmentBlock> ALCHEMY_EQUIPMENT_0 = registerBlockWithItem("alchemy_equipment_0", () -> new AlchemyEquipmentBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).offsetType(BlockBehaviour.OffsetType.XZ)));
    public static final RegistryObject<AlchemyEquipmentBlock> ALCHEMY_EQUIPMENT_1 = registerBlockWithItem("alchemy_equipment_1", () -> new AlchemyEquipmentBlock(BlockBehaviour.Properties.copy(Blocks.GLASS)));
    public static final RegistryObject<AlchemyEquipmentBlock> ALCHEMY_EQUIPMENT_2 = registerBlockWithItem("alchemy_equipment_2", () -> new AlchemyEquipmentBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).offsetType(BlockBehaviour.OffsetType.XZ)));
    public static final RegistryObject<ColoredTorchBlock> PRISON_TORCH = registerBlockWithItem("prison_torch", () -> new ColoredTorchBlock(BlockBehaviour.Properties.copy(Blocks.TORCH).instabreak().lightLevel(state -> 15).noCollission()));
    public static final RegistryObject<ColoredTorchBlock> PROMENADE_TORCH = registerBlockWithItem("promenade_torch", () -> new ColoredTorchBlock(BlockBehaviour.Properties.copy(Blocks.TORCH).instabreak().lightLevel(state -> 15).noCollission()));
    public static final RegistryObject<ColoredTorchBlock> RAMPARTS_TORCH = registerBlockWithItem("ramparts_torch", () -> new ColoredTorchBlock(BlockBehaviour.Properties.copy(Blocks.TORCH).instabreak().lightLevel(state -> 15).noCollission()));
    public static final RegistryObject<SkeletonDecorationBlock> HANGED_SKELETON = registerBlock("hanged_skeleton", () -> new SkeletonDecorationBlock(BlockBehaviour.Properties.copy(Blocks.BONE_BLOCK).noCollission().strength(0.5F)));
    public static final RegistryObject<SkeletonDecorationBlock> SKELETON = registerBlockWithItem("skeleton", () -> new SkeletonDecorationBlock(BlockBehaviour.Properties.copy(HANGED_SKELETON.get()).dropsLike(HANGED_SKELETON.get()), HANGED_SKELETON.get()));
    public static final RegistryObject<SkeletonDecorationBlock> HANGED_CORPSE = registerBlock("hanged_corpse", () -> new SkeletonDecorationBlock(BlockBehaviour.Properties.copy(Blocks.DIRT).noCollission().strength(0.5F).sound(net.minecraft.world.level.block.SoundType.MUD).randomTicks()));
    public static final RegistryObject<SkeletonDecorationBlock> CORPSE = registerBlockWithItem("corpse", () -> new SkeletonDecorationBlock(BlockBehaviour.Properties.copy(HANGED_CORPSE.get()).dropsLike(HANGED_CORPSE.get()).randomTicks(), HANGED_CORPSE.get()));
    public static final RegistryObject<SkeletonDecorationBlock> HANGED_ROTTING_CORPSE = registerBlock("hanged_rotting_corpse", () -> new SkeletonDecorationBlock(BlockBehaviour.Properties.copy(HANGED_CORPSE.get()).randomTicks()));
    public static final RegistryObject<SkeletonDecorationBlock> ROTTING_CORPSE = registerBlockWithItem("rotting_corpse", () -> new SkeletonDecorationBlock(BlockBehaviour.Properties.copy(HANGED_CORPSE.get()).dropsLike(HANGED_ROTTING_CORPSE.get()).randomTicks(), HANGED_ROTTING_CORPSE.get()));
    public static final RegistryObject<DecorativeStatueBlock> KING_STATUE = registerBlockWithItem("king_statue", () -> new DecorativeStatueBlock(BlockBehaviour.Properties.of().strength(5.0F).noCollission().noOcclusion()));
    public static final RegistryObject<FlagPoleBlock> FLAG_POLE = registerBlockWithItem("flag_pole", () -> new FlagPoleBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<FlagBlock> KINGS_CREST_FLAG = registerFlag("kings_crest", false);
    public static final RegistryObject<FlagBlock> TORN_KINGS_CREST_FLAG = registerFlag("torn_kings_crest", false);
    public static final RegistryObject<FlagBlock> PROMENADE_OF_THE_CONDEMNED_FLAG = registerFlag("promenade_of_the_condemned", false);
    public static final RegistryObject<FlagBlock> RAMPARTS_FLAG = registerFlag("ramparts", false);
    public static final RegistryObject<FlagBlock> INSUFFERABLE_CRYPT_FLAG = registerFlag("insufferable_crypt", false);
    public static final RegistryObject<FlagBlock> BLACK_BRIDGE_FLAG = registerFlag("black_bridge", false);
    public static final Map<String, RegistryObject<FlagBlock>> RIBBON_FLAGS = ORDERED_COLORS.stream().collect(Collectors.toMap(color -> color, color -> registerFlag(color + "_ribbon", false)));
    public static final Map<String, RegistryObject<FlagBlock>> LARGE_RIBBON_FLAGS = ORDERED_COLORS.stream().collect(Collectors.toMap(color -> color, color -> registerFlag("large_" + color + "_ribbon", true)));
    public static final RegistryObject<BarrierRuneBlock> BARRIER_RUNE = registerBlockWithItem("barrier_rune", () -> new BarrierRuneBlock(BlockBehaviour.Properties.copy(Blocks.BARRIER).noCollission(), false));
    public static final RegistryObject<BarrierRuneBlock> SOLID_BARRIER = registerBlockWithItem("solid_barrier_rune", () -> new BarrierRuneBlock(BlockBehaviour.Properties.copy(Blocks.BARRIER), true));
    public static final RegistryObject<ConditionalBarrierBlock> CONDITIONAL_BARRIER = registerBlock("conditional_barrier", () -> new ConditionalBarrierBlock(BlockBehaviour.Properties.copy(Blocks.BARRIER)));
    public static final RegistryObject<ReturnStoneBlock> RETURN_STONE = registerBlockWithItem("return_stone", () -> new ReturnStoneBlock(BlockBehaviour.Properties.copy(Blocks.BEDROCK).lightLevel(state -> 7)));
    public static final RegistryObject<Block> CONJUNCTIVIUS_BOX = registerBlock("conjunctivius_box", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BEDROCK).noLootTable()));
    public static final RegistryObject<Block> CONCIERGE_BOX = registerBlock("concierge_box", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BEDROCK).noLootTable()));
    public static final RegistryObject<Block> BEAM_PLACER = registerBlock("beam_placer", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BEDROCK).noLootTable()));
    public static final RegistryObject<Block> SPAWNER_RUNE = registerBlock("spawner_rune", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BARRIER).noCollission().noOcclusion().noLootTable()));
    public static final RegistryObject<BarrierControllerBlock> BOSS_BARRIER_CONTROLLER = registerBlock("boss_barrier_controller", () -> new BarrierControllerBlock(BlockBehaviour.Properties.copy(Blocks.BARRIER).noCollission().noOcclusion(), BarrierControllerBlock::bossPredicate));
    public static final RegistryObject<BarrierControllerBlock> BOSS_ENTRY_BARRIER_CONTROLLER = registerBlock("boss_entry_barrier_controller", () -> new BarrierControllerBlock(BlockBehaviour.Properties.copy(Blocks.BARRIER).noCollission().noOcclusion(), BarrierControllerBlock::bossEntryPredicate));
    public static final RegistryObject<BarrierControllerBlock> PLAYER_BARRIER_CONTROLLER = registerBlock("player_barrier_controller", () -> new BarrierControllerBlock(BlockBehaviour.Properties.copy(Blocks.BARRIER).noCollission().noOcclusion(), BarrierControllerBlock::playerPredicate));
    public static final RegistryObject<Block> TELEPORTER_CORE = registerBlock("teleporter_core", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BEDROCK).noCollission().noOcclusion().lightLevel(state -> 8)));
    public static final RegistryObject<TeleporterFrameBlock> TELEPORTER_FRAME = registerBlock("teleporter_frame", () -> new TeleporterFrameBlock(BlockBehaviour.Properties.copy(Blocks.BEDROCK).noOcclusion().lightLevel(state -> 8)));
    public static final RegistryObject<Block> DOORWAY_FRAME = registerBlock("doorway_frame", () -> new Block(BlockBehaviour.Properties.copy(Blocks.NETHER_PORTAL).strength(10.0F, 1200.0F).noOcclusion().noLootTable()));
    public static final RegistryObject<Block> UNBREAKABLE_DOORWAY_FRAME = registerBlock("unbreakable_doorway_frame", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BEDROCK).noOcclusion().noLootTable()));
    public static final RegistryObject<DoorwayPortalBlock> OVERWORLD_DOORWAY = registerBlock("overworld_doorway", () -> new DoorwayPortalBlock(doorwayProperties(), DoorwayPortalBlock.DoorwayType.OVERWORLD));
    public static final RegistryObject<DoorwayPortalBlock> PRISON_DOORWAY = registerBlockWithItem("prison_doorway", () -> new DoorwayPortalBlock(doorwayProperties(), DoorwayPortalBlock.DoorwayType.PRISON));
    public static final RegistryObject<DoorwayPortalBlock> PROMENADE_DOORWAY = registerBlockWithItem("promenade_doorway", () -> new DoorwayPortalBlock(doorwayProperties(), DoorwayPortalBlock.DoorwayType.PROMENADE));
    public static final RegistryObject<DoorwayPortalBlock> INSUFFERABLE_CRYPT_DOORWAY = registerBlockWithItem("insufferable_crypt_doorway", () -> new DoorwayPortalBlock(doorwayProperties(), DoorwayPortalBlock.DoorwayType.INSUFFERABLE_CRYPT));
    public static final RegistryObject<DoorwayPortalBlock> RAMPARTS_DOORWAY = registerBlockWithItem("ramparts_doorway", () -> new DoorwayPortalBlock(doorwayProperties(), DoorwayPortalBlock.DoorwayType.RAMPARTS));
    public static final RegistryObject<DoorwayPortalBlock> BLACK_BRIDGE_DOORWAY = registerBlockWithItem("black_bridge_doorway", () -> new DoorwayPortalBlock(doorwayProperties(), DoorwayPortalBlock.DoorwayType.BLACK_BRIDGE));
    public static final RegistryObject<RiftBlock> RIFT = registerBlock("rift", () -> new RiftBlock(BlockBehaviour.Properties.copy(Blocks.BARRIER).noOcclusion()));
    public static final RegistryObject<Block> PRISON_STONE = registerBlockWithItem("prison_stone", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<StairBlock> PRISON_STONE_STAIRS = registerStairs("prison_stone_stairs", Blocks.STONE);
    public static final RegistryObject<SlabBlock> PRISON_STONE_SLAB = registerSlab("prison_stone_slab", Blocks.STONE);
    public static final RegistryObject<WallBlock> PRISON_STONE_WALL = registerWall("prison_stone_wall", Blocks.STONE);
    public static final RegistryObject<ButtonBlock> PRISON_STONE_BUTTON = registerButton("prison_stone_button", Blocks.STONE);
    public static final RegistryObject<PressurePlateBlock> PRISON_STONE_PRESSURE_PLATE = registerPressurePlate("prison_stone_pressure_plate", Blocks.STONE);
    public static final RegistryObject<Block> PRISON_COBBLESTONE = registerBlockWithItem("prison_cobblestone", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBBLESTONE)));
    public static final RegistryObject<StairBlock> PRISON_COBBLESTONE_STAIRS = registerStairs("prison_cobblestone_stairs", Blocks.COBBLESTONE);
    public static final RegistryObject<SlabBlock> PRISON_COBBLESTONE_SLAB = registerSlab("prison_cobblestone_slab", Blocks.COBBLESTONE);
    public static final RegistryObject<WallBlock> PRISON_COBBLESTONE_WALL = registerWall("prison_cobblestone_wall", Blocks.COBBLESTONE);
    public static final RegistryObject<Block> PRISON_BRICKS = registerBlockWithItem("prison_bricks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
    public static final RegistryObject<StairBlock> PRISON_BRICK_STAIRS = registerStairs("prison_brick_stairs", Blocks.STONE_BRICKS);
    public static final RegistryObject<SlabBlock> PRISON_BRICK_SLAB = registerSlab("prison_brick_slab", Blocks.STONE_BRICKS);
    public static final RegistryObject<WallBlock> PRISON_BRICK_WALL = registerWall("prison_brick_wall", Blocks.STONE_BRICKS);
    public static final RegistryObject<Block> SMALL_PRISON_BRICKS = registerBlockWithItem("small_prison_bricks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
    public static final RegistryObject<StairBlock> SMALL_PRISON_BRICK_STAIRS = registerStairs("small_prison_brick_stairs", Blocks.STONE_BRICKS);
    public static final RegistryObject<SlabBlock> SMALL_PRISON_BRICK_SLAB = registerSlab("small_prison_brick_slab", Blocks.STONE_BRICKS);
    public static final RegistryObject<WallBlock> SMALL_PRISON_BRICK_WALL = registerWall("small_prison_brick_wall", Blocks.STONE_BRICKS);
    public static final RegistryObject<Block> CRACKED_PRISON_BRICKS = registerBlockWithItem("cracked_prison_bricks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
    public static final RegistryObject<StairBlock> CRACKED_PRISON_BRICK_STAIRS = registerStairs("cracked_prison_brick_stairs", Blocks.STONE_BRICKS);
    public static final RegistryObject<SlabBlock> CRACKED_PRISON_BRICK_SLAB = registerSlab("cracked_prison_brick_slab", Blocks.STONE_BRICKS);
    public static final RegistryObject<WallBlock> CRACKED_PRISON_BRICK_WALL = registerWall("cracked_prison_brick_wall", Blocks.STONE_BRICKS);
    public static final RegistryObject<Block> BLOOMROCK = registerBlockWithItem("bloomrock", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<StairBlock> BLOOMROCK_STAIRS = registerStairs("bloomrock_stairs", Blocks.STONE);
    public static final RegistryObject<SlabBlock> BLOOMROCK_SLAB = registerSlab("bloomrock_slab", Blocks.STONE);
    public static final RegistryObject<WallBlock> BLOOMROCK_WALL = registerWall("bloomrock_wall", Blocks.STONE);
    public static final RegistryObject<Block> BLOOMROCK_BRICKS = registerBlockWithItem("bloomrock_bricks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
    public static final RegistryObject<StairBlock> BLOOMROCK_BRICK_STAIRS = registerStairs("bloomrock_brick_stairs", Blocks.STONE_BRICKS);
    public static final RegistryObject<SlabBlock> BLOOMROCK_BRICK_SLAB = registerSlab("bloomrock_brick_slab", Blocks.STONE_BRICKS);
    public static final RegistryObject<WallBlock> BLOOMROCK_BRICK_WALL = registerWall("bloomrock_brick_wall", Blocks.STONE_BRICKS);
    public static final RegistryObject<Block> CRACKED_BLOOMROCK_BRICKS = registerBlockWithItem("cracked_bloomrock_bricks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
    public static final RegistryObject<StairBlock> CRACKED_BLOOMROCK_BRICK_STAIRS = registerStairs("cracked_bloomrock_brick_stairs", Blocks.STONE_BRICKS);
    public static final RegistryObject<SlabBlock> CRACKED_BLOOMROCK_BRICK_SLAB = registerSlab("cracked_bloomrock_brick_slab", Blocks.STONE_BRICKS);
    public static final RegistryObject<WallBlock> CRACKED_BLOOMROCK_BRICK_WALL = registerWall("cracked_bloomrock_brick_wall", Blocks.STONE_BRICKS);
    public static final RegistryObject<Block> BLOOMROCK_TILES = registerBlockWithItem("bloomrock_tiles", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));
    public static final RegistryObject<StairBlock> BLOOMROCK_TILE_STAIRS = registerStairs("bloomrock_tile_stairs", Blocks.STONE_BRICKS);
    public static final RegistryObject<SlabBlock> BLOOMROCK_TILE_SLAB = registerSlab("bloomrock_tile_slab", Blocks.STONE_BRICKS);
    public static final RegistryObject<WallBlock> BLOOMROCK_TILE_WALL = registerWall("bloomrock_tile_wall", Blocks.STONE_BRICKS);

    private MineCellsBlocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, java.util.function.Supplier<T> supplier) {
        return BLOCKS.register(name, supplier);
    }

    private static <T extends Block> RegistryObject<T> registerBlockWithItem(String name, java.util.function.Supplier<T> supplier) {
        RegistryObject<T> block = registerBlock(name, supplier);
        MineCellsItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static RegistryObject<FlagBlock> registerFlag(String name, boolean large) {
        RegistryObject<FlagBlock> block = registerBlock(name + "_flag", () -> new FlagBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_BANNER).noOcclusion(), name, large));
        MineCellsItems.ITEMS.register(name + "_flag", () -> new FlagBlockItem(block.get(), new Item.Properties()));
        FLAG_BLOCKS.add(block);
        return block;
    }

    private static RegistryObject<StairBlock> registerStairs(String name, Block base) {
        return registerBlockWithItem(name, () -> new StairBlock(base.defaultBlockState(), BlockBehaviour.Properties.copy(base)));
    }

    private static RegistryObject<SlabBlock> registerSlab(String name, Block base) {
        return registerBlockWithItem(name, () -> new SlabBlock(BlockBehaviour.Properties.copy(base)));
    }

    private static RegistryObject<WallBlock> registerWall(String name, Block base) {
        return registerBlockWithItem(name, () -> new WallBlock(BlockBehaviour.Properties.copy(base)));
    }

    private static RegistryObject<ButtonBlock> registerButton(String name, Block base) {
        return registerBlockWithItem(name, () -> new ButtonBlock(BlockBehaviour.Properties.copy(base).noCollission().strength(0.5F), BlockSetType.STONE, 20, false));
    }

    private static RegistryObject<PressurePlateBlock> registerPressurePlate(String name, Block base) {
        return registerBlockWithItem(name, () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.MOBS, BlockBehaviour.Properties.copy(base).noCollission().strength(0.5F), BlockSetType.STONE));
    }

    private static BlockBehaviour.Properties doorwayProperties() {
        return BlockBehaviour.Properties.copy(Blocks.NETHER_PORTAL)
            .strength(10.0F, 1200.0F)
            .noOcclusion()
            .lightLevel(state -> 8);
    }

    private static final class NoOpTreeGrower extends AbstractTreeGrower {
        private static final NoOpTreeGrower INSTANCE = new NoOpTreeGrower();

        @Nullable
        @Override
        protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hasFlowers) {
            return null;
        }
    }
}
