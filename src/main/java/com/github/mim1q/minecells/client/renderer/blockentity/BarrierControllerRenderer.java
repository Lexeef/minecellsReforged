package com.github.mim1q.minecells.client.renderer.blockentity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.ConditionalBarrierBlock;
import com.github.mim1q.minecells.block.blockentity.BarrierControllerBlockEntity;
import com.github.mim1q.minecells.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;

public class BarrierControllerRenderer implements BlockEntityRenderer<BarrierControllerBlockEntity> {
    public static final ModelLayerLocation BIG_DOOR_LAYER = new ModelLayerLocation(MineCells.id("big_door_barrier"), "main");
    private static final ResourceLocation TEXTURE = MineCells.id("textures/blockentity/big_door.png");

    private final ModelPart root;

    public BarrierControllerRenderer(BlockEntityRendererProvider.Context context) {
        root = context.bakeLayer(BIG_DOOR_LAYER).getChild("root");
    }

    @Override
    public void render(BarrierControllerBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        VertexConsumer vertices = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        Direction facing = entity.getBlockState().getValue(ConditionalBarrierBlock.FACING);
        float time = entity.getLevel() == null ? 0.0F : entity.getLevel().getGameTime() + partialTick;
        float openProgress = entity.openProgress.update(time);

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(new Quaternionf().rotationY(MathUtils.radians(facing.toYRot())));
        root.setRotation(0.0F, MathUtils.radians(-85.0F) * openProgress, 0.0F);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.5F, 0.0F);
        root.render(poseStack, vertices, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(BarrierControllerBlockEntity entity) {
        return true;
    }

    public static LayerDefinition createBigDoorLayer() {
        MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild("root", CubeListBuilder.create()
            .texOffs(0, 0).addBox(0.0F, -44.0F, -5.0F, 32.0F, 18.0F, 4.0F)
            .texOffs(0, 54).addBox(0.0F, -4.0F, -6.0F, 32.0F, 4.0F, 6.0F)
            .texOffs(0, 64).addBox(0.0F, -26.0F, -6.0F, 32.0F, 4.0F, 6.0F)
            .texOffs(72, 0).addBox(14.0F, -28.0F, -7.0F, 4.0F, 8.0F, 8.0F)
            .texOffs(96, 0).addBox(12.0F, -22.0F, 0.0F, 8.0F, 8.0F, 0.0F)
            .texOffs(96, 0).addBox(12.0F, -22.0F, -6.0F, 8.0F, 8.0F, 0.0F)
            .texOffs(0, 44).addBox(0.0F, -48.0F, -6.0F, 32.0F, 4.0F, 6.0F)
            .texOffs(0, 22).addBox(0.0F, -22.0F, -5.0F, 32.0F, 18.0F, 4.0F),
            PartPose.offset(-8.0F, 24.0F, 3.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }
}
