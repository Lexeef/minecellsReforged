package com.github.mim1q.minecells.entity.ai.goal;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.lang.Math.max;
import static org.joml.Math.clamp;

public class TimedDashGoal<E extends Monster> extends TimedActionGoal<E> {
    protected final TimedDashSettings settings;

    protected Entity target;
    protected Vec3 direction;
    protected double targetDistance;
    protected double distanceTravelled;
    protected Vec3 targetPos;
    private final List<Integer> attackedIds = new ArrayList<>();
    private boolean hasLanded = false;

    public TimedDashGoal(E entity, TimedDashSettings settings, Predicate<E> predicate) {
        super(entity, settings, predicate);
        this.settings = settings;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public TimedDashGoal(E entity, Consumer<TimedDashSettings> settingsConsumer, Predicate<E> predicate) {
        this(entity, TimedActionSettings.edit(new TimedDashSettings(), settingsConsumer), predicate);
    }

    @Override
    public boolean canUse() {
        target = entity.getTarget();
        return target != null && super.canUse() && (!settings.onGround || entity.getY() >= target.getY());
    }

    @Override
    public void start() {
        super.start();
        distanceTravelled = 0;
        targetDistance = 0;
        targetPos = target.position();
        attackedIds.clear();
        hasLanded = false;
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && target != null;
    }

    @Override
    protected void charge() {
        if (ticks() <= settings.alignTick) {
            targetPos = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
            Vec3 diff = targetPos.subtract(entity.position()).multiply(1.0D, settings.onGround ? 0.0D : 1.0D, 1.0D);
            direction = diff.normalize();
            targetDistance = clamp(settings.minDistance, settings.maxDistance, diff.length() + settings.overshoot);
            if (settings.rotate) {
                lookAtTarget();
            }
        }
        if (settings.particle != null) {
            spawnParticles();
        }
    }

    protected void lookAtTarget() {
        this.entity.getMoveControl().setWantedPosition(this.target.getX(), this.target.getY(), this.target.getZ(), 0.01F);
        this.entity.move(MoverType.SELF, this.direction.scale(0.01F));
        this.entity.getLookControl().setLookAt(this.target, 360.0F, 360.0F);
        this.entity.getNavigation().stop();
    }

    protected void spawnParticles() {
        super.charge();
        Vec3 entityPos = entity.position().add(0.0D, entity.getBbHeight() * 0.75F, 0.0D);
        Vec3 diff = targetPos.subtract(entityPos);
        Vec3 norm = diff.normalize();
        for (float i = 0; i < diff.length(); i += 0.1F) {
            Vec3 particlePos = entityPos.add(norm.scale(i));
            if (entity.level() instanceof ServerLevel serverLevel && entity.getRandom().nextFloat() < 0.1F) {
                serverLevel.sendParticles(settings.particle, particlePos.x, particlePos.y, particlePos.z, 1, 0.1D, 0.1D, 0.1D, 0.01D);
            }
        }
    }

    @Override
    protected void runAction() {
        if (settings.onGround) {
            double velY = max(settings.jumpHeight, targetDistance * settings.jumpHeight * 0.01);
            entity.push(0.0D, velY, 0.0D);
        }
    }

    @Override
    protected void release() {
        if (shouldSlowDown()) {
            if (settings.landSound != null && !hasLanded) {
                playSound(settings.landSound);
                settings.onLand.run();
                hasLanded = true;
            }
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.8D, 1.0D, 0.8D));
            return;
        }

        entity.setDeltaMovement(
            entity.getDeltaMovement()
                .multiply(0.0D, settings.onGround ? 1.0D : 0.0D, 0.0D)
                .add(direction.scale(settings.speed))
        );
        distanceTravelled += settings.speed;
        List<Entity> entitiesInRange = entity.level().getEntities(entity, entity.getBoundingBox().inflate(settings.margin));
        for (Entity e : entitiesInRange) {
            if (e instanceof LivingEntity && !(e instanceof Monster) && !attackedIds.contains(e.getId())) {
                e.hurt(entity.damageSources().mobAttack(entity), settings.damage);
                attackedIds.add(e.getId());
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (!hasLanded) {
            settings.onLand.run();
            playSound(settings.landSound);
        }
    }

    protected boolean shouldSlowDown() {
        return distanceTravelled > targetDistance;
    }

    public static class TimedDashSettings extends TimedActionSettings {
        public float speed = 1.0F;
        public float damage = 10.0F;
        public boolean rotate = true;
        public double margin = 0.0D;
        public boolean onGround = false;
        public double jumpHeight = 0.0D;
        public int alignTick = 0;
        public double minDistance = 0.0D;
        public double overshoot = 0.0D;
        public double maxDistance = 128.0D;
        public ParticleOptions particle = null;
        public SoundEvent landSound = null;
        public Runnable onLand = () -> {
        };
    }
}
