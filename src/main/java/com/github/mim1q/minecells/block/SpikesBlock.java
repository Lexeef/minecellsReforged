package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import com.github.mim1q.minecells.util.ModelUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;

public class SpikesBlock extends Block {
    public static final DirectionProperty FACING = DirectionProperty.create("facing");
    public static final BooleanProperty BLOODY = BooleanProperty.create("bloody");

    private static final VoxelShape SHAPE_BOTTOM = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    private static final VoxelShape SHAPE_TOP = Block.box(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_NORTH = Block.box(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);

    public SpikesBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(BLOODY, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, BLOODY);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MineCellsStatusEffects.BLEEDING.get(), 20 * 10 + 1, 0, false, false, true));
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1, false, false, true));
            livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0, false, false, true));
            if (livingEntity.tickCount % 10 == 0) {
                livingEntity.hurt(MineCellsDamageSource.BLEEDING.get(level, null), 1.0F);
            }
            if (!level.isClientSide && level.getRandom().nextFloat() < 0.01F) {
                level.setBlock(pos, state.setValue(BLOODY, true), 3);
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.GLASS_BOTTLE) && state.getValue(BLOODY)) {
            if (!level.isClientSide) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                player.addItem(new ItemStack(MineCellsItems.BLOOD_BOTTLE.get()));
                level.setBlock(pos, state.setValue(BLOODY, false), 3);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        return switch (direction) {
            case DOWN -> SHAPE_TOP;
            case UP -> SHAPE_BOTTOM;
            default -> ModelUtils.rotateShape(Direction.NORTH, direction, SHAPE_NORTH);
        };
    }
}
