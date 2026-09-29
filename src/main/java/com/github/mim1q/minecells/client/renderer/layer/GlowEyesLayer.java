package com.github.mim1q.minecells.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

public class GlowEyesLayer<E extends Entity, M extends EntityModel<E>> extends RenderLayer<E, M> {
    private final Function<E, ResourceLocation> glowTexture;
    private final BooleanSupplier enabled;

    public GlowEyesLayer(RenderLayerParent<E, M> renderer, ResourceLocation glowTexture) {
        this(renderer, entity -> glowTexture, () -> true);
    }

    public GlowEyesLayer(RenderLayerParent<E, M> renderer, Function<E, ResourceLocation> glowTexture, BooleanSupplier enabled) {
        super(renderer);
        this.glowTexture = glowTexture;
        this.enabled = enabled;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, E entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!enabled.getAsBoolean()) {
            return;
        }
        VertexConsumer consumer = buffer.getBuffer(RenderType.eyes(glowTexture.apply(entity)));
        getParentModel().renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
