package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.entity.nonliving.ShockwavePlacer;
import com.github.mim1q.minecells.item.weapon.interfaces.WeaponWithAbility;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.util.ScreenShakeUtils;
import com.github.mim1q.minecells.valuecalculators.ValueCalculator;
import com.github.mim1q.minecells.valuecalculators.ValueCalculators;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FlintItem extends CustomMeleeWeaponItem implements WeaponWithAbility {
    private static final ValueCalculator ABILITY_DAMAGE = ValueCalculators.of("melee/the_flint", "ability_damage", 8.0D);
    private static final ValueCalculator ABILITY_COOLDOWN = ValueCalculators.of("melee/the_flint", "ability_cooldown", 5.0D);

    public FlintItem(Properties properties) {
        super("the_flint", 8.0D, 1.0D, 0.0F, 0.0F, properties);
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingUseDuration) {
        int tick = (3600 * 20) - remainingUseDuration;
        if (level.isClientSide) {
            ParticleUtils.addAura(level, user.position().add(0.0D, 1.0D, 0.0D), ParticleTypes.FLAME, 1, 2.0D, -0.1D);
            if (tick == 20) {
                user.playSound(MineCellsSounds.CRIT.get(), 0.5F, 0.9F);
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int remainingUseDuration) {
        if (level.isClientSide || !(user instanceof Player player)) {
            return;
        }

        int tick = (3600 * 20) - remainingUseDuration;
        player.getCooldowns().addCooldown(this, tick >= 20 ? getAbilityCooldown(stack, user) : 20);
        if (tick < 20) {
            return;
        }

        user.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20, 0, false, false, false));

        List.of(-3.0F, 3.0F).forEach(degOffset -> {
            Vec3 offset = MathUtils.vectorRotateY(new Vec3(1.0D, 0.0D, 0.0D), MathUtils.radians(user.getYRot() + degOffset));
            ShockwavePlacer placer = ShockwavePlacer.createLine(
                level,
                user.position(),
                user.position().add(offset.scale(12.0D)),
                1.5F,
                MineCellsBlocks.SHOCKWAVE_FLAME_PLAYER.get().defaultBlockState(),
                user.getUUID(),
                getAbilityDamage(stack, null, null)
            );
            level.addFreshEntity(placer);
        });

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(user.position(), 3.0D, 2.0D, 3.0D), e -> e != user)) {
            entity.hurt(level.damageSources().playerAttack(player), getAbilityDamage(stack, user, entity) * 2.0F);
            if (entity.distanceToSqr(user) < 4.0D) {
                Vec3 knockback = user.position().subtract(entity.position()).normalize();
                entity.knockback(0.5D, knockback.x, knockback.z);
            }
        }

        InteractionHand flintHand = user.getMainHandItem().is(this) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        user.swing(flintHand, true);
        level.playSound(null, user.getX(), user.getY(), user.getZ(), MineCellsSounds.FLINT_RELEASE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        level.playSound(null, user.getX(), user.getY(), user.getZ(), MineCellsSounds.HIT_FLOOR.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        stack.hurtAndBreak(2, user, p -> p.broadcastBreakEvent(flintHand));

        if (level instanceof ServerLevel serverLevel) {
            ScreenShakeUtils.shakeAround(serverLevel, user.position(), 3.0F, 20, 0.0D, 5.0D, "minecells:weapon_flint");
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        return stack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 3600 * 20;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(), MineCellsSounds.FLINT_CHARGE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
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
