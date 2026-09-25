package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.entity.nonliving.projectile.CustomArrowEntity;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SingleUseProjectileItem extends Item implements CustomArrowShooter {
    private final CustomArrowType arrowType;

    public SingleUseProjectileItem(Properties properties, CustomArrowType arrowType) {
        super(properties);
        this.arrowType = arrowType;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        if (!level.isClientSide) {
            shoot(level, user, stack);
            user.getCooldowns().addCooldown(this, arrowType.getCooldown(user, stack));
        }
        stack.shrink(1);
        return InteractionResultHolder.consume(stack);
    }

    protected void shoot(Level level, LivingEntity user, ItemStack stack) {
        level.playSound(null, user.blockPosition(), MineCellsSounds.LEAPING_ZOMBIE_RELEASE.get(), SoundSource.PLAYERS, 0.5F, 1.3F);
        Vec3 velocity = user.getViewVector(1.0F);
        spawnArrow(level, (Player) user, stack, velocity);
    }

    protected void spawnArrow(Level level, Player user, ItemStack stack, Vec3 velocity) {
        CustomArrowEntity arrow = new CustomArrowEntity(level, user, arrowType, user.getEyePosition(), stack);
        arrow.shoot(velocity.x, velocity.y, velocity.z, arrowType.getSpeed(user, stack), arrowType.getSpread(user, stack));
        level.addFreshEntity(arrow);
    }

    @Override
    public CustomArrowType getArrowType() {
        return arrowType;
    }
}
