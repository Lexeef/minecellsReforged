package com.github.mim1q.minecells.client.renderer.monster;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.config.MineCellsConfig;
import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;
import com.github.mim1q.minecells.entity.ProtectorEntity;
import com.github.mim1q.minecells.util.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class ProtectorRenderer extends ModelBackedMineCellsMonsterRenderer<ProtectorModel> {
    private static final ResourceLocation TEXTURE = MineCells.id("textures/entity/protector/protector.png");
    private static final ResourceLocation GLOW_TEXTURE = MineCells.id("textures/entity/protector/protector_glow.png");
    private static final RenderType CONNECTION_LAYER = RenderType.entityCutout(MineCells.id("textures/misc/electric_arch.png"));
    private static final int[] CONNECTION_INDICES = {0, 1, 2, 3, 3, 2, 1, 0};

    public ProtectorRenderer(EntityRendererProvider.Context context) {
        super(context, new ProtectorModel(context.bakeLayer(MineCellsMonsterModelLayers.PROTECTOR)), 0.35F, TEXTURE, GLOW_TEXTURE, () -> MineCellsConfig.CLIENT.protectorGlow.get());
    }

    @Override
    public void render(MineCellsMonsterEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
        if (!(entity instanceof ProtectorEntity protector) || !protector.isActive()) {
            return;
        }
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        Vec3 origin = protector.getPosition(partialTick);
        for (Entity target : protector.trackedEntities) {
            if (!target.isAlive()) {
                continue;
            }
            Vec3 end = target.getPosition(partialTick).subtract(origin).add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
            renderConnection(buffers.getBuffer(CONNECTION_LAYER), pose, normal, new Vec3(0.0D, 1.25D, 0.0D), end, target.tickCount % 8);
        }
    }

    private static void renderConnection(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, Vec3 p0, Vec3 p1, int frame) {
        float x0 = (float) p0.x;
        float y0 = (float) p0.y;
        float z0 = (float) p0.z;
        float x1 = (float) p1.x;
        float y1 = (float) p1.y;
        float z1 = (float) p1.z;
        float dx = x1 - x0;
        float dy = y1 - y0;
        float dz = z1 - z0;
        dx = dx == 0 ? 0.001F : dx;
        float horizontal = Mth.sqrt(dx * dx + dz * dz);
        float length = Mth.sqrt(horizontal * horizontal + dy * dy);
        float offset = 0.5F;
        float yOffset = offset * (horizontal / length);
        float xOffset = offset * (dy / length) * (dx / horizontal);
        float zOffset = offset * (dy / length) * (dz / horizontal);
        float v0 = frame * 0.125F;
        float v1 = v0 + 0.125F;
        float[][] vertices = {
            {x0 + xOffset, y0 - yOffset, z0 + zOffset, 0.0F, v1},
            {x1 + xOffset, y1 - yOffset, z1 + zOffset, 1.0F, v1},
            {x1 - xOffset, y1 + yOffset, z1 - zOffset, 1.0F, v0},
            {x0 - xOffset, y0 + yOffset, z0 - zOffset, 0.0F, v0},
        };
        for (int i : CONNECTION_INDICES) {
            float[] v = vertices[i];
            RenderUtils.produceVertex(consumer, pose, normal, 0xF0, 0xFFFFFFFF, v[0], v[1], v[2], v[3], v[4], OverlayTexture.NO_OVERLAY);
        }
    }
}
