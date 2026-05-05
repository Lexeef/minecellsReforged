package com.github.mim1q.minecells.client.renderer.blockentity.statue;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.DecorativeStatueBlock;
import com.github.mim1q.minecells.block.blockentity.DecorativeStatueBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;

public class DecorativeStatueBlockEntityRenderer implements BlockEntityRenderer<DecorativeStatueBlockEntity> {
    public static final ModelLayerLocation KING_STATUE_LAYER = new ModelLayerLocation(MineCells.id("king_statue"), "main");
    private static final ResourceLocation TEXTURE = MineCells.id("textures/blockentity/statue/king.png");
    private final KingStatueModel model;

    public DecorativeStatueBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new KingStatueModel(context.bakeLayer(KING_STATUE_LAYER));
    }

    @Override
    public void render(DecorativeStatueBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float rotation = entity.getBlockState().getValue(DecorativeStatueBlock.ROTATION) * 22.5F;
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0.5F, -1.5F, -0.5F);
        poseStack.mulPose(new Quaternionf().rotationY((float) java.lang.Math.toRadians(180.0F + rotation)));
        model.setPose(entity.getBlockState().getValue(DecorativeStatueBlock.POSE));
        model.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(DecorativeStatueBlockEntity blockEntity) {
        return true;
    }
}
