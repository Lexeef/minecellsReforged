package com.github.mim1q.minecells.util;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import net.minecraftforge.fml.util.thread.EffectiveSide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class MineCellsText {
    private MineCellsText() {
    }

    /**
     * Fabric uses owo rich translations to color part of a name. Forge lang files are plain strings,
     * so the colored part is found inside the translated text: {@code <key>.highlight} if present
     * (inflected languages), otherwise {@code fallbackHighlightKey}. Only resolved on the client thread,
     * so server-created components stay translatable in the reader's language.
     */
    public static MutableComponent highlight(String key, String fallbackHighlightKey, int color) {
        if (!EffectiveSide.get().isClient()) {
            return Component.translatable(key);
        }
        Language language = Language.getInstance();
        String highlightKey = language.has(key + ".highlight") ? key + ".highlight" : fallbackHighlightKey;
        if (!language.has(key) || !language.has(highlightKey)) {
            return Component.translatable(key);
        }
        String text = language.getOrDefault(key);
        String part = language.getOrDefault(highlightKey);
        int start = text.indexOf(part);
        if (start < 0) {
            start = text.toLowerCase(Locale.ROOT).indexOf(part.toLowerCase(Locale.ROOT));
        }
        if (part.isEmpty() || start < 0) {
            return Component.translatable(key);
        }
        int end = start + part.length();
        MutableComponent result = Component.empty();
        if (start > 0) {
            result.append(text.substring(0, start));
        }
        result.append(Component.literal(text.substring(start, end)).withStyle(Style.EMPTY.withColor(color)));
        if (end < text.length()) {
            result.append(text.substring(end));
        }
        return result;
    }

    public static List<MutableComponent> splitIfExceeds(Component text, int maxLength) {
        String string = text.getString();
        if (string.length() <= maxLength) {
            return List.of(text.copy());
        }
        Style style = text.getStyle();
        List<MutableComponent> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : string.split(" ")) {
            if (!line.isEmpty() && line.length() + 1 + word.length() > maxLength) {
                lines.add(Component.literal(line.toString()).withStyle(style));
                line.setLength(0);
            }
            if (!line.isEmpty()) {
                line.append(' ');
            }
            line.append(word);
        }
        if (!line.isEmpty()) {
            lines.add(Component.literal(line.toString()).withStyle(style));
        }
        return lines;
    }
}
