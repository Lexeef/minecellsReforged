package com.github.mim1q.minecells.valuecalculators;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ValueCalculatorDefinition {
    private static final int MAX_DEPTH = 16;

    private final Map<String, Variable> variables;
    private final Map<String, ValueExpression> equations;

    private ValueCalculatorDefinition(Map<String, Variable> variables, Map<String, ValueExpression> equations) {
        this.variables = variables;
        this.equations = equations;
    }

    public boolean has(String equation) {
        return equations.containsKey(equation);
    }

    public double evaluate(String equation, ValueCalculatorContext context) {
        return evaluate(equation, context, new HashSet<>());
    }

    private double evaluate(String equation, ValueCalculatorContext context, Set<String> evaluating) {
        ValueExpression expression = equations.get(equation);
        if (expression == null || evaluating.size() > MAX_DEPTH || !evaluating.add(equation)) {
            return 0.0D;
        }
        try {
            return sanitize(expression.evaluate(name -> resolve(name, context, evaluating)));
        } finally {
            evaluating.remove(equation);
        }
    }

    private double resolve(String name, ValueCalculatorContext context, Set<String> evaluating) {
        Double contextValue = context.variable(name);
        if (contextValue != null) {
            return contextValue;
        }
        Variable variable = variables.get(name);
        if (variable != null) {
            return sanitize(variable.get(context));
        }
        if (equations.containsKey(name)) {
            return evaluate(name, context, evaluating);
        }
        return 0.0D;
    }

    private static double sanitize(double value) {
        return Double.isFinite(value) ? value : 0.0D;
    }

    public static ValueCalculatorDefinition parse(JsonObject json) {
        Map<String, Variable> variables = new HashMap<>();
        if (json.has("variables") && json.get("variables").isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("variables").entrySet()) {
                variables.put(entry.getKey(), parseVariable(entry.getKey(), entry.getValue()));
            }
        }

        Map<String, ValueExpression> equations = new HashMap<>();
        if (json.has("equations") && json.get("equations").isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("equations").entrySet()) {
                equations.put(entry.getKey(), parseEquation(entry.getKey(), entry.getValue()));
            }
        }
        return new ValueCalculatorDefinition(Map.copyOf(variables), Map.copyOf(equations));
    }

    private static ValueExpression parseEquation(String name, JsonElement element) {
        if (!element.isJsonPrimitive()) {
            throw new JsonParseException("Equation '" + name + "' must be a number or a string");
        }
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (primitive.isNumber()) {
            return ValueExpression.constant(primitive.getAsDouble());
        }
        if (primitive.isBoolean()) {
            return ValueExpression.constant(primitive.getAsBoolean() ? 1.0D : 0.0D);
        }
        try {
            return ValueExpression.parse(primitive.getAsString());
        } catch (ValueExpression.ParseException e) {
            throw new JsonParseException("Equation '" + name + "': " + e.getMessage());
        }
    }

    private static Variable parseVariable(String name, JsonElement element) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            double value = element.getAsDouble();
            return context -> value;
        }
        if (!element.isJsonObject()) {
            throw new JsonParseException("Variable '" + name + "' must be an object");
        }
        JsonObject object = element.getAsJsonObject();
        String type = object.has("type") ? object.get("type").getAsString() : "";
        String path = type.contains(":") ? type.substring(type.indexOf(':') + 1) : type;
        double fallback = object.has("fallback") ? object.get("fallback").getAsDouble() : 0.0D;
        return switch (path) {
            case "enchantment" -> enchantmentVariable(name, object);
            case "attribute" -> attributeVariable(name, object, fallback);
            case "constant", "value" -> {
                double value = object.has("value") ? object.get("value").getAsDouble() : fallback;
                yield context -> value;
            }
            case "holder_health" -> context -> context.holder() == null ? fallback : context.holder().getHealth();
            case "holder_max_health" -> context -> context.holder() == null ? fallback : context.holder().getMaxHealth();
            case "target_health" -> context -> context.target() == null ? fallback : context.target().getHealth();
            case "target_max_health" -> context -> context.target() == null ? fallback : context.target().getMaxHealth();
            default -> throw new JsonParseException("Variable '" + name + "' has unsupported type '" + type + "'");
        };
    }

    private static Variable enchantmentVariable(String name, JsonObject object) {
        if (!object.has("enchantment")) {
            throw new JsonParseException("Variable '" + name + "' is missing 'enchantment'");
        }
        ResourceLocation id = new ResourceLocation(object.get("enchantment").getAsString());
        return context -> {
            if (context.stack().isEmpty()) {
                return 0.0D;
            }
            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(id);
            return enchantment == null ? 0.0D : EnchantmentHelper.getItemEnchantmentLevel(enchantment, context.stack());
        };
    }

    private static Variable attributeVariable(String name, JsonObject object, double fallback) {
        if (!object.has("attribute")) {
            throw new JsonParseException("Variable '" + name + "' is missing 'attribute'");
        }
        ResourceLocation id = new ResourceLocation(object.get("attribute").getAsString());
        return context -> {
            LivingEntity holder = context.holder();
            if (holder == null || !ForgeRegistries.ATTRIBUTES.containsKey(id)) {
                return fallback;
            }
            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(id);
            return attribute != null && holder.getAttributes().hasAttribute(attribute) ? holder.getAttributeValue(attribute) : fallback;
        };
    }

    @FunctionalInterface
    private interface Variable {
        double get(ValueCalculatorContext context);
    }
}
