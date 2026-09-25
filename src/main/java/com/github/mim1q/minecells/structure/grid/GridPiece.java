package com.github.mim1q.minecells.structure.grid;

import com.github.mim1q.minecells.registry.MineCellsStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

/**
 * Legacy structure-piece type kept only so old world NBT ({@code minecells:grid_generator_piece}) can deserialize.
 * Live generation uses {@link GridBasedStructure} + {@link MineCellsStructurePoolBasedGenerator#collectPieces}.
 * <p>
 * Placement always uses the chunk {@link BoundingBox} — never {@link BoundingBox#infinite()}.
 */
public class GridPiece extends StructurePiece {
    private final RegistryAccess registryAccess;
    private final StructureTemplateManager templateManager;
    private final ResourceLocation template;
    private final Rotation rotation;
    private final BlockPos pos;
    private final int size;

    public GridPiece(Structure.GenerationContext context, ResourceLocation poolId, BlockPos pos, Rotation rotation, int size) {
        super(MineCellsStructurePieceTypes.GRID_PIECE.get(), 0, BoundingBox.fromCorners(pos, pos.offset(size, size, size)));
        this.templateManager = context.structureTemplateManager();
        this.registryAccess = context.registryAccess();
        this.pos = pos;
        this.rotation = rotation;
        this.size = size;
        this.template = poolId;
        if (context.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL).get(poolId) == null) {
            throw new IllegalStateException("Pool not found: " + poolId);
        }
    }

    public GridPiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(MineCellsStructurePieceTypes.GRID_PIECE.get(), tag);
        this.templateManager = context.structureTemplateManager();
        this.registryAccess = context.registryAccess();
        this.pos = new BlockPos(tag.getInt("PosX"), tag.getInt("PosY"), tag.getInt("PosZ"));
        this.rotation = Rotation.valueOf(tag.getString("Rot"));
        this.size = tag.getInt("Size");
        this.template = new ResourceLocation(tag.getString("Template"));
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putInt("PosX", pos.getX());
        tag.putInt("PosY", pos.getY());
        tag.putInt("PosZ", pos.getZ());
        tag.putString("Rot", rotation.name());
        tag.putInt("Size", size);
        tag.putString("Template", template.toString());
    }

    @Override
    public void postProcess(
        WorldGenLevel level,
        StructureManager structureManager,
        ChunkGenerator chunkGenerator,
        RandomSource random,
        BoundingBox chunkBox,
        ChunkPos chunkPos,
        BlockPos pivot
    ) {
        Registry<StructureTemplatePool> poolRegistry = registryAccess.registryOrThrow(Registries.TEMPLATE_POOL);
        Holder.Reference<StructureTemplatePool> pool = poolRegistry
            .getHolder(ResourceKey.create(Registries.TEMPLATE_POOL, template))
            .orElse(null);
        if (pool == null) {
            return;
        }

        RandomState randomState = level.getLevel().getChunkSource().randomState();
        BlockPos startingPos = getStartingPos();
        Structure.GenerationContext context = new Structure.GenerationContext(
            registryAccess,
            chunkGenerator,
            chunkGenerator.getBiomeSource(),
            randomState,
            templateManager,
            (int) level.getSeed() + this.pos.hashCode(),
            chunkPos,
            level,
            holder -> true
        );

        for (PoolElementStructurePiece poolPiece : MineCellsStructurePoolBasedGenerator.collectPieces(
            context,
            pool,
            8,
            startingPos,
            rotation
        )) {
            // Chunk-scoped place box only — infinite BB caused cross-chunk voids.
            poolPiece.place(level, structureManager, chunkGenerator, random, chunkBox, startingPos, false);
        }
    }

    private BlockPos getStartingPos() {
        return getStartingPos(pos, rotation, size);
    }

    public static BlockPos getStartingPos(BlockPos pos, Rotation rotation, int size) {
        return switch (rotation) {
            case CLOCKWISE_90 -> new BlockPos(pos.getX() + size - 1, pos.getY(), pos.getZ());
            case CLOCKWISE_180 -> new BlockPos(pos.getX() + size - 1, pos.getY(), pos.getZ() + size - 1);
            case COUNTERCLOCKWISE_90 -> new BlockPos(pos.getX(), pos.getY(), pos.getZ() + size - 1);
            default -> pos;
        };
    }
}
