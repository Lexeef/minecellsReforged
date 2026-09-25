package com.github.mim1q.minecells.client.renderer.boss;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.util.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ConjunctiviusEyeRenderer extends RenderLayer<ConjunctiviusEntity, ConjunctiviusEntityModel> {
    private static final ResourceLocation[] TEXTURES = {
        MineCells.id("textures/entity/conjunctivius/eye_pink.png"),
        MineCells.id("textures/entity/conjunctivius/eye_yellow.png"),
        MineCells.id("textures/entity/conjunctivius/eye_green.png"),
        MineCells.id("textures/entity/conjunctivius/eye_blue.png")
    };
    private static final ResourceLocation EYELID_TEXTURE = MineCells.id("textures/entity/conjunctivius/eyelid.png");

    private final ConjunctiviusEyeModel model;

    public ConjunctiviusEyeRenderer(RenderLayerParent<ConjunctiviusEntity, ConjunctiviusEntityModel> parent, ModelPart eyeRoot) {
        super(parent);
        this.model = new ConjunctiviusEyeModel(eyeRoot);
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
        renderEyelid(poseStack, buffer, packedLight, entity, ageInTicks);
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.2F, -15.5F / 16.0F);
        model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(getTexture(entity)));
        model.renderToBuffer(poseStack, consumer, 0xF000F0, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    private ResourceLocation getTexture(ConjunctiviusEntity entity) {
        ConjunctiviusEntity.EyeState state = entity.getEyeState();
        if (state == ConjunctiviusEntity.EyeState.SHAKING) {
            return TEXTURES[(entity.tickCount / 2) % TEXTURES.length];
        }
        return TEXTURES[state.index];
    }

    private void renderEyelid(
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight,
        ConjunctiviusEntity entity,
        float animationProgress
    ) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.25F, -1.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        float frame = entity.getEyelidFrame(animationProgress);
        float minV = frame * 0.2F;
        float maxV = minV + 0.2F;
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(EYELID_TEXTURE));
        int overlay = OverlayTexture.pack(0.0F, entity.hurtTime > 0);
        RenderUtils.drawBillboard(consumer, poseStack, packedLight, 2.0F, 2.0F, 0.0F, 1.0F, minV, maxV, 0xFFFFFFFF, overlay);
        poseStack.popPose();
    }

    public static class ConjunctiviusEyeModel extends EntityModel<ConjunctiviusEntity> {
        private final ModelPart eye;
        private final ModelPart highlight;

        public ConjunctiviusEyeModel(ModelPart root) {
            super(RenderType::eyes);
            this.eye = root.getChild("eye");
            this.highlight = root.getChild("highlight");
        }

        public static LayerDefinition createLayer() {
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition root = mesh.getRoot();
            root.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(0, 0).addBox(-5.5F, -5.5F, 0.0F, 11, 11, 0), PartPose.ZERO);
            root.addOrReplaceChild("highlight", CubeListBuilder.create().texOffs(0, 11).addBox(0.0F, 0.0F, 0.0F, 5, 4, 0), PartPose.offset(1.0F, -5.0F, -0.25F));
            return LayerDefinition.create(mesh, 32, 16);
        }

        @Override
        public void setupAnim(ConjunctiviusEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entity.isForDisplay()) {
                eye.x = 2.5F;
                eye.y = 0.0F;
                eye.z = -0.01F;
                highlight.x = 1.0F;
                highlight.y = -5.0F;
                highlight.z = -0.25F;
                return;
            }
            Entity camera = Minecraft.getInstance().getCameraEntity();
            if (camera != null) {
                Vec3 offset = entity.getEyeOffset(Minecraft.getInstance().getFrameTime());
                eye.x = (float) offset.x;
                eye.y = (float) offset.y;
                highlight.z = -0.25F;
            }
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            poseStack.pushPose();
            eye.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            highlight.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
        }
    }
}
