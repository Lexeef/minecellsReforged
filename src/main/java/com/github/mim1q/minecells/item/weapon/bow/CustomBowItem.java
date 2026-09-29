package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.entity.nonliving.projectile.CustomArrowEntity;
import com.github.mim1q.minecells.item.MineCellsItemTags;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsSounds;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class CustomBowItem extends ProjectileWeaponItem implements CustomArrowShooter {
    private static final int MAX_USE_TIME = 60 * 60 * 20;

    protected final CustomArrowType arrowType;
    protected final int maxProjectileCount;

    protected CustomBowItem(Properties properties, CustomArrowType arrowType, int maxProjectileCount) {
        super(properties);
        this.arrowType = arrowType;
        this.maxProjectileCount = maxProjectileCount;
    }

    public CustomBowItem(Properties properties, CustomArrowType arrowType) {
        this(properties, arrowType, 1);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.world.item.enchantment.Enchantment enchantment) {
        return enchantment == Enchantments.INFINITY_ARROWS && stack.is(MineCellsItemTags.BOWS_ACCEPTING_INFINITY)
            || enchantment == Enchantments.PUNCH_ARROWS && stack.is(MineCellsItemTags.BOWS_ACCEPTING_PUNCH)
            || enchantment == Enchantments.POWER_ARROWS && stack.is(MineCellsItemTags.BOWS_ACCEPTING_POWER)
            || enchantment == Enchantments.FLAMING_ARROWS && stack.is(MineCellsItemTags.BOWS_ACCEPTING_FLAME)
            || enchantment == Enchantments.QUICK_CHARGE && stack.is(MineCellsItemTags.BOWS_ACCEPTING_QUICK_CHARGE)
            || super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int remainingUseTicks) {
        if (level.isClientSide) {
            return;
        }

        int ticks = getUseDuration(stack) - remainingUseTicks;
        if (ticks < getDrawTime(user, stack) || !(user instanceof Player)) {
            return;
        }

        int loaded = loadMaxProjectiles(level, (Player) user, stack, user.getProjectile(stack), maxProjectileCount);
        setLoadedProjectiles(stack, loaded);
        shoot(level, user, stack);
        stack.hurtAndBreak(1, user, player -> player.broadcastBreakEvent(user.getUsedItemHand()));
    }

    protected void shoot(Level level, LivingEntity user, ItemStack stack) {
        level.playSound(null, user.blockPosition(), MineCellsSounds.BOW_RELEASE.get(), SoundSource.PLAYERS, 0.5F, 0.9F);

        Vec3 velocity = user.getViewVector(1.0F);
        spawnArrow(level, (Player) user, stack, velocity);
        setLoadedProjectiles(stack, 0);
    }

    protected CustomArrowEntity spawnArrow(Level level, Player user, ItemStack stack, Vec3 velocity) {
        CustomArrowEntity arrow = new CustomArrowEntity(level, user, arrowType, user.getEyePosition(), stack);
        arrow.shoot(velocity.x, velocity.y, velocity.z, arrowType.getSpeed(user, stack), arrowType.getSpread(user, stack));
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
            arrow.setSecondsOnFire(1000);
        }
        arrow.setKnockback(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack));
        level.addFreshEntity(arrow);
        return arrow;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        ItemStack projectileStack = user.getProjectile(stack);
        boolean hasInfinity = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
        boolean projectileNeeded = arrowType.getAmmoItem().isPresent();
        boolean hasProjectile = !projectileStack.isEmpty();

        if (hasProjectile || hasInfinity || !projectileNeeded) {
            level.playSound(null, user.blockPosition(), MineCellsSounds.BOW_CHARGE.get(), SoundSource.PLAYERS, 0.5F, 0.8F);
            user.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }

        return InteractionResultHolder.fail(stack);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return item -> arrowType.getAmmoItem()
            .map(arrow -> item.getItem() == arrow)
            .orElse(true);
    }

    protected final int loadMaxProjectiles(Level level, Player user, ItemStack bow, ItemStack arrow, int maxCount) {
        if (
            user.getAbilities().instabuild
                || level.isClientSide
                || arrow.isEmpty()
                || arrowType.getAmmoItem().isEmpty()
                || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bow) > 0
        ) {
            return maxCount;
        }

        for (int i = 0; i < maxCount; i++) {
            if (arrow.isEmpty()) {
                return i;
            }
            arrow.shrink(1);
        }
        return maxCount;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 100;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return MAX_USE_TIME;
    }

    public int getDrawTime(LivingEntity user, ItemStack stack) {
        return arrowType.getDrawTime(user, stack);
    }

    public float getFovMultiplier(Player player, ItemStack stack) {
        float multiplier = player.getTicksUsingItem() / (float) getDrawTime(player, stack);
        if (multiplier > 1.0F) {
            multiplier = 1.0F;
        } else {
            multiplier *= multiplier;
        }
        return 1.0F - multiplier * 0.15F;
    }

    public static int getLoadedProjectiles(ItemStack bow) {
        CustomBowItem bowItem = (CustomBowItem) bow.getItem();
        return bowItem.maxProjectileCount == 1 ? 1 : bow.getOrCreateTag().getInt("LoadedProjectiles");
    }

    public static void setLoadedProjectiles(ItemStack bow, int count) {
        CustomBowItem bowItem = (CustomBowItem) bow.getItem();
        if (bowItem.maxProjectileCount == 1) {
            return;
        }
        bow.getOrCreateTag().putInt("LoadedProjectiles", count);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return repairCandidate.is(MineCellsItems.CELL_INFUSED_STEEL.get()) || super.isValidRepairItem(stack, repairCandidate);
    }

    @Override
    public CustomArrowType getArrowType() {
        return arrowType;
    }
}
