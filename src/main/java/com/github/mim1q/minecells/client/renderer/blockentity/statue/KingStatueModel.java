package com.github.mim1q.minecells.client.renderer.blockentity.statue;

import com.github.mim1q.minecells.block.DecorativeStatueBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class KingStatueModel extends Model {
    private final ModelPart root;
    private final ModelPart main;
    private final ModelPart torso;
    private final ModelPart chest;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart rightArmLower;
    private final ModelPart leftArm;
    private final ModelPart leftArmLower;

    public KingStatueModel(ModelPart root) {
        super(RenderType::entityCutout);
        this.root = root.getChild("root");
        this.main = this.root.getChild("main");
        this.torso = main.getChild("torso");
        this.chest = torso.getChild("chest");
        this.head = chest.getChild("head");
        this.rightArm = chest.getChild("right_arm");
        this.rightArmLower = rightArm.getChild("right_arm_lower");
        this.leftArm = chest.getChild("left_arm");
        this.leftArmLower = leftArm.getChild("left_arm_lower");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition rootPart = mesh.getRoot();
        PartDefinition root = rootPart.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition main = root.addOrReplaceChild("main", CubeListBuilder.create(), PartPose.offset(0.0F, 6.0F, 0.0F));
        PartDefinition torso = main.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 72).addBox(-5.0F, -6.0F, -3.0F, 10.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -37.0F, 0.0F, -0.1745F, 0.0F, 0.0F));
        PartDefinition chest = torso.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(40, 40).addBox(-6.0F, -8.0F, -3.5F, 12.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, -0.5F, 0.3054F, 0.0F, 0.0F));
        PartDefinition rightArm = chest.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(96, 0).addBox(-4.0F, -1.0F, -1.5F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(44, 86).mirror().addBox(-5.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-6.0F, -6.0F, 0.0F, -0.1745F, 0.0F, 0.1309F));
        rightArm.addOrReplaceChild("right_arm_lower", CubeListBuilder.create().texOffs(90, 86).addBox(-2.0F, 0.0F, -4.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-2.0F, 9.0F, 2.5F, -0.1745F, 0.0F, 0.0F));
        PartDefinition leftArm = chest.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(96, 0).addBox(0.0F, -1.0F, -1.5F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(44, 86).addBox(-1.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -6.0F, 0.0F, -0.1745F, 0.0F, -0.1309F));
        leftArm.addOrReplaceChild("left_arm_lower", CubeListBuilder.create().texOffs(90, 86).addBox(-2.0F, 0.0F, -4.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(2.0F, 9.0F, 2.5F, -0.1745F, 0.0F, 0.0F));
        PartDefinition head = chest.addOrReplaceChild("head", CubeListBuilder.create().texOffs(71, 47).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.5F, -0.5F, 0.1745F, 0.0F, 0.0F));
        head.addOrReplaceChild("right_spike_r1", CubeListBuilder.create().texOffs(97, 16).mirror().addBox(-4.5F, -8.0F, 0.5F, 4.0F, 14.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.5F, -10.0F, -4.25F, -0.7854F, 1.0472F, -0.7854F));
        head.addOrReplaceChild("middle_spike_r1", CubeListBuilder.create().texOffs(96, 64).addBox(-2.5F, -12.0F, 1.0F, 5.0F, 18.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.0F, -4.75F, -0.3491F, 0.0F, 0.0F));
        head.addOrReplaceChild("left_spike_r1", CubeListBuilder.create().texOffs(97, 16).addBox(0.5F, -8.0F, 0.75F, 4.0F, 14.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, -10.0F, -4.25F, -0.6981F, -0.8727F, 0.6981F));
        PartDefinition collar = chest.addOrReplaceChild("collar", CubeListBuilder.create().texOffs(29, 112).addBox(-8.0F, -11.0F, 4.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -13.0F, 3.5F, -0.3491F, 0.0F, 0.0F));
        collar.addOrReplaceChild("collar_right_r1", CubeListBuilder.create().texOffs(69, 87).mirror().addBox(-9.0F, -10.0F, -0.75F, 10.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, 0.0F, 4.0F, -0.3491F, -1.2217F, 0.0F));
        collar.addOrReplaceChild("collar_left_r1", CubeListBuilder.create().texOffs(69, 87).addBox(-1.0F, -10.0F, -0.75F, 10.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 0.0F, 4.0F, -0.3491F, 1.2217F, 0.0F));
        PartDefinition waist = main.addOrReplaceChild("waist", CubeListBuilder.create().texOffs(33, 55).addBox(-6.0F, 0.0F, -3.5F, 12.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -37.0F, 0.5F, 0.0F, -0.1745F, 0.0F));
        waist.addOrReplaceChild("belt_r1", CubeListBuilder.create().texOffs(114, 112).addBox(-4.0F, 0.0F, 0.0F, 7.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, -3.75F, -0.1309F, 0.0F, 0.0F));
        PartDefinition leftLeg = waist.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(95, 40).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(0, 107).addBox(-4.0F, -4.0F, -3.5F, 7.0F, 14.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 8.0F, 0.0F, -0.1745F, -0.0873F, 0.0F));
        leftLeg.addOrReplaceChild("left_leg_lower", CubeListBuilder.create().texOffs(24, 86).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 14.0F, 5.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 10.0F, -2.5F, 0.1745F, 0.0F, 0.0F));
        PartDefinition rightLeg = waist.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(95, 40).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 8.0F, 0.0F, -0.2618F, 0.4363F, 0.0F));
        rightLeg.addOrReplaceChild("right_pants_r1", CubeListBuilder.create().texOffs(0, 107).mirror().addBox(-3.5F, -7.0F, -3.5F, 7.0F, 14.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-0.5F, 3.0F, 0.0F, 0.0F, -0.2182F, 0.0F));
        rightLeg.addOrReplaceChild("right_leg_lower", CubeListBuilder.create().texOffs(24, 86).mirror().addBox(-2.5F, 0.0F, 0.0F, 5.0F, 14.0F, 5.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(0.0F, 10.0F, -2.5F, 0.3491F, 0.0F, 0.0F));
        PartDefinition cape = root.addOrReplaceChild("cape", CubeListBuilder.create(), PartPose.offset(0.5F, 13.0F, 3.25F));
        PartDefinition segment1 = cape.addOrReplaceChild("segment1", CubeListBuilder.create().texOffs(32, 70).addBox(0.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -51.0F, 4.0F));
        segment1.addOrReplaceChild("right1_r1", CubeListBuilder.create().texOffs(0, 85).mirror().addBox(-12.0F, -8.0F, 0.0F, 12.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.9599F, 0.0F));
        segment1.addOrReplaceChild("left1_r1", CubeListBuilder.create().texOffs(0, 85).addBox(0.0F, -8.0F, 0.0F, 12.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(16.0F, 0.0F, 0.0F, 0.0F, 0.9599F, 0.0F));
        PartDefinition segment2 = segment1.addOrReplaceChild("segment2", CubeListBuilder.create().texOffs(0, 16).addBox(-8.0F, 0.0F, 17.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 8.0F, -17.0F, 0.1745F, 0.0F, 0.0F));
        segment2.addOrReplaceChild("right2_r1", CubeListBuilder.create().texOffs(0, 56).mirror().addBox(-16.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-8.0F, 8.0F, 17.0F, 0.0F, -0.9163F, 0.0F));
        segment2.addOrReplaceChild("left2_r1", CubeListBuilder.create().texOffs(0, 56).addBox(0.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 8.0F, 17.0F, 0.0F, 1.0036F, 0.0F));
        PartDefinition segment3 = segment2.addOrReplaceChild("segment3", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, 0.0F, -1.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 16.0F, 18.0F, -0.0873F, 0.0F, 0.0F));
        segment3.addOrReplaceChild("right3_r1", CubeListBuilder.create().texOffs(0, 40).mirror().addBox(-20.0F, -8.0F, 0.0F, 20.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-8.0F, 8.0F, -1.0F, 0.0F, -0.8727F, 0.0F));
        segment3.addOrReplaceChild("left3_r1", CubeListBuilder.create().texOffs(0, 40).addBox(0.0F, -8.0F, 0.0F, 20.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 8.0F, -1.0F, 0.0F, 0.9599F, 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void setPose(DecorativeStatueBlock.StatuePose pose) {
        root.getAllParts().forEach(ModelPart::resetPose);
        switch (pose) {
            case ARMS_UP -> {
                chest.xRot = radians(10.0F);
                leftArm.setRotation(radians(-70.0F), radians(-40.0F), 0.0F);
                leftArmLower.xRot = radians(-60.0F);
                rightArm.setRotation(radians(-70.0F), radians(40.0F), 0.0F);
                rightArmLower.xRot = radians(-60.0F);
                head.xRot = radians(-10.0F);
            }
            case ARM_UP_RIGHT -> {
                main.yRot = radians(5.0F);
                chest.setRotation(radians(7.5F), radians(5.0F), 0.0F);
                torso.yRot = radians(10.0F);
                head.setRotation(radians(-5.0F), radians(15.0F), 0.0F);
                rightArm.setRotation(radians(-50.0F), radians(10.0F), 0.0F);
                rightArmLower.xRot = radians(-80.0F);
            }
            case ARM_UP_LEFT -> {
                main.yRot = radians(-5.0F);
                chest.setRotation(radians(7.5F), radians(-5.0F), 0.0F);
                torso.yRot = radians(10.0F);
                head.setRotation(radians(-5.0F), radians(-15.0F), 0.0F);
                leftArm.setRotation(radians(-50.0F), radians(-10.0F), 0.0F);
                leftArmLower.xRot = radians(-80.0F);
            }
            case BASE -> {
            }
        }
    }

    private static float radians(float degrees) {
        return (float) java.lang.Math.toRadians(degrees);
    }
}
