package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.registry.MineCellsStatusEffects;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HealthFlaskItem extends Item {
    private static final String TOOLTIP_KEY = "item.minecells.health_flask.tooltip";

    public HealthFlaskItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        user.heal(4.0F);
        user.removeEffect(MineCellsStatusEffects.BLEEDING.get());
        user.removeEffect(net.minecraft.world.effect.MobEffects.POISON);
        if (!(user instanceof Player player) || !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 40;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public SoundEvent getDrinkingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(TOOLTIP_KEY, 2).withStyle(net.minecraft.ChatFormatting.GRAY));
    }
}
