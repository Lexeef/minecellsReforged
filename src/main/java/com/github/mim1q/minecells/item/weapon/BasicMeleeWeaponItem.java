package com.github.mim1q.minecells.item.weapon;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

public class BasicMeleeWeaponItem extends SwordItem {
    public BasicMeleeWeaponItem(int attackDamage, float attackSpeed, Properties properties) {
        super(MineCellsWeaponTier.CELL_INFUSED_STEEL, attackDamage, attackSpeed, properties);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return MineCellsWeaponTier.CELL_INFUSED_STEEL.getRepairIngredient().test(repairCandidate) || super.isValidRepairItem(stack, repairCandidate);
    }
}
