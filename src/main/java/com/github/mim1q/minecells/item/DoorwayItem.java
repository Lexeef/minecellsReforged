package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.block.blockentity.DoorwayPortalBlockEntity;
import com.github.mim1q.minecells.block.portal.DoorwayFrameBlock;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.network.MineCellsNetwork;
import com.github.mim1q.minecells.network.s2c.OpenDoorwayScreenS2CPacket;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.util.MineCellsText;
import com.github.mim1q.minecells.world.state.MineCellsData;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DoorwayItem extends BlockItem {
    private static final String TOOLTIP_BOUND = "item.minecells.prison_doorway.bound";
    private static final String TOOLTIP_NOT_BOUND = "item.minecells.prison_doorway.not_bound";
    private static final String DESCRIPTION = "item.minecells.prison_doorway.description";

    private final DoorwayPortalBlock doorwayBlock;

    public DoorwayItem(DoorwayPortalBlock doorwayBlock, Properties properties) {
        super(doorwayBlock, properties);
        this.doorwayBlock = doorwayBlock;
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        Level level = context.getLevel();
        if (level.dimension() != Level.OVERWORLD) {
            return false;
        }
        BlockPos pos = context.getClickedPos();
        boolean floorBelow = level.getBlockState(pos.below()).isSolidRender(level, pos.below());
        if (!floorBelow && !level.getBlockState(pos.below(2)).isSolidRender(level, pos.below(2))) {
            return false;
        }
        Direction side = getPlacementDirection(context);
        int dy = floorBelow ? 1 : 0;
        int x = side.getAxis() == Direction.Axis.X ? 0 : 1;
        int z = side.getAxis() == Direction.Axis.Z ? 0 : 1;
        for (int y = -1; y <= 1; y++) {
            for (int xz = -1; xz <= 1; xz++) {
                BlockPos wallPos = pos.subtract(side.getNormal()).offset(xz * x, y + dy, xz * z);
                if (!level.getBlockState(wallPos).isSolidRender(level, wallPos)) {
                    return false;
                }
                if (!level.getBlockState(wallPos.relative(side)).canBeReplaced(context)) {
                    return false;
                }
            }
        }
        return super.canPlace(context, state);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        Level level = context.getLevel();
        if (level.isClientSide) {
            return true;
        }
        Player player = context.getPlayer();
        DoorwayFrameBlock frame = player != null && player.getAbilities().instabuild
            ? MineCellsBlocks.UNBREAKABLE_DOORWAY_FRAME.get()
            : MineCellsBlocks.DOORWAY_FRAME.get();
        Direction direction = getPlacementDirection(context);
        BlockPos pos = context.getClickedPos();
        int dy = level.getBlockState(pos.below()).isSolidRender(level, pos.below()) ? 0 : -1;
        BlockPos base = pos.offset(0, dy, 0);
        BlockPos doorwayPos = base.above();
        BlockPos right = base.relative(direction.getCounterClockWise());
        BlockPos left = base.relative(direction.getClockWise());

        level.setBlock(doorwayPos, doorwayBlock.defaultBlockState().setValue(DoorwayPortalBlock.FACING, direction), 3);
        updateCustomBlockEntityTag(level, player, doorwayPos, context.getItemInHand());
        BlockPos itemAnchor = getPosOverride(context.getItemInHand());
        BlockPos anchor;
        if (player instanceof ServerPlayer serverPlayer && level instanceof ServerLevel serverLevel) {
            anchor = MineCellsData.get(serverLevel).getOrCreatePlayerRunCenter(serverPlayer);
        } else {
            anchor = itemAnchor != null ? itemAnchor : DoorwayPortalBlockEntity.toPortalAnchor(doorwayPos);
        }
        if (level.getBlockEntity(doorwayPos) instanceof DoorwayPortalBlockEntity doorway) {
            if (player != null) {
                doorway.update(player, anchor, true);
            } else {
                doorway.setPosOverride(anchor);
            }
        }

        level.setBlock(base, frame.getState(DoorwayFrameBlock.FillerType.MIDDLE, direction), 3);
        level.setBlock(base.above(2), frame.getState(DoorwayFrameBlock.FillerType.TOP, direction), 3);
        level.setBlock(left, frame.getState(DoorwayFrameBlock.FillerType.LEFT, direction), 3);
        level.setBlock(left.above(), frame.getState(DoorwayFrameBlock.FillerType.LEFT, direction), 3);
        level.setBlock(left.above(2), frame.getState(DoorwayFrameBlock.FillerType.TOP_LEFT, direction), 3);
        level.setBlock(right, frame.getState(DoorwayFrameBlock.FillerType.RIGHT, direction), 3);
        level.setBlock(right.above(), frame.getState(DoorwayFrameBlock.FillerType.RIGHT, direction), 3);
        level.setBlock(right.above(2), frame.getState(DoorwayFrameBlock.FillerType.TOP_RIGHT, direction), 3);

        if (player instanceof ServerPlayer serverPlayer) {
            MineCellsNetwork.sendToPlayer(serverPlayer, new OpenDoorwayScreenS2CPacket(doorwayPos, anchor));
        }
        return true;
    }

    private static Direction getPlacementDirection(BlockPlaceContext context) {
        return context.getClickedFace().getAxis().isVertical()
            ? context.getHorizontalDirection().getOpposite()
            : context.getClickedFace();
    }

    @Override
    public String getDescriptionId() {
        return getOrCreateDescriptionId();
    }

    @Override
    public Component getName(ItemStack stack) {
        DoorwayPortalBlock.DoorwayType type = doorwayBlock.getType();
        return MineCellsText.highlight(getDescriptionId(stack), "dimension.minecells." + type.getSerializedName(), type.getColor());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack) || getPosOverride(stack) != null;
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
