package com.github.mim1q.minecells.valuecalculators;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class ValueCalculatorContext {
    @Nullable
    private LivingEntity holder;
    @Nullable
    private LivingEntity target;
    private ItemStack holderStack = ItemStack.EMPTY;
    private final Map<String, Double> variables = new HashMap<>();

    private ValueCalculatorContext() {
    }

    public static ValueCalculatorContext create() {
        return new ValueCalculatorContext();
    }

    public static ValueCalculatorContext of(@Nullable LivingEntity holder, @Nullable ItemStack stack) {
        return create().holder(holder).stack(stack);
    }

    public static ValueCalculatorContext of(@Nullable LivingEntity holder, @Nullable ItemStack stack, @Nullable LivingEntity target) {
        return create().holder(holder).stack(stack).target(target);
    }

    public ValueCalculatorContext holder(@Nullable LivingEntity holder) {
        this.holder = holder;
        return this;
    }

    public ValueCalculatorContext target(@Nullable LivingEntity target) {
        this.target = target;
        return this;
    }

    public ValueCalculatorContext stack(@Nullable ItemStack stack) {
        this.holderStack = stack == null ? ItemStack.EMPTY : stack;
        return this;
    }

    public ValueCalculatorContext with(String name, double value) {
        this.variables.put(name, value);
        return this;
    }

    @Nullable
    public LivingEntity holder() {
        return holder;
    }

    @Nullable
    public LivingEntity target() {
        return target;
    }

    public ItemStack stack() {
        return holderStack;
    }

    @Nullable
    public Double variable(String name) {
        return variables.get(name);
    }
}
