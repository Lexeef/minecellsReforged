package com.github.mim1q.minecells.client.renderer.misc;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.util.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class AdvancementHintRenderer {
    private static final Map<ResourceLocation, Boolean> RENDER_ADVANCEMENT_MAP = new ConcurrentHashMap<>();
    private static final ResourceLocation TEXTURE = MineCells.id("textures/misc/hint_marker.png");
    private static final int FULL_BRIGHT = 0xF000F0;

    @Nullable
    private final ResourceLocation advancementId;
    private final int color;
    @Nullable
    private final Supplier<? extends ItemLike> item;

    public AdvancementHintRenderer(@Nullable ResourceLocation advancementId, int argb, @Nullable Supplier<? extends ItemLike> item) {
        this.advancementId = advancementId;
        this.color = argb;
        this.item = item;
        if (advancementId != null) {
            RENDER_ADVANCEMENT_MAP.putIfAbsent(advancementId, true);
        }
    }

    public void render(PoseStack poseStack, MultiBufferSource buffers, float animationProgress) {
        if (advancementId != null && !RENDER_ADVANCEMENT_MAP.getOrDefault(advancementId, true)) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        poseStack.pushPose();
        VertexConsumer vertices = buffers.getBuffer(RenderType.entityCutout(TEXTURE));
        float rotationAngle = minecraft.getEntityRenderDispatcher().cameraOrientation()
            .getEulerAnglesYXZ(new Vector3f())
            .y;
        poseStack.mulPose(new Quaternionf().rotationY(rotationAngle));
        poseStack.translate(-1.0F / 16.0F, 0.25F, 0.0F);
        float yOffset = Mth.sin(animationProgress * 0.33F) * 0.05F;
        poseStack.translate(0.0F, yOffset, 0.0F);
        RenderUtils.drawBillboard(vertices, poseStack, FULL_BRIGHT, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 1.0F, color | 0xFF000000, OverlayTexture.NO_OVERLAY);

        if (item != null) {
            poseStack.translate(1.0F / 16.0F, 0.5F - yOffset * 0.25F, 0.0F);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            minecraft.getItemRenderer().renderStatic(
                new ItemStack(item.get()),
                ItemDisplayContext.FIXED,
                FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffers,
                minecraft.level,
                0
            );
        }
        poseStack.popPose();
    }

    public static void applyServerState(Map<ResourceLocation, Boolean> completed, boolean replace) {
        if (replace) {
            RENDER_ADVANCEMENT_MAP.replaceAll((id, rendered) -> true);
        }
        completed.forEach((id, done) -> RENDER_ADVANCEMENT_MAP.put(id, !done));
    }

    public static void resetAdvancements() {
        RENDER_ADVANCEMENT_MAP.replaceAll((id, rendered) -> true);
    }
}
