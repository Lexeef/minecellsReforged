package com.github.mim1q.minecells.entity.damage;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public final class MineCellsDamageSource {
    public static final MineCellsDamageSource ELEVATOR = create("elevator");
    public static final MineCellsDamageSource CURSED = create("cursed");
    public static final MineCellsDamageSource BLEEDING = create("bleeding");
    public static final MineCellsDamageSource KATANA = create("katana");
    public static final MineCellsDamageSource BACKSTAB = create("backstab");
    public static final MineCellsDamageSource AURA = create("aura");
    public static final MineCellsDamageSource GRENADE = create("grenade");
    public static final MineCellsDamageSource HEAVY_BOLT = create("heavy_bolt");
    public static final MineCellsDamageSource ELECTRICITY = create("electricity");

    public final ResourceKey<DamageType> key;

    private MineCellsDamageSource(ResourceKey<DamageType> key) {
        this.key = key;
    }

    public DamageSource get(Level level, Entity directEntity, Entity causingEntity) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key), directEntity, causingEntity);
    }

    public DamageSource get(Level level, Entity entity) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key), entity);
    }

    public DamageSource get(Level level) {
        return get(level, null);
    }

    private static MineCellsDamageSource create(String name) {
        return new MineCellsDamageSource(ResourceKey.create(Registries.DAMAGE_TYPE, MineCells.id(name)));
    }
}
