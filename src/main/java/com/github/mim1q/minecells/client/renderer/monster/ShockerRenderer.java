package com.github.mim1q.minecells.client.renderer.monster;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.renderer.layer.GlowEyesLayer;
import com.github.mim1q.minecells.config.MineCellsConfig;
import com.github.mim1q.minecells.entity.ShockerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ShockerRenderer extends ModelBackedMineCellsMonsterRenderer<ShockerModel> {
    private static final ResourceLocation TEXTURE = MineCells.id("textures/entity/shocker/shocker.png");
    private static final ResourceLocation GLOW_TEXTURE = MineCells.id("textures/entity/shocker/shocker_glow.png");
    private static final ResourceLocation GLOW_TEXTURE_ANGRY = MineCells.id("textures/entity/shocker/shocker_glow_angry.png");

    public ShockerRenderer(EntityRendererProvider.Context context) {
        super(context, new ShockerModel(context.bakeLayer(MineCellsMonsterModelLayers.SHOCKER)), 0.5F, TEXTURE, null);
        addLayer(new GlowEyesLayer<>(this,
            entity -> entity instanceof ShockerEntity shocker && (shocker.isAuraCharging() || shocker.isAuraReleasing()) ? GLOW_TEXTURE_ANGRY : GLOW_TEXTURE,
            () -> MineCellsConfig.CLIENT.shockerGlow.get()
        ));
    }
}
