package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.entity.nonliving.projectile.CustomArrowEntity;
import com.github.mim1q.minecells.registry.MineCellsSounds;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class HeavyCrossbowItem extends CustomCrossbowItem {
    public HeavyCrossbowItem(Properties properties) {
        super(properties, CustomArrowType.HEAVY_BOLT, 3);
    }

    @Override
    protected void shoot(Level level, LivingEntity user, ItemStack stack) {
        level.playSound(null, user.blockPosition(), MineCellsSounds.BOW_RELEASE.get(), SoundSource.PLAYERS, 0.7F, 0.9F);
        Vec3 velocity = user.getViewVector(1.0F);

        int loaded = getLoadedProjectiles(stack);
        for (int y = 0; y < loaded * 3; y++) {
            spawnArrow(level, (Player) user, stack, velocity);
        }

        setLoadedProjectiles(stack, 0);
    }

    @Override
    protected CustomArrowEntity spawnArrow(Level level, Player user, ItemStack stack, Vec3 velocity) {
        CustomArrowEntity arrow = super.spawnArrow(level, user, stack, velocity);
        arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
        return arrow;
    }
}
