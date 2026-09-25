package com.github.mim1q.minecells.client.renderer.boss;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ConjunctiviusSpikeRenderer extends RenderLayer<ConjunctiviusEntity, ConjunctiviusEntityModel> {
    public static final ResourceLocation TEXTURE = MineCells.id("textures/entity/conjunctivius/spike.png");

    private final ConjunctiviusSpikeModel model;
    private final List<MathUtils.PosRotScale> posRotScales = new ArrayList<>();

    public ConjunctiviusSpikeRenderer(RenderLayerParent<ConjunctiviusEntity, ConjunctiviusEntityModel> parent, ModelPart spikeRoot) {
        super(parent);
        this.model = new ConjunctiviusSpikeModel(spikeRoot);
    }

    public void addPosRotScale(float px, float py, float pz, float rx, float ry, float rz, float s) {
        posRotScales.add(MathUtils.PosRotScale.ofDegrees(px, py, pz, rx, ry, rz, s, s, s));
    }

    @Override
    public void render(
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight,
        ConjunctiviusEntity entity,
        float limbSwing,
        float limbSwingAmount,
        float partialTicks,
        float ageInTicks,
        float netHeadYaw,
        float headPitch
    ) {
        for (MathUtils.PosRotScale posRotScale : posRotScales) {
            poseStack.pushPose();
            posRotScale.apply(poseStack);
            model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            boolean hurt = entity.hurtTime > 0;
            model.renderToBuffer(
                poseStack,
                buffer.getBuffer(model.renderType(TEXTURE)),
                packedLight,
                OverlayTexture.pack(0.0F, hurt),
                1.0F, 1.0F, 1.0F, 1.0F
            );
            poseStack.popPose();
        }
    }

    public static class ConjunctiviusSpikeModel extends EntityModel<ConjunctiviusEntity> {
        private final ModelPart base;
        private final ModelPart spike;

        public ConjunctiviusSpikeModel(ModelPart root) {
            super(RenderType::entityCutout);
            this.base = root.getChild("base");
            this.spike = this.base.getChild("spike");
        }

        public static LayerDefinition createLayer() {
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition root = mesh.getRoot();
            PartDefinition dBase = root.addOrReplaceChild(
                "base",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -2.0F, -2.5F, 5, 6, 5, new CubeDeformation(0.05F)),
                PartPose.ZERO
            );
            dBase.addOrReplaceChild(
                "spike",
                CubeListBuilder.create()
                    .texOffs(0, 11).addBox(-2.5F, -10.0F, 0.0F, 5, 8, 0)
                    .texOffs(0, 6).addBox(0.0F, -10.0F, -2.5F, 0, 8, 5),
                PartPose.ZERO
            );
            return LayerDefinition.create(mesh, 32, 32);
        }

        @Override
        public void setupAnim(ConjunctiviusEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            entity.spikeOffset.update(ageInTicks);
            spike.y = entity.spikeOffset.getValue();
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            base.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }
}
