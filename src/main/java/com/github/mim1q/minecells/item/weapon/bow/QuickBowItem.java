package com.github.mim1q.minecells.item.weapon.bow;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class QuickBowItem extends CustomBowItem {
    public QuickBowItem(Properties properties) {
        super(properties, CustomArrowType.QUICK);
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.onUseTick(level, user, stack, remainingUseTicks);
        if (level.isClientSide) {
            return;
        }

        if (remainingUseTicks == 1) {
            shoot(level, user, stack);
            ItemStack arrow = user.getProjectile(stack);
            loadMaxProjectiles(level, (Player) user, stack, arrow, this.maxProjectileCount);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int remainingUseTicks) {
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return this.getDrawTime(null, stack) + 2;
    }

    @Override
    public float getFovMultiplier(Player player, ItemStack stack) {
        if (player.isUsingItem()) {
            return 0.9F;
        }
        return 1.0F;
    }
}
