package com.github.mim1q.minecells.client.renderer.obelisk;

import com.github.mim1q.minecells.entity.nonliving.obelisk.ObeliskEntity;

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

public class ObeliskEntityModel extends EntityModel<ObeliskEntity> {

    private final ModelPart main;
    private final ModelPart glowMain;

    public ObeliskEntityModel(ModelPart root) {
        this.main = root.getChild("main");
        this.glowMain = root.getChild("glowMain");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        var main = modelPartData.addOrReplaceChild("main", CubeListBuilder.create().texOffs(80, 45).addBox(14.0F, -40.0F, -0.5F, 6.0F, 40.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 24.0F, 0.0F));
        main.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 48).addBox(0.0F, -12.0F, -5.0F, 10.0F, 12.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0873F));
        main.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(38, 38).addBox(0.0F, -24.0F, -4.0F, 11.0F, 24.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, -0.1745F));
        main.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(48, 0).addBox(0.0F, -15.0F, 1.0F, 10.0F, 15.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.0F, 0.0F, -5.0F, -0.1745F, 0.0F, 0.1745F));
        main.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(1, 0).addBox(1.0F, -36.0F, -12.0F, 11.0F, 36.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F));
        var glowMain = modelPartData.addOrReplaceChild("glowMain", CubeListBuilder.create().texOffs(104, 45).addBox(14.0F, -40.0F, -0.5F, 6.0F, 40.0F, 6.0F, new CubeDeformation(0.01F)), PartPose.offset(-7.0F, 24.0F, 0.0F));
        glowMain.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(1, 80).addBox(1.0F, -36.0F, -12.0F, 11.0F, 36.0F, 12.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(2.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F));
        glowMain.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(92, 25).addBox(0.0F, -12.0F, -5.0F, 10.0F, 12.0F, 8.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-6.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0873F));
        glowMain.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(48, 94).addBox(0.0F, -24.0F, -4.0F, 11.0F, 24.0F, 10.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, -0.1745F));
        glowMain.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(88, 0).addBox(0.0F, -15.0F, 1.0F, 10.0F, 15.0F, 10.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(12.0F, 0.0F, -5.0F, -0.1745F, 0.0F, 0.1745F));
        return LayerDefinition.create(modelData, 128, 128);
    }

    @Override
    public void setupAnim(ObeliskEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        var bury = entity.bury.update(animationProgress);
        this.main.y = 24.0F + bury;
        this.glowMain.y = 24.0F + bury;
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    public void renderGlow(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if (alpha <= 0) return;
        this.glowMain.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
