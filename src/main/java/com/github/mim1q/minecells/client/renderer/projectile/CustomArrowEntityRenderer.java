package com.github.mim1q.minecells.client.renderer.projectile;

import com.github.mim1q.minecells.entity.nonliving.projectile.CustomArrowEntity;
import com.github.mim1q.minecells.item.weapon.bow.CustomArrowType;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.util.RenderUtils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class CustomArrowEntityRenderer extends EntityRenderer<CustomArrowEntity> {
    private final HashMap<CustomArrowType, BakedModel> models = new HashMap<>();

    public CustomArrowEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        CustomArrowType.getAllNames().forEach(name -> {
            BakedModel model = context.getModelManager().getModel(MineCells.id("arrow/" + name));
            models.put(CustomArrowType.get(name), model);
        });
    }

    @Override
    public void render(CustomArrowEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        BakedModel model = models.get(entity.getArrowType());
        if (model == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F + entity.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));

        if (entity.getArrowType() == CustomArrowType.FIREBRANDS) {
            float time = (entity.tickCount + partialTick) * -30.0F;
            poseStack.mulPose(Axis.XP.rotationDegrees(time));
        }

        poseStack.translate(-0.5D, -0.5D, 0.0D);
        RenderUtils.renderBakedModel(
            model,
            entity.level().getRandom(),
            packedLight,
            poseStack,
            buffer.getBuffer(RenderType.cutout())
        );
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(CustomArrowEntity entity) {
        return null;
    }
}
