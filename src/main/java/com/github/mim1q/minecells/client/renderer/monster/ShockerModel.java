package com.github.mim1q.minecells.client.renderer.monster;

import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;
import com.github.mim1q.minecells.entity.ShockerEntity;
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

public class ShockerModel extends EntityModel<MineCellsMonsterEntity> {
    private final ModelPart root;
    private final ModelPart base;
    private final ModelPart eye;
    private final ModelPart rightFloater;
    private final ModelPart leftFloater;
    private final ModelPart bottomFloater;

    public ShockerModel(ModelPart root) {
        this.root = root.getChild("root");
        this.base = this.root.getChild("base");
        ModelPart eyeRim = this.base.getChild("eye_rim");
        this.eye = eyeRim.getChild("eye");
        this.rightFloater = this.root.getChild("right_floater");
        this.leftFloater = this.root.getChild("left_floater");
        this.bottomFloater = this.root.getChild("bottom_floater");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition dRoot = root.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition dBase = dRoot.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -22.0F, -7.0F, 14.0F, 22.0F, 14.0F), PartPose.offset(0.0F, -5.0F, 0.0F));
        dBase.addOrReplaceChild("base_ring", CubeListBuilder.create().texOffs(0, 36).addBox(-8.0F, -4.0F, -8.0F, 16.0F, 4.0F, 16.0F), PartPose.offset(0.0F, -2.0F, 0.0F));
        dBase.addOrReplaceChild("base_top", CubeListBuilder.create().texOffs(0, 56).addBox(-6.0F, -16.0F, -6.0F, 12.0F, 16.0F, 12.0F), PartPose.offset(0.0F, -22.0F, 0.0F));
        PartDefinition dEyeRim = dBase.addOrReplaceChild("eye_rim", CubeListBuilder.create().texOffs(42, 4).addBox(-5.0F, 3.0F, 0.0F, 10.0F, 2.0F, 2.0F).texOffs(42, 0).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 2.0F, 2.0F).texOffs(6, 6).addBox(-5.0F, -3.0F, 0.0F, 2.0F, 6.0F, 2.0F).texOffs(0, 0).addBox(3.0F, -3.0F, 0.0F, 2.0F, 6.0F, 2.0F), PartPose.offset(0.0F, -12.0F, -9.0F));
        dEyeRim.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(0, 36).addBox(-1.0F, -1.0F, 1.0F, 2.0F, 2.0F, 2.0F), PartPose.ZERO);
        dRoot.addOrReplaceChild("bottom_floater", CubeListBuilder.create().texOffs(48, 20).addBox(-8.0F, -3.0F, -8.0F, 16.0F, 3.0F, 16.0F), PartPose.ZERO);
        dRoot.addOrReplaceChild("left_floater", CubeListBuilder.create().texOffs(48, 58).addBox(-1.5F, -6.0F, -4.0F, 3.0F, 12.0F, 8.0F), PartPose.offsetAndRotation(15.0F, -32.0F, 0.0F, 0.0F, -20.0F * Mth.DEG_TO_RAD, -15.0F * Mth.DEG_TO_RAD));
        dRoot.addOrReplaceChild("right_floater", CubeListBuilder.create().texOffs(62, 70).addBox(-1.5F, -6.0F, -4.0F, 3.0F, 12.0F, 8.0F), PartPose.offsetAndRotation(-15.0F, -20.0F, 0.0F, -15.0F * Mth.DEG_TO_RAD, 30.0F * Mth.DEG_TO_RAD, 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(MineCellsMonsterEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float multiplier = entity instanceof ShockerEntity shocker && (shocker.isAuraCharging() || shocker.isAuraReleasing()) ? 15.0F : 1.0F;
        base.y = -5.0F + Mth.sin(ageInTicks * 0.1F) * 4.0F;
        bottomFloater.y = Mth.sin((ageInTicks + 2.0F) * 0.1F) * 4.0F;
        rightFloater.y = -20.0F + Mth.sin((ageInTicks + 5.0F) * 0.1F) * 10.0F;
        leftFloater.y = -32.0F + Mth.sin((ageInTicks + 8.0F) * 0.1F) * 8.0F;
        rightFloater.x = -15.0F - Mth.sin(ageInTicks * multiplier * 0.1F) * multiplier * 0.25F;
        leftFloater.x = 15.0F + Mth.sin((ageInTicks - 0.5F) * multiplier * 0.1F) * multiplier * 0.25F;
        eye.y = Mth.sin(ageInTicks * 0.1F * multiplier) * 1.5F;
        eye.x = Mth.cos(ageInTicks * 0.1F * multiplier) * 1.5F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
