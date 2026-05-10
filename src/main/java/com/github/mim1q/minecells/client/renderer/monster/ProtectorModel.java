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

public class ProtectorModel extends EntityModel<MineCellsMonsterEntity> {
    private final ModelPart root;

    public ProtectorModel(ModelPart root) {
        this.root = root.getChild("root");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition dRoot = root.addOrReplaceChild("root", CubeListBuilder.create().texOffs(28, 31).addBox(-1.5F, -10.0F, -1.5F, 3.0F, 10.0F, 3.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition dBody = dRoot.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(1, 0).addBox(-3.5F, -12.0F, -3.5F, 7.0F, 12.0F, 7.0F).texOffs(28, 55).addBox(-4.0F, -3.0F, -4.0F, 8.0F, 1.0F, 8.0F).texOffs(0, 49).addBox(2.0F, -13.0F, -4.0F, 3.0F, 2.0F, 8.0F).texOffs(0, 39).addBox(-5.0F, -13.0F, -4.0F, 3.0F, 2.0F, 8.0F), PartPose.offset(0.0F, -10.0F, 0.0F));
        PartDefinition dHead = dBody.addOrReplaceChild("head", CubeListBuilder.create().texOffs(1, 20).addBox(-3.0F, -7.0F, -3.0F, 6.0F, 7.0F, 6.0F).texOffs(38, 27).addBox(-3.0F, -10.0F, -3.0F, 6.0F, 3.0F, 0.0F).addBox(-3.0F, -10.0F, 3.0F, 6.0F, 3.0F, 0.0F).texOffs(-6, 19).addBox(-0.5F, -9.9F, -3.0F, 1.0F, 0.0F, 6.0F), PartPose.offset(0.0F, -12.0F, 0.0F));
        dHead.addOrReplaceChild("left_slope", CubeListBuilder.create().texOffs(0, 28).addBox(0.0F, -4.0F, -3.0F, 0.0F, 4.0F, 6.0F), PartPose.offsetAndRotation(3.0F, -7.0F, 0.0F, 0.0F, 0.0F, -45.0F * Mth.DEG_TO_RAD));
        dHead.addOrReplaceChild("right_slope", CubeListBuilder.create().texOffs(0, 28).addBox(0.0F, -4.0F, -3.0F, 0.0F, 4.0F, 6.0F), PartPose.offsetAndRotation(-3.0F, -7.0F, 0.0F, 0.0F, 0.0F, 45.0F * Mth.DEG_TO_RAD));
        PartDefinition dLeftArm = dBody.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 7).addBox(0.0F, -1.5F, -1.5F, 8.0F, 3.0F, 3.0F), PartPose.offset(3.0F, -10.5F, 0.0F));
        dLeftArm.addOrReplaceChild("shield", CubeListBuilder.create().texOffs(30, 17).addBox(-4.0F, -4.0F, -2.0F, 8.0F, 8.0F, 2.0F), PartPose.offsetAndRotation(6.0F, 0.0F, -1.0F, 0.0F, -25.0F * Mth.DEG_TO_RAD, 0.0F));
        dBody.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(25, 0).addBox(-8.0F, -1.5F, -1.5F, 8.0F, 3.0F, 3.0F), PartPose.offset(-3.0F, -10.5F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(MineCellsMonsterEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float angle = ageInTicks * 10.0F;
        float x = Mth.sin(angle * Mth.DEG_TO_RAD);
        float y = Mth.cos(angle * Mth.DEG_TO_RAD);
        root.xRot = limbSwingAmount * x * 0.3F;
        root.zRot = limbSwingAmount * y * 0.3F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
