package com.github.mim1q.minecells.item.weapon;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class AssassinsDaggerItem extends CustomMeleeWeaponItem {
    public AssassinsDaggerItem(Properties properties) {
        super("assassins_dagger", 5.0D, 1.9D, 0.0F, 4.0F, properties);
    }

    @Override
    public boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return target != null && Mth.degreesDifferenceAbs(target.yBodyRot, attacker.getYHeadRot()) < 60.0F;
    }
}
