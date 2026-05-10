package com.github.mim1q.minecells.client.renderer.blockentity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.SpawnerRuneBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public final class SpawnerRuneBlockEntityRenderer implements BlockEntityRenderer<SpawnerRuneBlockEntity> {
    private static final ResourceLocation TEXTURE = MineCells.id("textures/entity/spawner_rune.png");

    private final EntityRenderDispatcher dispatcher;

    public SpawnerRuneBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        dispatcher = context.getEntityRenderer();
    }

    @Override
    public void render(SpawnerRuneBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!entity.controller.isVisible() || entity.getLevel() == null) {
            return;
        }

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.03D, 0.5D);
        drawHorizontalQuad(consumer, poseStack, 0.9F, 0xD8FFFFFF);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D + floatingOffset(entity, partialTick), 0.5D);
        poseStack.mulPose(dispatcher.cameraOrientation());
        drawBillboard(consumer, poseStack, 0.9F, 0xE0FFFFFF);
        poseStack.popPose();
    }

    private static float floatingOffset(SpawnerRuneBlockEntity entity, float partialTick) {
        long time = entity.getLevel() == null ? 0L : entity.getLevel().getGameTime();
        return (float) Math.sin((time + partialTick) * 0.1F) * 0.15F;
    }

    private static void drawBillboard(VertexConsumer consumer, PoseStack poseStack, float width, int color) {
        float halfWidth = width * 0.5F;
        float halfHeight = width * 0.5F;
        PoseStack.Pose pose = poseStack.last();
        Matrix4f position = pose.pose();
        Matrix3f normal = pose.normal();
        vertex(consumer, position, normal, -halfWidth, -halfHeight, 0.0F, 0.0F, 1.0F, color);
        vertex(consumer, position, normal, halfWidth, -halfHeight, 0.0F, 1.0F, 1.0F, color);
        vertex(consumer, position, normal, halfWidth, halfHeight, 0.0F, 1.0F, 0.0F, color);
        vertex(consumer, position, normal, -halfWidth, halfHeight, 0.0F, 0.0F, 0.0F, color);
    }

    private static void drawHorizontalQuad(VertexConsumer consumer, PoseStack poseStack, float width, int color) {
        float halfWidth = width * 0.5F;
        PoseStack.Pose pose = poseStack.last();
        Matrix4f position = pose.pose();
        Matrix3f normal = pose.normal();
        vertex(consumer, position, normal, -halfWidth, 0.0F, -halfWidth, 0.0F, 1.0F, color, 0.0F, 1.0F, 0.0F);
        vertex(consumer, position, normal, halfWidth, 0.0F, -halfWidth, 1.0F, 1.0F, color, 0.0F, 1.0F, 0.0F);
        vertex(consumer, position, normal, halfWidth, 0.0F, halfWidth, 1.0F, 0.0F, color, 0.0F, 1.0F, 0.0F);
        vertex(consumer, position, normal, -halfWidth, 0.0F, halfWidth, 0.0F, 0.0F, color, 0.0F, 1.0F, 0.0F);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f position, Matrix3f normal, float x, float y, float z, float u, float v, int color) {
        vertex(consumer, position, normal, x, y, z, u, v, color, 0.0F, 0.0F, 1.0F);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f position, Matrix3f normal, float x, float y, float z, float u, float v, int color, float normalX, float normalY, float normalZ) {
        consumer.vertex(position, x, y, z)
            .color((color >> 16) & 255, (color >> 8) & 255, color & 255, (color >>> 24) & 255)
            .uv(u, v)
            .overlayCoords(0)
            .uv2(LightTexture.FULL_BRIGHT)
            .normal(normal, normalX, normalY, normalZ)
            .endVertex();
    }
}
