package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.registry.MineCellsItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public enum MineCellsWeaponTier implements Tier {
    CELL_INFUSED_STEEL;

    @Override
    public int getUses() {
        return 700;
    }

    @Override
    public float getSpeed() {
        return 6.0F;
    }

    @Override
    public float getAttackDamageBonus() {
        return 2.0F;
    }

    @Override
    public int getLevel() {
        return 2;
    }

    @Override
    public int getEnchantmentValue() {
        return 18;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(MineCellsItems.CELL_INFUSED_STEEL.get());
    }
}
