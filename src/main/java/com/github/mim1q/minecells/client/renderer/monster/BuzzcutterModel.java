package com.github.mim1q.minecells.client.renderer.monster;

import com.github.mim1q.minecells.entity.BuzzcutterEntity;
import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;
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
import net.minecraft.util.Mth;

public class BuzzcutterModel extends EntityModel<MineCellsMonsterEntity> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart[] leftWing = new ModelPart[2];
    private final ModelPart[] rightWing = new ModelPart[2];
    private final ModelPart leftTooth;
    private final ModelPart rightTooth;
    private final ModelPart biteTooth;

    public BuzzcutterModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.leftWing[0] = this.body.getChild("left_wing_0");
        this.leftWing[1] = this.leftWing[0].getChild("left_wing_1");
        this.rightWing[0] = this.body.getChild("right_wing_0");
        this.rightWing[1] = this.rightWing[0].getChild("right_wing_1");
        this.leftTooth = this.body.getChild("left_tooth");
        this.rightTooth = this.body.getChild("right_tooth");
        this.biteTooth = this.body.getChild("bite_tooth");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition dRoot = root.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 20.0F, 0.0F));
        PartDefinition dBody = dRoot.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        dBody.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 7.0F, 11.0F),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.8727F, 0.0F, 0.0F)
        );
        dBody.addOrReplaceChild(
            "bite_tooth",
            CubeListBuilder.create().texOffs(0, 32).addBox(0.0F, -15.0F, -12.0F, 0.0F, 16.0F, 16.0F),
            PartPose.offset(0.0F, 0.0F, -5.0F)
        );
        dBody.addOrReplaceChild(
            "left_tooth",
            CubeListBuilder.create()
                .texOffs(0, 4).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 2.0F, 2.0F)
                .texOffs(4, 4).addBox(-1.0F, 0.0F, -4.0F, 2.0F, 0.0F, 2.0F),
            PartPose.offsetAndRotation(2.0F, 1.0F, -5.0F, 1.4835F, 0.0F, 0.0F)
        );
        dBody.addOrReplaceChild(
            "right_tooth",
            CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 2.0F, 2.0F)
                .texOffs(4, 0).addBox(-1.0F, 0.0F, -4.0F, 2.0F, 0.0F, 2.0F),
            PartPose.offsetAndRotation(-2.25F, 1.0F, -5.0F, 1.4835F, 0.0F, 0.0F)
        );

        PartDefinition dLeftWing0 = dBody.addOrReplaceChild(
            "left_wing_0",
            CubeListBuilder.create().texOffs(21, 0).addBox(0.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F),
            PartPose.offset(4.0F, -2.0F, 1.0F)
        );
        dLeftWing0.addOrReplaceChild(
            "left_wing_1",
            CubeListBuilder.create().texOffs(0, 24).addBox(0.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F),
            PartPose.offset(8.0F, 0.0F, 0.0F)
        );

        PartDefinition dRightWing0 = dBody.addOrReplaceChild(
            "right_wing_0",
            CubeListBuilder.create().texOffs(21, 0).mirror().addBox(-8.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F).mirror(false),
            PartPose.offset(-4.0F, -2.0F, 1.0F)
        );
        dRightWing0.addOrReplaceChild(
            "right_wing_1",
            CubeListBuilder.create().texOffs(0, 24).mirror().addBox(-8.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F).mirror(false),
            PartPose.offset(-8.0F, 0.0F, 0.0F)
        );

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(MineCellsMonsterEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.leftWing[0].zRot = Mth.sin(ageInTicks * 0.5F) * 0.75F;
        this.leftWing[1].zRot = Mth.sin(ageInTicks * 0.5F - 0.8F);
        this.body.y = Mth.sin(ageInTicks * 0.5F + 1.5F) * 2.0F;
        this.body.xRot = 0.0F;
        this.leftTooth.zRot = 0.0F;
        this.rightTooth.zRot = 0.0F;
        this.biteTooth.xRot = 0.0F;
        this.biteTooth.yScale = 1.0F;
        this.biteTooth.zScale = 1.0F;

        if (entity instanceof BuzzcutterEntity buzzcutter) {
            buzzcutter.bite.update(ageInTicks);
            float bite = buzzcutter.bite.getValue();
            this.biteTooth.yScale = bite;
            this.biteTooth.zScale = bite;
            this.biteTooth.xRot = MathUtils.radians((1.0F - bite) * 160.0F);
            this.rightTooth.zRot = MathUtils.radians(bite * 20.0F);
            this.leftTooth.zRot = -this.rightTooth.zRot;
            this.leftWing[0].zRot = MathUtils.lerp(this.leftWing[0].zRot, MathUtils.radians(-60.0F), bite * 0.75F);
            this.leftWing[1].zRot = MathUtils.lerp(this.leftWing[0].zRot, MathUtils.radians(-30.0F), bite * 0.25F);
            this.body.y = MathUtils.lerp(this.body.y, (float) Math.sin(ageInTicks * 2.0F), bite * 0.75F);
            this.body.xRot = bite * -0.5F;
        }

        this.rightWing[0].zRot = -this.leftWing[0].zRot;
        this.rightWing[1].zRot = -this.leftWing[1].zRot;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
