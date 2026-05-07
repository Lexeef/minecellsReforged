package com.github.mim1q.minecells.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class MineCellsItemDescriptionTooltips {
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) {
            return;
        }

        String descriptionKey = stack.getDescriptionId() + ".description";
        if (!I18n.exists(descriptionKey)) {
            return;
        }

        String translated = I18n.get(descriptionKey);
        if (translated.isBlank()) {
            return;
        }

        for (String line : translated.split("\\R")) {
            if (!line.isBlank()) {
                event.getToolTip().add(Component.literal(line).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }
}
