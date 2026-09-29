package com.github.mim1q.minecells.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

public final class RenderUtils {
    private RenderUtils() {
    }

    public static void produceVertex(
        VertexConsumer vertexConsumer,
        Matrix4f positionMatrix,
        Matrix3f normalMatrix,
        int light,
        int argb,
        float x,
        float y,
        float z,
        float textureU,
        float textureV,
        int overlay
    ) {
        vertexConsumer.vertex(positionMatrix, x, y, z)
            .color(argb)
            .uv(textureU, textureV)
            .overlayCoords(overlay)
            .uv2(light)
            .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
            .endVertex();
    }

    public static void drawBillboard(
        VertexConsumer consumer,
        PoseStack poseStack,
        int light,
        float width,
        float height,
        float minU,
        float maxU,
        float minV,
        float maxV,
        int argb,
        int overlay
    ) {
        float dx = width / 2.0F;
        float dy = height / 2.0F;
        drawBillboard(consumer, poseStack, light, -dx, dx, -dy, dy, minU, maxU, minV, maxV, argb, overlay);
    }

    public static void drawBillboard(
        VertexConsumer consumer,
        PoseStack poseStack,
        int light,
        float minX,
        float maxX,
        float minY,
        float maxY,
        float minU,
        float maxU,
        float minV,
        float maxV,
        int argb,
        int overlay
    ) {
        Matrix3f normal = poseStack.last().normal();
        Matrix4f pose = poseStack.last().pose();
        produceVertex(consumer, pose, normal, light, argb, minX, maxY, 0.0F, minU, minV, overlay);
        produceVertex(consumer, pose, normal, light, argb, maxX, maxY, 0.0F, maxU, minV, overlay);
        produceVertex(consumer, pose, normal, light, argb, maxX, minY, 0.0F, maxU, maxV, overlay);
        produceVertex(consumer, pose, normal, light, argb, minX, minY, 0.0F, minU, maxV, overlay);
    }

    public static void renderBakedModel(
        BakedModel model,
        RandomSource random,
        int light,
        PoseStack poseStack,
        VertexConsumer buffer
    ) {
        for (Direction direction : Direction.values()) {
            renderBakedQuads(model.getQuads(null, direction, random), poseStack, buffer, light);
        }
        renderBakedQuads(model.getQuads(null, null, random), poseStack, buffer, light);
    }

    private static void renderBakedQuads(
        List<BakedQuad> quads,
        PoseStack poseStack,
        VertexConsumer buffer,
        int light
    ) {
        for (BakedQuad quad : quads) {
            buffer.putBulkData(
                poseStack.last(),
                quad,
                1.0F,
                1.0F,
                1.0F,
                light,
                OverlayTexture.NO_OVERLAY
            );
        }
    }
}
