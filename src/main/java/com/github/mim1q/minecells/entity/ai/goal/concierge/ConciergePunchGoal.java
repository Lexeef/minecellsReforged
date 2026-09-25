package com.github.mim1q.minecells.entity.ai.goal.concierge;

import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.boss.ConciergeEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.function.Consumer;

public class ConciergePunchGoal extends TimedActionGoal<ConciergeEntity> {
    private final float damage;
    private final double knockback;

    protected ConciergePunchGoal(ConciergeEntity entity, ConciergePunchSettings settings) {
        super(entity, settings, null);
        this.damage = settings.damage;
        this.knockback = settings.knockback;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public ConciergePunchGoal(ConciergeEntity entity, Consumer<ConciergePunchSettings> settingsConsumer) {
        this(entity, TimedActionSettings.edit(new ConciergePunchSettings(), settingsConsumer));
    }

    @Override
    public boolean canUse() {
        return super.canUse()
            && entity.getTarget() != null
            && entity.getTarget().distanceTo(entity) <= 3.0D
            && entity.canAttack();
    }

    @Override
    protected void runAction() {
        var punchPos = entity.position().add(entity.getLookAngle().multiply(1.0D, 0.0D, 1.0D).normalize());
        var punchBox = AABB.ofSize(punchPos, 5.0D, 3.0D, 5.0D);
        for (var target : entity.level().getEntities(entity, punchBox)) {
            if (target instanceof LivingEntity livingTarget) {
                target.hurt(entity.damageSources().mobAttack(entity), damage);
                livingTarget.knockback(knockback, entity.getX() - target.getX(), entity.getZ() - target.getZ());
            }
        }
    }

    public static class ConciergePunchSettings extends TimedActionSettings {
        public float damage = 10.0F;
        public double knockback = 1.0D;
    }
}
