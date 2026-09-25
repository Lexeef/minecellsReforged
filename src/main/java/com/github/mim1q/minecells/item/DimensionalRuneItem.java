package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.block.blockentity.DoorwayPortalBlockEntity;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MineCellsText;
import com.github.mim1q.minecells.world.state.MineCellsData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DimensionalRuneItem extends Item {
    private static final String ONLY_USABLE_IN_OVERWORLD_KEY = "item.minecells.dimensional_rune.only_usable_in_overworld";
    private static final String NOT_VISITED_KEY = "item.minecells.dimensional_rune.not_visited";
    private static final String TOOLTIP_KEY = "item.minecells.dimensional_rune.tooltip";

    private final DoorwayPortalBlock portalBlock;

    public DimensionalRuneItem(Properties properties, DoorwayPortalBlock portalBlock) {
        super(properties);
        this.portalBlock = portalBlock;
    }

    public DoorwayPortalBlock getPortalBlock() {
        return portalBlock;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.dimension().location().equals(DoorwayPortalBlock.DoorwayType.OVERWORLD.dimensionId())) {
            if (context.getPlayer() != null) {
                context.getPlayer().displayClientMessage(Component.translatable(ONLY_USABLE_IN_OVERWORLD_KEY), true);
            }
            return InteractionResult.FAIL;
        }

        if (!(level.getBlockState(context.getClickedPos()).getBlock() instanceof DoorwayPortalBlock doorway) || doorway == portalBlock) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(context.getPlayer() instanceof ServerPlayer player) || !(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }

        BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        if (blockEntity instanceof DoorwayPortalBlockEntity doorwayEntity && !doorwayEntity.canEdit(player)) {
            return InteractionResult.FAIL;
        }
        CompoundTag previousData = blockEntity != null ? blockEntity.saveWithoutMetadata() : null;
        BlockPos posOverride = blockEntity instanceof DoorwayPortalBlockEntity doorwayEntity && doorwayEntity.getPosOverride() != null
            ? doorwayEntity.getPosOverride()
            : MineCellsData.get(serverLevel).getOrCreatePlayerRunCenter(player);

        if (!portalBlock.getType().dimensionId().equals(DoorwayPortalBlock.DoorwayType.PRISON.dimensionId())
            && !MineCellsData.getPlayerData(player, serverLevel, posOverride).hasVisitedDimension(portalBlock.getType().dimensionId())) {
            player.displayClientMessage(Component.translatable(NOT_VISITED_KEY), true);
            return InteractionResult.FAIL;
        }

        level.setBlock(
            context.getClickedPos(),
            portalBlock.defaultBlockState().setValue(DoorwayPortalBlock.FACING, level.getBlockState(context.getClickedPos()).getValue(DoorwayPortalBlock.FACING)),
            3
        );

        BlockEntity newBlockEntity = level.getBlockEntity(context.getClickedPos());
        if (newBlockEntity instanceof DoorwayPortalBlockEntity newDoorwayEntity) {
            if (previousData != null) {
                newDoorwayEntity.load(previousData);
            }
            newDoorwayEntity.setPosOverride(posOverride);
        }

        serverLevel.sendParticles(MineCellsParticles.SPECKLE.get().get(portalBlock.getType().getColor()), context.getClickedPos().getX() + 0.5D, context.getClickedPos().getY() + 0.5D, context.getClickedPos().getZ() + 0.5D, 40, 0.4D, 0.2D, 0.4D, 0.05D);
        serverLevel.sendParticles(ParticleTypes.EXPLOSION, context.getClickedPos().getX() + 0.5D, context.getClickedPos().getY() + 0.5D, context.getClickedPos().getZ() + 0.5D, 3, 0.2D, 0.1D, 0.2D, 0.02D);
        level.playSound(null, context.getClickedPos(), MineCellsSounds.TELEPORT_RELEASE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        context.getItemInHand().shrink(1);
        return InteractionResult.CONSUME;
    }

    @Override
    public Component getName(ItemStack stack) {
        DoorwayPortalBlock.DoorwayType type = portalBlock.getType();
        return MineCellsText.highlight(getDescriptionId(stack), "dimension.minecells." + type.getSerializedName(), type.getColor());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable(TOOLTIP_KEY).withStyle(ChatFormatting.DARK_GRAY));
    }
}
