package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.item.weapon.interfaces.CritIndicator;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import com.github.mim1q.minecells.util.ScreenShakeUtils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

public class LightningBoltItem extends Item implements CritIndicator {
    private static final int MAX_USE_TIME = 60 * 60 * 20;
    private static final double SELECT_DISTANCE = 6.0D;
    private static final double MAX_DISTANCE = 12.0D;
    private static final com.github.mim1q.minecells.valuecalculators.ValueCalculator ABILITY_DAMAGE =
        com.github.mim1q.minecells.valuecalculators.ValueCalculators.of("spells/lightning_bolt", "damage", context -> {
            Double intensity = context.variable("INTENSITY");
            return 1.0D + 2.0D * (intensity == null ? 0.0D : intensity);
        });

    public LightningBoltItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (level.isClientSide || !(user instanceof Player)) {
            return;
        }

        LivingEntity entity = getTargetedEntity(stack, level);
        if (entity == null) {
            user.stopUsingItem();
            return;
        }

        int ticks = getUseDuration(stack) - remainingUseTicks;

        int intensity = 0;
        if (ticks > 20) {
            intensity = 1;
        }
        if (ticks > 40) {
            intensity = 2;
        }
        if (ticks > 60) {
            intensity = 3;
        }

        float damage = (float) ABILITY_DAMAGE.calculate(
            com.github.mim1q.minecells.valuecalculators.ValueCalculatorContext.of(user, stack, entity).with("INTENSITY", intensity)
        );

        if (ticks % 5 == 0 && user instanceof ServerPlayer serverPlayer) {
            ScreenShakeUtils.shakePlayer(serverPlayer, 0.33F * intensity, 20, "minecells:weapon_lightning_bolt");
        }

        if (ticks % 10 == 0) {
            if (ticks > 40) {
                level.playSound(
                    null,
                    user.blockPosition(),
                    MineCellsSounds.CRIT.get(),
                    user.getSoundSource(),
                    0.4F,
                    1.0F
                );
            }
            stack.hurtAndBreak(1, user, e -> e.broadcastBreakEvent(user.getUsedItemHand()));

            entity.hurt(MineCellsDamageSource.ELECTRICITY.get(level, user), damage);

            if (intensity > 0) {
                entity.addEffect(new MobEffectInstance(
                    MineCellsStatusEffects.ELECTRIFIED.get(),
                    20 + 20 * intensity,
                    intensity,
                    false,
                    false,
                    true
                ));
            }

            if (ticks > 60) {
                user.hurt(level.damageSources().magic(), 1.0F);
            }

            level.playSound(
                null,
                user.blockPosition(),
                MineCellsSounds.SHOCKER_RELEASE.get(),
                user.getSoundSource(),
                0.2F,
                1.0F
            );
        }

        if (ticks % 3 == 0) {
            int color = getLightningColor(ticks);
            Vec3 userPos = user.position().add(0.0D, user.getBbHeight() / 2.0D, 0.0D);
            Vec3 direction = entity.position().add(0.0D, entity.getBbHeight() / 2.0D, 0.0D).subtract(userPos);
            ((ServerLevel) level).sendParticles(
                MineCellsParticles.ELECTRICITY.get().get(direction, Math.max(1, (int) direction.length()), color, 1.0F),
                userPos.x,
                userPos.y,
                userPos.z,
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
            );
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int timeCharged) {
        super.releaseUsing(stack, level, user, timeCharged);
        if (level.isClientSide) {
            return;
        }
        setTargetedEntity(stack, null);
        if (user instanceof Player player) {
            player.getCooldowns().addCooldown(this, 20);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (getTargetedEntity(stack, level) == null) {
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (level.isClientSide || !(entity instanceof Player player)) {
            return;
        }

        if (!isSelected || player.getCooldowns().isOnCooldown(this)) {
            setTargetedEntity(stack, null);
            return;
        }

        Vec3 startPos = entity.position().add(0.0D, entity.getBbHeight() / 2.0D, 0.0D);
        Vec3 direction = entity.getLookAngle();
        double length = SELECT_DISTANCE;
        Vec3 endPos = startPos.add(direction.scale(length));

        HitResult blockRaycast = level.clip(new ClipContext(
            startPos,
            endPos,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            entity
        ));
        if (blockRaycast.getType() != HitResult.Type.MISS) {
            length = blockRaycast.getLocation().distanceTo(startPos);
        }

        LivingEntity selectedEntity = null;
        for (double delta = 1.0D; delta <= length; delta += 0.5D) {
            Vec3 pos = startPos.add(direction.scale(delta));
            AABB box = AABB.ofSize(pos, 1.0D, 1.0D, 1.0D);
            var entities = level.getEntitiesOfClass(LivingEntity.class, box, it -> it != entity);
            if (!entities.isEmpty()) {
                selectedEntity = entities.get(0);
                break;
            }
        }

        if (selectedEntity == null) {
            LivingEntity currentTarget = getTargetedEntity(stack, level);
            if (currentTarget == null) {
                return;
            }
            if (!player.isUsingItem() || currentTarget.distanceTo(player) > MAX_DISTANCE) {
                setTargetedEntity(stack, null);
            }
            return;
        }

        setTargetedEntity(stack, selectedEntity);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return MAX_USE_TIME;
    }

    private static void setTargetedEntity(ItemStack stack, @Nullable LivingEntity entity) {
        stack.getOrCreateTag().putInt("targetId", entity == null ? -1 : entity.getId());
    }

    public static LivingEntity getTargetedEntity(ItemStack stack, Level level) {
        int id = stack.getOrCreateTag().getInt("targetId");
        if (id == -1) {
            return null;
        }
        Entity entity = level.getEntity(id);
        return entity instanceof LivingEntity living ? living : null;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    public static int getLightningColor(int ticks) {
        if (ticks > 60) {
            return 0xFF998C;
        }
        if (ticks > 40) {
            return 0xFFC8A7;
        }
        if (ticks > 20) {
            return 0xF5FFBB;
        }
        return 0xBEC7FF;
    }

    @Override
    public boolean shouldShowCritIndicator(@Nullable Player player, @Nullable LivingEntity target, ItemStack stack) {
        return player != null && player.getTicksUsingItem() > 40;
    }
}
