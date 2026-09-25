package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.effect.MineCellsEffectFlags;
import com.github.mim1q.minecells.util.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraft.world.entity.player.Player;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Forge replacement for Fabric's {@code MineCellsEffectsFeatureRenderer}: three stars orbit above stunned entities.
 */
@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MineCellsEffectRenderEvents {
    private static final ResourceLocation STUNNED_STAR = MineCells.id("textures/entity/effect/stunned.png");
    private static final int FULL_BRIGHT = 0xF000F0;

    private MineCellsEffectRenderEvents() {
    }

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Post<?, ?> event) {
        LivingEntity entity = event.getEntity();
        if (!ClientEffectFlags.has(entity, MineCellsEffectFlags.STUNNED)) {
            return;
        }
        PoseStack poseStack = event.getPoseStack();
        VertexConsumer consumer = event.getMultiBufferSource().getBuffer(RenderType.entityCutoutNoCull(STUNNED_STAR));
        float theta = (entity.tickCount + event.getPartialTick()) * 20.0F;
        poseStack.pushPose();
        poseStack.translate(0.0D, entity.getBbHeight() + 0.5D, 0.0D);
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        for (int i = 0; i < 3; i++) {
            float radians = -theta * Mth.DEG_TO_RAD;
            poseStack.pushPose();
            poseStack.translate(Mth.cos(radians) * 0.2F, Mth.sin(radians) * 0.2F, 0.0F);
            RenderUtils.drawBillboard(consumer, poseStack, FULL_BRIGHT, 0.5F, 0.5F, 0.0F, 1.0F, 0.0F, 1.0F, 0xFFFFFFFF, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
            theta += 120.0F;
        }
        poseStack.popPose();
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide() || entity.getRandom().nextFloat() < 0.2F) {
            return;
        }
        if (ClientEffectFlags.has(entity, MineCellsEffectFlags.FROZEN)) {
            ParticleUtils.addInBox(entity.level(), ParticleTypes.SNOWFLAKE, entity.getBoundingBox().inflate(0.1D), 1, new Vec3(0.001D, 0.001D, 0.001D));
        }
    }

    @SubscribeEvent
    public static void onRenderBlockHighlight(RenderHighlightEvent.Block event) {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.hasEffect(MineCellsStatusEffects.DISARMED.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            ClientEffectFlags.remove(event.getEntity().getId());
        }
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientEffectFlags.clear();
    }
}
