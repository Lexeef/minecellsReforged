package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.particle.ChargeParticle;
import com.github.mim1q.minecells.particle.colored.ColoredParticle;
import com.github.mim1q.minecells.particle.DropParticle;
import com.github.mim1q.minecells.particle.electric.ElectricParticle;
import com.github.mim1q.minecells.particle.ExplosionParticle;
import com.github.mim1q.minecells.particle.FallingLeafParticle;
import com.github.mim1q.minecells.particle.FlyParticle;
import com.github.mim1q.minecells.particle.ProtectorParticle;
import com.github.mim1q.minecells.particle.RisingBubbleParticle;
import com.github.mim1q.minecells.particle.SmallDropParticle;
import com.github.mim1q.minecells.particle.SpeckleParticle;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class MineCellsParticleProviders {
    private MineCellsParticleProviders() {
    }

    @SubscribeEvent
    public static void register(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(MineCellsParticles.AURA.get(), net.minecraft.client.particle.FlameParticle.Provider::new);
        event.registerSpriteSet(MineCellsParticles.EXPLOSION.get(), ExplosionParticle.Factory::new);
        event.registerSpriteSet(MineCellsParticles.PROTECTOR.get(), ProtectorParticle.Factory::new);
        event.registerSpriteSet(MineCellsParticles.CHARGE.get(), ChargeParticle.Factory::new);
        event.registerSpriteSet(MineCellsParticles.FLY.get(), FlyParticle.Factory::new);
        event.registerSpriteSet(MineCellsParticles.SPECKLE.get(), spriteSet -> new ColoredParticle.Factory(spriteSet, SpeckleParticle::new));
        event.registerSpriteSet(MineCellsParticles.FALLING_LEAF.get(), spriteSet -> new ColoredParticle.Factory(spriteSet, FallingLeafParticle::new));
        event.registerSpriteSet(MineCellsParticles.ELECTRICITY.get(), ElectricParticle.Factory::new);
        event.registerSpriteSet(MineCellsParticles.DROP.get(), spriteSet -> new ColoredParticle.Factory(spriteSet, DropParticle::new));
        event.registerSpriteSet(MineCellsParticles.SMALL_DROP.get(), spriteSet -> new ColoredParticle.Factory(spriteSet, SmallDropParticle::new));
        event.registerSpriteSet(MineCellsParticles.RISING_BUBBLE.get(), spriteSet -> new ColoredParticle.Factory(spriteSet, RisingBubbleParticle::new));
    }
}
