package com.github.mim1q.minecells.client.renderer.boss;

import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.util.MathUtils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.RenderType;

public class ConjunctiviusEntityModel extends EntityModel<ConjunctiviusEntity> {

    private final ModelPart main;

    public ConjunctiviusEntityModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.main = root.getChild("main");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();

        PartDefinition dMain = modelPartData.addOrReplaceChild("main",
            CubeListBuilder.create()
                .texOffs(96, 0)
                .addBox(-12.0F, -4.0F, -12.0F, 24, 4, 24) // Teeth
                .texOffs(128, 28)
                .addBox(-8.0F, 0.0F, -8.0F, 16, 4, 16) // Mouth
                .texOffs(0, 0)
                .addBox(-16.0F, -36.0F, -16.0F, 32, 32, 32) // Main Body
                .texOffs(138, 94)
                .addBox(-12.0F, -31.0F, -15.25F, 24, 22, 0) // Eye Background
                .texOffs(92, 64)
                .addBox(-12.0F, -42.0F, -12.0F, 24, 6, 24) // Head Top
                .texOffs(46, 70)
                .addBox(-10.0F, -48.0F, 0.0F, 20, 6, 0) // Pulled Head Skin
                .texOffs(102, 98)
                .addBox(16.0F, -22.0F, -12.0F, 6, 16, 24) // Lower Left Side
                .texOffs(60, 122)
                .addBox(16.0F, -30.0F, -10.0F, 3, 8, 20) // Upper Left Side
                .texOffs(0, 110)
                .addBox(-22.0F, -22.0F, -12.0F, 6, 16, 24) // Lower Right Side
                .texOffs(86, 138)
                .addBox(-19.0F, -30.0F, -10.0F, 3, 8, 20) // Upper Right Side
                .texOffs(0, 64)
                .addBox(22.0F, -21.0F, 0.0F, 8, 14, 0) // Left Spike Flat
                .texOffs(16, 64)
                .addBox(22.0F, -16.0F, -2.0F, 4, 4, 4) // Left Spike Cube
                .texOffs(0, 64).mirror()
                .addBox(-30.0F, -21.0F, 0.0F, 8, 14, 0) // Right Spike Flat
                .texOffs(16, 64).mirror()
                .addBox(-26.0F, -16.0F, -2.0F, 4, 4, 4), // Right Spike Cube
            PartPose.offset(0.0F, 24.0F, 0.0F)
        );

        dMain.addOrReplaceChild("crown_front",
            CubeListBuilder.create()
                .texOffs(36, 122)
                .addBox(-9.0F, -4.0F, -4.0F, 18, 4, 4),
            PartPose.offsetAndRotation(0.0F, -36.0F, -12.0F, MathUtils.radians(20.0F), 0.0F, 0.0F)
        );

        dMain.addOrReplaceChild("crown_back",
            CubeListBuilder.create()
                .texOffs(128, 48)
                .addBox(-9.0F, -4.0F, 0.0F, 18, 4, 4),
            PartPose.offsetAndRotation(0.0F, -36.0F, 12.0F, MathUtils.radians(-20.0F), 0.0F, 0.0F)
        );

        dMain.addOrReplaceChild("crown_left",
            CubeListBuilder.create()
                .texOffs(0, 64)
                .addBox(-6.0F, 0.0F, -17.0F, 6, 12, 34),
            PartPose.offsetAndRotation(16.0F, -41.0F, 0.0F, 0.0F, 0.0F, MathUtils.radians(15.0F))
        );

        dMain.addOrReplaceChild("crown_right",
            CubeListBuilder.create()
                .texOffs(0, 64).mirror()
                .addBox(0.0F, 0.0F, -17.0F, 6, 12, 34),
            PartPose.offsetAndRotation(-16.0F, -41.0F, 0.0F, 0.0F, 0.0F, MathUtils.radians(-15.0F))
        );

        return LayerDefinition.create(modelData, 256, 256);
    }

    @Override
    public void setupAnim(ConjunctiviusEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
