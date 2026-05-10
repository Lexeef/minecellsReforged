package com.github.mim1q.minecells.item.weapon;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public class MultipleNocksBowItem extends BasicBowWeaponItem {
    public MultipleNocksBowItem(Properties properties) {
        super(properties);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (!(livingEntity instanceof Player player)) {
            return;
        }

        boolean infiniteArrows = player.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
        ItemStack projectile = player.getProjectile(stack);

        if (projectile.isEmpty() && !infiniteArrows) {
            return;
        }
        if (projectile.isEmpty()) {
            projectile = new ItemStack(Items.ARROW);
        }

        int charge = getUseDuration(stack) - timeLeft;
        float power = getBowPower(charge);
        if (power < 0.1F) {
            return;
        }

        if (!level.isClientSide) {
            ArrowItem arrowItem = projectile.getItem() instanceof ArrowItem item ? item : (ArrowItem) Items.ARROW;
            for (int i = -1; i <= 1; i++) {
                AbstractArrow arrow = arrowItem.createArrow(level, projectile, player);
                arrow = customArrow(arrow);
                arrow.shootFromRotation(player, player.getXRot(), player.getYRot() + i * 12.0F, 0.0F, power * 3.0F, 1.0F);
                if (power >= 1.0F) {
                    arrow.setCritArrow(true);
                }

                int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
                if (powerLevel > 0) {
                    arrow.setBaseDamage(arrow.getBaseDamage() + powerLevel * 0.5D + 0.5D);
                }

                int punchLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
                if (punchLevel > 0) {
                    arrow.setKnockback(punchLevel);
                }

                if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
                    arrow.setSecondsOnFire(100);
                }

                if (infiniteArrows || player.getAbilities().instabuild && (projectile.is(Items.ARROW) || projectile.is(Items.TIPPED_ARROW))) {
                    arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }

                level.addFreshEntity(arrow);
            }
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 0.9F / (level.getRandom().nextFloat() * 0.4F + 1.0F) + power * 0.25F);

        if (!infiniteArrows && !player.getAbilities().instabuild) {
            projectile.shrink(1);
            if (projectile.isEmpty()) {
                player.getInventory().removeItem(projectile);
            }
        }
    }

    private static float getBowPower(int charge) {
        float progress = charge / 20.0F;
        progress = (progress * progress + progress * 2.0F) / 3.0F;
        return Math.min(progress, 1.0F);
    }
}
