package com.github.mim1q.minecells.particle;

import com.github.mim1q.minecells.config.MineCellsConfig;

import net.minecraft.client.particle.ParticleRenderType;

public final class MineCellsParticleRenderTypes {
    private MineCellsParticleRenderTypes() {
    }

    public static ParticleRenderType translucent() {
        return MineCellsConfig.CLIENT.opaqueParticles.get()
            ? ParticleRenderType.PARTICLE_SHEET_OPAQUE
            : ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
