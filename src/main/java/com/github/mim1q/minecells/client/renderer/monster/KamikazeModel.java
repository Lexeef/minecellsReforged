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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;

public class KamikazeModel extends EntityModel<MineCellsMonsterEntity> {
    private final ModelPart root;
    private final ModelPart bulb;
    private final ModelPart lowerLeftWing;
    private final ModelPart upperLeftWing;
    private final ModelPart lowerRightWing;
    private final ModelPart upperRightWing;
    private int fuse = -1;

    public KamikazeModel(ModelPart root) {
        this.root = root.getChild("root");
        this.bulb = root.getChild("bulb");
        this.lowerLeftWing = this.root.getChild("lower_left_wing");
        this.upperLeftWing = this.lowerLeftWing.getChild("upper_left_wing");
        this.lowerRightWing = this.root.getChild("lower_right_wing");
        this.upperRightWing = this.lowerRightWing.getChild("upper_right_wing");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition dRoot = root.addOrReplaceChild(
            "root",
            CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.01F))
                .texOffs(12, 18).addBox(-1.5F, -5.0F, -1.0F, 3.0F, 4.0F, 2.0F),
            PartPose.offset(0.0F, 18.0F, 0.0F)
        );

        root.addOrReplaceChild(
            "bulb",
            CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 6.0F, 6.0F),
            PartPose.offset(0.0F, 18.0F, 0.0F)
        );

        PartDefinition dLowerLeftWing = dRoot.addOrReplaceChild(
            "lower_left_wing",
            CubeListBuilder.create().texOffs(18, 0).addBox(0.0F, -3.0F, 0.0F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.01F)),
            PartPose.offsetAndRotation(1.0F, -3.5F, 0.0F, 20.0F * Mth.DEG_TO_RAD, 0.0F, 15.0F * Mth.DEG_TO_RAD)
        );
        dLowerLeftWing.addOrReplaceChild(
            "upper_left_wing",
            CubeListBuilder.create().texOffs(0, 18).addBox(-1.0F, -6.0F, 0.0F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.01F)),
            PartPose.offset(1.0F, -2.0F, 0.0F)
        );

        PartDefinition dLowerRightWing = dRoot.addOrReplaceChild(
            "lower_right_wing",
            CubeListBuilder.create().texOffs(12, 12).addBox(-6.0F, -3.0F, 0.0F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.01F)),
            PartPose.offsetAndRotation(-1.0F, -3.5F, 0.0F, 20.0F * Mth.DEG_TO_RAD, 0.0F, -15.0F * Mth.DEG_TO_RAD)
        );
        dLowerRightWing.addOrReplaceChild(
            "upper_right_wing",
            CubeListBuilder.create().texOffs(0, 12).addBox(-5.0F, -6.0F, 0.0F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.01F)),
            PartPose.offset(-1.0F, -2.0F, 0.0F)
        );

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void setupAnim(MineCellsMonsterEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float rotation = 0.0F;
        boolean sleeping = false;
        this.fuse = -1;
        if (entity instanceof com.github.mim1q.minecells.entity.KamikazeEntity kamikaze) {
            sleeping = kamikaze.isSleeping();
            rotation = kamikaze.rotation.update(ageInTicks);
            this.fuse = kamikaze.getFuse();
        }
        if (sleeping) {
            this.lowerLeftWing.yRot = 45.0F * Mth.DEG_TO_RAD;
            this.upperLeftWing.xRot = 120.0F * Mth.DEG_TO_RAD;
        } else {
            this.lowerLeftWing.yRot = Mth.sin(ageInTicks * 0.8F) * Mth.DEG_TO_RAD * 45.0F;
            this.upperLeftWing.xRot = -Mth.sin(ageInTicks * 0.8F + 1.5F) * Mth.DEG_TO_RAD * 45.0F;
            this.upperLeftWing.xRot += 45.0F * Mth.DEG_TO_RAD;
        }
        this.root.xRot = rotation * Mth.DEG_TO_RAD;
        this.bulb.xRot = this.root.xRot;
        this.lowerRightWing.yRot = -this.lowerLeftWing.yRot;
        this.upperRightWing.xRot = this.upperLeftWing.xRot;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);

        poseStack.pushPose();
        if (this.fuse < 30 && this.fuse >= 0 && this.fuse / 2 % 3 == 0) {
            packedOverlay = OverlayTexture.pack(OverlayTexture.u(1.0F), 10);
        }
        this.bulb.render(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }
}
