package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.block.blockentity.DoorwayPortalBlockEntity;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.world.state.MineCellsData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DoorwayItem extends BlockItem {
    private static final String TOOLTIP_VISITED = "item.minecells.prison_doorway.visited";
    private static final String TOOLTIP_NOT_VISITED = "item.minecells.prison_doorway.not_visited";
    private static final String TOOLTIP_BOUND = "item.minecells.prison_doorway.bound";
    private static final String TOOLTIP_NOT_BOUND = "item.minecells.prison_doorway.not_bound";
    private static final String DESCRIPTION = "item.minecells.prison_doorway.description";

    private final DoorwayPortalBlock doorwayBlock;

    public DoorwayItem(DoorwayPortalBlock doorwayBlock, Properties properties) {
        super(doorwayBlock, properties);
        this.doorwayBlock = doorwayBlock;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack) || getPosOverride(stack) != null;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (!(entity instanceof ServerPlayer player) || !selected || level.isClientSide || !level.dimension().location().equals(DoorwayPortalBlock.DoorwayType.OVERWORLD.dimensionId())) {
            return;
        }
        if (entity.tickCount % 10 != 0 || getPosOverride(stack) != null) {
            return;
        }
        BlockPos anchor = DoorwayPortalBlockEntity.toPortalAnchor(player.blockPosition());
        String area = "[x: " + anchor.getX() + ", z: " + anchor.getZ() + "]";
        boolean visited = MineCellsData.getPlayerData(player, player.serverLevel(), anchor).hasVisitedDimension(doorwayBlock.getType().dimensionId());
        player.displayClientMessage(
            Component.translatable(
                visited ? TOOLTIP_VISITED : TOOLTIP_NOT_VISITED,
                Component.translatable(getDescriptionId()),
                area
            ),
            true
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable(DESCRIPTION).withStyle(ChatFormatting.DARK_GRAY));
        BlockPos posOverride = getPosOverride(stack);
        if (posOverride != null) {
            String area = "[x: " + posOverride.getX() + ", z: " + posOverride.getZ() + "]";
            tooltip.add(Component.translatable(TOOLTIP_BOUND, area).withStyle(ChatFormatting.GREEN));
        } else {
            tooltip.add(Component.translatable(TOOLTIP_NOT_BOUND).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @Nullable
    public static BlockPos getPosOverride(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("BlockEntityTag");
        return tag != null && tag.contains("posOverride") ? BlockPos.of(tag.getLong("posOverride")) : null;
    }
}
