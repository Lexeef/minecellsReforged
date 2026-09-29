package com.github.mim1q.minecells.item.weapon.interfaces;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

public interface CritIndicator {
    boolean shouldShowCritIndicator(@Nullable Player player, @Nullable LivingEntity target, ItemStack stack);
}
