package com.github.mim1q.minecells.structure.grid;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class MineCellsStructurePoolBasedGenerator {
    private static final Logger LOGGER = LogUtils.getLogger();

    private MineCellsStructurePoolBasedGenerator() {
    }

    public static Optional<Structure.GenerationStub> generate(
        Structure.GenerationContext context,
        Holder<StructureTemplatePool> structurePool,
        int size,
        BlockPos pos,
        Rotation rotation
    ) {
        RegistryAccess registryAccess = context.registryAccess();
        StructureTemplateManager structureTemplateManager = context.structureTemplateManager();
        RandomSource random = context.random();
        Registry<StructureTemplatePool> registry = registryAccess.registryOrThrow(Registries.TEMPLATE_POOL);
        StructureTemplatePool startPool = structurePool.value();
        StructurePoolElement element = startPool.getRandomTemplate(random);
        if (element == EmptyPoolElement.INSTANCE) {
            return Optional.empty();
        }

        PoolElementStructurePiece firstPiece = new PoolElementStructurePiece(
            structureTemplateManager,
            element,
            pos,
            element.getGroundLevelDelta(),
            rotation,
            element.getBoundingBox(structureTemplateManager, pos, rotation)
        );
        BoundingBox box = firstPiece.getBoundingBox();
        int centerX = (box.maxX() + box.minX()) / 2;
        int centerZ = (box.maxZ() + box.minZ()) / 2;
        int centerY = pos.getY();

        return Optional.of(new Structure.GenerationStub(new BlockPos(centerX, centerY, centerZ), builder -> {
            List<PoolElementStructurePiece> pieces = Lists.newArrayList();
            pieces.add(firstPiece);
            if (size > 0) {
                AABB aabb = new AABB(
                    centerX - 128, centerY - 128, centerZ - 128,
                    centerX + 129, centerY + 129, centerZ + 129
                );
                VoxelShape pieceShape = Shapes.join(Shapes.create(aabb), Shapes.create(AABB.of(box)), BooleanOp.ONLY_FIRST);
                generate(context.randomState(), size, context, registry, firstPiece, pieces, pieceShape);
            }
            pieces.forEach(builder::addPiece);
        }));
    }

    private static void generate(
        RandomState randomState,
        int maxSize,
        Structure.GenerationContext context,
        Registry<StructureTemplatePool> structurePoolRegistry,
        PoolElementStructurePiece firstPiece,
        List<PoolElementStructurePiece> pieces,
        VoxelShape pieceShape
    ) {
        StructurePoolGenerator generator = new StructurePoolGenerator(
            structurePoolRegistry,
            maxSize,
            context,
            pieces,
            context.random()
        );
        generator.structurePieces.addLast(new ShapedPoolStructurePiece(firstPiece, new MutableObject<>(pieceShape), 0));

        while (!generator.structurePieces.isEmpty()) {
            ShapedPoolStructurePiece shapedPiece = generator.structurePieces.removeFirst();
            generator.generatePiece(shapedPiece.piece, shapedPiece.pieceShape, shapedPiece.currentSize, context.heightAccessor(), randomState);
        }
    }

    private static final class StructurePoolGenerator {
        private final Registry<StructureTemplatePool> registry;
        private final int maxSize;
        private final Structure.GenerationContext context;
        private final List<? super PoolElementStructurePiece> children;
        private final RandomSource random;
        private final StructureTemplateManager structureTemplateManager;
        final Deque<ShapedPoolStructurePiece> structurePieces = Queues.newArrayDeque();

        StructurePoolGenerator(
            Registry<StructureTemplatePool> registry,
            int maxSize,
            Structure.GenerationContext context,
            List<? super PoolElementStructurePiece> children,
            RandomSource random
        ) {
            this.registry = registry;
            this.maxSize = maxSize;
            this.context = context;
            this.children = children;
            this.random = random;
            this.structureTemplateManager = context.structureTemplateManager();
        }

        void generatePiece(
            PoolElementStructurePiece piece,
            MutableObject<VoxelShape> pieceShape,
            int depth,
            LevelHeightAccessor heightAccessor,
            RandomState randomState
        ) {
            StructurePoolElement poolElement = piece.getElement();
            BlockPos blockPos = piece.getPosition();
            Rotation rotation = piece.getRotation();
            StructureTemplatePool.Projection projection = poolElement.getProjection();
            boolean rigid = projection == StructureTemplatePool.Projection.RIGID;
            MutableObject<VoxelShape> interiorShape = new MutableObject<>();
            BoundingBox pieceBox = piece.getBoundingBox();
            int pieceMinY = pieceBox.minY();
            Iterator<StructureTemplate.StructureBlockInfo> iterator = poolElement.getShuffledJigsawBlocks(
                this.structureTemplateManager, blockPos, rotation, this.random
            ).iterator();

            label92:
            while (iterator.hasNext()) {
                StructureTemplate.StructureBlockInfo jigsaw = iterator.next();
                Direction direction = JigsawBlock.getFrontFacing(jigsaw.state());
                BlockPos jigsawPos = jigsaw.pos();
                BlockPos connectPos = jigsawPos.relative(direction);
                int relativeY = jigsawPos.getY() - pieceMinY;
                int terrainHeight = -1;
                ResourceLocation poolId = ResourceLocation.tryParse(jigsaw.nbt().getString("pool"));
                if (poolId == null) {
                    continue;
                }

                Optional<Holder.Reference<StructureTemplatePool>> optional = this.registry.getHolder(net.minecraft.resources.ResourceKey.create(Registries.TEMPLATE_POOL, poolId));
                if (optional.isEmpty()) {
                    LOGGER.warn("Empty or non-existent pool: {}", poolId);
                    continue;
                }

                Holder<StructureTemplatePool> nextPoolHolder = optional.get();
                StructureTemplatePool nextPool = nextPoolHolder.value();
                Holder<StructureTemplatePool> fallback = nextPool.getFallback();
                if (nextPool.size() == 0 && !fallback.is(net.minecraft.data.worldgen.Pools.EMPTY)) {
                    LOGGER.warn("Empty or non-existent pool: {}", poolId);
                    continue;
                }
                if (fallback.value().size() == 0 && !fallback.is(net.minecraft.data.worldgen.Pools.EMPTY)) {
                    LOGGER.warn("Empty or non-existent fallback pool: {}", fallback.unwrapKey().orElseThrow().location());
                    continue;
                }

                boolean insideCurrentBox = pieceBox.isInside(connectPos);
                MutableObject<VoxelShape> joinShape;
                if (insideCurrentBox) {
                    joinShape = interiorShape;
                    if (interiorShape.getValue() == null) {
                        interiorShape.setValue(Shapes.create(AABB.of(pieceBox)));
                    }
                } else {
                    joinShape = pieceShape;
                }

                List<StructurePoolElement> elements = Lists.newArrayList();
                if (depth != this.maxSize) {
                    elements.addAll(nextPool.getShuffledTemplates(this.random));
                }
                elements.addAll(fallback.value().getShuffledTemplates(this.random));

                for (StructurePoolElement nextElement : elements) {
                    if (nextElement == EmptyPoolElement.INSTANCE) {
                        break;
                    }

                    for (Rotation nextRotation : Rotation.getShuffled(this.random)) {
                        List<StructureTemplate.StructureBlockInfo> jigsawBlocks = nextElement.getShuffledJigsawBlocks(
                            this.structureTemplateManager,
                            BlockPos.ZERO,
                            nextRotation,
                            this.random
                        );

                        for (StructureTemplate.StructureBlockInfo nextJigsaw : jigsawBlocks) {
                            if (!JigsawBlock.canAttach(jigsaw, nextJigsaw)) {
                                continue;
                            }

                            BlockPos nextJigsawPos = nextJigsaw.pos();
                            BlockPos pieceOrigin = connectPos.subtract(nextJigsawPos);
                            BoundingBox nextBox = nextElement.getBoundingBox(this.structureTemplateManager, pieceOrigin, nextRotation);
                            int nextMinY = nextBox.minY();
                            StructureTemplatePool.Projection nextProjection = nextElement.getProjection();
                            boolean nextRigid = nextProjection == StructureTemplatePool.Projection.RIGID;
                            int nextY = nextJigsawPos.getY();
                            int deltaY = relativeY - nextY + direction.getStepY();
                            int baseY;

                            if (rigid && nextRigid) {
                                baseY = pieceMinY + deltaY;
                            } else {
                                if (terrainHeight == -1) {
                                    terrainHeight = this.context.chunkGenerator().getFirstFreeHeight(
                                        jigsawPos.getX(),
                                        jigsawPos.getZ(),
                                        Heightmap.Types.WORLD_SURFACE_WG,
                                        heightAccessor,
                                        randomState
                                    );
                                }
                                baseY = terrainHeight - nextY;
                            }

                            int yOffset = baseY - nextMinY;
                            BoundingBox movedBox = nextBox.moved(0, yOffset, 0);
                            BlockPos movedOrigin = pieceOrigin.offset(0, yOffset, 0);

                            if (Shapes.joinIsNotEmpty(
                                joinShape.getValue(),
                                Shapes.create(AABB.of(movedBox).deflate(0.25D)),
                                BooleanOp.ONLY_SECOND
                            )) {
                                continue;
                            }

                            joinShape.setValue(Shapes.join(joinShape.getValue(), Shapes.create(AABB.of(movedBox)), BooleanOp.ONLY_FIRST));
                            int groundLevelDelta = piece.getGroundLevelDelta();
                            int nextGroundLevelDelta = nextRigid ? groundLevelDelta - deltaY : nextElement.getGroundLevelDelta();
                            PoolElementStructurePiece nextPiece = new PoolElementStructurePiece(
                                this.structureTemplateManager,
                                nextElement,
                                movedOrigin,
                                nextGroundLevelDelta,
                                nextRotation,
                                movedBox
                            );

                            int junctionY;
                            if (rigid) {
                                junctionY = pieceMinY + relativeY;
                            } else if (nextRigid) {
                                junctionY = baseY + nextY;
                            } else {
                                if (terrainHeight == -1) {
                                    terrainHeight = this.context.chunkGenerator().getFirstFreeHeight(
                                        jigsawPos.getX(),
                                        jigsawPos.getZ(),
                                        Heightmap.Types.WORLD_SURFACE_WG,
                                        heightAccessor,
                                        randomState
                                    );
                                }
                                junctionY = terrainHeight + deltaY / 2;
                            }

                            piece.addJunction(new JigsawJunction(connectPos.getX(), junctionY - relativeY + groundLevelDelta, connectPos.getZ(), deltaY, nextProjection));
                            nextPiece.addJunction(new JigsawJunction(jigsawPos.getX(), junctionY - nextY + nextGroundLevelDelta, jigsawPos.getZ(), -deltaY, projection));
                            this.children.add(nextPiece);
                            if (depth + 1 <= this.maxSize) {
                                this.structurePieces.addLast(new ShapedPoolStructurePiece(nextPiece, joinShape, depth + 1));
                            }
                            continue label92;
                        }
                    }
                }
            }
        }
    }

    private record ShapedPoolStructurePiece(PoolElementStructurePiece piece, MutableObject<VoxelShape> pieceShape, int currentSize) {
    }
}
