package com.github.mim1q.minecells.client.renderer.blockentity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.ArrowSignBlock;
import com.github.mim1q.minecells.block.blockentity.ArrowSignBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ArrowSignBlockEntityRenderer implements BlockEntityRenderer<ArrowSignBlockEntity> {
    public static final ModelLayerLocation ARROW_SIGN_LAYER = new ModelLayerLocation(MineCells.id("arrow_sign"), "main");
    private static final ResourceLocation TEXTURE = MineCells.id("textures/blockentity/arrow_sign.png");
    private final ModelPart root;
    private final ItemRenderer itemRenderer;
    private final BlockRenderDispatcher blockRenderer;

    public ArrowSignBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        root = context.bakeLayer(ARROW_SIGN_LAYER);
        itemRenderer = context.getItemRenderer();
        blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(ArrowSignBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = entity.getBlockState();
        poseStack.pushPose();
        renderChain(entity, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0.5F, -1.5F, -0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.getValue(ArrowSignBlock.ROTATION) * 22.5F));
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        if (state.getValue(ArrowSignBlock.MIDDLE)) {
            poseStack.translate(0.0F, -3.0F / 16.0F, -6.0F / 16.0F);
        }

        poseStack.translate(0.0F, 1.0F, 0.0F);
        int verticalRotation = entity.getVerticalRotation();
        float verticalRotationDegrees = verticalRotation * 22.5F;
        boolean flipRotation = verticalRotation >= 4 && verticalRotation <= 11;
        if (flipRotation) {
            verticalRotationDegrees -= 180.0F;
        }

        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(verticalRotationDegrees));
        poseStack.translate(0.0F, -1.0F, 0.0F);
        if (flipRotation) {
            poseStack.translate(0.0F, 0.0F, 6.0F / 16.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            poseStack.translate(0.0F, 0.0F, -6.0F / 16.0F);
        }
        root.render(poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.0F, 1.125F, 3.99F / 16.0F);
        renderIcon(entity, poseStack, bufferSource, packedOverlay);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.translate(0.0F, 0.0F, -4.02F / 16.0F);
        renderIcon(entity, poseStack, bufferSource, packedOverlay);
        poseStack.popPose();
        poseStack.popPose();
    }

    private void renderChain(ArrowSignBlockEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState chainState = entity.getChainState();
        if (!chainState.isAir() && chainState.getBlock() != Blocks.AIR) {
            blockRenderer.renderSingleBlock(chainState, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }

    private void renderIcon(ArrowSignBlockEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, int packedOverlay) {
        ItemStack itemStack = entity.getItemStack();
        if (!itemStack.isEmpty()) {
            poseStack.pushPose();
            poseStack.scale(-0.45F, -0.45F, 0.45F);
            poseStack.translate(0.0F, 2.5F, 0.0F);
            itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, LightTexture.FULL_BRIGHT, packedOverlay, poseStack, bufferSource, entity.getLevel(), 0);
            poseStack.popPose();
        }
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition main = root.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-15.5F, -13.0F, 4.0F, 27.0F, 10.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        main.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 14).addBox(-5.275F, -5.275F, -3.0F, 7.0F, 7.0F, 4.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-8.0F, -8.0F, 7.0F, 0.0F, 0.0F, -0.7854F));
        return LayerDefinition.create(mesh, 64, 64);
    }
}
