package com.github.mim1q.minecells.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class MineCellsMobEffect extends MobEffect {
    private final boolean appliesEveryTick;
    private final MineCellsEffectFlags flag;
    private final boolean curable;

    public MineCellsMobEffect(MobEffectCategory category, int color, boolean appliesEveryTick, MineCellsEffectFlags flag, boolean curable) {
        super(category, color);
        this.appliesEveryTick = appliesEveryTick;
        this.flag = flag;
        this.curable = curable;
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return this.appliesEveryTick;
    }

    @org.jetbrains.annotations.Nullable
    public MineCellsEffectFlags flag() {
        return this.flag;
    }

    public boolean isIncurable() {
        return !curable;
    }

    @Override
    public java.util.List<net.minecraft.world.item.ItemStack> getCurativeItems() {
        return curable ? super.getCurativeItems() : new java.util.ArrayList<>();
    }
}
