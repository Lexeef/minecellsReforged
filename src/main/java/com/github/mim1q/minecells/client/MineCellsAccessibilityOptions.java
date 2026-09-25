package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.config.MineCellsConfig;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Forge replacement for Fabric's {@code GameOptionsMixin} / {@code AccessibilityOptionsMixin}:
 * adds the global Mine Cells screen shake slider to the vanilla accessibility options.
 */
@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class MineCellsAccessibilityOptions {
    private MineCellsAccessibilityOptions() {
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof AccessibilityOptionsScreen)) {
            return;
        }
        for (var listener : event.getListenersList()) {
            if (listener instanceof OptionsList list) {
                list.addSmall(new OptionInstance<?>[] { createScreenShakeOption() });
                return;
            }
        }
    }

    private static OptionInstance<Double> createScreenShakeOption() {
        double current = Math.max(0.0D, Math.min(1.0D, MineCellsConfig.CLIENT.screenShakeGlobal.get()));
        return new OptionInstance<>(
            "options.minecells.screenshake",
            OptionInstance.cachedConstantTooltip(Component.translatable("options.minecells.screenshake.tooltip")),
            (caption, value) -> Component.translatable("options.percent_value", caption, (int) (value * 100.0D)),
            OptionInstance.UnitDouble.INSTANCE,
            current,
            value -> {
                MineCellsConfig.CLIENT.screenShakeGlobal.set(value);
                MineCellsConfig.CLIENT.screenShakeGlobal.save();
            }
        );
    }
}
