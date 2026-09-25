package com.github.mim1q.minecells.compat;

import com.github.mim1q.minecells.block.SpikesBlock;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.util.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

/** Renders a specific block state as a 16x16 GUI icon, shared by the recipe viewer integrations. */
public final class BlockStateIcon {
    private BlockStateIcon() {
    }

    public static BlockState bloodySpikes() {
        return MineCellsBlocks.SPIKES.get().defaultBlockState()
            .setValue(SpikesBlock.BLOODY, true)
            .setValue(SpikesBlock.FACING, Direction.UP);
    }

    public static void render(GuiGraphics graphics, BlockState state, int x, int y) {
        PoseStack poseStack = graphics.pose();
        var model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        poseStack.pushPose();
        poseStack.translate(x + 8.0F, y + 8.0F, 200.0F);
        poseStack.scale(10.0F, -10.0F, 10.0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(30.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(-45.0F));
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        RenderUtils.renderBakedModel(model, RandomSource.create(), 0xF000F0, poseStack, graphics.bufferSource().getBuffer(RenderType.cutout()));
        graphics.flush();
        poseStack.popPose();
    }
}
