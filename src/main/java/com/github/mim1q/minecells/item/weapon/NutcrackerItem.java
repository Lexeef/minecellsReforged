package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.util.MineCellsCombatHelper;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class NutcrackerItem extends CustomMeleeWeaponItem {
    public NutcrackerItem(Properties properties) {
        super("nutcracker", 8.0D, 1.0D, 0.0F, 6.0F, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, user -> user.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return target != null
            && (MineCellsCombatHelper.isFrozenOrStunned(target) || target.getTicksFrozen() >= target.getTicksRequiredToFreeze());
    }
}
