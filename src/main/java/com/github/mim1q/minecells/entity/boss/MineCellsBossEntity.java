package com.github.mim1q.minecells.entity.boss;

import com.github.mim1q.minecells.network.MineCellsNetwork;
import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal.State;
import com.github.mim1q.minecells.registry.MineCellsItems;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;

public abstract class MineCellsBossEntity extends Monster {
    protected final ServerBossEvent bossBar;

    protected MineCellsBossEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.bossBar = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
        this.xpReward = 500;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            this.bossBar.setProgress(this.getHealth() / this.getMaxHealth());
            decrementCooldowns();
        } else {
            processAnimations();
        }
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (level().isClientSide || sound == null) {
            if (sound != null) {
                level().playLocalSound(getX(), getY(), getZ(), sound, SoundSource.HOSTILE, volume, pitch, false);
            }
            return;
        }
        Holder<SoundEvent> holder = Holder.direct(sound);
        for (ServerPlayer player : level().getEntitiesOfClass(ServerPlayer.class, getBoundingBox().inflate(64.0D))) {
            Vec3 diff = this.position().subtract(player.position()).normalize();
            ClientboundSoundPacket packet = new ClientboundSoundPacket(
                holder,
                SoundSource.HOSTILE,
                player.getX() + diff.x,
                player.getY() + diff.y,
                player.getZ() + diff.z,
                volume,
                pitch,
                getRandom().nextLong()
            );
            if (MineCellsNetwork.canReceivePackets(player)) {
                player.connection.send(packet);
            }
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    protected void dropExperience() {
        if (!level().isClientSide) {
            this.bossBar.getPlayers().forEach(player -> {
                ItemEntity item = new ItemEntity(level(), getX(), getY(), getZ(), MineCellsItems.BOSS_STEM_CELL.get().getDefaultInstance());
                item.setTarget(player.getUUID());
                item.setDeltaMovement(new Vec3(
                    (getRandom().nextDouble() - 0.5D) * 0.3D,
                    getRandom().nextDouble() * 0.2D,
                    (getRandom().nextDouble() - 0.5D) * 0.3D
                ));
                level().addFreshEntity(item);
            });
        }
        super.dropExperience();
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    protected void decrementCooldowns() {
    }

    protected void processAnimations() {
    }

    protected void handleStateChange(State state, boolean value, EntityDataAccessor<Boolean> charging, EntityDataAccessor<Boolean> releasing) {
        switch (state) {
            case CHARGE -> this.entityData.set(charging, value);
            case RELEASE -> this.entityData.set(releasing, value);
            default -> {
            }
        }
    }

    protected BiConsumer<State, Boolean> handleStateChange(EntityDataAccessor<Boolean> charging, EntityDataAccessor<Boolean> releasing) {
        return (state, value) -> this.handleStateChange(state, value, charging, releasing);
    }

    public float getDamage(float scale) {
        return (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * scale;
    }

}
