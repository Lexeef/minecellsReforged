package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.registry.MineCellsItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public enum MineCellsWeaponTier implements Tier {
    CELL_INFUSED_STEEL;

    @Override
    public int getUses() {
        return 1400;
    }

    @Override
    public float getSpeed() {
        return 7.0F;
    }

    @Override
    public float getAttackDamageBonus() {
        return 2.5F;
    }

    @Override
    public int getLevel() {
        return 3;
    }

    @Override
    public int getEnchantmentValue() {
        return 16;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(MineCellsItems.CELL_INFUSED_STEEL.get());
    }
}
