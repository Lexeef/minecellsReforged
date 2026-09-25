package com.github.mim1q.minecells.entity.nonliving.projectile;

import com.github.mim1q.minecells.item.weapon.bow.CustomArrowType;
import com.github.mim1q.minecells.item.weapon.bow.CustomArrowType.ArrowBlockHitContext;
import com.github.mim1q.minecells.item.weapon.bow.CustomArrowType.ArrowEntityHitContext;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class CustomArrowEntity extends AbstractArrow {
    public static final EntityDataAccessor<String> ARROW_TYPE = SynchedEntityData.defineId(CustomArrowEntity.class, EntityDataSerializers.STRING);

    private CustomArrowType arrowType = CustomArrowType.DEFAULT;
    private Vec3 shotFromPos = Vec3.ZERO;
    private ItemStack bow = ItemStack.EMPTY;
    private ItemStack item = ItemStack.EMPTY;

    public CustomArrowEntity(EntityType<? extends CustomArrowEntity> entityType, Level level) {
        super(entityType, level);
    }

    public CustomArrowEntity(Level level, Player owner, CustomArrowType arrowType, Vec3 shotFromPos, ItemStack bow) {
        super(MineCellsEntities.CUSTOM_ARROW.get(), level);
        this.arrowType = arrowType;
        this.entityData.set(ARROW_TYPE, arrowType.getName());
        this.setSilent(true);

        setOwner(owner);
        setPos(owner.getEyePosition().subtract(0.0D, 0.2D, 0.0D));
        this.shotFromPos = shotFromPos;
        this.bow = bow.copy();
        this.item = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bow) > 0
            ? ItemStack.EMPTY
            : arrowType.getAmmoItem().map(ItemStack::new).orElse(ItemStack.EMPTY);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ARROW_TYPE, CustomArrowType.DEFAULT.getName());
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && !this.inGround) {
            ParticleOptions particle = this.getArrowType().getParticle();
            if (particle != null) {
                Vec3 reverseVelocity = getDeltaMovement().scale(-0.1D);
                level().addParticle(
                    particle,
                    getX(), getY(), getZ(),
                    reverseVelocity.x, reverseVelocity.y, reverseVelocity.z
                );
            }
        }

        if (level() instanceof ServerLevel serverLevel && tickCount > arrowType.getMaxAge()) {
            this.discard();
            ParticleOptions particle = this.getArrowType().getParticle();
            if (particle == null) {
                particle = ParticleTypes.CRIT;
            }
            serverLevel.sendParticles(
                particle,
                getX(), getY(), getZ(),
                3, 0.05D, 0.05D, 0.05D, 0.05D
            );
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (level().isClientSide || this.getOwner() == null) {
            return;
        }

        if (result.getEntity() instanceof LivingEntity target && getOwner() instanceof LivingEntity holder) {
            ItemStack bowStack = bow == null ? ItemStack.EMPTY : bow;
            Player shooter = getOwner() instanceof Player player ? player : null;
            if (shooter == null) {
                return;
            }

            ArrowEntityHitContext entityHitContext = new ArrowEntityHitContext(
                (ServerLevel) level(),
                bowStack,
                shooter,
                target,
                shotFromPos,
                result.getLocation(),
                this
            );

            float damage = arrowType.getDamage(holder, bowStack, target);
            float critDamage = 0.0F;

            if (arrowType.shouldCrit(entityHitContext)) {
                level().playSound(null, getOwner().blockPosition(), MineCellsSounds.CRIT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                critDamage = arrowType.getAdditionalCritDamage(holder, bowStack, target);
            }

            float globalExtraDamage = arrowType.getGlobalExtraDamage(holder, bowStack, target, damage, critDamage);
            damage += critDamage + globalExtraDamage;

            target.hurt(arrowType.getDamageSource(level(), this, holder), damage);
            if (this.getKnockback() > 0) {
                double resistance = Math.max(0.0D, 1.0D - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                Vec3 knockback = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale(this.getKnockback() * 0.6D * resistance);
                if (knockback.lengthSqr() > 0.0D) {
                    target.push(knockback.x, 0.1D, knockback.z);
                }
            }

            if (isOnFire()) {
                target.setSecondsOnFire(5);
            }

            this.getArrowType().onEntityHit(entityHitContext);
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        if (level().isClientSide || !(getOwner() instanceof Player shooter)) {
            return;
        }

        ArrowBlockHitContext blockHitContext = new ArrowBlockHitContext(
            (ServerLevel) level(),
            bow == null ? ItemStack.EMPTY : bow,
            shooter,
            shotFromPos,
            result.getBlockPos(),
            result.getLocation(),
            result.getDirection(),
            this
        );

        this.getArrowType().onBlockHit(blockHitContext);
    }

    public CustomArrowType getArrowType() {
        return arrowType;
    }

    @Override
    protected ItemStack getPickupItem() {
        return item.copy();
    }

    @Override
    protected Component getTypeName() {
        return arrowType.getTranslation();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (ARROW_TYPE.equals(key)) {
            this.arrowType = CustomArrowType.get(this.entityData.get(ARROW_TYPE));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("arrowType", arrowType.getName());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(ARROW_TYPE, tag.getString("arrowType"));
        this.arrowType = CustomArrowType.get(tag.getString("arrowType"));
    }
}
