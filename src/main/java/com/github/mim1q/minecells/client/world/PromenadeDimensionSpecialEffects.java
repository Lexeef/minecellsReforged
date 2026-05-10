package com.github.mim1q.minecells.client.world;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;

public final class PromenadeDimensionSpecialEffects extends DimensionSpecialEffects {
    public PromenadeDimensionSpecialEffects() {
        super(110.0F, true, SkyType.NORMAL, false, false);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 color, float brightness) {
        return color;
    }

    @Override
    public float[] getSunriseColor(float timeOfDay, float partialTick) {
        return null;
    }

    @Override
    public boolean isFoggyAt(int x, int z) {
        return true;
    }
}
