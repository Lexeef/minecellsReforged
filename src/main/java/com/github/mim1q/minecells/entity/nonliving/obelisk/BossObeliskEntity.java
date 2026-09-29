package com.github.mim1q.minecells.entity.nonliving.obelisk;

import com.github.mim1q.minecells.entity.boss.MineCellsBossEntity;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;

import net.minecraft.advancements.Advancement;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;

public abstract class BossObeliskEntity extends ObeliskEntity {
    public BossObeliskEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void serverTick() {
        super.serverTick();
        if (tickCount % 20 == 0) {
            for (Player player : level().getEntitiesOfClass(Player.class, getBox())) {
                player.addEffect(new MobEffectInstance(MineCellsStatusEffects.DISARMED.get(), 175, 0, false, false, true));
            }
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        InteractionResult result = super.interact(player, hand);
        if (!level().isClientSide && level().getServer() != null && result == InteractionResult.SUCCESS && player instanceof ServerPlayer serverPlayer) {
            Advancement advancement = level().getServer().getAdvancements().getAdvancement(MineCells.id("respawn_boss"));
            if (advancement != null) {
                serverPlayer.getAdvancements().award(advancement, "obelisk_used");
            }
        }
        return result;
    }

    @Override
    protected boolean isEntityPresent() {
        return !level().getEntitiesOfClass(MineCellsBossEntity.class, getBox(), e -> true).isEmpty();
    }
}
