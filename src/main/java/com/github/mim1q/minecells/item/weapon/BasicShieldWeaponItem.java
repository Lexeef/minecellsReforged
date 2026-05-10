package com.github.mim1q.minecells.item.weapon;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

public class BasicShieldWeaponItem extends ShieldItem {
    public BasicShieldWeaponItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return MineCellsWeaponTier.CELL_INFUSED_STEEL.getRepairIngredient().test(repairCandidate) || super.isValidRepairItem(stack, repairCandidate);
    }
}
