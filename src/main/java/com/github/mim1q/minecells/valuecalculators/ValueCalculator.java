package com.github.mim1q.minecells.valuecalculators;

import net.minecraft.resources.ResourceLocation;

import java.util.function.ToDoubleFunction;

public final class ValueCalculator {
    private final ResourceLocation id;
    private final String name;
    private final ToDoubleFunction<ValueCalculatorContext> fallback;

    ValueCalculator(ResourceLocation id, String name, ToDoubleFunction<ValueCalculatorContext> fallback) {
        this.id = id;
        this.name = name;
        this.fallback = fallback;
    }

    public double calculate(ValueCalculatorContext context) {
        ValueCalculatorDefinition definition = ValueCalculators.definition(id);
        if (definition != null && definition.has(name)) {
            return definition.evaluate(name, context);
        }
        return fallback.applyAsDouble(context);
    }

    public double calculate() {
        return calculate(ValueCalculatorContext.create());
    }

    public ResourceLocation id() {
        return id;
    }

    public String name() {
        return name;
    }
}
