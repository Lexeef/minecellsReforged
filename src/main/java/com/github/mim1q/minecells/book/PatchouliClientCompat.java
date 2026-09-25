package com.github.mim1q.minecells.book;

import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;
import com.github.mim1q.minecells.entity.boss.MineCellsBossEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import vazkii.patchouli.client.book.gui.GuiBook;

/**
 * Must only be class-loaded when Patchouli is present.
 */
public final class PatchouliClientCompat {
    private PatchouliClientCompat() {
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(PatchouliClientCompat::onScreenRender);
        MinecraftForge.EVENT_BUS.addListener(PatchouliClientCompat::onRenderLiving);
    }

    private static void onScreenRender(ScreenEvent.Render.Pre event) {
        ReferenceListComponent.onScreenRender(event.getScreen());
    }

    /**
     * Fabric renders Mine Cells entities on book pages with {@code age = 10}, so spawn/emerge animations
     * driven by the entity age show their finished pose. Book entities are never added to the level.
     */
    private static void onRenderLiving(RenderLivingEvent.Pre<?, ?> event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof MineCellsMonsterEntity) && !(entity instanceof MineCellsBossEntity)) {
            return;
        }
        if (!(Minecraft.getInstance().screen instanceof GuiBook)) {
            return;
        }
        if (entity.level().getEntity(entity.getId()) != entity) {
            entity.tickCount = 10;
        }
    }
}
