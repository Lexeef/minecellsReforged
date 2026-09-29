package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.effect.BleedingMobEffect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class BloodSwordItem extends CustomMeleeWeaponItem {
    public BloodSwordItem(Properties properties) {
        super("blood_sword", 7.0D, 1.6D, 0.0F, 0.0F, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        BleedingMobEffect.apply(target, 20 * 5);
        return super.hurtEnemy(stack, target, attacker);
    }
}
