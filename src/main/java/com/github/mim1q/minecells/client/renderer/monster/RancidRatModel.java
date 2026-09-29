package com.github.mim1q.minecells.client.renderer.monster;

import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;
import com.github.mim1q.minecells.entity.RancidRatEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.util.Mth;

public class RancidRatModel extends EntityModel<MineCellsMonsterEntity> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart leftHindLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart[] tail = new ModelPart[3];

    public RancidRatModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.head = this.body.getChild("head");
        this.leftHindLeg = this.body.getChild("left_hind_leg");
        this.rightHindLeg = this.body.getChild("right_hind_leg");
        this.leftFrontLeg = this.body.getChild("left_front_leg");
        this.rightFrontLeg = this.body.getChild("right_front_leg");
        this.tail[0] = this.body.getChild("tail_0");
        this.tail[1] = this.tail[0].getChild("tail_1");
        this.tail[2] = this.tail[1].getChild("tail_2");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition dRoot = root.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition dBody = dRoot.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.0F, -5.0F, -5.0F, 6.0F, 5.0F, 10.0F)
                .texOffs(0, 15).addBox(-4.0F, -9.0F, 0.5F, 5.0F, 5.0F, 5.0F)
                .texOffs(28, 15).addBox(1.0F, -6.0F, 1.5F, 3.0F, 3.0F, 3.0F)
                .addBox(-4.5F, -6.0F, -3.0F, 3.0F, 3.0F, 3.0F)
                .texOffs(22, 0).addBox(0.0F, -7.5F, -4.0F, 4.0F, 4.0F, 4.0F),
            PartPose.offset(0.0F, -3.0F, 3.0F)
        );
        dBody.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
                .texOffs(14, 19).addBox(-2.0F, -2.0F, -6.0F, 4.0F, 4.0F, 6.0F)
                .texOffs(14, 29).addBox(-3.5F, -4.0F, -1.0F, 7.0F, 4.0F, 0.0F, new CubeDeformation(0.01F)),
            PartPose.offset(0.0F, -2.0F, -5.0F)
        );
        dBody.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(-2.0F, 0.0F, 3.0F));
        dBody.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(2.0F, 0.0F, 3.0F));
        dBody.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(-2.0F, 0.0F, -4.0F));
        dBody.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(2.0F, 0.0F, -4.0F));
        PartDefinition dTail0 = dBody.addOrReplaceChild("tail_0", CubeListBuilder.create().texOffs(0, 25).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 6.0F), PartPose.offset(0.0F, -3.5F, 5.0F));
        PartDefinition dTail1 = dTail0.addOrReplaceChild("tail_1", CubeListBuilder.create().texOffs(0, 25).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 6.0F), PartPose.offset(0.0F, 0.0F, 6.0F));
        dTail1.addOrReplaceChild("tail_2", CubeListBuilder.create().texOffs(0, 25).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 6.0F), PartPose.offset(0.0F, 0.0F, 6.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(MineCellsMonsterEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        MineCellsModelAnimationUtils.rotateHead(netHeadYaw, headPitch, this.head);
        this.rightFrontLeg.xRot = Mth.sin(limbSwing) * limbSwingAmount * 45.0F * Mth.DEG_TO_RAD;
        this.leftFrontLeg.xRot = -this.rightFrontLeg.xRot;
        this.rightHindLeg.xRot = -this.rightFrontLeg.xRot;
        this.leftHindLeg.xRot = this.rightFrontLeg.xRot;
        this.body.xRot = Mth.sin(limbSwing) * limbSwingAmount * 10.0F * Mth.DEG_TO_RAD;
        this.root.y = 24.0F - Mth.abs(Mth.sin(limbSwing * 0.5F)) * limbSwingAmount;

        float multiplier = 1.0F - limbSwingAmount;
        for (int i = 0; i < 3; i++) {
            this.tail[i].xRot = (float) Math.sin(ageInTicks * 0.5F - i) * 10.0F * Mth.DEG_TO_RAD * limbSwingAmount;
            this.tail[i].xRot += (float) Math.sin(ageInTicks * 0.25F - i) * 5.0F * Mth.DEG_TO_RAD * multiplier;
        }
        this.tail[0].xRot -= 25.0F * Mth.DEG_TO_RAD * multiplier;
        this.tail[1].xRot += 15.0F * Mth.DEG_TO_RAD * multiplier;
        this.tail[2].xRot += 15.0F * Mth.DEG_TO_RAD * multiplier;

        if (entity instanceof RancidRatEntity rat) {
            rat.torsoRotation.update(ageInTicks);
            float torso = rat.torsoRotation.getValue() * Mth.DEG_TO_RAD;
            this.body.xRot += torso;
            this.head.xRot -= torso;
            this.tail[0].xRot -= torso;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
