package com.github.mim1q.minecells.block.portal;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.DoorwayPortalBlockEntity;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.world.DoorwayPortalService;
import com.github.mim1q.minecells.util.ModelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class DoorwayPortalBlock extends BaseEntityBlock implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape COLLISION_SHAPE = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);

    private final DoorwayType type;

    public DoorwayPortalBlock(Properties properties, DoorwayType type) {
        super(properties);
        this.type = type;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public DoorwayType getType() {
        return type;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.getBlockEntity(pos) instanceof DoorwayPortalBlockEntity doorway) {
            doorway.ensureBoundPosition();
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }
        if (level.getBlockEntity(pos) instanceof DoorwayPortalBlockEntity doorway) {
            return DoorwayPortalService.useDoorway(serverPlayer, serverPlayer.serverLevel(), pos, this, doorway);
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DoorwayPortalBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return ModelUtils.rotateShape(Direction.NORTH, state.getValue(FACING), SHAPE);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (entity instanceof Player player && level.getBlockEntity(pos) instanceof DoorwayPortalBlockEntity doorway && !doorway.canPlayerEnter(player)) {
                return getShape(state, level, pos, context);
            }
        }
        return ModelUtils.rotateShape(Direction.NORTH, state.getValue(FACING), COLLISION_SHAPE);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (level.isClientSide || !(entity instanceof net.minecraft.server.level.ServerPlayer serverPlayer)) {
            return;
        }
        if (!(level.getBlockEntity(pos) instanceof DoorwayPortalBlockEntity doorway)) {
            return;
        }
        AABB box = ModelUtils.rotateShape(Direction.NORTH, state.getValue(FACING), COLLISION_SHAPE).bounds().move(pos).inflate(0.01D);
        if (entity.getBoundingBox().intersects(box)) {
            DoorwayPortalService.useDoorway(serverPlayer, serverPlayer.serverLevel(), pos, this, doorway);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        Direction facing = state.getValue(FACING);
        Vec3 sideways = Vec3.atLowerCornerOf(facing.getClockWise().getNormal());
        Vec3 center = Vec3.atCenterOf(pos).add(Vec3.atLowerCornerOf(facing.getOpposite().getNormal()).scale(0.48D));
        for (int i = 0; i < 3; i++) {
            double dx = sideways.x * (random.nextDouble() * 1.4D - 0.7D);
            double dy = random.nextDouble() * 2.4D - 1.5D;
            double dz = sideways.z * (random.nextDouble() * 1.4D - 0.7D);
            Vec3 particlePos = center.add(dx, dy, dz);
            level.addParticle(
                MineCellsParticles.SPECKLE.get().get(type.getColor()),
                particlePos.x,
                particlePos.y,
                particlePos.z,
                (random.nextDouble() * 0.02D + 0.03D) * facing.getStepX(),
                random.nextDouble() * 0.02D - 0.01D,
                (random.nextDouble() * 0.02D + 0.03D) * facing.getStepZ()
            );
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == MineCellsBlockEntities.DOORWAY.get() ? (entityLevel, entityPos, entityState, blockEntity) -> {
            if (entityLevel.isClientSide && entityLevel.getGameTime() % 40L == 0L && blockEntity instanceof DoorwayPortalBlockEntity doorway) {
                doorway.updateClientVisited();
            }
        } : null;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    public enum DoorwayType implements StringRepresentable {
        OVERWORLD("overworld", 0x8EF96D, new ResourceLocation("minecraft", "overworld")),
        PRISON("prison", 0x54EF88, MineCells.id("prison")),
        PROMENADE("promenade", 0x93FFF7, MineCells.id("promenade")),
        INSUFFERABLE_CRYPT("insufferable_crypt", 0xFF4CF4, MineCells.id("insufferable_crypt")),
        RAMPARTS("ramparts", 0xFFC540, MineCells.id("ramparts")),
        BLACK_BRIDGE("black_bridge", 0x623CC9, MineCells.id("black_bridge"));

        private final String serializedName;
        private final int color;
        private final ResourceLocation dimensionId;

        DoorwayType(String serializedName, int color, ResourceLocation dimensionId) {
            this.serializedName = serializedName;
            this.color = color;
            this.dimensionId = dimensionId;
        }

        public int getColor() {
            return color;
        }

        public ResourceLocation dimensionId() {
            return dimensionId;
        }

        @Override
        public String getSerializedName() {
            return serializedName;
        }
    }
}
