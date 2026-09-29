package com.github.mim1q.minecells.client.renderer.projectile;

import com.github.mim1q.minecells.entity.nonliving.TentacleWeaponEntity;
import com.github.mim1q.minecells.MineCells;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import org.joml.Quaternionf;

public class TentacleWeaponEntityRenderer extends EntityRenderer<TentacleWeaponEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(MineCells.id("tentacle_weapon"), "main");
    private static final ResourceLocation TEXTURE = MineCells.id("textures/entity/tentacle_weapon.png");

    private final ModelPart main;

    public TentacleWeaponEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.main = context.bakeLayer(LAYER);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 16.0F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void render(TentacleWeaponEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float length = entity.getLength(partialTick);
        if (entity.tickCount < 2 || length < 0.01F) {
            return;
        }

        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        Vec3 normalized = entity.getEndPos(1.0F).subtract(entity.getPosition(partialTick)).normalize();
        float xRot = (float) Math.asin(-normalized.y);
        float yRot = (float) Math.atan2(normalized.x, normalized.z);
        poseStack.mulPose(new Quaternionf().rotationYXZ(-yRot, -xRot, 0.0F));

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        renderSegment(poseStack, consumer, packedLight, length * 0.25F, 1.0F);
        poseStack.translate(0.0D, 0.0D, 0.01D);
        renderSegment(poseStack, consumer, packedLight, length * 0.5F, 0.8F);
        poseStack.translate(0.0D, 0.0D, 0.01D);
        renderSegment(poseStack, consumer, packedLight, length * 0.75F, 0.6F);
        poseStack.translate(0.0D, 0.0D, 0.01D);
        renderSegment(poseStack, consumer, packedLight, length, 0.4F);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private void renderSegment(PoseStack poseStack, VertexConsumer consumer, int packedLight, float length, float scale) {
        poseStack.pushPose();
        float size = Mth.lerp(easeInOutQuad(length), 1.1F, 0.5F) * scale;
        poseStack.scale(size, size, length * 16.0F);
        main.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private static float easeInOutQuad(float t) {
        return t < 0.5F ? 2.0F * t * t : 1.0F - (float) Math.pow(-2.0D * t + 2.0D, 2.0D) / 2.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(TentacleWeaponEntity entity) {
        return TEXTURE;
    }
}
