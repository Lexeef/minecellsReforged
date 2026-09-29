package com.github.mim1q.minecells.client.renderer.blockentity;

import com.github.mim1q.minecells.block.blockentity.FlagBlockEntity;
import com.github.mim1q.minecells.block.FlagBlock;
import com.github.mim1q.minecells.block.FlagBlock.Placement;
import com.github.mim1q.minecells.MineCells;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.joml.Math;
import org.joml.Quaternionf;

import java.util.ArrayList;

public class FlagBlockEntityRenderer implements BlockEntityRenderer<FlagBlockEntity> {
    public static final ModelLayerLocation FLAG_LAYER = new ModelLayerLocation(MineCells.id("flag"), "main");
    public static final ModelLayerLocation FLAG_LARGE_LAYER = new ModelLayerLocation(MineCells.id("flag"), "large");

    private final BiomeBannerBlockEntityModel model;
    private final BiomeBannerBlockEntityModel largeModel;

    public FlagBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new BiomeBannerBlockEntityModel(context.bakeLayer(FLAG_LAYER));
        this.largeModel = new BiomeBannerBlockEntityModel(context.bakeLayer(FLAG_LARGE_LAYER));
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public void render(FlagBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = entity.getBlockState();
        if (!(state.getBlock() instanceof FlagBlock flagBlock)) {
            return;
        }

        Placement placement = state.getValue(FlagBlock.PLACEMENT);
        Level level = entity.getLevel();
        Vec3 offset = level == null ? Vec3.ZERO : state.getOffset(level, entity.getBlockPos());
        boolean large = flagBlock.large;
        BiomeBannerBlockEntityModel model = large ? largeModel : this.model;
        float strength = (placement == Placement.HORIZONTAL ? 0.2F : 0.125F) * (large ? 0.3F : 1.0F);

        if (level != null && state.getValue(FlagBlock.WAVING)) {
            float time = Minecraft.getInstance().gui.getGuiTicks() + partialTick;
            model.wave(
                time * (large ? 0.15F : 0.1F),
                entity.getBlockPos().hashCode() % 100,
                strength * (1.0F + 0.15F * Math.cos(time * 0.13F)),
                true
            );
        } else {
            model.resetSegments();
        }

        poseStack.pushPose();
        poseStack.translate(offset.x, offset.y, offset.z);
        float yRotation = state.getValue(FlagBlock.FACING).toYRot();
        switch (placement) {
            case SIDE -> {
                poseStack.translate(0.5F, 15 / 16F, 0.5F);
                poseStack.scale(1.0F, -1.0F, -1.0F);
                poseStack.mulPose(new Quaternionf().rotationY((float) java.lang.Math.toRadians(yRotation)));
                poseStack.translate(0.0F, 0.0F, 7 / 16F);
            }
            case CENTERED -> {
                poseStack.translate(0.5F, 15 / 16F, 0.5F);
                poseStack.scale(1.0F, -1.0F, -1.0F);
                poseStack.mulPose(new Quaternionf().rotationY((float) java.lang.Math.toRadians(yRotation)));
            }
            case HORIZONTAL -> {
                poseStack.translate(0.5F, 0.5F, 0.5F);
                poseStack.scale(1.0F, -1.0F, -1.0F);
                poseStack.mulPose(new Quaternionf().rotationZ((float) java.lang.Math.toRadians(90.0F)));
                poseStack.mulPose(new Quaternionf().rotationX((float) java.lang.Math.toRadians(-90.0F + yRotation)));
                poseStack.translate(0.0F, -13 / 16F, 0.0F);
            }
        }

        ResourceLocation texture = flagBlock.texture;
        VertexConsumer buffer = bufferSource.getBuffer(model.renderType(texture));
        model.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(FlagBlockEntity blockEntity) {
        return true;
    }

    public static class BiomeBannerBlockEntityModel extends Model {
        private final ModelPart main;
        private final ModelPart[] segments;

        public BiomeBannerBlockEntityModel(ModelPart root) {
            super(RenderType::entityCutout);
            this.main = root.getChild("main");
            ArrayList<ModelPart> segments = new ArrayList<>();
            ModelPart parent = this.main;
            for (int i = 0; parent.hasChild("segment" + i); i++) {
                segments.add(parent.getChild("segment" + i));
                parent = segments.get(i);
            }
            this.segments = segments.toArray(new ModelPart[0]);
        }

        public static LayerDefinition createLayer(boolean large) {
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition root = mesh.getRoot();
            int width = large ? 24 : 16;
            int height = 8;
            int segmentCount = large ? 14 : 6;
            PartDefinition[] segmentDefinitions = new PartDefinition[segmentCount];
            PartDefinition main = root.addOrReplaceChild(
                "main",
                CubeListBuilder.create().texOffs(0, 0).addBox(-width / 2.0F, -1.0F, -1.0F, width, 2.0F, 2.0F),
                PartPose.ZERO
            );

            for (int i = 0; i < segmentCount; i++) {
                PartDefinition parent = i == 0 ? main : segmentDefinitions[i - 1];
                segmentDefinitions[i] = parent.addOrReplaceChild(
                    "segment" + i,
                    CubeListBuilder.create().texOffs(0, 16 + i * height).addBox(-width / 2.0F, 0.0F, 0.0F, width, height, 0.0F),
                    PartPose.offset(0.0F, i == 0 ? 0.0F : height, 0.0F)
                );
            }

            return LayerDefinition.create(mesh, 64, large ? 128 : 64);
        }

        public void wave(float animationProgress, float offset, float strength, boolean tapered) {
            for (int i = 0; i < segments.length; i++) {
                segments[i].xRot = Math.sin(animationProgress - i + offset) * strength * (tapered ? i : 1.0F);
            }
        }

        public void setupLargeItemModel() {
            for (int i = 5; i < segments.length; i++) {
                segments[i].visible = false;
            }
        }

        public void resetSegments() {
            for (ModelPart part : segments) {
                part.xRot = 0.0F;
                part.visible = true;
            }
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            main.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }
}
