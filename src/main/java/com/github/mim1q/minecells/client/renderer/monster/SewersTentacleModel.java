package com.github.mim1q.minecells.client.renderer.monster;

import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;
import com.github.mim1q.minecells.entity.SewersTentacleEntity;

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

public class SewersTentacleModel extends EntityModel<MineCellsMonsterEntity> {
    private final ModelPart root;
    private final ModelPart[] segments = new ModelPart[5];

    public SewersTentacleModel(ModelPart root) {
        this.root = root.getChild("root");
        this.segments[0] = this.root.getChild("segment_0");
        this.segments[1] = this.segments[0].getChild("segment_1");
        this.segments[2] = this.segments[1].getChild("segment_2");
        this.segments[3] = this.segments[2].getChild("segment_3");
        this.segments[4] = this.segments[3].getChild("segment_4");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition dRoot = root.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition dSegment0 = dRoot.addOrReplaceChild(
            "segment_0",
            CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -12.0F, -3.0F, 8.0F, 16.0F, 6.0F, new CubeDeformation(0.01F))
                .texOffs(26, 31).addBox(-2.0F, -9.0F, -4.0F, 4.0F, 5.0F, 1.0F),
            PartPose.ZERO
        );

        PartDefinition dSegment1 = dSegment0.addOrReplaceChild(
            "segment_1",
            CubeListBuilder.create()
                .texOffs(0, 22).addBox(-3.5F, -11.0F, -3.0F, 7.0F, 11.0F, 6.0F)
                .texOffs(26, 31).addBox(-2.0F, -8.0F, -4.0F, 4.0F, 5.0F, 1.0F),
            PartPose.offset(0.0F, -10.0F, 0.0F)
        );

        PartDefinition dSegment2 = dSegment1.addOrReplaceChild(
            "segment_2",
            CubeListBuilder.create()
                .texOffs(26, 18).addBox(-3.0F, -9.0F, -2.0F, 6.0F, 9.0F, 4.0F)
                .texOffs(36, 31).addBox(-1.5F, -6.0F, -3.0F, 3.0F, 4.0F, 1.0F),
            PartPose.offset(0.0F, -10.0F, 0.0F)
        );

        PartDefinition dSegment3 = dSegment2.addOrReplaceChild(
            "segment_3",
            CubeListBuilder.create()
                .texOffs(28, 0).addBox(-2.0F, -8.0F, -1.5F, 4.0F, 8.0F, 3.0F, new CubeDeformation(0.01F))
                .texOffs(0, 0).addBox(-1.0F, -6.0F, -2.5F, 2.0F, 3.0F, 1.0F),
            PartPose.offset(0.0F, -8.0F, 0.0F)
        );

        dSegment3.addOrReplaceChild(
            "segment_4",
            CubeListBuilder.create().texOffs(0, 39).addBox(-1.5F, -4.0F, -1.0F, 3.0F, 5.0F, 2.0F),
            PartPose.offset(0.0F, -8.0F, -0.5F)
        );

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(MineCellsMonsterEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity instanceof SewersTentacleEntity tentacle) {
            float wobble = tentacle.wobble.update(ageInTicks);
            float wobbleOffset = tentacle.wobbleOffset.update(ageInTicks);
            float belowGroundProgress = tentacle.belowGround.update(ageInTicks);
            float belowGround = entity.isForDisplay() ? 0.75F : belowGroundProgress;
            wiggleTentacle(this.segments, ageInTicks, 15.0F * wobble, entity.getId(), wobbleOffset * 0.2F);
            this.root.y = 24.0F - belowGround * 16.0F + 8.5F;
            return;
        }
        wiggleTentacle(this.segments, ageInTicks, 15.0F, entity.getId(), 0.0F);
        this.root.y = 24.0F + 8.5F;
    }

    public static void wiggleTentacle(ModelPart[] segments, float animationProgress, float degrees, int id, float offset) {
        float radians = degrees * (float) Math.PI / 180.0F;
        for (int i = 0; i < segments.length - 1; i++) {
            segments[i].yRot = (float) Math.sin(animationProgress * 0.125F) * 0.25F;
            segments[i].xRot = (float) Math.sin(animationProgress * 0.25F - i + id) * radians - offset;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
