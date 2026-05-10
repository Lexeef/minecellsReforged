package com.github.mim1q.minecells.client.renderer.blockentity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.DoorwayPortalBlockEntity;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public final class DoorwayPortalBlockEntityRenderer implements BlockEntityRenderer<DoorwayPortalBlockEntity> {
    private static final ResourceLocation BARS_TEXTURE = MineCells.id("textures/item/doorway_glow.png");

    private final Font font;
    private final EntityRenderDispatcher dispatcher;

    public DoorwayPortalBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        font = context.getFont();
        dispatcher = context.getEntityRenderer();
    }

    @Override
    public void render(DoorwayPortalBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = entity.getBlockState();
        if (!(state.getBlock() instanceof DoorwayPortalBlock doorway)) {
            return;
        }

        ResourceLocation foreground = MineCells.id("textures/block/doorway/" + doorway.getType().getSerializedName() + ".png");
        ResourceLocation background = MineCells.id("textures/block/doorway/" + doorway.getType().getSerializedName() + "_background.png");
        float rotation = 180.0F - state.getValue(DoorwayPortalBlock.FACING).toYRot();

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.25D, 0.5D);
        poseStack.mulPose(new Quaternionf().rotationY((float) Math.toRadians(rotation)));

        VertexConsumer backgroundConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(background));
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 0.49D);
        drawBillboard(backgroundConsumer, poseStack, 1.5F, 2.5F, 0.3125F, 0.6875F, 0.1875F, 0.8125F, 0xFFFFFFFF, LightTexture.FULL_BRIGHT);
        poseStack.popPose();

        VertexConsumer foregroundConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(foreground));
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 0.48D);
        drawBillboard(foregroundConsumer, poseStack, 1.5F, 2.5F, 104.0F / 128.0F, 1.0F, 0.0F, 40.0F / 128.0F, 0xFFFFFFFF, LightTexture.FULL_BRIGHT);
        poseStack.popPose();

        Player player = Minecraft.getInstance().player;
        float barsProgress = player != null && entity.canPlayerEnter(player) ? 0.25F : 1.0F;
        float minY = 1.25F - barsProgress * 2.5F;
        float minV = (40.0F - 40.0F * barsProgress) / 128.0F;
        VertexConsumer barsConsumer = bufferSource.getBuffer(RenderType.entityCutout(BARS_TEXTURE));
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.01D, 0.23D);
        drawRect(barsConsumer, poseStack, -0.75F, 0.75F, minY, 1.25F, 80.0F / 128.0F, 104.0F / 128.0F, minV, 40.0F / 128.0F, 0xFFFFFFFF, packedLight);
        poseStack.popPose();

        if (shouldShowLabel(entity, player)) {
            renderLabel(doorway, entity, poseStack, bufferSource);
        }

        poseStack.popPose();
    }

    private boolean shouldShowLabel(DoorwayPortalBlockEntity entity, Player player) {
        if (player == null || entity.getLevel() == null) {
            return false;
        }
        return player.distanceToSqr(entity.getBlockPos().getX() + 0.5D, entity.getBlockPos().getY() + 0.5D, entity.getBlockPos().getZ() + 0.5D) <= 36.0D;
    }

    private void renderLabel(DoorwayPortalBlock doorway, DoorwayPortalBlockEntity entity, PoseStack poseStack, MultiBufferSource bufferSource) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 2.5D, 0.0D);
        poseStack.mulPose(dispatcher.cameraOrientation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);

        String baseKey = doorway.getType() == DoorwayPortalBlock.DoorwayType.OVERWORLD
            ? "block.minecells.overworld_doorway"
            : "block.minecells." + doorway.getType().getSerializedName() + "_doorway";
        Component title = Component.translatable(baseKey);
        int titleWidth = font.width(title);
        font.drawInBatch(title, -titleWidth / 2.0F, 0.0F, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0x80000000, LightTexture.FULL_BRIGHT);

        if (entity.getPosOverride() != null) {
            Component coords = Component.literal("[x: " + entity.getPosOverride().getX() + ", z: " + entity.getPosOverride().getZ() + "]");
            int coordsWidth = font.width(coords);
            font.drawInBatch(coords, -coordsWidth / 2.0F, 10.0F, 0xFFFFFF, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0x80000000, LightTexture.FULL_BRIGHT);
        }

        poseStack.popPose();
    }

    private static void drawBillboard(VertexConsumer consumer, PoseStack poseStack, float width, float height, float minU, float maxU, float minV, float maxV, int color, int light) {
        float dx = width / 2.0F;
        float dy = height / 2.0F;
        drawRect(consumer, poseStack, -dx, dx, -dy, dy, minU, maxU, minV, maxV, color, light);
    }

    private static void drawRect(VertexConsumer consumer, PoseStack poseStack, float minX, float maxX, float minY, float maxY, float minU, float maxU, float minV, float maxV, int color, int light) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f position = pose.pose();
        Matrix3f normal = pose.normal();
        vertex(consumer, position, normal, minX, minY, 0.0F, minU, maxV, color, light);
        vertex(consumer, position, normal, maxX, minY, 0.0F, maxU, maxV, color, light);
        vertex(consumer, position, normal, maxX, maxY, 0.0F, maxU, minV, color, light);
        vertex(consumer, position, normal, minX, maxY, 0.0F, minU, minV, color, light);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f position, Matrix3f normal, float x, float y, float z, float u, float v, int color, int light) {
        consumer.vertex(position, x, y, z)
            .color((color >> 16) & 255, (color >> 8) & 255, color & 255, (color >>> 24) & 255)
            .uv(u, v)
            .overlayCoords(0)
            .uv2(light)
            .normal(normal, 0.0F, 0.0F, 1.0F)
            .endVertex();
    }
}
