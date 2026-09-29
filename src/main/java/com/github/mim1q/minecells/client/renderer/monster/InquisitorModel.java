package com.github.mim1q.minecells.client.renderer.monster;

import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;

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

public class InquisitorModel extends EntityModel<MineCellsMonsterEntity> {
    private final ModelPart root;
    private final ModelPart waist;
    private final ModelPart belt;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart upperTorso;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart head;

    public InquisitorModel(ModelPart root) {
        this.root = root.getChild("root");
        this.waist = this.root.getChild("waist");
        this.belt = this.waist.getChild("belt");
        this.rightLeg = this.waist.getChild("right_leg");
        this.leftLeg = this.waist.getChild("left_leg");
        this.upperTorso = this.waist.getChild("upper_torso");
        this.leftArm = this.upperTorso.getChild("left_arm");
        this.rightArm = this.upperTorso.getChild("right_arm");
        ModelPart neck = this.upperTorso.getChild("neck");
        this.head = neck.getChild("head");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition dRoot = root.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition dWaist = dRoot.addOrReplaceChild(
            "waist",
            CubeListBuilder.create().texOffs(48, 34).addBox(-3.0F, -3.0F, -1.5F, 6.0F, 3.0F, 3.0F),
            PartPose.offset(0.0F, -15.0F, 0.0F)
        );
        dWaist.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
                .texOffs(0, 35).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 11.0F, 4.0F)
                .texOffs(42, 50).addBox(-1.5F, 11.0F, -1.5F, 3.0F, 4.0F, 3.0F),
            PartPose.offset(-2.0F, 0.0F, 0.0F)
        );
        dWaist.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
                .texOffs(38, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 11.0F, 4.0F)
                .texOffs(51, 40).addBox(-1.5F, 11.0F, -1.5F, 3.0F, 4.0F, 3.0F),
            PartPose.offset(2.0F, 0.0F, 0.0F)
        );
        dWaist.addOrReplaceChild(
            "belt",
            CubeListBuilder.create().texOffs(34, 50).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 12.0F, 0.0F, new CubeDeformation(0.01F)),
            PartPose.offset(0.0F, 0.0F, -2.05F)
        );

        PartDefinition dUpperTorso = dWaist.addOrReplaceChild(
            "upper_torso",
            CubeListBuilder.create().texOffs(24, 32).addBox(-4.0F, -6.0F, -2.0F, 8.0F, 6.0F, 4.0F),
            PartPose.offset(0.0F, -3.0F, 0.0F)
        );
        dUpperTorso.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
                .texOffs(10, 50).addBox(0.0F, -1.5F, -1.5F, 2.0F, 14.0F, 3.0F)
                .texOffs(16, 42).addBox(-1.5F, -2.0F, -2.5F, 5.0F, 3.0F, 5.0F)
                .texOffs(46, 15).addBox(-1.5F, 1.0F, -2.5F, 4.0F, 2.0F, 5.0F)
                .texOffs(20, 50).addBox(-0.5F, 8.5F, -2.0F, 3.0F, 2.0F, 4.0F),
            PartPose.offset(4.5F, -4.5F, 0.0F)
        );
        dUpperTorso.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
                .texOffs(0, 50).addBox(-2.0F, -1.5F, -1.5F, 2.0F, 14.0F, 3.0F)
                .texOffs(36, 42).addBox(-3.5F, -2.0F, -2.5F, 5.0F, 3.0F, 5.0F)
                .texOffs(47, 27).addBox(-2.5F, 1.0F, -2.5F, 4.0F, 2.0F, 5.0F)
                .texOffs(20, 50).addBox(-2.5F, 8.5F, -2.0F, 3.0F, 2.0F, 4.0F),
            PartPose.offset(-4.5F, -4.5F, 0.0F)
        );

        PartDefinition dNeck = dUpperTorso.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, -1.0F));
        dNeck.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
                .texOffs(28, 20).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F)
                .texOffs(0, 0).addBox(-9.5F, -19.0F, 3.51F, 19.0F, 20.0F, 0.0F, new CubeDeformation(0.01F))
                .texOffs(0, 20).addBox(-3.5F, -8.0F, -3.5F, 7.0F, 8.0F, 7.0F),
            PartPose.ZERO
        );

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(MineCellsMonsterEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        MineCellsModelAnimationUtils.rotateHead(netHeadYaw, headPitch, this.head);
        MineCellsModelAnimationUtils.bipedWalk(limbSwing * 2.0F, limbSwingAmount, this.root, this.rightLeg, this.leftLeg, this.rightArm, this.leftArm, this.upperTorso, this.waist);

        this.rightArm.zRot = 30.0F * Mth.DEG_TO_RAD;
        this.leftArm.zRot = -30.0F * Mth.DEG_TO_RAD;
        this.leftArm.xRot += -30.0F * Mth.DEG_TO_RAD;
        this.rightArm.xRot += -30.0F * Mth.DEG_TO_RAD;
        this.belt.xRot = -Mth.abs(Mth.sin(limbSwing) * limbSwingAmount) * 1.5F;

        if (entity instanceof com.github.mim1q.minecells.entity.InquisitorEntity inquisitor) {
            inquisitor.armUpProgress.update(ageInTicks);
            float rot = inquisitor.armUpProgress.getValue() * 45.0F * Mth.DEG_TO_RAD;
            this.leftArm.yRot = -rot;
            this.rightArm.yRot = rot;
            this.upperTorso.xRot = -rot * 0.2F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
