package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.particle.colored.ColoredParticleType;
import com.github.mim1q.minecells.particle.electric.ElectricParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class MineCellsParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MineCells.MOD_ID);

    public static final RegistryObject<SimpleParticleType> AURA = registerSimple("aura");
    public static final RegistryObject<SimpleParticleType> EXPLOSION = registerSimple("explosion");
    public static final RegistryObject<SimpleParticleType> PROTECTOR = registerSimple("protector");
    public static final RegistryObject<SimpleParticleType> CHARGE = registerSimple("charge");
    public static final RegistryObject<SimpleParticleType> FLY = registerSimple("fly");
    public static final RegistryObject<ColoredParticleType> SPECKLE = PARTICLES.register("speckle", ColoredParticleType::new);
    public static final RegistryObject<ColoredParticleType> FALLING_LEAF = PARTICLES.register("falling_leaf", ColoredParticleType::new);
    public static final RegistryObject<ElectricParticleType> ELECTRICITY = PARTICLES.register("electricity", ElectricParticleType::new);
    public static final RegistryObject<ColoredParticleType> DROP = PARTICLES.register("drop", ColoredParticleType::new);
    public static final RegistryObject<ColoredParticleType> SMALL_DROP = PARTICLES.register("small_drop", ColoredParticleType::new);
    public static final RegistryObject<ColoredParticleType> RISING_BUBBLE = PARTICLES.register("rising_bubble", ColoredParticleType::new);

    private MineCellsParticles() {
    }

    public static void register(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }

    private static RegistryObject<SimpleParticleType> registerSimple(String name) {
        return PARTICLES.register(name, () -> new SimpleParticleType(true));
    }
}
