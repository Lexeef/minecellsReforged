package com.github.mim1q.minecells.client.renderer.item;

import com.github.mim1q.minecells.block.FlagBlock;
import com.github.mim1q.minecells.client.renderer.blockentity.FlagBlockEntityRenderer;
import com.github.mim1q.minecells.client.renderer.blockentity.FlagBlockEntityRenderer.BiomeBannerBlockEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class FlagBlockItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final FlagBlock flagBlock;
    private BiomeBannerBlockEntityModel model;

    public FlagBlockItemRenderer(FlagBlock flagBlock) {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.flagBlock = flagBlock;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (model == null) {
            model = new BiomeBannerBlockEntityModel(Minecraft.getInstance().getEntityModels().bakeLayer(flagBlock.large ? FlagBlockEntityRenderer.FLAG_LARGE_LAYER : FlagBlockEntityRenderer.FLAG_LAYER));
        }

        VertexConsumer buffer = bufferSource.getBuffer(model.renderType(flagBlock.texture));
        poseStack.pushPose();
        float scale = 0.4F;
        float x = 1.2F;
        float y = displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND ? -1.25F : -2.5F;
        float z = -1.0F;
        poseStack.scale(scale, -scale, -scale);
        poseStack.translate(x, y, z);
        model.setupLargeItemModel();
        model.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        model.resetSegments();
        poseStack.popPose();
    }
}
