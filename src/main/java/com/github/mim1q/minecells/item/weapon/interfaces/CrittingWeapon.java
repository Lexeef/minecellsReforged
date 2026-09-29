package com.github.mim1q.minecells.item.weapon.interfaces;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

public interface CrittingWeapon extends CritIndicator {
    default float getExtraDamage(ItemStack stack, @Nullable LivingEntity target, LivingEntity attacker) {
        return 0.0F;
    }

    boolean canCrit(ItemStack stack, @Nullable LivingEntity target, LivingEntity attacker);

    default float getAdditionalCritDamage(ItemStack stack, @Nullable LivingEntity target, @Nullable LivingEntity attacker) {
        return 4.0F;
    }

    default boolean shouldPlayCritSound(ItemStack stack, @Nullable LivingEntity target, @Nullable LivingEntity attacker) {
        return true;
    }

    @Override
    default boolean shouldShowCritIndicator(@Nullable Player player, @Nullable LivingEntity target, ItemStack stack) {
        return player != null && canCrit(stack, target, player) && shouldPlayCritSound(stack, target, player);
    }
}
