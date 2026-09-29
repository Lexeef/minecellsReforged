package com.github.mim1q.minecells.client.renderer.monster;

import com.github.mim1q.minecells.entity.InquisitorEntity;
import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.RenderUtils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class InquisitorRenderer extends ModelBackedMineCellsMonsterRenderer<InquisitorModel> {
    private static final ResourceLocation TEXTURE = MineCells.id("textures/entity/inquisitor.png");
    private static final RenderType ORB_LAYER = RenderType.entityCutout(MineCells.id("textures/particle/magic_orb.png"));
    private static final int FULL_BRIGHT = 0xF000F0;

    public InquisitorRenderer(EntityRendererProvider.Context context) {
        super(context, new InquisitorModel(context.bakeLayer(MineCellsMonsterModelLayers.INQUISITOR)), 0.35F, TEXTURE, null);
    }

    @Override
    public void render(MineCellsMonsterEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
        if (!(entity instanceof InquisitorEntity inquisitor) || !inquisitor.isAlive()) {
            return;
        }
        float offset = inquisitor.armUpProgress.getValue() * 0.3F;
        float progress = (inquisitor.tickCount + partialTick) * 0.2F;
        VertexConsumer consumer = buffers.getBuffer(ORB_LAYER);
        float headYaw = MathUtils.lerp(inquisitor.yHeadRotO, inquisitor.yHeadRot, partialTick);
        float bodyYaw = MathUtils.lerp(inquisitor.yBodyRotO, inquisitor.yBodyRot, partialTick);
        boolean forDisplay = inquisitor.isForDisplay();
        renderOrb(poseStack, consumer, headYaw, progress, new Vector3f(-0.25F, 2.25F, 0.0F), forDisplay);
        renderOrb(poseStack, consumer, bodyYaw, progress, new Vector3f(0.6F, 1.0F + offset, 0.4F + offset), forDisplay);
        renderOrb(poseStack, consumer, bodyYaw, progress, new Vector3f(0.6F, 1.0F + offset, -0.4F - offset), forDisplay);
    }

    private void renderOrb(PoseStack poseStack, VertexConsumer consumer, float yaw, float age, Vector3f offset, boolean forDisplay) {
        poseStack.pushPose();
        Vector3f rotated = MathUtils.vectorRotateY(offset, MathUtils.radians(yaw));
        poseStack.translate(rotated.x(), rotated.y() + Math.sin(age) * 0.1F, rotated.z());
        poseStack.scale(0.375F, 0.375F, 0.375F);
        if (forDisplay) {
            poseStack.mulPose(new Quaternionf().rotationZ(MathUtils.radians(1.0F)));
            poseStack.mulPose(new Quaternionf().rotationY(MathUtils.radians(30.0F)));
        } else {
            poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
            poseStack.mulPose(new Quaternionf().rotationY(MathUtils.radians(180.0F)));
        }
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        RenderUtils.produceVertex(consumer, pose, normal, FULL_BRIGHT, 0xFFFFFFFF, -0.5F, -0.5F, 0.0F, 0.0F, 1.0F, OverlayTexture.NO_OVERLAY);
        RenderUtils.produceVertex(consumer, pose, normal, FULL_BRIGHT, 0xFFFFFFFF, 0.5F, -0.5F, 0.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY);
        RenderUtils.produceVertex(consumer, pose, normal, FULL_BRIGHT, 0xFFFFFFFF, 0.5F, 0.5F, 0.0F, 1.0F, 0.0F, OverlayTexture.NO_OVERLAY);
        RenderUtils.produceVertex(consumer, pose, normal, FULL_BRIGHT, 0xFFFFFFFF, -0.5F, 0.5F, 0.0F, 0.0F, 0.0F, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
