package com.github.mim1q.minecells.client.renderer.obelisk;

import com.github.mim1q.minecells.client.renderer.misc.AdvancementHintRenderer;
import com.github.mim1q.minecells.entity.nonliving.obelisk.ObeliskEntity;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.util.MathUtils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ObeliskEntityRenderer extends EntityRenderer<ObeliskEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(MineCells.id("obelisk"), "main");

    private final ResourceLocation texture;
    private final ObeliskEntityModel model;
    private final AdvancementHintRenderer hintRenderer = new AdvancementHintRenderer(MineCells.id("elite"), 0xFFFF4E3A, null);

    public ObeliskEntityRenderer(EntityRendererProvider.Context context, String textureName) {
        super(context);
        this.model = new ObeliskEntityModel(context.bakeLayer(LAYER));
        this.texture = MineCells.id("textures/entity/obelisk/" + textureName + ".png");
    }

    @Override
    public void render(ObeliskEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0.0F, -1.5F, 0.0F);
        poseStack.mulPose(Axis.YP.rotation(MathUtils.radians(entityYaw)));
        float animationProgress = entity.tickCount + partialTick;
        model.setupAnim(entity, 0.0F, 0.0F, animationProgress, 0.0F, 0.0F);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(texture));
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        VertexConsumer glowConsumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(texture));
        float glow = entity.glow.update(animationProgress);
        model.renderGlow(poseStack, glowConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, glow);
        poseStack.popPose();
        if (entity.bury.getValue() < 0.1F) {
            poseStack.pushPose();
            poseStack.translate(0.0D, 2.75D, 0.0D);
            hintRenderer.render(poseStack, buffer, entity.tickCount + partialTick);
            poseStack.popPose();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(ObeliskEntity entity) {
        return texture;
    }
}
