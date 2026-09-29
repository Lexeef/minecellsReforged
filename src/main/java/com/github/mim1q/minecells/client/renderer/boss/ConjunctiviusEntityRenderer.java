package com.github.mim1q.minecells.client.renderer.boss;

import com.github.mim1q.minecells.client.renderer.monster.MineCellsMonsterModelLayers;
import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.MineCells;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;

public class ConjunctiviusEntityRenderer extends MobRenderer<ConjunctiviusEntity, ConjunctiviusEntityModel> {
    public static final ResourceLocation TEXTURE = MineCells.id("textures/entity/conjunctivius/conjunctivius.png");

    public ConjunctiviusEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new ConjunctiviusEntityModel(context.bakeLayer(MineCellsMonsterModelLayers.CONJUNCTIVIUS)), 1.5F);
        addLayer(new ConjunctiviusEyeRenderer(this, context.bakeLayer(MineCellsMonsterModelLayers.CONJUNCTIVIUS_EYE)));

        ConjunctiviusTentacleRenderer tentacles = new ConjunctiviusTentacleRenderer(this, context.bakeLayer(MineCellsMonsterModelLayers.CONJUNCTIVIUS_TENTACLE));
        tentacles.addPosRotScale(0.75F, 1.9F, 0.725F, 0.0F, 45.0F, 0.0F, 0.5F);
        tentacles.addPosRotScale(-0.75F, 1.9F, 0.725F, 0.0F, -45.0F, 0.0F, 0.5F);
        tentacles.addPosRotScale(1.1F, 1.5F, 0.55F, 15.0F, 60.0F, -10.0F, 0.75F);
        tentacles.addPosRotScale(-1.1F, 1.5F, 0.55F, 15.0F, -60.0F, 10.0F, 0.75F);
        tentacles.addPosRotScale(1.0F, 1.75F, 0.25F, 5.0F, 80.0F, -5.0F, 0.66F);
        tentacles.addPosRotScale(-1.0F, 1.75F, 0.25F, -5.0F, -80.0F, -5.0F, 0.66F);
        addLayer(tentacles);

        ConjunctiviusSpikeRenderer spikes = new ConjunctiviusSpikeRenderer(this, context.bakeLayer(MineCellsMonsterModelLayers.CONJUNCTIVIUS_SPIKE));
        spikes.addPosRotScale(1.0F, -0.5F, -0.6F, 25.0F, 0.0F, 75.0F, 1.0F);
        spikes.addPosRotScale(1.2F, -0.15F, -0.25F, 15.0F, 0.0F, 80.0F, 1.0F);
        spikes.addPosRotScale(1.35F, 0.4F, -0.6F, 35.0F, 0.0F, 90.0F, 1.0F);
        spikes.addPosRotScale(1.05F, 0.8F, -0.8F, 35.0F, 0.0F, 110.0F, 1.0F);
        spikes.addPosRotScale(1.0F, 1.2F, -0.6F, 25.0F, 0.0F, 135.0F, 1.0F);
        spikes.addPosRotScale(-1.2F, -0.15F, -0.25F, 25.0F, 0.0F, -75.0F, 1.0F);
        spikes.addPosRotScale(-1.0F, -0.5F, -0.6F, 15.0F, 0.0F, -80.0F, 1.0F);
        spikes.addPosRotScale(-1.35F, 0.4F, -0.6F, 35.0F, 0.0F, -90.0F, 1.0F);
        spikes.addPosRotScale(-1.05F, 0.8F, -0.8F, 35.0F, 0.0F, -110.0F, 1.0F);
        spikes.addPosRotScale(-1.0F, 1.2F, -0.6F, 25.0F, 0.0F, -135.0F, 1.0F);
        addLayer(spikes);

        addLayer(new ConjunctiviusChainRenderer(this, context.getModelManager()));
    }

    @Override
    public void render(ConjunctiviusEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float scale = entity.isForDisplay() ? 1.5F : 2.0F;
        poseStack.scale(scale, scale, scale);
        if (entity.isForDisplay()) {
            poseStack.translate(0.0F, 0.5F, 0.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.popPose();
    }

    @Override
    protected float getFlipDegrees(ConjunctiviusEntity entity) {
        return 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(ConjunctiviusEntity entity) {
        return TEXTURE;
    }

    @Override
    protected boolean shouldShowName(ConjunctiviusEntity entity) {
        return false;
    }
}
