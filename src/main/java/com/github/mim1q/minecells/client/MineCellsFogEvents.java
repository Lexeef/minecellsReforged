package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Forge replacement for Fabric's {@code BackgroundRendererMixin}: Promenade fog starts at 24 blocks.
 */
@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MineCellsFogEvents {
    private static final float PROMENADE_FOG_START = 24.0F;

    private MineCellsFogEvents() {
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        var level = Minecraft.getInstance().level;
        if (level == null || MineCellsDimension.of(level) != MineCellsDimension.PROMENADE_OF_THE_CONDEMNED) {
            return;
        }
        if (event.getCamera().getFluidInCamera() != FogType.NONE) {
            return;
        }
        event.setNearPlaneDistance(PROMENADE_FOG_START);
        event.setCanceled(true);
    }
}
