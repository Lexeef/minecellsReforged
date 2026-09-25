package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.item.weapon.interfaces.WeaponWithAbility;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MineCellsCombatHelper;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.valuecalculators.ValueCalculator;
import com.github.mim1q.minecells.valuecalculators.ValueCalculators;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HattorisKatanaItem extends CustomMeleeWeaponItem implements WeaponWithAbility {
    private static final int CHARGE_TICKS = 20;
    private static final ValueCalculator ABILITY_DAMAGE = ValueCalculators.of("melee/hattoris_katana", "ability_damage",
        context -> 20.0D + EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SHARPNESS, context.stack()) * 2.0D);
    private static final ValueCalculator ABILITY_COOLDOWN = ValueCalculators.of("melee/hattoris_katana", "ability_cooldown", 10.0D);

    public HattorisKatanaItem(Properties properties) {
        super("hattoris_katana", 7.0D, 1.8D, 0.0F, 0.0F, properties);
    }

    @Override
    public ValueCalculator getAbilityDamageCalculator() {
        return ABILITY_DAMAGE;
    }

    @Override
    public ValueCalculator getAbilityCooldownCalculator() {
        return ABILITY_COOLDOWN;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.playSound(MineCellsSounds.KATANA_CHARGE.get(), 1.0F, 1.0F);
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (level.isClientSide()) {
            ParticleUtils.addAura(level, user.position().add(0.0D, 1.0D, 0.0D), ParticleTypes.END_ROD, 1, 3.0D, -0.2D);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int timeLeft) {
        if (user instanceof Player player) {
            player.getCooldowns().addCooldown(this, 20);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        if (!(user instanceof Player player)) {
            return stack;
        }
        player.getCooldowns().addCooldown(this, getAbilityCooldown(stack, user));

        Vec3 start = player.position().add(0.0D, 0.25D, 0.0D);
        Vec3 direction = player.getViewVector(0.0F).multiply(1.0D, 0.0D, 1.0D);
        direction = direction.lengthSqr() < 1.0E-6D ? new Vec3(0.0D, 0.0D, 1.0D) : direction.normalize();

        Vec3 hitPos = getHitPos(player, start, direction, 10.0D).subtract(direction.scale(0.5D));
        damageEntities(level, stack, player, start, hitPos);
        if (level.isClientSide()) {
            spawnTrailParticles(level, start, hitPos);
        } else {
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(user.getUsedItemHand()));
            player.teleportTo(hitPos.x, hitPos.y, hitPos.z);
            MineCellsCombatHelper.setTemporaryInvulnerability(player, 10);
        }
        user.playSound(MineCellsSounds.KATANA_RELEASE.get(), 2.0F, 1.0F);
        return stack;
    }

    private static void spawnTrailParticles(Level level, Vec3 start, Vec3 hitPos) {
        for (Vec3 increment : getIncrements(start, hitPos, 20)) {
            ParticleUtils.addAura(level, increment, ParticleTypes.END_ROD, 2, 3.0D, -0.1D);
            double speed = increment.distanceTo(hitPos) / 200.0D;
            ParticleUtils.addAura(level, increment, ParticleTypes.CAMPFIRE_COSY_SMOKE, 3, 0.5D, speed);
        }
        ParticleUtils.addParticle(level, ParticleTypes.FLASH, start.add(0.0D, 1.0D, 0.0D), Vec3.ZERO);
        ParticleUtils.addParticle(level, ParticleTypes.FLASH, hitPos.add(0.0D, 1.0D, 0.0D), Vec3.ZERO);
    }

    private static Vec3 getHitPos(Player player, Vec3 start, Vec3 direction, double distance) {
        Vec3 end = start.add(direction.scale(distance));
        HitResult lowerHit = player.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        HitResult upperHit = player.level().clip(new ClipContext(start.add(0.0D, 1.0D, 0.0D), end.add(0.0D, 1.0D, 0.0D), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        HitResult selected = lowerHit.getLocation().distanceToSqr(player.position()) < upperHit.getLocation().distanceToSqr(player.position()) ? lowerHit : upperHit;
        return new Vec3(selected.getLocation().x, player.getY(), selected.getLocation().z);
    }

    private static List<Vec3> getIncrements(Vec3 start, Vec3 end, int count) {
        List<Vec3> result = new ArrayList<>(count + 1);
        Vec3 diff = end.subtract(start);
        for (int i = 0; i <= count; i++) {
            result.add(start.add(diff.scale(i / (double) count)));
        }
        return result;
    }

    private void damageEntities(Level level, ItemStack stack, Player player, Vec3 start, Vec3 end) {
        List<LivingEntity> hitEntities = new ArrayList<>();
        for (Vec3 pos : getIncrements(start, end, 10)) {
            for (Entity entity : level.getEntities(player, AABB.ofSize(pos, 1.5D, 1.5D, 1.5D))) {
                if (entity instanceof LivingEntity living && !hitEntities.contains(living)) {
                    living.hurt(MineCellsDamageSource.KATANA.get(level, player, player), getAbilityDamage(stack, player, living));
                    Vec3 knockback = pos.subtract(living.position()).normalize();
                    living.knockback(0.5D, knockback.x, knockback.z);
                    hitEntities.add(living);
                }
            }
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return CHARGE_TICKS;
    }
}
