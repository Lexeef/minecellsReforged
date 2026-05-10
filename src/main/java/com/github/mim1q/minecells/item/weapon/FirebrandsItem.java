package com.github.mim1q.minecells.item.weapon;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FirebrandsItem extends Item {
    public FirebrandsItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            SmallFireball fireball = new SmallFireball(level, player, player.getLookAngle().x, player.getLookAngle().y, player.getLookAngle().z);
            fireball.setPos(player.getX(), player.getEyeY() - 0.1D, player.getZ());
            level.addFreshEntity(fireball);
            level.playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 0.6F, 1.2F);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.getCooldowns().addCooldown(this, 12);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
