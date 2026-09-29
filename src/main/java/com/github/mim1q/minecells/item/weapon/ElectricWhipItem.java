package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.item.weapon.interfaces.WeaponWithAbility;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import com.github.mim1q.minecells.valuecalculators.ValueCalculator;
import com.github.mim1q.minecells.valuecalculators.ValueCalculators;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ElectricWhipItem extends Item implements WeaponWithAbility {
    private static final ValueCalculator ABILITY_DAMAGE = ValueCalculators.of("spells/electric_whip", "damage", 7.0D);
    private static final ValueCalculator ABILITY_COOLDOWN = ValueCalculators.of("spells/electric_whip", "cooldown", 0.8D);
    private static final double MAX_DISTANCE = 5.5D;

    public ElectricWhipItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        Vec3 eyePos = player.getEyePosition().subtract(0.0D, 0.5D, 0.0D);
        Vec3 direction = player.getLookAngle();
        Vec3 maxPos = eyePos.add(direction.scale(MAX_DISTANCE));
        HitResult raycast = level.clip(new ClipContext(
            eyePos,
            maxPos,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            player
        ));

        int particleLength = 4;
        if (raycast.getType() == HitResult.Type.BLOCK) {
            maxPos = raycast.getLocation();
            particleLength = Math.max(1, (int) raycast.getLocation().distanceTo(eyePos));
        }

        ((ServerLevel) level).sendParticles(
            MineCellsParticles.ELECTRICITY.get().get(direction, particleLength, 0x95DDFF, 1.0F),
            eyePos.x,
            eyePos.y,
            eyePos.z,
            1,
            0.0D,
            0.0D,
            0.0D,
            0.0D
        );

        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

        double length = maxPos.subtract(eyePos).length();
        for (double delta = 0.0D; delta < length; delta += 0.5D) {
            Vec3 center = eyePos.add(direction.scale(delta));
            AABB box = AABB.ofSize(center, 1.0D, 1.0D, 1.0D);
            for (var entity : level.getEntities(player, box, it -> it instanceof LivingEntity)) {
                LivingEntity living = (LivingEntity) entity;
                living.hurt(
                    MineCellsDamageSource.ELECTRICITY.get(level, player),
                    getAbilityDamage(stack, player, living)
                );
                living.addEffect(new MobEffectInstance(
                    MineCellsStatusEffects.ELECTRIFIED.get(),
                    20 * 3 + 1,
                    2,
                    false,
                    false,
                    true
                ), player);
            }
        }

        level.playSound(null, player.blockPosition(), MineCellsSounds.SHOCK.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        player.getCooldowns().addCooldown(this, getAbilityCooldown(stack, player));
        return InteractionResultHolder.success(stack);
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
