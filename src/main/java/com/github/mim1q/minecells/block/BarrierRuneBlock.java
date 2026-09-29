package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.registry.MineCellsParticles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

public class BarrierRuneBlock extends BarrierBlock {
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");

    private final boolean solid;

    public BarrierRuneBlock(Properties properties, boolean solid) {
        super(properties);
        this.solid = solid;
        registerDefaultState(stateDefinition.any().setValue(BOTTOM, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BOTTOM);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (solid) {
            return Shapes.block();
        }
        if (context instanceof EntityCollisionContext entityContext && entityContext.getEntity() != null) {
            var entity = entityContext.getEntity();
            if (entity instanceof Projectile || entity instanceof Monster) {
                return Shapes.block();
            }
        }
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        var item = asItem();
        if (item == Blocks.AIR.asItem()) {
            return Shapes.empty();
        }
        return context.isHoldingItem(item) || context.isHoldingItem(Items.DEBUG_STICK) ? Shapes.block() : Shapes.empty();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(BOTTOM, !context.getLevel().getBlockState(context.getClickedPos().below()).is(this));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (!state.getValue(BOTTOM)) {
            return;
        }
        Vec3 particlePos = Vec3.atBottomCenterOf(pos).add(random.nextDouble() - 0.5D, 0.05D, random.nextFloat() - 0.5D);
        level.addParticle(
            MineCellsParticles.SPECKLE.get().get(0x00AAEE),
            particlePos.x,
            particlePos.y,
            particlePos.z,
            0.0D,
            0.01D + random.nextDouble() * 0.05D,
            0.0D
        );
    }
}
