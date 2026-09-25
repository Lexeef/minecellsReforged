package com.github.mim1q.minecells.client.renderer.layer;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.ClientEffectFlags;
import com.github.mim1q.minecells.effect.MineCellsEffectFlags;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

/**
 * Frozen (ice shell) and bleeding (scrolling blood) overlays drawn over any living entity model.
 */
public class EffectOverlayLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation ICE_TEXTURE = new ResourceLocation("textures/block/ice.png");
    private static final ResourceLocation BLOOD_TEXTURE = MineCells.id("textures/entity/effect/blood_overlay.png");

    public EffectOverlayLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffers, int light, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) {
            return;
        }
        if (ClientEffectFlags.has(entity, MineCellsEffectFlags.FROZEN)) {
            renderOverlay(poseStack, buffers, light, ICE_TEXTURE, 2.0322F, 0.0F);
        }
        if (ClientEffectFlags.has(entity, MineCellsEffectFlags.BLEEDING)) {
            renderOverlay(poseStack, buffers, light, BLOOD_TEXTURE, 0.0321F, (-0.06F * ageInTicks) % 1.0F);
        }
    }

    private void renderOverlay(PoseStack poseStack, MultiBufferSource buffers, int light, ResourceLocation texture, float offset, float vScroll) {
        ModelOverlayVertexConsumer consumer = new ModelOverlayVertexConsumer(buffers.getBuffer(RenderType.entityTranslucent(texture)), offset, vScroll);
        getParentModel().renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
