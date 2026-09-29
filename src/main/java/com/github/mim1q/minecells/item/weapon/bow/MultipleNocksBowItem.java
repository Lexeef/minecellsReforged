package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MathUtils;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MultipleNocksBowItem extends CustomBowItem {
    public MultipleNocksBowItem(Properties properties) {
        super(properties, CustomArrowType.MULTIPLE_NOCKS, 3);
    }

    @Override
    protected void shoot(Level level, LivingEntity user, ItemStack stack) {
        level.playSound(null, user.blockPosition(), MineCellsSounds.BOW_RELEASE.get(), SoundSource.PLAYERS, 0.7F, 0.9F);

        Vec3 velocity = user.getViewVector(1.0F).scale(arrowType.getSpeed(user, stack));
        int angle = 15;
        int loaded = getLoadedProjectiles(stack);

        for (int i = 0; i < loaded; i++) {
            spawnArrow(level, (Player) user, stack, velocity.yRot(MathUtils.radians(angle)));
            angle -= 15;
        }

        setLoadedProjectiles(stack, 0);
    }
}
