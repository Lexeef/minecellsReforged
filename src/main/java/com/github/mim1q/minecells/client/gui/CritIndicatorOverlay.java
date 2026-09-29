package com.github.mim1q.minecells.client.gui;

import com.github.mim1q.minecells.config.MineCellsConfig;
import com.github.mim1q.minecells.item.weapon.interfaces.CritIndicator;
import com.github.mim1q.minecells.MineCells;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CritIndicatorOverlay {
    private static final ResourceLocation TEXTURE = MineCells.id("textures/gui/crosshair/crit_indicator.png");
    private static final int SIZE = 16;
    private static final int OFFSET_X = 12;

    private CritIndicatorOverlay() {
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "crit_indicator", CritIndicatorOverlay::render);
    }

    private static void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui || !minecraft.options.getCameraType().isFirstPerson()) {
            return;
        }
        if (minecraft.player.isSpectator() || !MineCellsConfig.CLIENT.showCritIndicator.get()) {
            return;
        }
        ItemStack stack = minecraft.player.getMainHandItem();
        if (!(stack.getItem() instanceof CritIndicator indicator)) {
            return;
        }
        LivingEntity target = minecraft.hitResult instanceof EntityHitResult entityHit
            && entityHit.getEntity() instanceof LivingEntity living ? living : null;
        if (!indicator.shouldShowCritIndicator(minecraft.player, target, stack)) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int x = width / 2 + OFFSET_X - SIZE / 2;
        int y = height / 2 - SIZE / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, SIZE, SIZE, SIZE, SIZE);
    }
}
