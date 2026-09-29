package com.github.mim1q.minecells.client.renderer.monster;

import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;
import com.github.mim1q.minecells.entity.ScorpionEntity;
import com.github.mim1q.minecells.util.MathUtils;

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

public class ScorpionModel extends EntityModel<MineCellsMonsterEntity> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart[] tail = new ModelPart[5];
    private boolean shouldRender = true;

    public ScorpionModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.head = this.body.getChild("head");
        this.leftHindLeg = this.root.getChild("left_hind_leg");
        this.leftFrontLeg = this.root.getChild("left_front_leg");
        this.rightHindLeg = this.root.getChild("right_hind_leg");
        this.rightFrontLeg = this.root.getChild("right_front_leg");
        this.tail[0] = this.body.getChild("tail_0");
        for (int i = 1; i < this.tail.length; i++) {
            this.tail[i] = this.tail[i - 1].getChild("tail_" + i);
        }
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition dRoot = root.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition dBody = dRoot.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -6.0F, -6.0F, 8.0F, 6.0F, 12.0F), PartPose.offset(0.0F, -4.0F, 0.0F));
        dBody.addOrReplaceChild("plate_0", CubeListBuilder.create().texOffs(28, 0).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 2.0F, 9.0F, new CubeDeformation(0.02F)), PartPose.offsetAndRotation(0.0F, -6.0F, -6.0F, 40.0F * Mth.DEG_TO_RAD, 0.0F, 0.0F));
        dBody.addOrReplaceChild("plate_1", CubeListBuilder.create().texOffs(28, 0).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 2.0F, 9.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -6.0F, -3.0F, 25.0F * Mth.DEG_TO_RAD, 0.0F, 0.0F));
        dRoot.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 14).addBox(0.0F, 0.0F, -2.0F, 0.0F, 9.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(4.5F, -9.0F, -5.5F, 0.0F, 25.0F * Mth.DEG_TO_RAD, 0.0F));
        dRoot.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(0, 14).addBox(0.0F, 0.0F, -2.0F, 0.0F, 9.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(4.5F, -9.0F, 5.5F, 0.0F, -25.0F * Mth.DEG_TO_RAD, 0.0F));
        dRoot.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 14).addBox(0.0F, 0.0F, -2.0F, 0.0F, 9.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-4.5F, -9.0F, -5.5F, 0.0F, -25.0F * Mth.DEG_TO_RAD, 0.0F));
        dRoot.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(0, 14).addBox(0.0F, 0.0F, -2.0F, 0.0F, 9.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-4.5F, -9.0F, 5.5F, 0.0F, 25.0F * Mth.DEG_TO_RAD, 0.0F));
        PartDefinition dHead = dBody.addOrReplaceChild("head", CubeListBuilder.create().texOffs(26, 37).addBox(-3.5F, -3.5F, -5.0F, 7.0F, 7.0F, 5.0F), PartPose.offset(0.0F, -3.0F, -6.0F));
        PartDefinition dUpperRightMandible = dHead.addOrReplaceChild("upper_right_mandible", CubeListBuilder.create().texOffs(0, 45).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 3.0F, 1.0F), PartPose.offsetAndRotation(-2.5F, -3.0F, -5.0F, 45.0F * Mth.DEG_TO_RAD, 0.0F, -30.0F * Mth.DEG_TO_RAD));
        dUpperRightMandible.addOrReplaceChild("upper_right_spike", CubeListBuilder.create().texOffs(6, 45).addBox(-0.5F, -2.0F, -1.0F, 1.0F, 2.0F, 1.0F), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 45.0F * Mth.DEG_TO_RAD, 0.0F, 0.0F));
        PartDefinition dUpperLeftMandible = dHead.addOrReplaceChild("upper_left_mandible", CubeListBuilder.create().texOffs(0, 45).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 3.0F, 1.0F), PartPose.offsetAndRotation(2.5F, -3.0F, -5.0F, 45.0F * Mth.DEG_TO_RAD, 0.0F, 30.0F * Mth.DEG_TO_RAD));
        dUpperLeftMandible.addOrReplaceChild("upper_left_spike", CubeListBuilder.create().texOffs(6, 45).addBox(-0.5F, -2.0F, -1.0F, 1.0F, 2.0F, 1.0F), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 45.0F * Mth.DEG_TO_RAD, 0.0F, 0.0F));
        PartDefinition dLowerLeftMandible = dHead.addOrReplaceChild("lower_left_mandible", CubeListBuilder.create().texOffs(0, 45).mirror().addBox(-1.0F, -1.0F, -1.0F, 2.0F, 3.0F, 1.0F).mirror(false), PartPose.offsetAndRotation(2.5F, 3.0F, -5.0F, -45.0F * Mth.DEG_TO_RAD, 0.0F, -30.0F * Mth.DEG_TO_RAD));
        dLowerLeftMandible.addOrReplaceChild("lower_left_spike", CubeListBuilder.create().texOffs(6, 45).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -45.0F * Mth.DEG_TO_RAD, 0.0F, 0.0F));
        PartDefinition dLowerRightMandible = dHead.addOrReplaceChild("lower_right_mandible", CubeListBuilder.create().texOffs(0, 45).mirror().addBox(-1.0F, -1.0F, -1.0F, 2.0F, 3.0F, 1.0F).mirror(false), PartPose.offsetAndRotation(-2.5F, 3.0F, -5.0F, -45.0F * Mth.DEG_TO_RAD, 0.0F, 30.0F * Mth.DEG_TO_RAD));
        dLowerRightMandible.addOrReplaceChild("lower_right_spike", CubeListBuilder.create().texOffs(6, 45).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -45.0F * Mth.DEG_TO_RAD, 0.0F, 0.0F));
        PartDefinition dTail0 = dBody.addOrReplaceChild("tail_0", CubeListBuilder.create().texOffs(0, 18).addBox(-3.0F, -5.0F, 0.0F, 6.0F, 5.0F, 10.0F, new CubeDeformation(0.02F)), PartPose.offset(0.0F, -1.0F, 6.0F));
        PartDefinition dTail1 = dTail0.addOrReplaceChild("tail_1", CubeListBuilder.create().texOffs(0, 18).mirror().addBox(-3.0F, -5.0F, 0.0F, 6.0F, 5.0F, 10.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(0.0F, 0.0F, 10.0F));
        PartDefinition dTail2 = dTail1.addOrReplaceChild("tail_2", CubeListBuilder.create().texOffs(22, 23).addBox(-3.0F, -4.0F, 0.0F, 6.0F, 4.0F, 10.0F), PartPose.offset(0.0F, 0.0F, 10.0F));
        PartDefinition dTail3 = dTail2.addOrReplaceChild("tail_3", CubeListBuilder.create().texOffs(34, 12).addBox(-2.5F, -4.0F, 0.0F, 5.0F, 4.0F, 6.0F), PartPose.offset(0.0F, 0.0F, 10.0F));
        dTail3.addOrReplaceChild(
            "tail_4",
            CubeListBuilder.create()
                .texOffs(0, 33).addBox(-3.0F, -5.0F, 0.0F, 6.0F, 6.0F, 6.0F)
                .texOffs(0, 0).addBox(0.0F, -5.0F, 6.0F, 0.0F, 5.0F, 3.0F, new CubeDeformation(0.01F)),
            PartPose.offset(0.0F, 0.0F, 6.0F)
        );
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(MineCellsMonsterEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        MineCellsModelAnimationUtils.rotateHead(netHeadYaw, headPitch, this.head);
        this.body.xRot = 0.0F;
        this.root.y = 24.0F;
        this.shouldRender = true;

        float[] baseTailAngles = {55.0F, 30.0F, 30.0F, 45.0F, 35.0F};
        for (int i = 0; i < this.tail.length; i++) {
            float deltaPitch = Mth.sin(limbSwing * 0.5F) * limbSwingAmount;
            this.tail[i].xRot = baseTailAngles[i] * Mth.DEG_TO_RAD + deltaPitch * MathUtils.radians(5.0F);
            float deltaRoll = Mth.sin(limbSwing * 0.25F) * limbSwingAmount;
            this.tail[i].zRot = deltaRoll * MathUtils.radians(5.0F);
            this.tail[i].yRot = this.tail[i].zRot;
        }

        float deltaPivotY = Mth.sin(limbSwing * 0.5F) * limbSwingAmount * 3.0F;
        float deltaPivotZ = -Mth.cos(limbSwing * 0.5F) * limbSwingAmount * 2.0F;
        this.leftHindLeg.y = -9.0F - Math.max(0.0F, deltaPivotY);
        this.rightHindLeg.y = -9.0F - Math.max(0.0F, -deltaPivotY);
        this.leftFrontLeg.y = this.rightHindLeg.y;
        this.rightFrontLeg.y = this.leftHindLeg.y;
        this.leftHindLeg.z = 5.5F - deltaPivotZ;
        this.rightHindLeg.z = 5.5F + deltaPivotZ;
        this.leftFrontLeg.z = -5.5F + deltaPivotZ;
        this.rightFrontLeg.z = -5.5F - deltaPivotZ;

        if (entity instanceof ScorpionEntity scorpion) {
            this.shouldRender = !scorpion.isSleeping();
            scorpion.buriedProgress.update(ageInTicks);
            scorpion.swingProgress.update(ageInTicks);
            this.root.y = 24.0F + scorpion.buriedProgress.getValue() * 32.0F;

            float swing = scorpion.swingProgress.getValue();
            float shake = Mth.sin(ageInTicks * 10.0F);
            this.body.xRot = MathUtils.lerp(0.0F, MathUtils.radians(-30.0F), swing);
            this.head.xRot -= MathUtils.lerp(0.0F, MathUtils.radians(-30.0F + shake * 5.0F), swing);
            for (int i = 0; i < this.tail.length; i++) {
                float target = i == 0 ? MathUtils.radians(30.0F) : MathUtils.radians(5.0F);
                this.tail[i].xRot = MathUtils.lerp(this.tail[i].xRot, target, swing);
            }
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (this.shouldRender) {
            this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }
}
