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

public class GrenadierModel extends EntityModel<MineCellsMonsterEntity> {
    private static final float[] BALL_OFFSETS = {-5.0F, -3.0F, -3.0F, 2.0F, 2.0F};

    private final ModelPart root;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart lowerTorso;
    private final ModelPart upperTorso;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart[] balls = new ModelPart[5];

    public GrenadierModel(ModelPart root) {
        this.root = root.getChild("root");
        this.leftLeg = this.root.getChild("left_leg");
        this.rightLeg = this.root.getChild("right_leg");
        this.lowerTorso = this.root.getChild("lower_torso");
        this.upperTorso = this.lowerTorso.getChild("upper_torso");
        this.leftArm = this.upperTorso.getChild("left_arm");
        this.rightArm = this.upperTorso.getChild("right_arm");
        this.neck = this.upperTorso.getChild("neck");
        this.head = this.neck.getChild("head");
        for (int i = 0; i < balls.length; i++) {
            balls[i] = upperTorso.getChild("ball_" + i);
        }
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition dRoot = root.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        dRoot.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 37).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 14.0F, 3.0F).texOffs(16, 56).addBox(-2.0F, 9.0F, -2.0F, 4.0F, 2.0F, 4.0F), PartPose.offset(2.0F, -13.0F, 0.0F));
        dRoot.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(27, 34).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 14.0F, 3.0F).texOffs(0, 56).addBox(-2.0F, 9.0F, -2.0F, 4.0F, 2.0F, 4.0F), PartPose.offset(-2.0F, -13.0F, 0.0F));
        dRoot.addOrReplaceChild("waist", CubeListBuilder.create().texOffs(24, 0).addBox(-4.5F, -3.0F, -2.5F, 9.0F, 3.0F, 5.0F), PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition dLowerTorso = dRoot.addOrReplaceChild("lower_torso", CubeListBuilder.create().texOffs(39, 34).addBox(-3.0F, -5.0F, -1.5F, 6.0F, 6.0F, 3.0F).texOffs(52, 0).addBox(-1.5F, -3.0F, -3.0F, 3.0F, 3.0F, 3.0F), PartPose.offset(0.0F, -15.0F, 0.0F));
        PartDefinition dUpperTorso = dLowerTorso.addOrReplaceChild("upper_torso", CubeListBuilder.create().texOffs(0, 26).addBox(-5.0F, -6.0F, -2.5F, 10.0F, 6.0F, 5.0F).texOffs(0, 16).addBox(-4.0F, -6.5F, -3.5F, 8.0F, 2.0F, 7.0F), PartPose.offset(0.0F, -4.0F, 0.0F));

        PartDefinition dNeck = dUpperTorso.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -5.0F, -1.0F));
        dNeck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(30, 22).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.ZERO);
        dUpperTorso.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(39, 43).addBox(0.0F, -1.5F, -1.5F, 2.0F, 16.0F, 3.0F), PartPose.offset(5.0F, -3.5F, 0.0F));
        dUpperTorso.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(12, 37).addBox(-2.0F, -1.5F, -1.5F, 2.0F, 16.0F, 3.0F), PartPose.offset(-5.0F, -3.5F, 0.0F));
        dUpperTorso.addOrReplaceChild("ball_0", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.offsetAndRotation(0.5F, -5.0F, 7.5F, -10.0F * Mth.DEG_TO_RAD, 0.0F, 0.0F));
        dUpperTorso.addOrReplaceChild("ball_1", CubeListBuilder.create().texOffs(26, 10).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.offsetAndRotation(5.0F, -3.0F, 6.0F, 10.0F * Mth.DEG_TO_RAD, 25.0F * Mth.DEG_TO_RAD, 0.0F));
        dUpperTorso.addOrReplaceChild("ball_2", CubeListBuilder.create().texOffs(26, 10).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.offsetAndRotation(-5.0F, -3.0F, 6.0F, 10.0F * Mth.DEG_TO_RAD, -25.0F * Mth.DEG_TO_RAD, 0.0F));
        dUpperTorso.addOrReplaceChild("ball_3", CubeListBuilder.create().texOffs(26, 10).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.offsetAndRotation(3.5F, 2.0F, 3.5F, -60.0F * Mth.DEG_TO_RAD, 10.0F * Mth.DEG_TO_RAD, -15.0F * Mth.DEG_TO_RAD));
        dUpperTorso.addOrReplaceChild("ball_4", CubeListBuilder.create().texOffs(26, 10).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.offsetAndRotation(-3.5F, 2.0F, 3.5F, -45.0F * Mth.DEG_TO_RAD, -20.0F * Mth.DEG_TO_RAD, 15.0F * Mth.DEG_TO_RAD));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(MineCellsMonsterEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        MineCellsModelAnimationUtils.rotateHead(netHeadYaw, headPitch, head);
        MineCellsModelAnimationUtils.bipedWalk(limbSwing, limbSwingAmount, root, rightLeg, leftLeg, rightArm, leftArm, lowerTorso, upperTorso);
        neck.xRot = -30.0F * Mth.DEG_TO_RAD;
        upperTorso.xRot += 20.0F * Mth.DEG_TO_RAD;
        lowerTorso.xRot += 10.0F * Mth.DEG_TO_RAD;
        leftArm.xRot -= 30.0F * Mth.DEG_TO_RAD;
        rightArm.xRot -= 30.0F * Mth.DEG_TO_RAD;

        for (int i = 0; i < balls.length; i++) {
            balls[i].y = BALL_OFFSETS[i] + Mth.sin(ageInTicks * 0.25F + i * 0.2F) * 1.75F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
