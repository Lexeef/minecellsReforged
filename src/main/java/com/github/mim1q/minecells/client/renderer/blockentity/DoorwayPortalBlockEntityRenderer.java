package com.github.mim1q.minecells.client.renderer.blockentity;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sin;
import static org.joml.Math.clamp;

import com.github.mim1q.minecells.block.blockentity.DoorwayPortalBlockEntity;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.RenderUtils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.gui.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.List;

public final class DoorwayPortalBlockEntityRenderer implements BlockEntityRenderer<DoorwayPortalBlockEntity> {
    private final Font font;

    public DoorwayPortalBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        font = context.getFont();
    }

    @Override
    public void render(DoorwayPortalBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = entity.getBlockState();
        if (!(state.getBlock() instanceof DoorwayPortalBlock doorway)) {
            return;
        }

        String typeName = doorway.getType().getSerializedName();
        ResourceLocation texture = MineCells.id("textures/block/doorway/" + typeName + ".png");
        ResourceLocation backgroundTexture = MineCells.id("textures/block/doorway/" + typeName + "_background.png");
        float rotation = state.getValue(DoorwayPortalBlock.FACING).toYRot();

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.25D, 0.5D);
        poseStack.mulPose(new Quaternionf().rotationY(MathUtils.radians(180.0F - rotation)));

        VertexConsumer backgroundVertices = bufferSource.getBuffer(RenderType.entityCutoutNoCull(backgroundTexture));
        poseStack.translate(0.0D, 0.0D, 0.49D);
        renderBackground(poseStack, backgroundVertices, entity, rotation);

        VertexConsumer foregroundVertices = bufferSource.getBuffer(RenderType.entityTranslucent(texture));
        poseStack.translate(0.0D, 0.0D, -0.01D);
        RenderUtils.drawBillboard(foregroundVertices, poseStack, LightTexture.FULL_BRIGHT, 1.5F, 2.5F, 104.0F / 128, 1.0F, 0.0F, 40.0F / 128, 0xFFFFFFFF, OverlayTexture.NO_OVERLAY);

        float time = entity.getLevel() != null ? entity.getLevel().getGameTime() + partialTick : 0.0F;
        float barsProgress = 0.25F + entity.closedBarsAnimation.update(time) * 0.75F;
        float minY = 1.25F - barsProgress * 2.5F;
        float minV = (40.0F - 40.0F * barsProgress) / 128;

        VertexConsumer barsVertices = bufferSource.getBuffer(RenderType.entityCutout(texture));
        poseStack.translate(0.0D, 0.01D, -0.25D);
        RenderUtils.drawBillboard(barsVertices, poseStack, packedLight, -0.75F, 0.75F, minY, 1.25F, 80.0F / 128, 104.0F / 128, minV, 40.0F / 128, 0xFFFFFFFF, OverlayTexture.NO_OVERLAY);

        renderLabel(entity.getLabel(), poseStack, bufferSource);
        poseStack.popPose();
    }

    private void renderBackground(PoseStack poseStack, VertexConsumer vertices, DoorwayPortalBlockEntity entity, float rotation) {
        Player player = Minecraft.getInstance().player;

        float bgCenterV = 0.5F;
        float bgCenterU = 0.5F;

        if (player != null) {
            Vec3 pos = Vec3.atCenterOf(entity.getBlockPos());
            Vec3 direction = new Vec3(0.0D, 0.0D, 1.0D).yRot(MathUtils.radians(90.0F - rotation));
            Vec3 playerPos = player.getPosition(Minecraft.getInstance().getFrameTime());
            Vec3 playerVector = playerPos.subtract(pos);
            double playerVectorLength = playerVector.length();
            playerVector = playerVector.normalize().scale(min(playerVectorLength * 0.33F, 1.0D));

            float dot = (float) direction.dot(playerVector) * 1 / 8.0F;
            float bgCenterUOffset = MathUtils.easeInOutQuad(-1.0F, 1.0F, (float) (sin(dot) + 1) / 2.0F);

            bgCenterU += clamp(-40 / 128.0F, 40 / 128.0F, bgCenterUOffset);

            float playerHeightDiff = (float) (playerPos.y - pos.y) + 1.5F;
            playerVectorLength = max(playerVectorLength, 0.9D);
            bgCenterV += clamp(-24 / 128.0F, 24 / 128.0F, playerHeightDiff * 0.1F * 1 / (float) playerVectorLength);
        }

        RenderUtils.drawBillboard(
            vertices,
            poseStack,
            LightTexture.FULL_BRIGHT,
            1.5F, 2.5F,
            bgCenterU - 24 / 128.0F, bgCenterU + 24 / 128.0F,
            bgCenterV - 40 / 128.0F, bgCenterV + 40 / 128.0F,
            0xFFFFFFFF,
            OverlayTexture.NO_OVERLAY
        );
    }

    private void renderLabel(List<MutableComponent> text, PoseStack poseStack, MultiBufferSource bufferSource) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 2.5D, 0.0D);
        poseStack.scale(-0.025F, -0.025F, 0.025F);
        Matrix4f matrix = poseStack.last().pose();
        int y = 15;
        for (int i = text.size() - 1; i >= 0; --i) {
            MutableComponent line = text.get(i);
            float x = -font.width(line) / 2.0F;
            font.drawInBatch(line, x, y, 0xFFFFFFFF, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0x80000000, LightTexture.FULL_BRIGHT);
            y -= 10;
        }
        poseStack.popPose();
    }
}
