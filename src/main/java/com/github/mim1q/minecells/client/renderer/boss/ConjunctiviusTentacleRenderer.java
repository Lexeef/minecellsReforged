package com.github.mim1q.minecells.client.renderer.boss;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.renderer.monster.SewersTentacleModel;
import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ConjunctiviusTentacleRenderer extends RenderLayer<ConjunctiviusEntity, ConjunctiviusEntityModel> {
    private static final ResourceLocation TEXTURE = MineCells.id("textures/entity/sewers_tentacle/purple.png");

    private final ConjunctiviusTentacleModel model;
    private final RenderType layer;
    private final List<MathUtils.PosRotScale> posRotScales = new ArrayList<>();

    public ConjunctiviusTentacleRenderer(RenderLayerParent<ConjunctiviusEntity, ConjunctiviusEntityModel> parent, ModelPart tentacleRoot) {
        super(parent);
        this.model = new ConjunctiviusTentacleModel(tentacleRoot);
        this.layer = this.model.renderType(TEXTURE);
    }

    public void addPosRotScale(float px, float py, float pz, float rx, float ry, float rz, float s) {
        posRotScales.add(MathUtils.PosRotScale.ofDegrees(px, py, pz, rx, ry, rz, s, s, s));
    }

    @Override
    public void render(
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight,
        ConjunctiviusEntity entity,
        float limbSwing,
        float limbSwingAmount,
        float partialTicks,
        float ageInTicks,
        float netHeadYaw,
        float headPitch
    ) {
        int offset = 0;
        for (MathUtils.PosRotScale posRotScale : posRotScales) {
            poseStack.pushPose();
            posRotScale.apply(poseStack);
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            model.setOffset(offset * 256);
            offset++;
            float anim = entity.isForDisplay() ? 140.0F : ageInTicks * 0.75F;
            model.setupAnim(entity, limbSwing, limbSwingAmount, anim, netHeadYaw, headPitch);
            boolean hurt = entity.hurtTime > 0;
            model.renderToBuffer(
                poseStack,
                buffer.getBuffer(layer),
                packedLight,
                OverlayTexture.pack(0.0F, hurt),
                1.0F, 1.0F, 1.0F, 1.0F
            );
            poseStack.popPose();
        }
    }

    public static class ConjunctiviusTentacleModel extends EntityModel<ConjunctiviusEntity> {
        private final ModelPart root;
        private final ModelPart[] segments = new ModelPart[5];
        private int offset = 0;

        public ConjunctiviusTentacleModel(ModelPart root) {
            this.root = root.getChild("root");
            this.segments[0] = this.root.getChild("segment_0");
            this.segments[1] = this.segments[0].getChild("segment_1");
            this.segments[2] = this.segments[1].getChild("segment_2");
            this.segments[3] = this.segments[2].getChild("segment_3");
            this.segments[4] = this.segments[3].getChild("segment_4");
        }

        public static LayerDefinition createLayer() {
            return SewersTentacleModel.createLayer();
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        @Override
        public void setupAnim(ConjunctiviusEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            SewersTentacleModel.wiggleTentacle(this.segments, ageInTicks, 10.0F, this.offset, 0.0F);
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }
}
