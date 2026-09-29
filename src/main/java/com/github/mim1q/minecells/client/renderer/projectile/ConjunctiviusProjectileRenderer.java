package com.github.mim1q.minecells.client.renderer.projectile;

import com.github.mim1q.minecells.entity.nonliving.projectile.ConjunctiviusProjectileEntity;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.util.MathUtils;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
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

public class ConjunctiviusProjectileRenderer extends EntityRenderer<ConjunctiviusProjectileEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(MineCells.id("conjunctivius_projectile"), "main");
    private static final ResourceLocation TEXTURE = MineCells.id("textures/entity/conjunctivius/projectile.png");

    private final ModelPart root;
    private final ModelPart main;

    public ConjunctiviusProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.root = context.bakeLayer(LAYER);
        this.main = root.getChild("main");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild("main",
            CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 12.0F),
            PartPose.ZERO
        );
        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void render(ConjunctiviusProjectileEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        root.xRot = MathUtils.radians(-180.0F + entity.getXRot());
        root.yRot = MathUtils.radians(-entity.getYRot());
        main.zRot = ((int) (-entity.tickCount * 0.5F) % 4) * Mth.HALF_PI;
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.25F, 0.0F);
        root.render(poseStack, buffers.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ConjunctiviusProjectileEntity entity) {
        return TEXTURE;
    }
}
