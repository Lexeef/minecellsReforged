package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.util.MineCellsCombatHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BalancedBladeItem extends CustomMeleeWeaponItem {
    public BalancedBladeItem(Properties properties) {
        super("balanced_blade", 5.0D, 1.6D, 0.0F, 0.75F, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            MineCellsCombatHelper.addBalancedBladeStack(player);
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public float getAdditionalCritDamage(ItemStack stack, @Nullable LivingEntity target, @Nullable LivingEntity attacker) {
        if (attacker instanceof Player player) {
            return MineCellsCombatHelper.getBalancedBladeStacks(player) * super.getAdditionalCritDamage(stack, target, attacker);
        }
        return 9 * super.getAdditionalCritDamage(stack, target, attacker);
    }

    @Override
    public boolean shouldPlayCritSound(ItemStack stack, @Nullable LivingEntity target, @Nullable LivingEntity attacker) {
        return attacker instanceof Player player && MineCellsCombatHelper.getBalancedBladeStacks(player) >= 9;
    }
}
