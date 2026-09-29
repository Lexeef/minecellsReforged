package com.github.mim1q.minecells.item.weapon.shield;

import com.github.mim1q.minecells.config.MineCellsSyncedConfig;
import com.github.mim1q.minecells.item.weapon.MineCellsWeaponTier;
import com.github.mim1q.minecells.registry.MineCellsItems;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class CustomShieldItem extends Item {
    private static final String PARRIED_TAG = "parried";
    private static final int MAX_USE_DURATION = 60 * 60 * 20;

    private final CustomShieldType shieldType;

    public CustomShieldItem(Properties properties, CustomShieldType shieldType) {
        super(properties);
        this.shieldType = shieldType;
    }

    public CustomShieldType getShieldType() {
        return shieldType;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        shieldType.onUse(level, player, hand, stack);
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (livingEntity instanceof Player player) {
            int useTicks = getUseDuration(stack) - remainingUseDuration;
            shieldType.onHold(level, player, useTicks);
        }
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (livingEntity instanceof Player player) {
            player.getCooldowns().addCooldown(this, shieldType.getCooldown(player, stack, hasParried(stack)));
        }
        setParried(stack, false);
        super.releaseUsing(stack, level, livingEntity, timeCharged);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return MAX_USE_DURATION;
    }

    @Override
    public int getEnchantmentValue() {
        return MineCellsWeaponTier.CELL_INFUSED_STEEL.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return repairCandidate.is(MineCellsItems.CELL_INFUSED_STEEL.get()) || super.isValidRepairItem(stack, repairCandidate);
    }

    public static boolean hasParried(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(PARRIED_TAG);
    }

    public static void setParried(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(PARRIED_TAG, value);
    }

    public static float getAngleDifference(Player player, net.minecraft.world.damagesource.DamageSource source) {
        Vec3 sourcePosition = source.getSourcePosition();
        if (sourcePosition == null) {
            return Float.MAX_VALUE;
        }

        Vec3 damageDirection = sourcePosition.subtract(player.position());
        Vec3 lookDirection = player.getLookAngle();
        double directionLength = damageDirection.length();
        double lookLength = lookDirection.length();
        if (directionLength < 1.0E-6D || lookLength < 1.0E-6D) {
            return Float.MAX_VALUE;
        }

        double dot = damageDirection.dot(lookDirection) / (directionLength * lookLength);
        return (float) Math.toDegrees(Math.acos(Mth.clamp(dot, -1.0D, 1.0D)));
    }

    public int getParryTime() {
        return shieldType.getParryTime() + MineCellsSyncedConfig.additionalParryTime();
    }
}
