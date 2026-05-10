package com.github.mim1q.minecells.client.renderer.monster;

import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;
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
import net.minecraft.util.Mth;

public class RunnerModel extends EntityModel<MineCellsMonsterEntity> {
    private final ModelPart root;
    private final ModelPart lowerTorso;
    private final ModelPart upperTorso;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart neck;
    private final ModelPart head;

    public RunnerModel(ModelPart root) {
        this.root = root.getChild("root");
        this.lowerTorso = this.root.getChild("lower_torso");
        this.upperTorso = this.lowerTorso.getChild("upper_torso");
        this.leftLeg = this.root.getChild("left_leg");
        this.rightLeg = this.root.getChild("right_leg");
        this.leftArm = this.upperTorso.getChild("left_arm");
        this.rightArm = this.upperTorso.getChild("right_arm");
        this.neck = this.upperTorso.getChild("neck");
        this.head = this.neck.getChild("head");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition dRoot = root.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition dLowerTorso = dRoot.addOrReplaceChild("lower_torso", CubeListBuilder.create().texOffs(32, 25).addBox(-3.5F, -4.0F, -1.5F, 7.0F, 4.0F, 3.0F), PartPose.offset(0.0F, -18.0F, 0.0F));
        dRoot.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(32, 32).addBox(-1.5F, 8.0F, -1.5F, 3.0F, 10.0F, 3.0F).texOffs(16, 25).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F), PartPose.offset(2.0F, -18.0F, 0.0F));
        dRoot.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(32, 32).mirror().addBox(-1.5F, 8.0F, -1.5F, 3.0F, 10.0F, 3.0F).texOffs(16, 25).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F), PartPose.offset(-2.0F, -18.0F, 0.0F));
        PartDefinition dUpperTorso = dLowerTorso.addOrReplaceChild("upper_torso", CubeListBuilder.create().texOffs(0, 14).addBox(-4.0F, -5.0F, -2.5F, 8.0F, 5.0F, 5.0F).texOffs(21, 7).addBox(3.0F, -5.0F, -3.5F, 5.0F, 2.0F, 7.0F).texOffs(20, 18).addBox(2.5F, -6.0F, -3.0F, 7.0F, 1.0F, 6.0F).texOffs(21, 7).mirror().addBox(-8.0F, -5.0F, -3.5F, 5.0F, 2.0F, 7.0F).texOffs(20, 18).mirror().addBox(-9.5F, -6.0F, -3.0F, 7.0F, 1.0F, 6.0F), PartPose.offset(0.0F, -4.0F, 0.0F));
        dUpperTorso.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(10, 39).addBox(0.0F, -1.0F, -1.5F, 2.0F, 8.0F, 3.0F).texOffs(22, 0).addBox(-1.5F, 7.0F, -2.5F, 5.0F, 2.0F, 5.0F).texOffs(0, 38).addBox(-1.5F, 9.0F, 0.0F, 5.0F, 12.0F, 0.0F, new CubeDeformation(0.01F)), PartPose.offset(4.0F, -3.5F, 0.0F));
        dUpperTorso.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(10, 39).mirror().addBox(-2.0F, -1.0F, -1.5F, 2.0F, 8.0F, 3.0F).texOffs(22, 0).mirror().addBox(-3.5F, 7.0F, -2.5F, 5.0F, 2.0F, 5.0F).texOffs(0, 38).mirror().addBox(-3.5F, 9.0F, 0.0F, 5.0F, 12.0F, 0.0F, new CubeDeformation(0.01F)), PartPose.offset(-4.0F, -3.5F, 0.0F));
        PartDefinition dNeck = dUpperTorso.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -5.0F, -0.5F));
        dNeck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -8.0F, -3.0F, 5.0F, 8.0F, 6.0F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(MineCellsMonsterEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        MineCellsModelAnimationUtils.rotateHead(netHeadYaw, headPitch, head);
        MineCellsModelAnimationUtils.bipedWalk(limbSwing, limbSwingAmount, root, rightLeg, leftLeg, rightArm, leftArm, lowerTorso, upperTorso);
        rightArm.zRot = 35.0F * Mth.DEG_TO_RAD;
        leftArm.zRot = -35.0F * Mth.DEG_TO_RAD;
        rightArm.xRot = 0.0F;
        leftArm.xRot = 0.0F;
        upperTorso.zRot = 0.0F;
        upperTorso.yRot = 0.0F;
        lowerTorso.yRot = 0.0F;
        leftArm.yRot = 0.0F;
        rightArm.yRot = 0.0F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
