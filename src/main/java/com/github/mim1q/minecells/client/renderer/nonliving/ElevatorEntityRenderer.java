package com.github.mim1q.minecells.client.renderer.nonliving;

import com.github.mim1q.minecells.entity.nonliving.ElevatorEntity;
import com.github.mim1q.minecells.MineCells;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import org.joml.Quaternionf;

public class ElevatorEntityRenderer extends EntityRenderer<ElevatorEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(MineCells.id("elevator"), "main");
    private static final ResourceLocation TEXTURE = MineCells.id("textures/entity/elevator.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE);

    private final ElevatorEntityModel model;

    public ElevatorEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ElevatorEntityModel(context.bakeLayer(LAYER));
    }

    public static LayerDefinition createLayer() {
        return ElevatorEntityModel.createLayer();
    }

    @Override
    public ResourceLocation getTextureLocation(ElevatorEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(ElevatorEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        if (entity.isRotated()) {
            poseStack.mulPose(new Quaternionf().rotationYXZ(Mth.HALF_PI, 0.0F, 0.0F));
        }
        VertexConsumer consumer = buffer.getBuffer(RENDER_TYPE);
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }
}
