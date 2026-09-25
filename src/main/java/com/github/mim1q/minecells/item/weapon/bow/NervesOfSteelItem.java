package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NervesOfSteelItem extends CustomBowItem {
    public NervesOfSteelItem(Properties properties) {
        super(properties, CustomArrowType.NERVES_OF_STEEL);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int remainingUseTicks) {
        if (level.isClientSide) {
            return;
        }

        int ticks = getUseDuration(stack) - remainingUseTicks;
        if (ticks >= 30 && ticks <= 40) {
            stack.getOrCreateTag().putBoolean("crit", true);
        }

        super.releaseUsing(stack, level, user, remainingUseTicks);
        stack.getOrCreateTag().remove("crit");
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.onUseTick(level, user, stack, remainingUseTicks);

        if (level.isClientSide) {
            return;
        }
        int ticks = getUseDuration(stack) - remainingUseTicks;
        if (ticks == 29) {
            level.playSound(null, user.blockPosition(), MineCellsSounds.CRIT.get(), SoundSource.PLAYERS, 0.5F, 1.2F);
        }
    }
}
