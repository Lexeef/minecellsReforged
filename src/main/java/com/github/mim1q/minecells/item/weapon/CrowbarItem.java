package com.github.mim1q.minecells.item.weapon;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class CrowbarItem extends CustomMeleeWeaponItem {
    public CrowbarItem(Properties properties) {
        super("crowbar", 6.0D, 1.6D, 0.0F, 4.0F, properties);
    }

    @Override
    public boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        long lastDoorBreakTime = stack.getOrCreateTag().getLong("lastDoorBreakTime");
        return attacker.level().getGameTime() - lastDoorBreakTime < 20 * 5;
    }
}
