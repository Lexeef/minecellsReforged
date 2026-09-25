package com.github.mim1q.minecells.entity.nonliving.projectile;

import com.github.mim1q.minecells.entity.GrenadeProjectileEntity;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BigGrenadeEntity extends GrenadeProjectileEntity {
    public BigGrenadeEntity(EntityType<? extends BigGrenadeEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public int getMaxFuse() {
        return 30;
    }

    @Override
    public void explode() {
        super.explode();
        if (level().isClientSide) {
            return;
        }
        for (int i = 0; i < 3; i++) {
            Vec3 velocity = new Vec3(random.nextDouble() - 0.5D, random.nextDouble(), random.nextDouble() - 0.5D).scale(0.7D);
            GrenadeProjectileEntity grenade = new GrenadeProjectileEntity(MineCellsEntities.GRENADE.get(), level());
            grenade.setPos(position());
            grenade.setOwner(getOwner());
            grenade.shoot(velocity);
            level().addFreshEntity(grenade);
        }
    }
}
