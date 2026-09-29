package com.github.mim1q.minecells.entity.ai.goal.conjunctivius;

import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.entity.nonliving.projectile.ConjunctiviusProjectileEntity;
import com.github.mim1q.minecells.network.MineCellsNetwork;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MathUtils;

import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ConjunctiviusBarrageGoal extends ConjunctiviusMoveAroundGoal {
    protected int ticks = 0;
    private Entity target;
    protected final BarrageSettings settings;

    public ConjunctiviusBarrageGoal(ConjunctiviusEntity entity, Consumer<BarrageSettings> settings) {
        super(entity);
        BarrageSettings settingsObj = new BarrageSettings();
        settings.accept(settingsObj);
        this.settings = settingsObj;
        this.speed = this.settings.speed;
    }

    @Override
    public boolean canUse() {
        this.target = entity.getTarget();
        return super.canUse()
            && this.entity.barrageCooldown <= 0
            && this.target != null
            && this.entity.moving
            && this.entity.canAttack()
            && this.entity.getRandom().nextFloat() < settings.chance;
    }

    @Override
    public boolean canContinueToUse() {
        this.target = entity.getTarget();
        return this.target != null && this.ticks < settings.length + 60 && this.entity.canAttack();
    }

    @Override
    public void start() {
        super.start();
        this.entity.getEntityData().set(ConjunctiviusEntity.BARRAGE_ACTIVE, true);
        this.entity.playSound(MineCellsSounds.CHARGE.get(), 2.0F, 1.0F);
    }

    @Override
    public void tick() {
        if (this.entity.level().isClientSide) {
            return;
        }
        if (this.ticks > 60) {
            super.tick();
            if (this.ticks % 6 == 0 && this.entity.level() instanceof ServerLevel serverLevel) {
                ClientboundSoundPacket packet = new ClientboundSoundPacket(
                    Holder.direct(MineCellsSounds.CONJUNCTIVIUS_SHOT.get()),
                    SoundSource.HOSTILE,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    0.25F,
                    1.0F,
                    serverLevel.getRandom().nextLong()
                );
                for (ServerPlayer player : serverLevel.getEntitiesOfClass(ServerPlayer.class, entity.getBoundingBox().inflate(32.0D))) {
                    if (MineCellsNetwork.canReceivePackets(player)) {
                        player.connection.send(packet);
                    }
                }
            }
            if (this.ticks % settings.interval == 0) {
                this.shoot(this.entity, this.target);
            }
        }
        this.ticks++;
    }

    protected abstract void shoot(ConjunctiviusEntity entity, Entity target);

    @Override
    protected int getNextCooldown() {
        return entity.getRandom().nextInt(settings.maxPause - settings.minPause + 1) + settings.minPause;
    }

    @Override
    public void stop() {
        this.ticks = 0;
        this.entity.barrageCooldown = settings.cooldown;
        this.entity.getEntityData().set(ConjunctiviusEntity.BARRAGE_ACTIVE, false);
        super.stop();
    }

    public static class Targeted extends ConjunctiviusBarrageGoal {
        public Targeted(ConjunctiviusEntity entity, Consumer<BarrageSettings> settings) {
            super(entity, settings);
        }

        @Override
        protected void shoot(ConjunctiviusEntity entity, Entity target) {
            if (target != null) {
                Vec3 targetPos = target.position().add(
                    (entity.getRandom().nextDouble() - 0.5D) * 2.0D,
                    (entity.getRandom().nextDouble() - 0.5D) * 2.0D + 2.0D,
                    (entity.getRandom().nextDouble() - 0.5D) * 2.0D
                );
                ConjunctiviusProjectileEntity.spawn(
                    entity.level(),
                    entity.position().add(0.0D, 2.5D, 0.0D),
                    targetPos,
                    this.entity,
                    this.entity.getDamage(1.0F)
                );
            }
        }
    }

    public static class Around extends ConjunctiviusBarrageGoal {
        public Around(ConjunctiviusEntity entity, Consumer<BarrageSettings> settings) {
            super(entity, settings);
        }

        @Override
        protected void shoot(ConjunctiviusEntity entity, Entity target) {
            if (target != null) {
                for (int i = 0; i < settings.count.get(); i++) {
                    float yaw = MathUtils.radians(entity.getYRot());
                    yaw += (float) (entity.getRandom().nextDouble() - 0.5D) * Mth.PI * 1.5F;
                    float pitch = (float) (entity.getRandom().nextDouble() - 0.8D) * Mth.PI;
                    Vec3 offset = new Vec3(
                        Mth.sin(yaw) * Mth.cos(pitch),
                        Mth.sin(pitch) + 2.5D,
                        Mth.cos(yaw) * Mth.cos(pitch)
                    );
                    Vec3 targetPos = entity.position().add(offset);
                    ConjunctiviusProjectileEntity.spawn(
                        entity.level(),
                        entity.position().add(0.0D, 2.5D, 0.0D),
                        targetPos,
                        this.entity,
                        this.entity.getDamage(1.0F)
                    );
                }
            }
        }
    }

    public static class BarrageSettings {
        public float chance = 0.5F;
        public float speed = 0.05F;
        public int interval = 8;
        public int length = 40;
        public int cooldown = 200;
        public int minPause = 40;
        public int maxPause = 80;
        public Supplier<Integer> count = () -> 1;
    }
}
