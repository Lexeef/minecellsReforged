package com.github.mim1q.minecells.client.renderer;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class NoopMineCellsMonsterRenderer extends EntityRenderer<MineCellsMonsterEntity> {
    private static final ResourceLocation TEXTURE = MineCells.id("textures/misc/empty.png");

    public NoopMineCellsMonsterRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(MineCellsMonsterEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
    }

    @Override
    public ResourceLocation getTextureLocation(MineCellsMonsterEntity entity) {
        return TEXTURE;
    }
}
