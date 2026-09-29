package com.github.mim1q.minecells.client.renderer.blockentity;

import com.github.mim1q.minecells.block.blockentity.RunicVinePlantBlockEntity;
import com.github.mim1q.minecells.block.RunicVineBlock;
import com.github.mim1q.minecells.block.RunicVinePlantBlock;
import com.github.mim1q.minecells.client.renderer.misc.AdvancementHintRenderer;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsItems;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.client.model.data.ModelData;

public class RunicVinePlantBlockEntityRenderer implements BlockEntityRenderer<RunicVinePlantBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;
    private final AdvancementHintRenderer hintRenderer;

    public RunicVinePlantBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderer = context.getBlockRenderDispatcher();
        hintRenderer = new AdvancementHintRenderer(MineCells.id("vine_rune"), 0xFFF0FFD0, MineCellsItems.VINE_RUNE);
    }

    @Override
    public void render(RunicVinePlantBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (entity.getLevel() == null) {
            return;
        }
        float time = entity.getLevel().getGameTime() + partialTick;
        BlockState plantState = MineCellsBlocks.RUNIC_VINE_PLANT.get().defaultBlockState();

        poseStack.pushPose();
        if (entity.getBlockState().getValue(RunicVinePlantBlock.ACTIVATED)) {
            BlockState vineState = MineCellsBlocks.RUNIC_VINE.get().defaultBlockState().setValue(RunicVineBlock.TOP, false);
            renderBlock(vineState, poseStack, bufferSource, packedLight, packedOverlay);
        }
        float wobble = entity.wobble.update(time) * 0.2F;
        float vScale = 1.25F + Mth.sin(time * 0.2F) * 0.075F;
        float hScale = 1.25F + Mth.cos(time * 0.2F) * 0.05F;
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.scale(hScale - wobble, vScale + wobble, hScale - wobble);
        poseStack.translate(-0.51D, 0.0D, -0.5D);
        renderBlock(plantState, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.5F, 1.0F, 0.5F);
        hintRenderer.render(poseStack, bufferSource, time);
        poseStack.popPose();
    }

    private void renderBlock(BlockState state, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        BakedModel model = blockRenderer.getBlockModel(state);
        RenderType renderType = RenderType.cutout();
        blockRenderer.getModelRenderer().renderModel(
            poseStack.last(),
            bufferSource.getBuffer(renderType),
            state,
            model,
            1.0F,
            1.0F,
            1.0F,
            light,
            overlay,
            ModelData.EMPTY,
            renderType
        );
    }
}
