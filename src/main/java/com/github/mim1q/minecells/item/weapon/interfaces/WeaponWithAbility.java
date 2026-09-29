package com.github.mim1q.minecells.item.weapon.interfaces;

import com.github.mim1q.minecells.valuecalculators.ValueCalculator;
import com.github.mim1q.minecells.valuecalculators.ValueCalculatorContext;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

public interface WeaponWithAbility {
    ValueCalculator getAbilityDamageCalculator();

    ValueCalculator getAbilityCooldownCalculator();

    default float getAbilityDamage(ItemStack stack, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        return (float) getAbilityDamageCalculator().calculate(ValueCalculatorContext.of(attacker, stack, target));
    }

    default int getAbilityCooldown(ItemStack stack, @Nullable LivingEntity attacker) {
        return (int) (getAbilityCooldownCalculator().calculate(ValueCalculatorContext.of(attacker, stack)) * 20.0D);
    }
}
