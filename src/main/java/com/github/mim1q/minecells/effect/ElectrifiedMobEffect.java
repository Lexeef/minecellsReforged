package com.github.mim1q.minecells.effect;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.particle.electric.ElectricParticleEffect;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ElectrifiedMobEffect extends MobEffect {
    public ElectrifiedMobEffect() {
        super(MobEffectCategory.HARMFUL, 0xCCF4FF);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.isDeadOrDying() || entity.level().isClientSide) {
            return;
        }

        var damage = 1.0F;
        var interval = 15;
        if (entity.isInWaterOrBubble()) {
            damage *= 1.25F;
            interval = 10;
        }

        if (entity.tickCount % interval != 0) {
            return;
        }

        entity.hurt(MineCellsDamageSource.ELECTRICITY.get(entity.level()), damage);
        entity.level().playSound(null, entity.blockPosition(), MineCellsSounds.SHOCK.get(), SoundSource.NEUTRAL, 0.5F, 0.8F + entity.getRandom().nextFloat() * 0.4F);

        var particleSize = 0.3F + entity.getBbWidth() * 0.15F;
        if (entity.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 5; ++i) {
                var randomDirection = new Vec3(
                    (entity.getRandom().nextDouble() - 0.5D) * 2.0D,
                    (entity.getRandom().nextDouble() - 0.5D) * 2.0D,
                    (entity.getRandom().nextDouble() - 0.5D) * 2.0D
                );
                serverLevel.sendParticles(
                    new ElectricParticleEffect(randomDirection, 2, 0xFFFFFF, particleSize, true),
                    entity.getX(),
                    entity.getY() + entity.getBbHeight() / 2.0D,
                    entity.getZ(),
                    1,
                    entity.getBbWidth() / 4.0D,
                    entity.getBbHeight() / 4.0D,
                    entity.getBbWidth() / 4.0D,
                    0.0D
                );
            }
        }
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, net.minecraft.world.entity.ai.attributes.AttributeMap attributes, int amplifier) {
        super.addAttributeModifiers(entity, attributes, amplifier);
        if ((amplifier != 1 || !entity.isInWaterOrBubble()) && amplifier < 2) {
            return;
        }

        var effect = new MobEffectInstance(MineCellsStatusEffects.ELECTRIFIED.get(), 20 * 3 + 1, amplifier - 1, false, false, true);
        for (var other : entity.level().getEntities(entity, entity.getBoundingBox().inflate(5.0D), e -> e instanceof LivingEntity && !(e instanceof Player))) {
            if (!(other instanceof LivingEntity livingEntity) || other.distanceTo(entity) > 3.0D || !(other.level() instanceof ServerLevel serverLevel)) {
                continue;
            }

            livingEntity.addEffect(new MobEffectInstance(effect));

            var particlePos = entity.position().add(0.0D, entity.getBbHeight() / 2.0D, 0.0D);
            var targetPos = other.position().add(0.0D, other.getBbHeight() / 2.0D, 0.0D);
            var direction = targetPos.subtract(particlePos);
            var length = (int) (particlePos.distanceTo(targetPos) * 2.0D);

            serverLevel.sendParticles(
                new ElectricParticleEffect(direction, length, 0xBBEEFF, 0.5F, true),
                particlePos.x(),
                particlePos.y(),
                particlePos.z(),
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
            );
        }
    }
}
