package com.github.mim1q.minecells.client.renderer.monster;

import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;
import com.github.mim1q.minecells.entity.SweeperEntity;
import com.github.mim1q.minecells.util.MathUtils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.util.Mth;

public class SweeperModel extends EntityModel<MineCellsMonsterEntity> {
    private final ModelPart root;
    private final ModelPart torsoWrapper;
    private final ModelPart lowerTorso;
    private final ModelPart upperTorso;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public SweeperModel(ModelPart root) {
        this.root = root;
        this.torsoWrapper = root.getChild("torso_wrapper");
        this.lowerTorso = torsoWrapper.getChild("lower_torso");
        this.upperTorso = lowerTorso.getChild("upper_torso");
        this.neck = upperTorso.getChild("neck");
        this.head = neck.getChild("head");
        this.leftArm = upperTorso.getChild("left_arm");
        this.rightArm = upperTorso.getChild("right_arm");
        this.leftLeg = torsoWrapper.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition dTorsoWrapper = root.addOrReplaceChild("torso_wrapper", CubeListBuilder.create(), PartPose.offset(-4.0F, 14.0F, 0.0F));
        PartDefinition dLowerTorso = dTorsoWrapper.addOrReplaceChild(
            "lower_torso",
            CubeListBuilder.create().texOffs(0, 17).addBox(-6.0F, -10.0F, -3.0F, 12.0F, 10.0F, 6.0F),
            PartPose.offset(4.0F, 0.0F, 0.0F)
        );
        PartDefinition dUpperTorso = dLowerTorso.addOrReplaceChild(
            "upper_torso",
            CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -8.0F, -7.5F, 12.0F, 8.0F, 9.0F, new CubeDeformation(0.1F)),
            PartPose.offset(0.0F, -10.0F, 2.5F)
        );
        PartDefinition dNeck = dUpperTorso.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, -7.5F));
        dNeck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(12, 51).addBox(-3.0F, -4.0F, -6.0F, 6.0F, 6.0F, 6.0F), PartPose.ZERO);

        dUpperTorso.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
                .texOffs(0, 48).addBox(0.0F, -1.5F, -1.5F, 3.0F, 24.0F, 3.0F)
                .texOffs(42, 0).addBox(-0.5F, -2.5F, -2.0F, 4.0F, 6.0F, 4.0F),
            PartPose.offset(6.0F, -4.5F, -2.5F)
        );

        PartDefinition dRightArm = dUpperTorso.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
                .texOffs(31, 28).addBox(-5.0F, -2.5F, -2.5F, 5.0F, 24.0F, 5.0F)
                .texOffs(0, 33).addBox(-6.9F, -3.5F, -3.5F, 7.0F, 8.0F, 7.0F)
                .texOffs(36, 11).addBox(-5.5F, 17.0F, -3.0F, 6.0F, 10.0F, 6.0F),
            PartPose.offset(-6.0F, -3.5F, -2.5F)
        );
        dRightArm.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -4.5F, -1.0F, 2.0F, 5.0F, 2.0F),
            PartPose.offsetAndRotation(-5.0F, -2.5F, 0.0F, 0.0F, 0.0F, -0.7854F)
        );
        dRightArm.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -5.0F, -2.0F, 2.0F, 5.0F, 2.0F),
            PartPose.offsetAndRotation(-1.0F, -3.5F, -0.5F, 0.3491F, 0.0F, -0.3491F)
        );
        dRightArm.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -5.0F, 0.0F, 2.0F, 5.0F, 2.0F),
            PartPose.offsetAndRotation(-1.0F, -3.5F, 0.5F, -0.3491F, 0.0F, -0.3491F)
        );

        dTorsoWrapper.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(51, 41).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 10.0F, 3.0F),
            PartPose.offset(7.5F, 0.0F, -0.5F)
        );
        root.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(51, 28).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 10.0F, 3.0F),
            PartPose.offset(-3.5F, 14.0F, -0.5F)
        );

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(MineCellsMonsterEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        lowerTorso.xRot = radians(10.0F) + wobble(ageInTicks, 0.1F, 5.0F, 0.0F);
        upperTorso.xRot = radians(10.0F) + wobble(ageInTicks, 0.1F, 5.0F, -15.0F);
        lowerTorso.zRot = radians(-10.0F + limbSwingAmount * 35.0F);
        upperTorso.zRot = radians(5.0F + limbSwingAmount * 25.0F);
        lowerTorso.yRot = 0.0F;
        upperTorso.yRot = 0.0F;
        torsoWrapper.xRot = 0.0F;
        torsoWrapper.zRot = 0.0F;
        head.xRot = 0.0F;

        neck.xRot = radians(headPitch) - upperTorso.xRot - lowerTorso.xRot;
        head.yRot = radians(netHeadYaw);

        leftArm.xRot = Mth.sin(limbSwing * 0.5F) * limbSwingAmount - wobble(ageInTicks, 0.1F, 15.0F, -30.0F) - radians(20.0F);
        rightArm.xRot = radians(30.0F) - (lowerTorso.xRot + upperTorso.xRot) * 0.65F;
        rightArm.zRot = radians(15.0F - limbSwingAmount * 50.0F);
        rightArm.yRot = -radians(limbSwingAmount * 15.0F);
        rightLeg.xRot = Mth.sin(limbSwing * 0.5F) * limbSwingAmount;
        leftArm.zRot = -radians(limbSwingAmount * 45.0F);
        leftLeg.xRot = -this.rightLeg.xRot;

        if (entity instanceof SweeperEntity sweeper) {
            sweeper.sweepCharge.update(ageInTicks);
            float charge = sweeper.sweepCharge.getValue();
            upperTorso.yRot = radians(30.0F * charge);
            rightArm.zRot += radians(40.0F * charge);
            rightArm.yRot += radians(20.0F * charge);
            torsoWrapper.zRot = -radians(20.0F * charge);
            leftArm.zRot -= radians(60.0F * charge);
            leftArm.xRot -= radians(20.0F * charge);

            sweeper.sweepRelease.update(ageInTicks);
            float release = sweeper.sweepRelease.getValue();
            upperTorso.yRot = MathUtils.lerp(upperTorso.yRot, radians(-15.0F), release);
            lowerTorso.yRot = MathUtils.lerp(upperTorso.yRot, radians(-15.0F), release);
            upperTorso.xRot += radians(10.0F * release);
            lowerTorso.zRot += radians(10.0F * release);
            upperTorso.zRot += radians(10.0F * release);
            rightArm.xRot -= radians(90.0F * release);
            rightArm.yRot -= radians(30.0F * release);
            rightArm.zRot += radians(15.0F * release);

            float rollAnimation = sweeper.rollCharge.update(ageInTicks);
            head.xRot = radians(rollAnimation * 20.0F);
            upperTorso.xRot += radians(rollAnimation * 20.0F);
            lowerTorso.xRot += radians(rollAnimation * 45.0F);
            leftArm.xRot -= radians(rollAnimation * 50.0F);
            leftArm.zRot += radians(rollAnimation * 15.0F);
            rightArm.xRot -= radians(rollAnimation * 50.0F);
            rightArm.zRot -= radians(rollAnimation * 15.0F);

            float rollPitch = sweeper.getRollAnimation(Minecraft.getInstance().getFrameTime());
            torsoWrapper.xRot = -radians(rollPitch);
            rightLeg.xRot += torsoWrapper.xRot;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private static float radians(float degrees) {
        return degrees * Mth.DEG_TO_RAD;
    }

    private static float wobble(float progress, float speed, float scale, float offset) {
        return Mth.sin(radians(offset) + progress * speed) * radians(scale);
    }
}
