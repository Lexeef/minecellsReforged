package com.github.mim1q.minecells.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class FrozenMobEffect extends MineCellsMobEffect {
    private final boolean slow;

    public FrozenMobEffect(MineCellsEffectFlags flag, int color, boolean slow) {
        super(MobEffectCategory.HARMFUL, color, false, flag, false);
        this.slow = slow;
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        super.addAttributeModifiers(entity, attributes, amplifier);
        if (entity instanceof Mob mob) {
            mob.getNavigation().stop();
            mob.setSpeed(0.0F);
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        super.removeAttributeModifiers(entity, attributes, amplifier);
        if (slow) {
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100 * (amplifier + 1), amplifier, false, false, true));
        }
    }
}
