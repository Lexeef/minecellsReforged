package com.github.mim1q.minecells.client.renderer.blockentity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.ReturnStoneBlock;
import com.github.mim1q.minecells.block.blockentity.ReturnStoneBlockEntity;
import com.github.mim1q.minecells.util.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class ReturnStoneBlockEntityRenderer implements BlockEntityRenderer<ReturnStoneBlockEntity> {
    private static final Component TITLE = Component.translatable("block.minecells.return_stone.title");
    private static final ResourceLocation TEXTURE = MineCells.id("textures/block/return_stone.png");
    private final Font font;
    private final EntityRenderDispatcher dispatcher;

    public ReturnStoneBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        font = context.getFont();
        dispatcher = context.getEntityRenderer();
    }

    @Override
    public void render(ReturnStoneBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(-0.5F, -0.5F, 0.5F);
        renderArrow(poseStack, bufferSource, entity.getBlockState().getValue(ReturnStoneBlock.FACING).toYRot());
        renderBall(poseStack, bufferSource);
        renderLabel(poseStack, bufferSource);
        poseStack.popPose();
    }

    private void renderArrow(PoseStack poseStack, MultiBufferSource bufferSource, float rotation) {
        poseStack.pushPose();
        poseStack.mulPose(new Quaternionf().rotationY((float) Math.toRadians(rotation)));
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityShadow(TEXTURE));
        poseStack.translate(0.0F, 0.5F / 16.0F, 5.01F / 16.0F);
        PoseStack.Pose pose = poseStack.last();
        Matrix4f position = pose.pose();
        Matrix3f normal = pose.normal();
        float dx = 5.0F / 16.0F;
        float dy = 3.5F / 16.0F;
        produceVertex(consumer, position, normal, -dx, -dy, 0.0F, 19.0F / 32.0F, 17.0F / 32.0F, 0xFF);
        produceVertex(consumer, position, normal, dx, -dy, 0.0F, 9.0F / 32.0F, 17.0F / 32.0F, 0xFF);
        produceVertex(consumer, position, normal, dx, dy, 0.0F, 9.0F / 32.0F, 24.0F / 32.0F, 0xFF);
        produceVertex(consumer, position, normal, -dx, dy, 0.0F, 19.0F / 32.0F, 24.0F / 32.0F, 0xFF);
        poseStack.popPose();
    }

    private void renderBall(PoseStack poseStack, MultiBufferSource bufferSource) {
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, 0.75F, 0.0F);
        poseStack.mulPose(dispatcher.cameraOrientation());
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE));
        float d = 7.0F / 16.0F;
        RenderUtils.drawBillboard(consumer, poseStack, LightTexture.FULL_BRIGHT, d, d, 1.0F / 32.0F, 8.0F / 32.0F, 17.0F / 32.0F, 24.0F / 32.0F, 0xFFFFFFFF, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private void renderLabel(PoseStack poseStack, MultiBufferSource bufferSource) {
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, 1.25F, 0.0F);
        poseStack.mulPose(dispatcher.cameraOrientation());
        poseStack.scale(-0.02F, -0.02F, 0.02F);
        float width = font.width(TITLE);
        font.drawInBatch(TITLE, -width / 2.0F, 0.0F, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0x80000000, LightTexture.FULL_BRIGHT);
        poseStack.popPose();
    }

    private static void produceVertex(VertexConsumer consumer, Matrix4f position, Matrix3f normal, float x, float y, float z, float u, float v, int alpha) {
        consumer.vertex(position, x, y, z)
            .color(255, 255, 255, alpha)
            .uv(u, v)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(LightTexture.FULL_BRIGHT)
            .normal(normal, 0.0F, 1.0F, 0.0F)
            .endVertex();
    }
}
