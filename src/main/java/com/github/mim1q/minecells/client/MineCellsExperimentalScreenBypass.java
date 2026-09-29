package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.MineCells;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.worldselection.ConfirmExperimentalFeaturesScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.packs.repository.Pack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

/**
 * Skips the "Experimental settings" dialogs caused by Mine Cells' datapack dimensions: the feature-flag
 * screen, the vanilla create-world warning ({@code WorldOpenFlows.confirmWorldCreation}) and Forge's
 * load-world warning ({@code ForgeHooksClient.createWorldConfirmationScreen}).
 * Forge runs with SRG names, so fields are resolved by type — not by mapped name.
 */
@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class MineCellsExperimentalScreenBypass {
    private static final Field FEATURES_CALLBACK_FIELD = findField(ConfirmExperimentalFeaturesScreen.class, BooleanConsumer.class);
    private static final Field ENABLED_PACKS_FIELD = findField(ConfirmExperimentalFeaturesScreen.class, Collection.class);
    private static final Field CONFIRM_CALLBACK_FIELD = findField(ConfirmScreen.class, BooleanConsumer.class);
    private static final Set<String> EXPERIMENTAL_WORLD_TITLE_KEYS = Set.of(
        "selectWorld.backupQuestion.experimental",
        "selectWorld.warning.experimental.title"
    );

    private MineCellsExperimentalScreenBypass() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onScreenOpening(ScreenEvent.Opening event) {
        BooleanConsumer callback = null;
        if (event.getNewScreen() instanceof ConfirmScreen confirmScreen && isExperimentalWorldWarning(confirmScreen)) {
            callback = readCallback(CONFIRM_CALLBACK_FIELD, confirmScreen);
        } else if (event.getNewScreen() instanceof ConfirmExperimentalFeaturesScreen screen && shouldBypass(screen)) {
            callback = readCallback(FEATURES_CALLBACK_FIELD, screen);
        } else {
            return;
        }
        if (callback == null) {
            MineCells.LOGGER.warn("Mine Cells: experimental warning screen found, but its callback field is missing");
            return;
        }
        event.setCanceled(true);
        BooleanConsumer proceed = callback;
        Minecraft.getInstance().tell(() -> proceed.accept(true));
    }

    private static boolean isExperimentalWorldWarning(ConfirmScreen screen) {
        return screen.getTitle().getContents() instanceof TranslatableContents contents
            && EXPERIMENTAL_WORLD_TITLE_KEYS.contains(contents.getKey());
    }

    private static boolean shouldBypass(ConfirmExperimentalFeaturesScreen screen) {
        Collection<?> packs = null;
        if (ENABLED_PACKS_FIELD != null) {
            try {
                packs = (Collection<?>) ENABLED_PACKS_FIELD.get(screen);
            } catch (IllegalAccessException ignored) {
            }
        }
        if (packs == null || packs.isEmpty()) {
            return true;
        }
        for (Object packObject : packs) {
            String id = packId(packObject);
            if (id != null && id.toLowerCase().contains(MineCells.MOD_ID)) {
                return true;
            }
        }
        return false;
    }

    private static String packId(Object packObject) {
        if (packObject instanceof Pack pack) {
            return pack.getId();
        }
        if (packObject instanceof String string) {
            return string;
        }
        try {
            for (Field field : packObject.getClass().getDeclaredFields()) {
                if (field.getType() == String.class) {
                    field.setAccessible(true);
                    if (field.get(packObject) instanceof String id && !id.isEmpty()) {
                        return id;
                    }
                }
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }
        return String.valueOf(packObject);
    }

    private static BooleanConsumer readCallback(Field field, Object screen) {
        if (field == null) {
            return null;
        }
        try {
            return (BooleanConsumer) field.get(screen);
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }

    private static Field findField(Class<?> owner, Class<?> type) {
        for (Field field : owner.getDeclaredFields()) {
            if (type.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                return field;
            }
        }
        return null;
    }
}
