package com.github.mim1q.minecells.structure.grid;

import com.github.mim1q.minecells.registry.MineCellsStructureTypes;
import com.github.mim1q.minecells.structure.grid.generator.BetterPromenadeGridGenerator;
import com.github.mim1q.minecells.structure.grid.generator.BlackBridgeGridGenerator;
import com.github.mim1q.minecells.structure.grid.generator.PrisonGridGenerator;
import com.github.mim1q.minecells.structure.grid.generator.PromenadeWallGenerator;
import com.github.mim1q.minecells.structure.grid.generator.RampartsGridGenerator;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomData;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomGridGenerator;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import org.slf4j.Logger;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class GridBasedStructure extends Structure {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Codec<GridBasedStructure> PRISON_CODEC = createMultipartGridBasedStructureCodec(
        PrisonGridGenerator::new,
        () -> MineCellsStructureTypes.PRISON.get()
    );

    public static final Codec<GridBasedStructure> PROMENADE_CODEC = createMultipartGridBasedStructureCodec(
        BetterPromenadeGridGenerator::new,
        () -> MineCellsStructureTypes.PROMENADE.get()
    );

    public static final Codec<GridBasedStructure> PROMENADE_WALL_X_CODEC = createGridBasedStructureCodec(
        ctx -> new PromenadeWallGenerator(false),
        () -> MineCellsStructureTypes.PROMENADE_WALL_X.get(),
        ctx -> Math.abs(Math.floorMod(ctx.chunkPos().z, 64)) == 32 && Math.floorMod(ctx.chunkPos().x, 16) == 0
    );
    public static final Codec<GridBasedStructure> PROMENADE_WALL_Z_CODEC = createGridBasedStructureCodec(
        ctx -> new PromenadeWallGenerator(true),
        () -> MineCellsStructureTypes.PROMENADE_WALL_Z.get(),
        ctx -> Math.abs(Math.floorMod(ctx.chunkPos().x, 64)) == 32 && Math.floorMod(ctx.chunkPos().z, 16) == 0
    );
    public static final Codec<GridBasedStructure> RAMPARTS_CODEC = createMultipartGridBasedStructureCodec(
        RampartsGridGenerator::new,
        () -> MineCellsStructureTypes.RAMPARTS.get()
    );

    public static final Codec<GridBasedStructure> BLACK_BRIDGE_CODEC = createMultipartGridBasedStructureCodec(
        BlackBridgeGridGenerator::new,
        () -> MineCellsStructureTypes.BLACK_BRIDGE.get()
    );

    public static Codec<GridBasedStructure> createMultipartGridBasedStructureCodec(
        BiFunction<Integer, Integer, RoomGridGenerator> generatorProvider,
        Supplier<StructureType<?>> typeSupplier
    ) {
        return createMultipartGridBasedStructureCodec(generatorProvider, typeSupplier, -32, -32, 4, 4);
    }

    private final Function<GenerationContext, RoomGridGenerator> generatorProvider;
    private final HeightProvider heightProvider;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final Supplier<StructureType<?>> typeSupplier;
    private final Predicate<GenerationContext> spawnPredicate;

    protected GridBasedStructure(
        StructureSettings settings,
        HeightProvider heightProvider,
        Optional<Heightmap.Types> projectStartToHeightmap,
        Function<GenerationContext, RoomGridGenerator> generatorProvider,
        Supplier<StructureType<?>> typeSupplier,
        Predicate<GenerationContext> spawnPredicate
    ) {
        super(settings);
        this.generatorProvider = generatorProvider;
        this.heightProvider = heightProvider;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.typeSupplier = typeSupplier;
        this.spawnPredicate = spawnPredicate;
    }

    public static Codec<GridBasedStructure> createGridBasedStructureCodec(
        Function<GenerationContext, RoomGridGenerator> generatorProvider,
        Supplier<StructureType<?>> typeSupplier
    ) {
        return createGridBasedStructureCodec(generatorProvider, typeSupplier, ctx -> true);
    }

    public static Codec<GridBasedStructure> createGridBasedStructureCodec(
        Function<GenerationContext, RoomGridGenerator> generatorProvider,
        Supplier<StructureType<?>> typeSupplier,
        Predicate<GenerationContext> spawnPredicate
    ) {
        return RecordCodecBuilder.<GridBasedStructure>mapCodec(instance ->
            instance.group(
                Structure.settingsCodec(instance),
                HeightProvider.CODEC.fieldOf("start_height").forGetter(GridBasedStructure::getHeightProvider),
                Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(GridBasedStructure::getProjectStartToHeightmap)
            ).apply(instance, (settings, heightProvider, projectStartToHeightmap) ->
                new GridBasedStructure(settings, heightProvider, projectStartToHeightmap, generatorProvider, typeSupplier, spawnPredicate)
            )
        ).codec();
    }

    public static Codec<GridBasedStructure> createMultipartGridBasedStructureCodec(
        BiFunction<Integer, Integer, RoomGridGenerator> generatorProvider,
        Supplier<StructureType<?>> typeSupplier,
        int startX,
        int startZ,
        int sizeX,
        int sizeZ
    ) {
        return createGridBasedStructureCodec(
            ctx -> {
                int x = Math.floorMod(ctx.chunkPos().x - startX - 8, 64) / 16;
                int z = Math.floorMod(ctx.chunkPos().z - startZ - 8, 64) / 16;
                return generatorProvider.apply(x, z);
            },
            typeSupplier,
            ctx -> {
                int x = Math.floorMod(ctx.chunkPos().x - startX - 8, 64);
                int z = Math.floorMod(ctx.chunkPos().z - startZ - 8, 64);
                return x % 16 == 0 && z % 16 == 0 && (x / 16) < sizeX && (z / 16) < sizeZ;
            }
        );
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (!this.canSpawn(context)) {
            return Optional.empty();
        }

        ChunkPos chunkPos = context.chunkPos();
        int x = chunkPos.x * 16;
        int z = chunkPos.z * 16;
        int y = this.heightProvider.sample(context.random(), null);
        int heightmapY = this.projectStartToHeightmap.map(
            type -> y + context.chunkGenerator().getFirstFreeHeight(x + 8, z + 8, type, context.heightAccessor(), context.randomState())
        ).orElse(0);
        BlockPos startPos = new BlockPos(x, y + heightmapY, z);

        // Multipart layouts regenerate the whole run and keep only rooms inside this part's 16x16 cell slice,
        // so most parts of a compact layout (e.g. Prison) are legitimately empty: no start for them.
        List<RoomData> roomDataList = List.copyOf(GridPiecesGenerator.generateRoomData(context, this.getGenerator(context)));
        if (roomDataList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new GenerationStub(startPos, builder -> this.addPieces(builder, roomDataList, startPos, context)));
    }

    private void addPieces(
        StructurePiecesBuilder builder,
        List<RoomData> roomDataList,
        BlockPos startPos,
        GenerationContext context
    ) {
        Registry<StructureTemplatePool> poolRegistry = context.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        int totalPieces = 0;
        int filledRooms = 0;
        for (RoomData data : roomDataList) {
            if (Pools.EMPTY.location().equals(data.poolId)) {
                continue;
            }
            filledRooms++;
            Holder.Reference<StructureTemplatePool> pool = poolRegistry
                .getHolder(ResourceKey.create(Registries.TEMPLATE_POOL, data.poolId))
                .orElse(null);
            if (pool == null) {
                LOGGER.warn("Mine Cells grid structure missing template pool: {}", data.poolId);
                continue;
            }

            BlockPos piecePos = data.terrainFit
                ? GridPiecesGenerator.getTerrainFitStart(data, startPos, this.projectStartToHeightmap, context, 16)
                : startPos.offset(data.pos.multiply(16)).offset(data.offset);

            List<PoolElementStructurePiece> pieces = MineCellsStructurePoolBasedGenerator.collectPieces(
                context,
                pool,
                8,
                GridPiece.getStartingPos(piecePos, data.rotation, 16),
                data.rotation
            );
            if (pieces.isEmpty()) {
                LOGGER.warn("Mine Cells room pool expanded to 0 pieces: {} at {}", data.poolId, piecePos);
                continue;
            }
            for (PoolElementStructurePiece piece : pieces) {
                builder.addPiece(piece);
                totalPieces++;
            }
        }
        if (totalPieces == 0 && filledRooms > 0) {
            // Vanilla turns an empty builder into StructureStart.INVALID_START for this chunk (not retried).
            LOGGER.warn("Mine Cells grid structure had {} room(s) but no pool produced pieces (start={}, first pool={})",
                roomDataList.size(), startPos, roomDataList.get(0).poolId);
        }
    }

    @Override
    public StructureType<?> type() {
        return this.typeSupplier.get();
    }

    protected boolean canSpawn(GenerationContext context) {
        return this.spawnPredicate.test(context);
    }

    protected RoomGridGenerator getGenerator(GenerationContext context) {
        return this.generatorProvider.apply(context);
    }

    public HeightProvider getHeightProvider() {
        return this.heightProvider;
    }

    public Optional<Heightmap.Types> getProjectStartToHeightmap() {
        return this.projectStartToHeightmap;
    }
}
