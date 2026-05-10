package com.github.mim1q.minecells.client.gui;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.config.MineCellsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
public final class CustomBossBarRenderer {
    private static final ResourceLocation TEXTURE = MineCells.id("textures/gui/boss_bars.png");
    private static final String CONJUNCTIVIUS_KEY = "entity.minecells.conjunctivius";
    private static final String CONCIERGE_KEY = "entity.minecells.concierge";
    private static final int TEXTURE_SIZE = 256;

    private CustomBossBarRenderer() {
    }

    @SubscribeEvent
    public static void onBossBarRender(CustomizeGuiOverlayEvent.BossEventProgress event) {
        if (!MineCellsConfig.CLIENT.customBossBars.get()) {
            return;
        }

        LerpingBossEvent bossEvent = event.getBossEvent();
        String key = getMineCellsBossKey(bossEvent);
        if (key == null) {
            return;
        }

        int verticalIndex = switch (key) {
            case CONJUNCTIVIUS_KEY -> 0;
            case CONCIERGE_KEY -> 1;
            default -> -1;
        };
        if (verticalIndex < 0) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        Minecraft minecraft = Minecraft.getInstance();
        int x = event.getX() - 12;
        int y = event.getY() - 8;
        int u = 0;
        int v = verticalIndex * 64;

        guiGraphics.blit(TEXTURE, x, y, u, v, 208, 32, TEXTURE_SIZE, TEXTURE_SIZE);
        int progressWidth = Mth.clamp(14 + (int) (180.0F * bossEvent.getProgress()), 14, 194);
        guiGraphics.blit(TEXTURE, x, y, u, v + 32, progressWidth, 32, TEXTURE_SIZE, TEXTURE_SIZE);

        Component title = styleTitle(bossEvent.getName());
        int titleX = (guiGraphics.guiWidth() - minecraft.font.width(title)) / 2;
        guiGraphics.drawString(minecraft.font, title, titleX, y + 11, 0xFFFFFF, false);

        event.setCanceled(true);
        event.setIncrement(0);
    }

    private static String getMineCellsBossKey(LerpingBossEvent bossEvent) {
        if (bossEvent.getName().getContents() instanceof TranslatableContents translatable) {
            String key = translatable.getKey();
            if (key.startsWith("entity.minecells.")) {
                return key;
            }
        }
        return null;
    }

    private static Component styleTitle(Component component) {
        if (component.getContents() instanceof TranslatableContents translatable) {
            return switch (translatable.getKey()) {
                case CONJUNCTIVIUS_KEY -> component.copy().withStyle(style -> style.withBold(true).withColor(0xDF5FE2));
                case CONCIERGE_KEY -> component.copy().withStyle(style -> style.withBold(true).withColor(0xF8AF0C));
                default -> component;
            };
        }
        return component;
    }
}
