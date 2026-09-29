package com.github.mim1q.minecells.client.renderer.projectile;

import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.RenderUtils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class BillboardProjectileRenderer<T extends Entity> extends EntityRenderer<T> {
    private final ResourceLocation texture;
    private final RenderType renderType;
    private final float minY;
    private final boolean fullBright;

    public BillboardProjectileRenderer(EntityRendererProvider.Context context, ResourceLocation texture, float minY, boolean fullBright) {
        super(context);
        this.texture = texture;
        this.renderType = RenderType.entityCutout(texture);
        this.minY = minY;
        this.fullBright = fullBright;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        int light = fullBright ? 0xF0 : packedLight;
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.25F, 0.0F);
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(new Quaternionf().rotationY(MathUtils.radians(180.0F)));
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        VertexConsumer consumer = buffers.getBuffer(renderType);
        float maxY = minY + 1.0F;
        RenderUtils.produceVertex(consumer, pose, normal, light, 0xFFFFFFFF, -0.5F, minY, 0.0F, 0.0F, 1.0F, OverlayTexture.NO_OVERLAY);
        RenderUtils.produceVertex(consumer, pose, normal, light, 0xFFFFFFFF, 0.5F, minY, 0.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY);
        RenderUtils.produceVertex(consumer, pose, normal, light, 0xFFFFFFFF, 0.5F, maxY, 0.0F, 1.0F, 0.0F, OverlayTexture.NO_OVERLAY);
        RenderUtils.produceVertex(consumer, pose, normal, light, 0xFFFFFFFF, -0.5F, maxY, 0.0F, 0.0F, 0.0F, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return texture;
    }
}
