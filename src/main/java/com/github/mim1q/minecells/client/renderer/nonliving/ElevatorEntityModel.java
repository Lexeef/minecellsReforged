package com.github.mim1q.minecells.client.renderer.nonliving;

import com.github.mim1q.minecells.entity.nonliving.ElevatorEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;

public class ElevatorEntityModel extends EntityModel<ElevatorEntity> {
    private final ModelPart root;

    public ElevatorEntityModel(ModelPart root) {
        this.root = root.getChild("root");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
            "root",
            CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-16.0F, 0.0F, -16.0F, 32.0F, 3.0F, 32.0F)
                .addBox(-16.0F, 5.0F, -16.0F, 32.0F, 3.0F, 32.0F)
                .addBox(-18.0F, -1.0F, -2.0F, 4.0F, 10.0F, 4.0F)
                .addBox(14.0F, -1.0F, -2.0F, 4.0F, 10.0F, 4.0F)
                .texOffs(0, 5)
                .addBox(-15.0F, 3.0F, -15.0F, 30.0F, 2.0F, 30.0F),
            PartPose.ZERO
        );
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(ElevatorEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, consumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
