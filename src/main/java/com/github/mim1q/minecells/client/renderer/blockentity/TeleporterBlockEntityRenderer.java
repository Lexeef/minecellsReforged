package com.github.mim1q.minecells.client.renderer.blockentity;

import com.github.mim1q.minecells.block.portal.TeleporterBlock;
import com.github.mim1q.minecells.block.portal.TeleporterBlockEntity;
import com.github.mim1q.minecells.MineCells;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class TeleporterBlockEntityRenderer implements BlockEntityRenderer<TeleporterBlockEntity> {
    public static final ModelLayerLocation TELEPORTER_LAYER = new ModelLayerLocation(MineCells.id("teleporter"), "main");
    private static final ResourceLocation TEXTURE = MineCells.id("textures/blockentity/teleporter_core.png");
    private static final int FILL_COLOR = 0xFFFBED;
    private static final int OUTLINE_COLOR = 0xFF7C00;
    private static final int FILL_LIGHT = 0xF000F0;
    private static final int OUTLINE_LIGHT = 0xF000D0;

    private final ModelPart runesFill;
    private final ModelPart runesOutline;
    private final ModelPart portalOutline;
    private final ModelPart portalFill;

    public TeleporterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(TELEPORTER_LAYER);
        runesFill = root.getChild("runes_fill");
        runesOutline = root.getChild("runes_outline");
        portalOutline = root.getChild("portal_outline");
        portalFill = root.getChild("portal_fill");
    }

    @Override
    public void render(TeleporterBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        VertexConsumer vertices = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        Direction facing = entity.getBlockState().getValue(TeleporterBlock.FACING);
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(-0.5F, -0.5F, 0.5F);
        poseStack.mulPose(facing.getRotation());
        poseStack.translate(-0.5F, -1.5F, 0.0F);
        renderPart(portalFill, poseStack, vertices, FILL_LIGHT, packedOverlay, FILL_COLOR);
        renderPart(portalOutline, poseStack, vertices, OUTLINE_LIGHT, packedOverlay, OUTLINE_COLOR);
        renderPart(runesFill, poseStack, vertices, FILL_LIGHT, packedOverlay, FILL_COLOR);
        renderPart(runesOutline, poseStack, vertices, OUTLINE_LIGHT, packedOverlay, OUTLINE_COLOR);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(TeleporterBlockEntity entity) {
        return true;
    }

    private static void renderPart(ModelPart part, PoseStack poseStack, VertexConsumer vertices, int light, int overlay, int color) {
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        part.render(poseStack, vertices, light, overlay, red, green, blue, 1.0F);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("runes_outline", CubeListBuilder.create()
            .texOffs(112, 0).addBox(-4.0F, -39.0F, -5.05F, 8.0F, 8.0F, 0.0F)
            .texOffs(96, 0).addBox(-26.0F, -28.0F, -5.05F, 8.0F, 8.0F, 0.0F)
            .texOffs(96, 16).addBox(-30.0F, -8.0F, -5.05F, 8.0F, 8.0F, 0.0F)
            .texOffs(112, 16).addBox(25.0F, -8.0F, -5.05F, 8.0F, 8.0F, 0.0F)
            .texOffs(96, 32).addBox(20.0F, -27.0F, -5.05F, 8.0F, 8.0F, 0.0F)
            .texOffs(96, 48).addBox(-5.0F, 16.0F, -5.05F, 10.0F, 5.0F, 0.0F)
            .texOffs(96, 48).mirror().addBox(-5.0F, 16.0F, 5.05F, 10.0F, 5.0F, 0.0F).mirror(false)
            .texOffs(112, 0).mirror().addBox(-4.0F, -39.0F, 5.05F, 8.0F, 8.0F, 0.0F).mirror(false)
            .texOffs(96, 0).mirror().addBox(18.0F, -28.0F, 5.05F, 8.0F, 8.0F, 0.0F).mirror(false)
            .texOffs(96, 16).mirror().addBox(22.0F, -8.0F, 5.05F, 8.0F, 8.0F, 0.0F).mirror(false)
            .texOffs(112, 16).mirror().addBox(-33.0F, -8.0F, 5.05F, 8.0F, 8.0F, 0.0F).mirror(false)
            .texOffs(96, 32).mirror().addBox(-28.0F, -27.0F, 5.05F, 8.0F, 8.0F, 0.0F).mirror(false),
            PartPose.offset(0.0F, 24.0F, 0.0F));
        root.addOrReplaceChild("runes_fill", CubeListBuilder.create()
            .texOffs(112, 8).addBox(-4.0F, -39.0F, 0.05F, 8.0F, 8.0F, 0.0F)
            .texOffs(96, 56).addBox(-5.0F, 16.0F, 0.05F, 10.0F, 5.0F, 0.0F)
            .texOffs(96, 8).addBox(-26.0F, -28.0F, 0.05F, 8.0F, 8.0F, 0.0F)
            .texOffs(96, 24).addBox(-30.0F, -8.0F, 0.05F, 8.0F, 8.0F, 0.0F)
            .texOffs(112, 24).addBox(25.0F, -8.0F, 0.05F, 8.0F, 8.0F, 0.0F)
            .texOffs(96, 40).addBox(20.0F, -27.0F, 0.05F, 8.0F, 8.0F, 0.0F)
            .texOffs(96, 40).mirror().addBox(-28.0F, -27.0F, 10.15F, 8.0F, 8.0F, 0.0F).mirror(false)
            .texOffs(112, 8).mirror().addBox(-4.0F, -39.0F, 10.15F, 8.0F, 8.0F, 0.0F).mirror(false)
            .texOffs(96, 56).mirror().addBox(-5.0F, 16.0F, 10.15F, 10.0F, 5.0F, 0.0F).mirror(false)
            .texOffs(96, 8).mirror().addBox(18.0F, -28.0F, 10.15F, 8.0F, 8.0F, 0.0F).mirror(false)
            .texOffs(96, 24).mirror().addBox(22.0F, -8.0F, 10.15F, 8.0F, 8.0F, 0.0F).mirror(false)
            .texOffs(112, 24).mirror().addBox(-33.0F, -8.0F, 10.15F, 8.0F, 8.0F, 0.0F).mirror(false),
            PartPose.offset(0.0F, 24.0F, -5.1F));
        root.addOrReplaceChild("portal_outline", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-20.0F, -28.0F, 0.0F, 40.0F, 40.0F, 0.0F),
            PartPose.offset(0.0F, 24.0F, 0.0F));
        root.addOrReplaceChild("portal_fill", CubeListBuilder.create()
            .texOffs(0, 40).addBox(-20.0F, -27.0F, 0.0F, 40.0F, 38.0F, 0.0F),
            PartPose.offset(0.0F, 24.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }
}
