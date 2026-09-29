package com.github.mim1q.minecells.client.renderer.monster;

import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.util.Mth;

public class DisgustingWormModel extends EntityModel<MineCellsMonsterEntity> {
    private final ModelPart head;
    private final ModelPart frontSegment;
    private final ModelPart middleSegment;
    private final ModelPart backSegment;

    private float limbSwing = 0.0F;
    private float limbSwingAmount = 0.0F;

    public DisgustingWormModel(ModelPart root) {
        root.y = 24.0F;
        this.head = root.getChild("head");
        this.frontSegment = root.getChild("front_segment");
        this.middleSegment = root.getChild("middle_segment");
        this.backSegment = root.getChild("back_segment");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
                .texOffs(0, 35).addBox(-5.0F, -3.0F, -4.0F, 10.0F, 3.0F, 4.0F)
                .texOffs(32, 0).addBox(-5.0F, -10.0F, -4.0F, 10.0F, 3.0F, 4.0F)
                .texOffs(43, 31).addBox(-5.0F, -7.0F, -4.0F, 3.0F, 4.0F, 4.0F)
                .texOffs(16, 42).addBox(2.0F, -7.0F, -4.0F, 3.0F, 4.0F, 4.0F)
                .texOffs(45, 50).addBox(-2.0F, -7.0F, -3.0F, 4.0F, 4.0F, 0.0F)
                .texOffs(0, 29).addBox(-2.0F, -7.0F, -1.0F, 4.0F, 4.0F, 1.0F),
            PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        PartDefinition dFrontSegment = root.addOrReplaceChild(
            "front_segment",
            CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -9.0F, 0.0F, 10.0F, 9.0F, 6.0F),
            PartPose.offset(0.0F, 24.0F, 0.0F)
        );
        dFrontSegment.addOrReplaceChild(
            "egg_1",
            CubeListBuilder.create().texOffs(22, 23).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F),
            PartPose.offsetAndRotation(3.0F, -10.0F, 3.0F, -5.0F * Mth.DEG_TO_RAD, 10.0F * Mth.DEG_TO_RAD, 10.0F * Mth.DEG_TO_RAD)
        );
        dFrontSegment.addOrReplaceChild(
            "egg_2",
            CubeListBuilder.create().texOffs(28, 35).addBox(-2.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F),
            PartPose.offsetAndRotation(-3.5F, -9.0F, 4.5F, 0.0F, 45.0F * Mth.DEG_TO_RAD, 10.0F * Mth.DEG_TO_RAD)
        );
        dFrontSegment.addOrReplaceChild(
            "egg_3",
            CubeListBuilder.create().texOffs(44, 7).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F),
            PartPose.offsetAndRotation(4.5F, -2.5F, 5.5F, 10.0F * Mth.DEG_TO_RAD, -10.0F * Mth.DEG_TO_RAD, 0.0F)
        );

        PartDefinition dMiddleSegment = root.addOrReplaceChild(
            "middle_segment",
            CubeListBuilder.create().texOffs(0, 15).addBox(-4.0F, -8.0F, 0.0F, 8.0F, 8.0F, 6.0F),
            PartPose.offset(0.0F, 24.0F, 0.0F)
        );
        dMiddleSegment.addOrReplaceChild(
            "egg_4",
            CubeListBuilder.create().texOffs(28, 35).addBox(-2.5F, -2.5F, -2.75F, 5.0F, 5.0F, 5.0F),
            PartPose.offsetAndRotation(2.5F, -8.5F, 3.5F, 0.0F, -35.0F * Mth.DEG_TO_RAD, 0.0F)
        );
        dMiddleSegment.addOrReplaceChild(
            "egg_5",
            CubeListBuilder.create().texOffs(0, 42).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F),
            PartPose.offsetAndRotation(-2.5F, -8.5F, 3.0F, -20.0F * Mth.DEG_TO_RAD, 0.0F, -15.0F * Mth.DEG_TO_RAD)
        );
        dMiddleSegment.addOrReplaceChild(
            "egg_6",
            CubeListBuilder.create().texOffs(44, 7).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F),
            PartPose.offsetAndRotation(-3.5F, -2.5F, 6.0F, -10.0F * Mth.DEG_TO_RAD, 15.0F * Mth.DEG_TO_RAD, 0.0F)
        );

        PartDefinition dBackSegment = root.addOrReplaceChild(
            "back_segment",
            CubeListBuilder.create().texOffs(26, 9).addBox(-3.0F, -5.0F, 0.0F, 6.0F, 5.0F, 6.0F),
            PartPose.offset(0.0F, 24.0F, 0.0F)
        );
        dBackSegment.addOrReplaceChild(
            "egg_7",
            CubeListBuilder.create().texOffs(30, 45).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F),
            PartPose.offsetAndRotation(2.0F, -5.5F, 1.5F, -10.0F * Mth.DEG_TO_RAD, -15.0F * Mth.DEG_TO_RAD, 10.0F * Mth.DEG_TO_RAD)
        );
        dBackSegment.addOrReplaceChild(
            "egg_8",
            CubeListBuilder.create().texOffs(0, 42).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F),
            PartPose.offsetAndRotation(-2.0F, -6.0F, 2.0F, -10.0F * Mth.DEG_TO_RAD, -20.0F * Mth.DEG_TO_RAD, 0.0F)
        );
        dBackSegment.addOrReplaceChild(
            "egg_9",
            CubeListBuilder.create().texOffs(44, 7).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F),
            PartPose.offsetAndRotation(2.5F, -2.5F, 5.5F, -20.0F * Mth.DEG_TO_RAD, -35.0F * Mth.DEG_TO_RAD, 0.0F)
        );

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(MineCellsMonsterEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float scale1 = Mth.sin(this.limbSwing) * this.limbSwingAmount * 0.5F;
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, -0.4F);
        poseStack.scale(1.0F, 1.0F, 1.0F + scale1);
        this.frontSegment.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();

        poseStack.pushPose();
        float scale2 = Mth.sin(this.limbSwing + Mth.PI * 0.33F) * this.limbSwingAmount * 0.5F;
        poseStack.translate(0.0F, 0.0F, -0.025F + scale1 * 0.375F);
        poseStack.scale(1.0F, 1.0F, 1.0F + scale2);
        this.middleSegment.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();

        poseStack.pushPose();
        float scale3 = Mth.sin(this.limbSwing + Mth.PI * 0.66F) * this.limbSwingAmount * 0.5F;
        poseStack.translate(0.0F, 0.0F, 0.35F + (scale1 + scale2) * 0.375F);
        poseStack.scale(1.0F, 1.0F, 1.0F + scale3);
        this.backSegment.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.0F, 1.5F, -0.4F);
        poseStack.scale(1.0F, 1.0F + scale2 * 0.25F, 1.0F);
        this.head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }
}
