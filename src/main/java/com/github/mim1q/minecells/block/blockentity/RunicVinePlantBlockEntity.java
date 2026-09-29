package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.block.RunicVinePlantBlock;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.util.animation.AnimationProperty;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RunicVinePlantBlockEntity extends MineCellsBlockEntity {
    private static final String REQUIRED_MESSAGE = "block.minecells.runic_vine_plant.message";
    private static final ResourceLocation ADVANCEMENT_ID = MineCells.id("vine_rune");
    private static final int MAX_BLOCKS_ABOVE = 24;

    private int usedTicks = 0;
    private int blocksAbove = 0;
    public final AnimationProperty wobble = new AnimationProperty(0.0F, AnimationProperty::easeInOutQuad);

    public RunicVinePlantBlockEntity(BlockPos pos, BlockState state) {
        super(MineCellsBlockEntities.RUNIC_VINE_PLANT.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) {
            usedTicks = Math.max(0, usedTicks - 1);
            if (usedTicks >= MAX_BLOCKS_ABOVE - 2) {
                wobble.setupTransitionTo(1.0F, 2.0F);
            } else {
                wobble.setupTransitionTo(0.0F, 5.0F);
            }
            return;
        }
        if (blocksAbove <= 0 || blocksAbove >= MAX_BLOCKS_ABOVE || level.getGameTime() % 2 != 0) {
            return;
        }
        BlockPos posAbove = pos.above(blocksAbove);
        BlockState stateAbove = level.getBlockState(posAbove);
        BlockState stateBelow = level.getBlockState(posAbove.below());
        boolean canGrow = (stateAbove.isAir() || stateAbove.is(MineCellsBlocks.RUNIC_VINE_STONE.get()))
            && (stateBelow.is(MineCellsBlocks.RUNIC_VINE_PLANT.get()) || stateBelow.is(MineCellsBlocks.RUNIC_VINE.get()));
        if (!canGrow) {
            blocksAbove = 0;
            return;
        }
        level.playSound(null, posAbove, SoundEvents.WET_GRASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (level instanceof ServerLevel serverLevel) {
            Vec3 particlePos = Vec3.atCenterOf(posAbove);
            serverLevel.sendParticles(MineCellsParticles.SPECKLE.get().get(RunicVinePlantBlock.PARTICLE_COLOR), particlePos.x, particlePos.y, particlePos.z, 10, 0.25D, 0.5D, 0.25D, 0.01D);
        }
        level.destroyBlock(posAbove, false);
        level.setBlockAndUpdate(posAbove, MineCellsBlocks.RUNIC_VINE.get().defaultBlockState());
        blocksAbove++;
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
        usedTicks = MAX_BLOCKS_ABOVE;
        level.playSound(null, pos, SoundEvents.WET_GRASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (state.getValue(RunicVinePlantBlock.ACTIVATED) || level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(MineCellsItems.VINE_RUNE.get())) {
            stack.hurtAndBreak(1, player, user -> user.broadcastBreakEvent(hand));
            blocksAbove = 1;
            level.setBlockAndUpdate(pos, state.setValue(RunicVinePlantBlock.ACTIVATED, true));
            if (player instanceof ServerPlayer serverPlayer && serverPlayer.getServer() != null) {
                Advancement advancement = serverPlayer.getServer().getAdvancements().getAdvancement(ADVANCEMENT_ID);
                if (advancement != null) {
                    serverPlayer.getAdvancements().award(advancement, "vine_rune");
                }
            }
        } else {
            player.displayClientMessage(Component.translatable(REQUIRED_MESSAGE), true);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition).inflate(1.0D, 2.0D, 1.0D);
    }
}
