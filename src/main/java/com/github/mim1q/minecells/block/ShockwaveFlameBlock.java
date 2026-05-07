package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.network.MineCellsNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShockwaveFlameBlock extends Block {
    private final boolean playerPlaced;

    public ShockwaveFlameBlock(Properties properties, boolean playerPlaced) {
        super(properties);
        this.playerPlaced = playerPlaced;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState downState = level.getBlockState(pos.below());
        BlockState posState = level.getBlockState(pos);
        return posState.canBeReplaced()
            && posState.getFluidState().isEmpty()
            && (
                downState.isFaceSturdy(level, pos.below(), Direction.UP, SupportType.CENTER)
                    || downState.isCollisionShapeFullBlock(level, pos.below())
                    || downState.is(BlockTags.STAIRS)
            );
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide && !oldState.is(state.getBlock()) && level instanceof ServerLevel serverLevel) {
            MineCellsNetwork.sendShockwaveClientEvent(serverLevel, state.getBlock(), pos, false);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        boolean fireImmune = entity.fireImmune()
            || entity instanceof LivingEntity living && living.hasEffect(MobEffects.FIRE_RESISTANCE);
        if (!fireImmune && new AABB(pos).move(0.0D, -0.75D, 0.0D).intersects(entity.getBoundingBox())) {
            entity.setSecondsOnFire(playerPlaced ? 4 : 2);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        MineCellsNetwork.sendShockwaveClientEvent(level, state.getBlock(), pos, true);
        if (random.nextFloat() < 0.1F && level.getBlockState(pos.below()).isFlammable(level, pos.below(), net.minecraft.core.Direction.UP)) {
            level.setBlock(pos, Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
            return;
        }
        level.removeBlock(pos, false);
    }
}
