package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.MineCells;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.ConfirmExperimentalFeaturesScreen;
import net.minecraft.server.packs.repository.Pack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.Collection;

@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class MineCellsExperimentalScreenBypass {
    private static final Field CALLBACK_FIELD = findField("callback");
    private static final Field ENABLED_PACKS_FIELD = findField("enabledPacks");

    private MineCellsExperimentalScreenBypass() {
    }

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (!(event.getNewScreen() instanceof ConfirmExperimentalFeaturesScreen screen)) {
            return;
        }
        if (!containsMineCellsPack(screen)) {
            return;
        }
        BooleanConsumer callback = getCallback(screen);
        if (callback == null) {
            return;
        }
        event.setNewScreen(event.getCurrentScreen());
        Minecraft.getInstance().execute(() -> callback.accept(true));
    }

    private static boolean containsMineCellsPack(ConfirmExperimentalFeaturesScreen screen) {
        Collection<?> packs = getEnabledPacks(screen);
        if (packs == null) {
            return false;
        }
        for (Object packObject : packs) {
            if (packObject instanceof Pack pack && pack.getId().contains(MineCells.MOD_ID)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static Collection<?> getEnabledPacks(ConfirmExperimentalFeaturesScreen screen) {
        if (ENABLED_PACKS_FIELD == null) {
            return null;
        }
        try {
            return (Collection<?>) ENABLED_PACKS_FIELD.get(screen);
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }

    private static BooleanConsumer getCallback(ConfirmExperimentalFeaturesScreen screen) {
        if (CALLBACK_FIELD == null) {
            return null;
        }
        try {
            return (BooleanConsumer) CALLBACK_FIELD.get(screen);
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }

    private static Field findField(String name) {
        try {
            Field field = ConfirmExperimentalFeaturesScreen.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}
