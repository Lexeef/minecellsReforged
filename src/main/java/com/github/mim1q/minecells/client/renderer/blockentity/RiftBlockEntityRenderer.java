package com.github.mim1q.minecells.client.renderer.blockentity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.RiftBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public final class RiftBlockEntityRenderer implements BlockEntityRenderer<RiftBlockEntity> {
    private static final ResourceLocation BASE_TEXTURE = MineCells.id("textures/blockentity/rift/base.png");
    private static final ResourceLocation BACKGROUND_TEXTURE = MineCells.id("textures/blockentity/rift/background.png");
    private static final Component LABEL = Component.translatable("block.minecells.rift.description");

    private final Font font;
    private final EntityRenderDispatcher dispatcher;

    public RiftBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        font = context.getFont();
        dispatcher = context.getEntityRenderer();
    }

    @Override
    public void render(RiftBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (Minecraft.getInstance().player == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(new Quaternionf().rotationY((float) Math.toRadians(entity.getRotation(partialTick))));

        VertexConsumer frameConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(BASE_TEXTURE));
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 0.001D);
        drawBillboard(frameConsumer, poseStack, -0.5F, 0.5F, -1.0F, 1.0F, 0.0F, 0.5F, 0.0F, 1.0F, 0x80FFFFFF, LightTexture.FULL_BRIGHT);
        poseStack.translate(0.0D, 0.0D, -0.002D);
        drawBillboard(frameConsumer, poseStack, -0.5F, 0.5F, -1.0F, 1.0F, 0.0F, 0.5F, 0.0F, 1.0F, 0xFFFFFFFF, LightTexture.FULL_BRIGHT);
        poseStack.popPose();

        Vec3 blockPos = Vec3.atCenterOf(entity.getBlockPos());
        Vec3 playerPos = Minecraft.getInstance().player.getPosition(partialTick);
        Vec3 toPlayer = playerPos.subtract(blockPos).normalize();
        float uOffset = (float) Math.sin(toPlayer.x * 0.5D) * -0.15F;
        float vOffset = (float) Math.max(-0.1D, Math.min(0.1D, (playerPos.y - blockPos.y) * 0.05D));

        VertexConsumer backgroundConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(BACKGROUND_TEXTURE));
        poseStack.pushPose();
        drawBillboard(backgroundConsumer, poseStack, -0.25F, 0.25F, -0.6875F, 0.6875F, 0.35F + uOffset, 0.65F + uOffset, 0.15F + vOffset, 0.85F + vOffset, 0xFFFFFFFF, LightTexture.FULL_BRIGHT);
        poseStack.popPose();

        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.5D, 2.0D, 0.5D);
        poseStack.mulPose(dispatcher.cameraOrientation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);
        float width = font.width(LABEL);
        font.drawInBatch(LABEL, -width / 2.0F, 0.0F, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0x80000000, LightTexture.FULL_BRIGHT);
        poseStack.popPose();
    }

    private static void drawBillboard(VertexConsumer consumer, PoseStack poseStack, float minX, float maxX, float minY, float maxY, float minU, float maxU, float minV, float maxV, int color, int light) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f position = pose.pose();
        Matrix3f normal = pose.normal();
        vertex(consumer, position, normal, minX, minY, 0.0F, minU, maxV, color, light);
        vertex(consumer, position, normal, maxX, minY, 0.0F, maxU, maxV, color, light);
        vertex(consumer, position, normal, maxX, maxY, 0.0F, maxU, minV, color, light);
        vertex(consumer, position, normal, minX, maxY, 0.0F, minU, minV, color, light);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f position, Matrix3f normal, float x, float y, float z, float u, float v, int color, int light) {
        consumer.vertex(position, x, y, z)
            .color((color >> 16) & 255, (color >> 8) & 255, color & 255, (color >>> 24) & 255)
            .uv(u, v)
            .overlayCoords(0)
            .uv2(light)
            .normal(normal, 0.0F, 0.0F, 1.0F)
            .endVertex();
    }
}
