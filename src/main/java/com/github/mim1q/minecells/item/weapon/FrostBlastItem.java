package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.item.weapon.interfaces.WeaponWithAbility;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import com.github.mim1q.minecells.valuecalculators.ValueCalculator;
import com.github.mim1q.minecells.valuecalculators.ValueCalculators;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public class FrostBlastItem extends Item implements WeaponWithAbility {
    private static final ValueCalculator ABILITY_DAMAGE = ValueCalculators.of("spells/frost_blast", "damage", 2.0D);
    private static final ValueCalculator ABILITY_COOLDOWN = ValueCalculators.of("spells/frost_blast", "cooldown", 6.0D);

    public FrostBlastItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        stack.hurtAndBreak(1, user, e -> e.broadcastBreakEvent(user.getUsedItemHand()));
        user.playSound(MineCellsSounds.FROST_BLAST.get(), 1.0F, 1.1F);
        if (level.isClientSide()) {
            for (int i = 0; i < 20; i++) {
                Vec3 pos = user.position().add(0.0D, 1.25D, 0.0D);
                Vec3 vel = Vec3.directionFromRotation(
                    user.getXRot() + (user.getRandom().nextFloat() - 0.5F) * 45.0F,
                    user.getYRot() + (user.getRandom().nextFloat() - 0.5F) * 45.0F
                ).scale(0.25D + user.getRandom().nextDouble() * 0.25D).add(0.0D, 0.1D, 0.0D);
                level.addParticle(ParticleTypes.SNOWFLAKE, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
            }
            return stack;
        }
        if (user instanceof Player player) {
            player.getCooldowns().addCooldown(this, getAbilityCooldown(stack, user));
        }
        Set<LivingEntity> entities = new HashSet<>();
        Vec3 look = user.getLookAngle();
        for (int i = 1; i <= 3; ++i) {
            Vec3 searchPos = user.position().add(look.scale(i * 1.5D));
            AABB searchBox = AABB.ofSize(searchPos, 1.0D + 0.75D * i, 1.5D, 1.0D + 0.75D * i);
            entities.addAll(level.getEntitiesOfClass(LivingEntity.class, searchBox, e -> e != user));
        }
        for (LivingEntity entity : entities) {
            applyFreeze(entity);
            entity.hurt(level.damageSources().freeze(), getAbilityDamage(stack, user, entity));
        }
        return stack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 20;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    public static void applyFreeze(LivingEntity entity) {
        int duration = 20 * 5;
        Level level = entity.level();
        if (level.getBlockState(entity.blockPosition()).getFluidState().is(FluidTags.WATER)) {
            level.setBlockAndUpdate(entity.blockPosition(), Blocks.ICE.defaultBlockState());
            duration = 20 * 10;
        }
        entity.playSound(MineCellsSounds.FREEZE.get(), 1.0F, 1.0F);
        entity.addEffect(new MobEffectInstance(MineCellsStatusEffects.FROZEN.get(), duration, 0, false, false, true));
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
