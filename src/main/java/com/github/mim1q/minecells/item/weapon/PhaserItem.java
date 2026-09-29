package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.item.weapon.interfaces.WeaponWithAbility;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import com.github.mim1q.minecells.util.MineCellsCombatHelper;
import com.github.mim1q.minecells.valuecalculators.ValueCalculator;
import com.github.mim1q.minecells.valuecalculators.ValueCalculators;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

public class PhaserItem extends Item implements WeaponWithAbility {
    private static final ValueCalculator ABILITY_DAMAGE = ValueCalculators.of("spells/phaser", "damage", 4.0D);
    private static final ValueCalculator ABILITY_COOLDOWN = ValueCalculators.of("spells/phaser", "cooldown", 1.5D);

    public PhaserItem(Properties properties) {
        super(properties);
    }

    private static boolean teleportBehindTarget(Level level, Player player, @Nullable LivingEntity target) {
        if (target == null) {
            return false;
        }
        BlockHitResult raycast = level.clip(new ClipContext(player.getEyePosition(), target.getEyePosition(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (raycast.isInside()) {
            return false;
        }
        Vec3 targetPos = target.position().add(target.getViewVector(0.0F).multiply(-1.0D, 0.0D, -1.0D).scale(0.5D + target.getBbWidth()));
        BlockPos targetBlock = BlockPos.containing(targetPos);
        if (level.getBlockState(targetBlock).canOcclude() || level.getBlockState(targetBlock.above()).canOcclude()) {
            return false;
        }
        if (level.isClientSide()) {
            return true;
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), MineCellsSounds.TELEPORT_RELEASE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        target.addEffect(new MobEffectInstance(MineCellsStatusEffects.STUNNED.get(), 30, 0, false, false, true));
        MineCellsCombatHelper.setTemporaryInvulnerability(player, 10);
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.teleportTo((ServerLevel) level, targetPos.x, targetPos.y, targetPos.z, target.getYHeadRot(), player.getXRot());
        } else {
            player.teleportTo(targetPos.x, targetPos.y, targetPos.z);
        }
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Vec3 searchPos = player.position().add(player.getLookAngle().scale(4.0D));
        LivingEntity closestEntity = level.getNearestEntity(
            LivingEntity.class,
            TargetingConditions.forCombat(),
            player,
            searchPos.x,
            searchPos.y,
            searchPos.z,
            AABB.ofSize(searchPos, 8.0D, 8.0D, 8.0D)
        );

        boolean canTeleport = teleportBehindTarget(level, player, closestEntity);
        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }

        if (canTeleport) {
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            player.addEffect(new MobEffectInstance(MineCellsStatusEffects.ASSASSINS_STRENGTH.get(), 20 * 5));
            player.getCooldowns().addCooldown(this, getAbilityCooldown(stack, player));
            closestEntity.hurt(player.damageSources().mobAttack(player), getAbilityDamage(stack, player, closestEntity));
            return InteractionResultHolder.success(stack);
        }
        player.getCooldowns().addCooldown(this, 20);
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public ValueCalculator getAbilityDamageCalculator() {
        return ABILITY_DAMAGE;
    }

    @Override
    public ValueCalculator getAbilityCooldownCalculator() {
        return ABILITY_COOLDOWN;
    }
}
