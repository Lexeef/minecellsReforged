package com.github.mim1q.minecells.client.renderer;

import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;
import com.github.mim1q.minecells.MineCells;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SpriteMineCellsMonsterRenderer extends EntityRenderer<MineCellsMonsterEntity> {
    private static final Map<String, RenderProfile> RENDER_PROFILES = Map.ofEntries(
        Map.entry("leaping_zombie", profile("textures/entity/leaping_zombie/leaping_zombie.png", "textures/entity/leaping_zombie/leaping_zombie_glow.png", 0.92F, 1.25F, 0.42F, 0.60F)),
        Map.entry("shocker", profile("textures/entity/shocker/shocker.png", "textures/entity/shocker/shocker_glow.png", 1.08F, 1.65F, 0.50F, 0.82F)),
        Map.entry("grenadier", profile("textures/entity/grenadier/grenadier.png", "textures/entity/grenadier/grenadier_glow.png", 0.96F, 1.32F, 0.44F, 0.62F)),
        Map.entry("disgusting_worm", profile("textures/entity/disgusting_worm/disgusting_worm.png", "textures/entity/disgusting_worm/disgusting_worm_glow.png", 1.20F, 0.55F, 0.26F, 0.28F)),
        Map.entry("inquisitor", profile("textures/entity/inquisitor.png", null, 0.88F, 1.38F, 0.42F, 0.66F)),
        Map.entry("kamikaze", profile("textures/entity/kamikaze.png", null, 0.74F, 0.74F, 0.25F, 0.24F)),
        Map.entry("protector", profile("textures/entity/protector/protector.png", "textures/entity/protector/protector_glow.png", 0.98F, 1.42F, 0.48F, 0.72F)),
        Map.entry("undead_archer", profile("textures/entity/undead_archer.png", null, 0.86F, 1.40F, 0.42F, 0.68F)),
        Map.entry("shieldbearer", profile("textures/entity/shieldbearer.png", null, 0.95F, 1.42F, 0.48F, 0.72F)),
        Map.entry("mutated_bat", profile("textures/entity/mutated_bat.png", null, 0.78F, 0.62F, 0.18F, 0.02F)),
        Map.entry("sewers_tentacle", profile("textures/entity/sewers_tentacle/purple.png", null, 0.72F, 1.95F, 0.12F, 0.98F)),
        Map.entry("rancid_rat", profile("textures/entity/rancid_rat/rancid_rat.png", "textures/entity/rancid_rat/rancid_rat_glow.png", 0.78F, 0.44F, 0.14F, 0.10F)),
        Map.entry("runner", profile("textures/entity/runner/runner.png", "textures/entity/runner/runner_glow.png", 0.90F, 1.50F, 0.38F, 0.74F)),
        Map.entry("scorpion", profile("textures/entity/scorpion/scorpion.png", "textures/entity/scorpion/scorpion_glow.png", 1.10F, 0.72F, 0.26F, 0.20F)),
        Map.entry("buzzcutter", profile("textures/entity/fly/buzzcutter.png", "textures/entity/fly/buzzcutter_glow.png", 0.80F, 0.64F, 0.20F, 0.18F)),
        Map.entry("sweeper", profile("textures/entity/sweeper/sweeper.png", "textures/entity/sweeper/sweeper_glow.png", 1.10F, 1.12F, 0.42F, 0.42F))
    );
    private static final ResourceLocation FALLBACK_TEXTURE = MineCells.id("textures/misc/empty.png");
    private static final RenderProfile FALLBACK_PROFILE = new RenderProfile(FALLBACK_TEXTURE, null, 0.9F, 1.2F, 0.35F, 0.6F);

    public SpriteMineCellsMonsterRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = FALLBACK_PROFILE.shadowRadius();
    }

    @Override
    public void render(MineCellsMonsterEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        RenderProfile profile = getProfile(entity);
        this.shadowRadius = profile.shadowRadius();
        poseStack.pushPose();
        float width = Mth.clamp(Math.max(entity.getBbWidth(), 0.55F) * profile.widthScale(), 0.35F, 1.6F);
        float height = Mth.clamp(Math.max(entity.getBbHeight(), 0.55F) * profile.heightScale(), 0.35F, 3.2F);
        poseStack.translate(0.0F, profile.yOffset() + height * 0.5F, 0.0F);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(width, height, 1.0F);
        renderQuad(poseStack, buffer.getBuffer(RenderType.entityCutoutNoCull(profile.texture())), packedLight);
        if (profile.glowTexture() != null) {
            renderQuad(poseStack, buffer.getBuffer(RenderType.eyes(profile.glowTexture())), LightTexture.FULL_BRIGHT);
        }
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(MineCellsMonsterEntity entity) {
        return getProfile(entity).texture();
    }

    private static String entityName(EntityType<?> type) {
        ResourceLocation key = EntityType.getKey(type);
        return key == null ? "" : key.getPath();
    }

    private static RenderProfile getProfile(MineCellsMonsterEntity entity) {
        return RENDER_PROFILES.getOrDefault(entityName(entity.getType()), FALLBACK_PROFILE);
    }

    private static RenderProfile profile(String texturePath, @Nullable String glowTexturePath, float widthScale, float heightScale, float shadowRadius, float yOffset) {
        return new RenderProfile(
            MineCells.id(texturePath),
            glowTexturePath == null ? null : MineCells.id(glowTexturePath),
            widthScale,
            heightScale,
            shadowRadius,
            yOffset
        );
    }

    private static void renderQuad(PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        PoseStack.Pose pose = poseStack.last();
        consumer.vertex(pose.pose(), -0.5F, -0.5F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
        consumer.vertex(pose.pose(), 0.5F, -0.5F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
        consumer.vertex(pose.pose(), 0.5F, 0.5F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
        consumer.vertex(pose.pose(), -0.5F, 0.5F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
    }

    private record RenderProfile(
        ResourceLocation texture,
        @Nullable ResourceLocation glowTexture,
        float widthScale,
        float heightScale,
        float shadowRadius,
        float yOffset
    ) {
    }
}
