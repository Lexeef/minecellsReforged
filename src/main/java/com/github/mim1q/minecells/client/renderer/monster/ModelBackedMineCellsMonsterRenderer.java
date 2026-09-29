package com.github.mim1q.minecells.client.renderer.monster;

import com.github.mim1q.minecells.client.renderer.layer.GlowEyesLayer;
import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.RenderUtils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.function.BooleanSupplier;

/**
 * Vanilla {@link MobRenderer} already calls {@link EntityModel#setupAnim} each frame on the render thread
 * with {@code limbSwing}/{@code limbSwingAmount}/{@code ageInTicks}. No GeckoLib controllers.
 */
public class ModelBackedMineCellsMonsterRenderer<M extends EntityModel<MineCellsMonsterEntity>> extends MobRenderer<MineCellsMonsterEntity, M> {
    private static final ResourceLocation RED_STAR_TEXTURE = MineCells.id("textures/entity/effect/red_star.png");
    private static final String ELITE_KEY = "entity.minecells.elite";
    private static final int FULL_BRIGHT = 0xF000F0;

    private final ResourceLocation texture;

    public ModelBackedMineCellsMonsterRenderer(EntityRendererProvider.Context context, M model, float shadowRadius, ResourceLocation texture, @Nullable ResourceLocation glowTexture) {
        this(context, model, shadowRadius, texture, glowTexture, () -> true);
    }

    public ModelBackedMineCellsMonsterRenderer(EntityRendererProvider.Context context, M model, float shadowRadius, ResourceLocation texture, @Nullable ResourceLocation glowTexture, BooleanSupplier glowEnabled) {
        super(context, model, shadowRadius);
        this.texture = texture;
        if (glowTexture != null) {
            addLayer(new GlowEyesLayer<>(this, entity -> glowTexture, glowEnabled));
        }
        if (model instanceof ArmedModel) {
            addHeldItemLayer(context);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(MineCellsMonsterEntity entity) {
        return texture;
    }

    @Override
    public void render(MineCellsMonsterEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
        if (entity.isElite()) {
            renderEliteLabel(entity, poseStack, buffers);
        }
    }

    @Override
    protected void scale(MineCellsMonsterEntity entity, PoseStack poseStack, float partialTick) {
        super.scale(entity, poseStack, partialTick);
        if (entity.isElite()) {
            float scale = MineCellsMonsterEntity.ELITE_SCALE;
            poseStack.scale(scale, scale, scale);
        }
    }

    private void renderEliteLabel(MineCellsMonsterEntity entity, PoseStack poseStack, MultiBufferSource buffers) {
        Font font = getFont();
        Component text = Component.translatable(ELITE_KEY);
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        float textScale = 1.0F / 48.0F;

        poseStack.pushPose();
        poseStack.translate(0.0D, entity.getBbHeight() + 0.66D, 0.0D);
        poseStack.scale(textScale, -textScale, -textScale);
        poseStack.translate(0.0D, -1.5D, 0.0D);
        poseStack.mulPose(new Quaternionf()
            .rotationY(MathUtils.radians(180.0F + camera.getYRot()))
            .rotateX(-MathUtils.radians(camera.getXRot()))
        );

        poseStack.pushPose();
        poseStack.translate(-1.0D, -6.0D, 0.1D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        VertexConsumer starVertices = buffers.getBuffer(RenderType.entityCutout(RED_STAR_TEXTURE));
        RenderUtils.drawBillboard(starVertices, poseStack, FULL_BRIGHT, 24.0F, 24.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0xFFFFFFFF, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        font.drawInBatch8xOutline(
            text.getVisualOrderText(),
            -font.width(text) / 2.0F,
            0.0F,
            0xFFC755,
            0x6D3100,
            poseStack.last().pose(),
            buffers,
            FULL_BRIGHT
        );
        poseStack.popPose();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void addHeldItemLayer(EntityRendererProvider.Context context) {
        addLayer(new ItemInHandLayer(this, context.getItemInHandRenderer()));
    }
}
