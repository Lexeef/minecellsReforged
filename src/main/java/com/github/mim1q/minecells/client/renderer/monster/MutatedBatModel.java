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

public class MutatedBatModel extends EntityModel<MineCellsMonsterEntity> {
    private final ModelPart root;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart tailFront;
    private final ModelPart tailBack;

    public MutatedBatModel(ModelPart root) {
        this.root = root.getChild("root");
        this.rightWing = this.root.getChild("right_wing");
        this.leftWing = this.root.getChild("left_wing");
        this.tailFront = this.root.getChild("tail_front");
        this.tailBack = this.tailFront.getChild("tail_back");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition dRoot = root.addOrReplaceChild(
            "root",
            CubeListBuilder.create().texOffs(22, 16).addBox(-2.5F, -5.0F, -5.0F, 5.0F, 5.0F, 5.0F),
            PartPose.offset(0.0F, 24.0F, 0.0F)
        );
        dRoot.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
                .texOffs(12, 27).addBox(-1.5F, -1.0F, -2.0F, 3.0F, 4.0F, 2.0F)
                .texOffs(22, 26).addBox(-4.5F, -1.0F, -1.0F, 9.0F, 8.0F, 0.0F),
            PartPose.offset(0.0F, -3.0F, -5.0F)
        );
        dRoot.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 8).addBox(0.0F, 0.0F, -4.0F, 12.0F, 0.0F, 8.0F), PartPose.offset(1.5F, -5.0F, -2.5F));
        dRoot.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, 0.0F, -4.0F, 12.0F, 0.0F, 8.0F), PartPose.offset(-1.5F, -5.0F, -2.5F));
        PartDefinition dTailFront = dRoot.addOrReplaceChild("tail_front", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 7.0F), PartPose.offset(0.0F, -2.5F, 0.0F));
        dTailFront.addOrReplaceChild("tail_back", CubeListBuilder.create().texOffs(0, 27).addBox(-2.0F, -2.0F, 0.0F, 3.0F, 3.0F, 3.0F), PartPose.offset(0.0F, 0.5F, 7.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(MineCellsMonsterEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.xRot = headPitch * Mth.DEG_TO_RAD;
        this.root.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.root.y = 18.0F + Mth.sin(ageInTicks * 0.5F - 1.5F) * 2.0F;
        this.leftWing.zRot = -Mth.PI * 0.25F - Mth.sin(ageInTicks * 0.5F) * 0.5F;
        this.rightWing.zRot = -this.leftWing.zRot;
        this.tailFront.xRot = (-15.0F * Mth.DEG_TO_RAD) + Mth.sin(ageInTicks * 0.5F - 1.0F) * 10.0F * Mth.DEG_TO_RAD;
        this.tailBack.xRot = this.tailFront.xRot;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
