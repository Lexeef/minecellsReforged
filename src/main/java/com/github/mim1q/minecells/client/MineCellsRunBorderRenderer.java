package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.world.MineCellsRunBorder;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Forge replacement for Fabric's {@code WorldRendererMixin#renderWorldBorder} changes: draws the run border
 * with vanilla's force field texture, fading in within 8 blocks.
 */
@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MineCellsRunBorderRenderer {
    private static final ResourceLocation FORCEFIELD = new ResourceLocation("textures/misc/forcefield.png");
    private static final double FADE_DISTANCE = 8.0D;
    private static final double EXTENT = 32.0D;
    private static final int COLOR = 0x20A0FF;

    private MineCellsRunBorderRenderer() {
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null || !MineCellsDimension.isMineCellsDimension(minecraft.level)) {
            return;
        }
        Camera camera = event.getCamera();
        Vec3 cam = camera.getPosition();
        double px = minecraft.player.getX();
        double pz = minecraft.player.getZ();
        double cx = MineCellsRunBorder.centerX(px, pz);
        double cz = MineCellsRunBorder.centerZ(px, pz);
        double distance = MineCellsRunBorder.distanceInside(cx, cz, px, pz);
        float alpha = (float) Math.max(0.0D, (FADE_DISTANCE - distance) / FADE_DISTANCE);
        if (alpha <= 0.0F) {
            return;
        }

        double minX = cx - MineCellsRunBorder.HALF_SIZE;
        double maxX = cx + MineCellsRunBorder.HALF_SIZE;
        double minZ = cz - MineCellsRunBorder.HALF_SIZE;
        double maxZ = cz + MineCellsRunBorder.HALF_SIZE;

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderTexture(0, FORCEFIELD);
        RenderSystem.depthMask(Minecraft.useShaderTransparency());
        RenderSystem.setShaderColor(((COLOR >> 16) & 0xFF) / 255.0F, ((COLOR >> 8) & 0xFF) / 255.0F, (COLOR & 0xFF) / 255.0F, alpha);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.polygonOffset(-3.0F, -3.0F);
        RenderSystem.enablePolygonOffset();
        RenderSystem.disableCull();

        float time = (Util.getMillis() % 3000L) / 3000.0F;
        double minY = -EXTENT;
        double maxY = EXTENT;
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        double fromZ = Math.max(minZ, cam.z - EXTENT);
        double toZ = Math.min(maxZ, cam.z + EXTENT);
        double fromX = Math.max(minX, cam.x - EXTENT);
        double toX = Math.min(maxX, cam.x + EXTENT);
        if (fromZ < toZ) {
            wallAlongZ(buffer, minX - cam.x, fromZ - cam.z, toZ - cam.z, fromZ, toZ, minY, maxY, cam.y, time);
            wallAlongZ(buffer, maxX - cam.x, fromZ - cam.z, toZ - cam.z, fromZ, toZ, minY, maxY, cam.y, time);
        }
        if (fromX < toX) {
            wallAlongX(buffer, minZ - cam.z, fromX - cam.x, toX - cam.x, fromX, toX, minY, maxY, cam.y, time);
            wallAlongX(buffer, maxZ - cam.z, fromX - cam.x, toX - cam.x, fromX, toX, minY, maxY, cam.y, time);
        }
        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.enableCull();
        RenderSystem.polygonOffset(0.0F, 0.0F);
        RenderSystem.disablePolygonOffset();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
    }

    private static void wallAlongZ(BufferBuilder buffer, double x, double z0, double z1, double worldZ0, double worldZ1, double y0, double y1, double camY, float time) {
        float u0 = (float) (worldZ0 * 0.5D) + time;
        float u1 = (float) (worldZ1 * 0.5D) + time;
        float v0 = (float) ((camY + y0) * 0.5D) + time;
        float v1 = (float) ((camY + y1) * 0.5D) + time;
        buffer.vertex(x, y1, z0).uv(u0, v0).endVertex();
        buffer.vertex(x, y1, z1).uv(u1, v0).endVertex();
        buffer.vertex(x, y0, z1).uv(u1, v1).endVertex();
        buffer.vertex(x, y0, z0).uv(u0, v1).endVertex();
    }

    private static void wallAlongX(BufferBuilder buffer, double z, double x0, double x1, double worldX0, double worldX1, double y0, double y1, double camY, float time) {
        float u0 = (float) (worldX0 * 0.5D) + time;
        float u1 = (float) (worldX1 * 0.5D) + time;
        float v0 = (float) ((camY + y0) * 0.5D) + time;
        float v1 = (float) ((camY + y1) * 0.5D) + time;
        buffer.vertex(x0, y1, z).uv(u0, v0).endVertex();
        buffer.vertex(x1, y1, z).uv(u1, v0).endVertex();
        buffer.vertex(x1, y0, z).uv(u1, v1).endVertex();
        buffer.vertex(x0, y0, z).uv(u0, v1).endVertex();
    }
}
