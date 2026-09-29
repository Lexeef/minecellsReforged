package com.github.mim1q.minecells.client.renderer.blockentity;

import com.github.mim1q.minecells.block.blockentity.CellCrafterBlockEntity;
import com.github.mim1q.minecells.client.renderer.misc.AdvancementHintRenderer;
import com.github.mim1q.minecells.MineCells;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.MultiBufferSource;

public class CellCrafterBlockEntityRenderer implements BlockEntityRenderer<CellCrafterBlockEntity> {
    private final AdvancementHintRenderer hintRenderer = new AdvancementHintRenderer(MineCells.id("cell_crafter"), 0xCCEFFF, null);

    public CellCrafterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CellCrafterBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float time = entity.getLevel() == null ? 0.0F : entity.getLevel().getGameTime() + partialTick;
        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        hintRenderer.render(poseStack, bufferSource, time);
        poseStack.popPose();
    }
}
