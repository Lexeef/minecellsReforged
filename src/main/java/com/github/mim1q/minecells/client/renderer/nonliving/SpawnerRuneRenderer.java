package com.github.mim1q.minecells.client.renderer.nonliving;

import com.github.mim1q.minecells.block.blockentity.SpawnerRuneBlockEntity;
import com.github.mim1q.minecells.data.spawner_runes.SpawnerRuneController;
import com.github.mim1q.minecells.entity.nonliving.SpawnerRuneEntity;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.registry.MineCellsBlocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.client.model.data.ModelData;

public final class SpawnerRuneRenderer {
    private static final ResourceLocation TEXTURE = MineCells.id("textures/block/spawner_rune.png");

    private SpawnerRuneRenderer() {
    }

    private static void render(SpawnerRuneController controller, BlockState state, BlockPos pos, PoseStack poseStack, MultiBufferSource buffer) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (!controller.isVisible() || level == null) {
            return;
        }

        float age = minecraft.gui.getGuiTicks() + minecraft.getFrameTime();
        double yOffset = 0.5D + Math.sin(0.1F * age) * 0.15F;
        poseStack.pushPose();
        poseStack.scale(0.75F, 0.75F, 0.75F);
        poseStack.translate(0.0D, yOffset, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(age + pos.hashCode()));
        poseStack.translate(-0.5D, 0.0D, -0.5D);

        BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();
        BakedModel model = blockRenderer.getBlockModel(state);
        RenderType renderType = RenderType.translucentNoCrumbling();
        blockRenderer.getModelRenderer().tesselateBlock(
            level,
            model,
            state,
            pos,
            poseStack,
            buffer.getBuffer(renderType),
            true,
            level.random,
            0L,
            OverlayTexture.NO_OVERLAY,
            ModelData.EMPTY,
            renderType
        );
        poseStack.popPose();
    }

    public static final class Entity extends EntityRenderer<SpawnerRuneEntity> {
        public Entity(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public void render(SpawnerRuneEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
            SpawnerRuneRenderer.render(entity.controller, MineCellsBlocks.SPAWNER_RUNE.get().defaultBlockState(), entity.blockPosition(), poseStack, buffer);
        }

        @Override
        public ResourceLocation getTextureLocation(SpawnerRuneEntity entity) {
            return TEXTURE;
        }
    }

    public static final class BlockEntity implements BlockEntityRenderer<SpawnerRuneBlockEntity> {
        public BlockEntity(BlockEntityRendererProvider.Context context) {
        }

        @Override
        public void render(SpawnerRuneBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
            if (entity.getLevel() == null || !entity.controller.isVisible()) {
                return;
            }
            poseStack.pushPose();
            poseStack.translate(0.5D, 0.0D, 0.5D);
            SpawnerRuneRenderer.render(entity.controller, entity.getBlockState(), entity.getBlockPos(), poseStack, buffer);
            poseStack.popPose();
        }
    }
}
