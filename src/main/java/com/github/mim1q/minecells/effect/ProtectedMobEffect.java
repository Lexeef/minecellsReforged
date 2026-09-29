package com.github.mim1q.minecells.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.LivingEntity;

public class ProtectedMobEffect extends MineCellsMobEffect {
    public ProtectedMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x99BBFF, false, MineCellsEffectFlags.PROTECTED, false);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        entity.setInvulnerable(true);
        super.addAttributeModifiers(entity, attributes, amplifier);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        entity.setInvulnerable(false);
        super.removeAttributeModifiers(entity, attributes, amplifier);
    }
}
