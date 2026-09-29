package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.effect.BleedingMobEffect;
import com.github.mim1q.minecells.effect.ElectrifiedMobEffect;
import com.github.mim1q.minecells.effect.FrozenMobEffect;
import com.github.mim1q.minecells.effect.MineCellsEffectFlags;
import com.github.mim1q.minecells.effect.MineCellsMobEffect;
import com.github.mim1q.minecells.effect.ProtectedMobEffect;
import com.github.mim1q.minecells.MineCells;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class MineCellsStatusEffects {
    private static final String ASSASSINS_STRENGTH_UUID = "3a0efcb6-2dfe-46d8-86ed-7c9246afc29a";

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MineCells.MOD_ID);

    public static final RegistryObject<MobEffect> CURSED = EFFECTS.register("cursed", () -> new MineCellsMobEffect(MobEffectCategory.HARMFUL, 0x000000, false, MineCellsEffectFlags.CURSED, false));
    public static final RegistryObject<MobEffect> ELECTRIFIED = EFFECTS.register("electrified", ElectrifiedMobEffect::new);
    public static final RegistryObject<MobEffect> PROTECTED = EFFECTS.register("protected", ProtectedMobEffect::new);
    public static final RegistryObject<MobEffect> BLEEDING = EFFECTS.register("bleeding", BleedingMobEffect::new);
    public static final RegistryObject<MobEffect> DISARMED = EFFECTS.register("disarmed", () -> new MineCellsMobEffect(MobEffectCategory.HARMFUL, 0x000000, false, MineCellsEffectFlags.DISARMED, false));
    public static final RegistryObject<MobEffect> FROZEN = EFFECTS.register("frozen", () -> new FrozenMobEffect(MineCellsEffectFlags.FROZEN, 0x96F0FF, true));
    public static final RegistryObject<MobEffect> STUNNED = EFFECTS.register("stunned", () -> new FrozenMobEffect(MineCellsEffectFlags.STUNNED, 0xF1F8B5, false));
    public static final RegistryObject<MobEffect> ASSASSINS_STRENGTH = EFFECTS.register("assassins_strength", () -> new MineCellsMobEffect(MobEffectCategory.BENEFICIAL, 0xDA1C1C, false, null, false)
        .addAttributeModifier(Attributes.ATTACK_DAMAGE, ASSASSINS_STRENGTH_UUID, 1.25F, AttributeModifier.Operation.MULTIPLY_TOTAL));

    private MineCellsStatusEffects() {
    }

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}
