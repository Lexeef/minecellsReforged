package com.github.mim1q.minecells.client.renderer.monster;

import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class UndeadArcherModel extends EntityModel<MineCellsMonsterEntity> implements ArmedModel {
    private final ModelPart root;
    private final ModelPart waist;
    private final ModelPart torso;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart head;

    public UndeadArcherModel(ModelPart root) {
        this.root = root.getChild("root");
        this.waist = this.root.getChild("waist");
        this.rightLeg = waist.getChild("right_leg");
        this.leftLeg = waist.getChild("left_leg");
        this.torso = waist.getChild("torso");
        this.leftArm = torso.getChild("left_arm");
        this.rightArm = torso.getChild("right_arm");
        this.head = torso.getChild("neck").getChild("head");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition dRoot = root.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition dWaist = dRoot.addOrReplaceChild("waist", CubeListBuilder.create().texOffs(27, 11).addBox(-4.0F, -2.0F, -2.5F, 8.0F, 2.0F, 5.0F), PartPose.offset(0.0F, -13.0F, 0.0F));
        dWaist.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 46).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 14.0F, 2.0F).texOffs(24, 40).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 9.0F, 4.0F), PartPose.offset(-2.0F, 0.0F, 0.0F));
        dWaist.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(8, 46).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 14.0F, 2.0F).texOffs(50, 29).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 9.0F, 4.0F), PartPose.offset(2.0F, 0.0F, 0.0F));
        PartDefinition dTorso = dWaist.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(50, 0).addBox(-1.0F, -9.0F, -1.0F, 2.0F, 10.0F, 2.0F).texOffs(23, 29).addBox(-4.0F, -9.0F, -3.5F, 8.0F, 6.0F, 5.0F), PartPose.offset(0.0F, -2.0F, 0.5F));
        dTorso.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(16, 46).addBox(0.25F, -1.0F, -1.0F, 2.0F, 12.0F, 2.0F).texOffs(30, 0).addBox(-0.5F, -2.0F, -1.5F, 3.0F, 5.0F, 3.0F), PartPose.offset(4.0F, -8.0F, -1.0F));
        dTorso.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(45, 18).addBox(-2.0F, -1.0F, -1.0F, 2.0F, 12.0F, 2.0F).texOffs(33, 21).addBox(-2.5F, -2.0F, -1.5F, 3.0F, 5.0F, 3.0F), PartPose.offset(-4.0F, -8.0F, -1.0F));
        dTorso.addOrReplaceChild("scarf", CubeListBuilder.create().texOffs(0, 11).addBox(-4.5F, -2.0F, -4.5F, 9.0F, 4.0F, 9.0F), PartPose.offsetAndRotation(0.0F, -9.0F, -1.0F, 30.0F * Mth.DEG_TO_RAD, 0.0F, 0.0F));
        PartDefinition dNeck = dTorso.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -9.0F, -2.0F));
        dNeck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 34).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F).texOffs(36, 51).addBox(-3.5F, -6.0F, -3.5F, 7.0F, 6.0F, 7.0F).texOffs(0, 0).addBox(-5.0F, -7.0F, -5.0F, 10.0F, 1.0F, 10.0F).texOffs(0, 24).addBox(-3.0F, -9.0F, -3.0F, 6.0F, 2.0F, 8.0F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(MineCellsMonsterEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        root.getAllParts().forEach(ModelPart::resetPose);
        MineCellsModelAnimationUtils.rotateHead(netHeadYaw, headPitch, head);
        MineCellsModelAnimationUtils.bipedWalk(limbSwing, limbSwingAmount, root, rightLeg, leftLeg, rightArm, leftArm, null, null);
        leftArm.zRot = 0.0F;
        leftArm.yRot = 0.0F;
        rightArm.xRot *= 0.2F;
        rightArm.xRot -= 60.0F * Mth.DEG_TO_RAD;
        rightArm.yRot = 0.0F;
        leftArm.z = -1.0F;

        if (entity instanceof com.github.mim1q.minecells.entity.UndeadArcherEntity archer) {
            float rightArmPitch = -90.0F * Mth.DEG_TO_RAD;
            float rightArmYaw = -15.0F * Mth.DEG_TO_RAD;
            float leftArmPitch = -90.0F * Mth.DEG_TO_RAD;
            float leftArmYaw = 30.0F * Mth.DEG_TO_RAD;

            archer.handsUpProgess.update(ageInTicks);
            float delta = archer.handsUpProgess.getValue();
            archer.pullProgress.update(ageInTicks);
            float deltaPull = archer.pullProgress.getValue();

            leftArm.z = 2.0F - delta * 4.0F;
            leftArm.x = 4.0F + delta;
            leftArm.yRot = (15.0F * delta + 15.0F * deltaPull) * Mth.DEG_TO_RAD;

            rightArm.xRot = rightArmPitch * delta;
            leftArm.xRot = leftArmPitch * delta;
            leftArm.yRot += leftArmYaw * delta;
            rightArm.yRot += rightArmYaw * delta;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
        poseStack.translate(0.0F, 0.5F, 0.0F);
        (arm == HumanoidArm.RIGHT ? rightArm : leftArm).translateAndRotate(poseStack);
    }
}
