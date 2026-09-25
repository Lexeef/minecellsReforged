package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class TimedAuraGoal<E extends LivingEntity> extends TimedActionGoal<E> {
    private final double radius;
    private final float damage;
    private final SoundEvent endSound;

    protected TimedAuraGoal(E entity, TimedAuraSettings settings, Predicate<E> predicate) {
        super(entity, settings, predicate);
        this.radius = settings.radius;
        this.damage = settings.damage;
        this.endSound = settings.endSound;
    }

    public TimedAuraGoal(E entity, Consumer<TimedAuraSettings> settingsConsumer, Predicate<E> predicate) {
        this(entity, TimedActionSettings.edit(new TimedAuraSettings(), settingsConsumer), predicate);
    }

    @Override
    protected void release() {
        AABB box = AABB.ofSize(
            this.entity.position().add(0.0D, this.entity.getBbHeight() * 0.5D, 0.0D),
            this.radius * 2.0D,
            this.radius * 2.0D,
            this.radius * 2.0D
        );
        for (Player player : this.entity.level().getNearbyPlayers(TargetingConditions.forCombat(), this.entity, box)) {
            if (player.distanceTo(this.entity) <= this.radius) {
                player.hurt(MineCellsDamageSource.AURA.get(player.level(), this.entity), this.damage);
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        playSound(endSound);
    }

    public static class TimedAuraSettings extends TimedActionSettings {
        public double radius = 10.0D;
        public float damage = 10.0F;
        public SoundEvent endSound = null;
    }
}
