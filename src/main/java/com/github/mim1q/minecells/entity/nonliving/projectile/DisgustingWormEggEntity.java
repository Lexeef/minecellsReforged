package com.github.mim1q.minecells.entity.nonliving.projectile;

import com.github.mim1q.minecells.entity.GrenadeProjectileEntity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** Worm-death grenade with Fabric damage/radius (6 / 4). */
public class DisgustingWormEggEntity extends GrenadeProjectileEntity {
    public DisgustingWormEggEntity(EntityType<? extends DisgustingWormEggEntity> type, Level level) {
        super(type, level);
        this.damage = 6.0F;
        this.radius = 4.0F;
    }
}
