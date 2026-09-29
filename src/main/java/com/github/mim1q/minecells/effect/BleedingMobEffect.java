package com.github.mim1q.minecells.effect;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.particle.colored.ColoredParticleEffect;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class BleedingMobEffect extends MineCellsMobEffect {
    public BleedingMobEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF0000, true, MineCellsEffectFlags.BLEEDING, false);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        entity.hurt(MineCellsDamageSource.BLEEDING.get(entity.level(), null), 0.5F);
        if (entity.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                new ColoredParticleEffect(MineCellsParticles.DROP.get(), 0xFF0000),
                entity.getX(),
                entity.getY() + entity.getBbHeight() / 2.0F,
                entity.getZ(),
                2,
                entity.getBbWidth() / 4.0F,
                entity.getBbHeight() / 4.0F,
                entity.getBbWidth() / 4.0F,
                0.1D
            );
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % getInterval(amplifier) == 0;
    }

    private int getInterval(int amplifier) {
        return Math.max(5, 20 - amplifier * 4);
    }

    public static void apply(LivingEntity entity, int duration) {
        var effect = entity.getEffect(MineCellsStatusEffects.BLEEDING.get());
        int newLevel = 0;
        if (effect != null) {
            newLevel = effect.getAmplifier() + 1;
        }
        if (newLevel >= 6) {
            entity.hurt(MineCellsDamageSource.BLEEDING.get(entity.level(), null), 12.0F);
            entity.removeEffect(MineCellsStatusEffects.BLEEDING.get());
            return;
        }

        entity.addEffect(new MobEffectInstance(MineCellsStatusEffects.BLEEDING.get(), duration, newLevel, false, false, true));
    }
}
