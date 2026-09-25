package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import com.github.mim1q.minecells.client.renderer.item.CustomCrossbowClientExtensions;

import java.util.function.Consumer;

public class CustomCrossbowItem extends CustomBowItem {
    public CustomCrossbowItem(Properties properties, CustomArrowType arrowType) {
        super(properties, arrowType);
    }

    protected CustomCrossbowItem(Properties properties, CustomArrowType arrowType, int maxProjectileCount) {
        super(properties, arrowType, maxProjectileCount);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int remainingUseTicks) {
        if (level.isClientSide) {
            return;
        }

        int ticks = getUseDuration(stack) - remainingUseTicks;
        if (!CrossbowItem.isCharged(stack) && ticks > getDrawTime(user, stack) && user instanceof Player player) {
            CrossbowItem.setCharged(stack, true);
            int loaded = loadMaxProjectiles(level, player, stack, user.getProjectile(stack), maxProjectileCount);
            CustomBowItem.setLoadedProjectiles(stack, loaded);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);

        if (CrossbowItem.isCharged(stack)) {
            shoot(level, user, stack);
            CrossbowItem.setCharged(stack, false);
            stack.hurtAndBreak(1, user, player -> player.broadcastBreakEvent(hand));
            return InteractionResultHolder.consume(stack);
        }

        boolean hasAmmo = !user.getProjectile(stack).isEmpty() || user.getAbilities().instabuild;
        if (!hasAmmo) {
            return InteractionResultHolder.fail(stack);
        }

        level.playSound(null, user.blockPosition(), MineCellsSounds.BOW_CHARGE.get(), SoundSource.PLAYERS, 1.0F, 0.8F);
        return ItemUtils.startUsingInstantly(level, user, hand);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CROSSBOW;
    }

    @Override
    public float getFovMultiplier(Player player, ItemStack stack) {
        return 1.0F;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(CustomCrossbowClientExtensions.INSTANCE);
    }
}
