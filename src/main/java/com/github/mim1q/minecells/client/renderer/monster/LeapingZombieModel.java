package com.github.mim1q.minecells.client.renderer.monster;

import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class LeapingZombieModel extends EntityModel<MineCellsMonsterEntity> {
    private final ModelPart root;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart lowerTorso;
    private final ModelPart upperTorso;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart neck;
    private final ModelPart head;

    public LeapingZombieModel(ModelPart root) {
        this.root = root.getChild("root");
        ModelPart waist = this.root.getChild("waist");
        this.leftLeg = waist.getChild("left_leg");
        this.rightLeg = waist.getChild("right_leg");
        this.lowerTorso = waist.getChild("lower_torso");
        this.upperTorso = this.lowerTorso.getChild("upper_torso");
        this.leftArm = this.upperTorso.getChild("left_arm");
        this.rightArm = this.upperTorso.getChild("right_arm");
        this.neck = this.upperTorso.getChild("neck");
        this.head = this.neck.getChild("head");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition dRoot = root.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition dWaist = dRoot.addOrReplaceChild("waist", CubeListBuilder.create().texOffs(20, 20).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 3.0F, 4.0F), PartPose.offset(0.0F, -15.0F, 0.0F));
        dWaist.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(26, 0).addBox(0.0F, -1.0F, 0.0F, 3.0F, 14.0F, 3.0F), PartPose.offset(0.5F, 2.0F, -1.5F));
        dWaist.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 24).addBox(0.0F, -1.0F, 0.0F, 3.0F, 14.0F, 3.0F), PartPose.offset(-3.5F, 2.0F, -1.5F));

        PartDefinition dLowerTorso = dWaist.addOrReplaceChild("lower_torso", CubeListBuilder.create().texOffs(32, 27).addBox(-3.0F, -5.0F, -1.5F, 6.0F, 6.0F, 3.0F), PartPose.ZERO);
        PartDefinition dUpperTorso = dLowerTorso.addOrReplaceChild("upper_torso", CubeListBuilder.create().texOffs(0, 1).addBox(-4.0F, -4.0F, -2.5F, 8.0F, 5.0F, 5.0F), PartPose.offset(0.0F, -5.0F, 0.0F));

        dUpperTorso.addOrReplaceChild("wing_1", CubeListBuilder.create().texOffs(38, 0).addBox(0.0F, -2.0F, 0.0F, 1.0F, 2.0F, 3.0F), PartPose.offsetAndRotation(2.0F, -2.0F, 2.0F, Mth.PI / 6.0F, 0.0F, 0.1F));
        dUpperTorso.addOrReplaceChild("wing_2", CubeListBuilder.create().texOffs(38, 0).addBox(-1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 3.0F), PartPose.offsetAndRotation(-2.0F, -2.0F, 2.0F, Mth.PI / 6.0F, 0.0F, -0.1F));
        dUpperTorso.addOrReplaceChild("wing_3", CubeListBuilder.create().texOffs(38, 0).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 3.0F), PartPose.offsetAndRotation(2.0F, -1.0F, 2.0F, -Mth.PI / 6.0F, 0.0F, 0.1F));
        dUpperTorso.addOrReplaceChild("wing_4", CubeListBuilder.create().texOffs(38, 0).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 3.0F), PartPose.offsetAndRotation(-2.0F, -1.0F, 2.0F, -Mth.PI / 6.0F, 0.0F, -0.1F));
        dUpperTorso.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(22, 27).addBox(0.0F, -1.5F, -1.5F, 2.0F, 12.0F, 3.0F), PartPose.offset(4.0F, -2.5F, 0.0F));
        dUpperTorso.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(12, 24).addBox(-2.0F, -1.5F, -1.5F, 2.0F, 12.0F, 3.0F), PartPose.offset(-4.0F, -2.5F, 0.0F));

        PartDefinition dNeck = dUpperTorso.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0F, -0.5F));
        dNeck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 11).addBox(-3.0F, -7.0F, -3.0F, 6.0F, 7.0F, 6.0F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(MineCellsMonsterEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        MineCellsModelAnimationUtils.rotateHead(netHeadYaw, headPitch, head);
        MineCellsModelAnimationUtils.bipedWalk(limbSwing, limbSwingAmount, root, rightLeg, leftLeg, rightArm, leftArm, lowerTorso, upperTorso);
        upperTorso.xRot *= 10.0F;
        lowerTorso.xRot *= 5.0F;
        lowerTorso.xRot += 10.0F * Mth.DEG_TO_RAD;
        upperTorso.xRot += 10.0F * Mth.DEG_TO_RAD;
        leftArm.xRot -= 20.0F * Mth.DEG_TO_RAD;
        rightArm.xRot -= 20.0F * Mth.DEG_TO_RAD;
        neck.xRot = -20.0F * Mth.DEG_TO_RAD;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
