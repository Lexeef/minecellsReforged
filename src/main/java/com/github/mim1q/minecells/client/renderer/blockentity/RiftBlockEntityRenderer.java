package com.github.mim1q.minecells.client.renderer.blockentity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.RiftBlockEntity;
import com.github.mim1q.minecells.util.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static com.github.mim1q.minecells.util.MathUtils.radians;
import static org.joml.Math.clamp;
import static org.joml.Math.sin;

public final class RiftBlockEntityRenderer implements BlockEntityRenderer<RiftBlockEntity> {
    private static final ResourceLocation BASE_TEXTURE = MineCells.id("textures/blockentity/rift/base.png");
    private static final ResourceLocation BACKGROUND_TEXTURE = MineCells.id("textures/blockentity/rift/background.png");
    private static final String DESCRIPTION_KEY = "block.minecells.rift.description";

    private final Font font;

    public RiftBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        font = context.getFont();
    }

    @Override
    public void render(RiftBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(new Quaternionf().rotationY(radians(entity.getRotation(partialTick))));

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 0.001F);

        VertexConsumer frameConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(BASE_TEXTURE));
        RenderUtils.drawBillboard(frameConsumer, poseStack, LightTexture.FULL_BRIGHT, 1.0F, 2.0F, 0.0F, 0.5F, 0.0F, 1.0F, 0x80FFFFFF, OverlayTexture.NO_OVERLAY);
        poseStack.translate(0.0F, 0.0F, -0.002F);
        RenderUtils.drawBillboard(frameConsumer, poseStack, LightTexture.FULL_BRIGHT, 1.0F, 2.0F, 0.0F, 0.5F, 0.0F, 1.0F, 0xFFFFFFFF, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();

        Vector3f direction = new Vector3f(0.0F, 0.0F, 1.0F).rotateY(Mth.HALF_PI + radians(entity.getRotation(partialTick)));
        Vec3 blockPos = Vec3.atCenterOf(entity.getBlockPos());
        Vec3 playerPos = player.getPosition(partialTick);
        Vec3 playerVector = playerPos.subtract(blockPos).normalize();
        float dot = direction.dot(playerVector.toVector3f());
        VertexConsumer backgroundConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(BACKGROUND_TEXTURE));
        float uOffset = sin(dot) * -8.0F;
        float yOffset = clamp(-5.0F, 5.0F, 1 + playerPos.toVector3f().y - (float) blockPos.y) * 2.0F;
        float scale = 0.33F;

        drawPartialPortal(backgroundConsumer, poseStack, 2, 22, 5, uOffset, yOffset, scale);
        drawPartialPortal(backgroundConsumer, poseStack, 3, 28, 2, uOffset, yOffset, scale);
        drawPartialPortal(backgroundConsumer, poseStack, 4, 30, -2, uOffset, yOffset, scale);
        drawPartialPortal(backgroundConsumer, poseStack, 3, 28, -5, uOffset, yOffset, scale);
        drawPartialPortal(backgroundConsumer, poseStack, 2, 22, -7, uOffset, yOffset, scale);

        poseStack.popPose();

        poseStack.pushPose();
        Component text = Component.translatable(DESCRIPTION_KEY);
        poseStack.translate(0.5D, 2.0D, 0.5D);
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);
        font.drawInBatch(text, -font.width(text) / 2.0F, 0.0F, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0x80000000, LightTexture.FULL_BRIGHT);
        poseStack.popPose();
    }

    private static void drawPartialPortal(VertexConsumer consumer, PoseStack poseStack, int width, int height, int x, float uOffset, float vOffset, float scale) {
        int y = -height / 2;
        float startU = 0.5F + (x + uOffset) * scale / 16.0F;
        float startV = 0.5F + (y + vOffset) * scale / 16.0F;
        RenderUtils.drawBillboard(
            consumer,
            poseStack,
            LightTexture.FULL_BRIGHT,
            x / 16.0F,
            (x + width) / 16.0F,
            -height / 32.0F,
            height / 32.0F,
            startU,
            startU + width * scale / 16.0F,
            startV,
            startV + height * scale / 16.0F,
            0xFFFFFFFF,
            OverlayTexture.NO_OVERLAY
        );
    }
}
