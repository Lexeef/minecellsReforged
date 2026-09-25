package com.github.mim1q.minecells.client.renderer;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.renderer.layer.GlowEyesLayer;
import com.github.mim1q.minecells.client.renderer.monster.ConciergeEntityModel;
import com.github.mim1q.minecells.client.renderer.monster.MineCellsMonsterModelLayers;
import com.github.mim1q.minecells.entity.boss.ConciergeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ConciergeEntityRenderer extends MobRenderer<ConciergeEntity, ConciergeEntityModel> {
    public static final ResourceLocation TEXTURE = MineCells.id("textures/entity/concierge/concierge.png");
    public static final ResourceLocation TEXTURE_GLOW = MineCells.id("textures/entity/concierge/concierge_glow.png");

    public ConciergeEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new ConciergeEntityModel(context.bakeLayer(MineCellsMonsterModelLayers.CONCIERGE)), 0.75F);
        addLayer(new GlowEyesLayer<>(this, TEXTURE_GLOW));
    }

    @Override
    protected float getFlipDegrees(ConciergeEntity entity) {
        return 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(ConciergeEntity entity) {
        return TEXTURE;
    }

    @Override
    protected boolean shouldShowName(ConciergeEntity entity) {
        return false;
    }
}
