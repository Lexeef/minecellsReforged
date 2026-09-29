package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.util.MineCellsCombatHelper;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class SpiteSwordItem extends CustomMeleeWeaponItem {
    public SpiteSwordItem(Properties properties) {
        super("spite_sword", 7.0D, 1.5D, 0.0F, 2.0F, properties);
    }

    @Override
    public boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return attacker.level().getGameTime() - MineCellsCombatHelper.getLastDamageTime(attacker) <= 80;
    }
}
