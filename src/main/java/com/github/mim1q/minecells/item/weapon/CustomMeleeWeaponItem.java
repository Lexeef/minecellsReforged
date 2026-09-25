package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.item.weapon.interfaces.CrittingWeapon;
import com.github.mim1q.minecells.valuecalculators.ValueCalculator;
import com.github.mim1q.minecells.valuecalculators.ValueCalculatorContext;
import com.github.mim1q.minecells.valuecalculators.ValueCalculators;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class CustomMeleeWeaponItem extends SwordItem implements CrittingWeapon {
    private static final Set<CustomMeleeWeaponItem> ALL_MELEE_WEAPONS = new CopyOnWriteArraySet<>();
    private static final ValueCalculator GLOBAL_DAMAGE_MULTIPLIER = ValueCalculators.of("melee/global", "global_damage_multiplier", 1.0D);
    private static final ValueCalculator GLOBAL_SPEED_MULTIPLIER = ValueCalculators.of("melee/global", "global_speed_multiplier", 1.0D);

    static {
        ValueCalculators.onReload(CustomMeleeWeaponItem::updateAttributes);
    }

    private final ValueCalculator damageCalculator;
    private final ValueCalculator speedCalculator;
    private final ValueCalculator extraDamageCalculator;
    private final ValueCalculator critDamageCalculator;
    private volatile Multimap<Attribute, AttributeModifier> defaultModifiers;

    public CustomMeleeWeaponItem(String calculatorName, double damage, double speed, Properties properties) {
        this(calculatorName, damage, speed, 0.0F, 0.0F, properties);
    }

    public CustomMeleeWeaponItem(String calculatorName, double damage, double speed, float extraDamage, float additionalCritDamage, Properties properties) {
        super(MineCellsWeaponTier.CELL_INFUSED_STEEL, 0, 0.0F, properties);
        String path = "melee/" + calculatorName;
        this.damageCalculator = ValueCalculators.of(path, "damage", damage);
        this.speedCalculator = ValueCalculators.of(path, "speed", speed);
        this.extraDamageCalculator = ValueCalculators.of(path, "extra_damage", extraDamage);
        this.critDamageCalculator = ValueCalculators.of(path, "crit_damage", additionalCritDamage);
        rebuildModifiers();
        ALL_MELEE_WEAPONS.add(this);
    }

    public static void updateAttributes() {
        ALL_MELEE_WEAPONS.forEach(CustomMeleeWeaponItem::rebuildModifiers);
    }

    public static Set<Item> getAllMeleeWeapons() {
        return Set.copyOf(ALL_MELEE_WEAPONS);
    }

    private void rebuildModifiers() {
        double damage = (damageCalculator.calculate() - 1.0D) * GLOBAL_DAMAGE_MULTIPLIER.calculate();
        double speed = (speedCalculator.calculate() - 4.0D) * GLOBAL_SPEED_MULTIPLIER.calculate();
        this.defaultModifiers = ImmutableMultimap.<Attribute, AttributeModifier>builder()
            .put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", damage, AttributeModifier.Operation.ADDITION))
            .put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", speed, AttributeModifier.Operation.ADDITION))
            .build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return MineCellsWeaponTier.CELL_INFUSED_STEEL.getRepairIngredient().test(repairCandidate) || super.isValidRepairItem(stack, repairCandidate);
    }

    @Override
    public float getExtraDamage(ItemStack stack, @Nullable LivingEntity target, LivingEntity attacker) {
        return (float) extraDamageCalculator.calculate(ValueCalculatorContext.of(attacker, stack, target));
    }

    @Override
    public boolean canCrit(ItemStack stack, @Nullable LivingEntity target, LivingEntity attacker) {
        return false;
    }

    @Override
    public float getAdditionalCritDamage(ItemStack stack, @Nullable LivingEntity target, @Nullable LivingEntity attacker) {
        return (float) critDamageCalculator.calculate(ValueCalculatorContext.of(attacker, stack, target));
    }
}
