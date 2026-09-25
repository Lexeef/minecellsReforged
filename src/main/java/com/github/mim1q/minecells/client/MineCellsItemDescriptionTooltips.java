package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.item.weapon.CustomMeleeWeaponItem;
import com.github.mim1q.minecells.item.weapon.bow.CustomArrowShooter;
import com.github.mim1q.minecells.item.weapon.bow.CustomArrowType;
import com.github.mim1q.minecells.item.weapon.bow.CustomBowItem;
import com.github.mim1q.minecells.item.weapon.interfaces.CrittingWeapon;
import com.github.mim1q.minecells.item.weapon.interfaces.WeaponWithAbility;
import com.github.mim1q.minecells.item.weapon.shield.CustomShieldItem;
import com.github.mim1q.minecells.item.weapon.shield.CustomShieldType;
import com.github.mim1q.minecells.registry.MineCellsItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class MineCellsItemDescriptionTooltips {
    private static final int DEFAULT_MAX_LINE_WIDTH = 1024;
    private static final int DESCRIPTION_MAX_LINE_WIDTH = 40;
    private static final int HIDE_ADDITIONAL_MASK = ItemStack.TooltipPart.ADDITIONAL.getMask();
    private static final Style DEFAULT_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY);
    private static final Style SPECIAL_STYLE = Style.EMPTY.withColor(ChatFormatting.GOLD);

    private enum Group { MELEE, RANGED, SHIELD }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();
        if (stack.isEmpty() || player == null) {
            return;
        }
        Group group = groupOf(stack.getItem());
        if (group == null) {
            return;
        }

        List<Component> lines = new ArrayList<>();
        int maxLineWidth = DEFAULT_MAX_LINE_WIDTH;
        String descriptionKey = stack.getDescriptionId() + ".description";
        if (I18n.exists(descriptionKey) && !I18n.get(descriptionKey).isBlank()) {
            maxLineWidth = DESCRIPTION_MAX_LINE_WIDTH;
            lines.add(Component.translatable(descriptionKey));
        }
        switch (group) {
            case MELEE -> addWeaponStats(lines, stack, player);
            case RANGED -> {
                if (stack.getItem() instanceof CustomArrowShooter shooter) {
                    addRangedStats(lines, stack, shooter.getArrowType(), player);
                }
            }
            case SHIELD -> {
                if (stack.getItem() instanceof CustomShieldItem shield) {
                    addShieldStats(lines, stack, shield.getShieldType(), player);
                }
            }
        }
        if (lines.isEmpty()) {
            return;
        }

        List<Component> result = new ArrayList<>();
        for (Component line : lines) {
            for (Component part : splitTextIfExceeds(line, maxLineWidth)) {
                result.add(part.getStyle().isEmpty() ? part.copy().setStyle(DEFAULT_STYLE) : part);
            }
        }
        List<Component> tooltip = event.getToolTip();
        int index = Math.min(tooltip.size(), 1 + countItemLines(stack, player, event.getFlags()));
        tooltip.addAll(index, result);
    }

    private static Group groupOf(Item item) {
        if (CustomMeleeWeaponItem.getAllMeleeWeapons().contains(item)
            || item == MineCellsItems.FROST_BLAST.get()
            || item == MineCellsItems.LIGHTNING_BOLT.get()
            || item == MineCellsItems.ELECTRIC_WHIP.get()
            || item == MineCellsItems.PHASER.get()) {
            return Group.MELEE;
        }
        if (item instanceof CustomBowItem
            || item == MineCellsItems.FIREBRANDS.get()
            || item == MineCellsItems.THROWING_KNIFE.get()) {
            return Group.RANGED;
        }
        if (item instanceof CustomShieldItem) {
            return Group.SHIELD;
        }
        return null;
    }

    private static int countItemLines(ItemStack stack, Player player, TooltipFlag flag) {
        if (stack.getTag() != null && (stack.getTag().getInt("HideFlags") & HIDE_ADDITIONAL_MASK) != 0) {
            return 0;
        }
        List<Component> itemLines = new ArrayList<>();
        stack.getItem().appendHoverText(stack, player.level(), itemLines, flag);
        return itemLines.size();
    }

    private static List<Component> splitTextIfExceeds(Component text, int width) {
        String full = text.getString();
        if (full.length() <= width && full.indexOf('\n') < 0) {
            return List.of(text);
        }
        List<Component> parts = new ArrayList<>();
        for (String segment : full.split("\n", -1)) {
            splitSegment(parts, segment, text.getStyle(), width);
        }
        return parts;
    }

    private static void splitSegment(List<Component> parts, String string, Style style, int width) {
        while (string.length() > width) {
            int lastSpace = 0;
            for (int i = 0; i < width; i++) {
                if (string.charAt(i) == ' ') {
                    lastSpace = i;
                }
            }
            parts.add(Component.literal(string.substring(0, lastSpace)).setStyle(style));
            string = string.substring(lastSpace + 1);
        }
        parts.add(Component.literal(string).setStyle(style));
    }

    private static void addWeaponStats(List<Component> lines, ItemStack stack, Player player) {
        Item item = stack.getItem();
        boolean doesCrit = false;
        if (item instanceof CrittingWeapon crittingWeapon) {
            float critDamage = crittingWeapon.getAdditionalCritDamage(stack, null, player);
            if (critDamage > 0) {
                doesCrit = true;
                lines.add(special("item.minecells.crit_damage", critDamage));
            }
        }
        if (item instanceof WeaponWithAbility weaponWithAbility) {
            if (doesCrit) {
                lines.add(Component.empty());
            }
            float damage = weaponWithAbility.getAbilityDamage(stack, player, null);
            double cooldown = weaponWithAbility.getAbilityCooldown(stack, player) / 20.0D;
            lines.add(special("item.minecells.special_ability_hold", damage, cooldown));
        }
    }

    private static void addRangedStats(List<Component> lines, ItemStack stack, CustomArrowType type, Player player) {
        double drawTime = type.getDrawTime(player, stack) / 20.0D;
        double cooldown = type.getCooldown(player, stack) / 20.0D;
        float damage = type.getDamage(player, stack, null);
        float critDamage = type.getAdditionalCritDamage(player, stack, null);
        if (damage > 0) {
            lines.add(special("item.minecells.bow.damage", damage));
        }
        if (critDamage > 0) {
            lines.add(special("item.minecells.bow.crit_damage", critDamage));
        }
        if (drawTime > 0) {
            lines.add(special("item.minecells.bow.draw_time", drawTime));
        }
        if (cooldown > 0) {
            lines.add(special("item.minecells.bow.cooldown", cooldown));
        }
    }

    private static void addShieldStats(List<Component> lines, ItemStack stack, CustomShieldType type, Player player) {
        float parryDamage = type.getParryDamage(player, stack, null);
        float damageReduction = type.getBlockDamageReduction(player, stack, null);
        double cooldown = type.getCooldown(player, stack, false) / 20.0D;
        if (parryDamage > 0) {
            lines.add(special("item.minecells.shield.parry_damage", parryDamage));
        }
        if (damageReduction > 0) {
            lines.add(special("item.minecells.shield.damage_reduction", String.format(Locale.ROOT, "%.1f", damageReduction * 100.0D)));
        }
        if (cooldown > 0) {
            lines.add(special("item.minecells.shield.cooldown", cooldown));
        }
    }

    private static Component special(String key, Object... args) {
        return Component.translatable(key, args).setStyle(SPECIAL_STYLE);
    }
}
