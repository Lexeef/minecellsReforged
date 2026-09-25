package com.github.mim1q.minecells.client.renderer.boss;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ConjunctiviusChainRenderer extends RenderLayer<ConjunctiviusEntity, ConjunctiviusEntityModel> {
    private static final ResourceLocation MODEL_ID = MineCells.id("misc/conjunctivius/chain");
    private static final ResourceLocation DASH_MODEL_ID = MineCells.id("misc/conjunctivius/dash_chain");

    private final BakedModel model;
    private final BakedModel dashModel;

    public ConjunctiviusChainRenderer(RenderLayerParent<ConjunctiviusEntity, ConjunctiviusEntityModel> parent, ModelManager modelManager) {
        super(parent);
        this.model = modelManager.getModel(MODEL_ID);
        this.dashModel = modelManager.getModel(DASH_MODEL_ID);
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
        if (entity.isForDisplay()) {
            return;
        }

        Vec3 startPos = entity.getPosition(partialTicks);
        VertexConsumer vertices = buffer.getBuffer(RenderType.cutout());
        int stage = entity.getStage();
        if (stage < 2) {
            renderChain(entity, poseStack, vertices, startPos, Vec3.atBottomCenterOf(entity.getRightAnchor()), entity.getYRot(), new Vector3f(2.2F, 0.85F, 0.0F), false);
        }
        if (stage < 4) {
            renderChain(entity, poseStack, vertices, startPos, Vec3.atBottomCenterOf(entity.getLeftAnchor()), entity.getYRot(), new Vector3f(-2.2F, 0.85F, 0.0F), false);
        }
        if (stage < 6) {
            renderChain(entity, poseStack, vertices, startPos, Vec3.atBottomCenterOf(entity.getTopAnchor().above()), entity.getYRot(), new Vector3f(0.0F, -1.75F, 0.0F), false);
        }

        VertexConsumer dashVertices = buffer.getBuffer(RenderType.translucent());
        if (entity.getDashState() == TimedActionGoal.State.CHARGE) {
            Vec3 target = entity.getDashTarget();
            if (!target.equals(Vec3.ZERO)) {
                renderChain(entity, poseStack, dashVertices, startPos, target, entity.getYRot(), new Vector3f(0.0F, 0.0F, 0.0F), true);
            }
        }
    }

    private void renderChain(
        ConjunctiviusEntity entity,
        PoseStack poseStack,
        VertexConsumer vertices,
        Vec3 startPos,
        Vec3 targetPos,
        float headYaw,
        Vector3f offset,
        boolean isDash
    ) {
        BakedModel modelToUse = isDash ? dashModel : model;
        poseStack.pushPose();
        poseStack.scale(0.75F, 0.75F, 0.75F);
        startPos = startPos.add(0.0D, -offset.y() * 2.0D + 3.0D, 0.01D - offset.z() * 1.5D);
        Vec3 direction = targetPos.subtract(startPos);
        Vec3 normDir = direction.normalize().scale(0.75D);
        direction = direction.yRot(MathUtils.radians(headYaw));
        direction = direction.add(-offset.x() * 1.5D, 0.0D, 0.0D);

        float rx = (float) -Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z));
        float ry = (float) -Math.atan2(direction.x, direction.z);
        MathUtils.PosRotScale.ofRadians(offset, new Vector3f(rx, ry, 0.0F), new Vector3f(1.0F, 1.0F, 1.0F)).apply(poseStack);
        int count = (int) direction.length();
        poseStack.translate(-0.5D, -0.5D, -1.25D);

        Vec3 lightPos = startPos.add(-offset.x() * 1.5D, 0.0D, offset.z());
        int light = 0xF000F0;
        for (int i = 0; i < count; i++) {
            if (!isDash) {
                int lightLevel = entity.level().getMaxLocalRawBrightness(BlockPos.containing(lightPos));
                light = LightTexture.pack(lightLevel, lightLevel);
                lightPos = lightPos.add(normDir);
            }
            RenderUtils.renderBakedModel(modelToUse, entity.getRandom(), light, poseStack, vertices);
            poseStack.translate(0.0F, 0.0F, -1.0F);
        }
        poseStack.popPose();
    }
}
