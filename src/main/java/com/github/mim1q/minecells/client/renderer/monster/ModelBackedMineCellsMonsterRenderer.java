package com.github.mim1q.minecells.client.renderer.monster;

import com.github.mim1q.minecells.client.renderer.layer.GlowEyesLayer;
import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ModelBackedMineCellsMonsterRenderer<M extends EntityModel<MineCellsMonsterEntity>> extends MobRenderer<MineCellsMonsterEntity, M> {
    private final ResourceLocation texture;

    public ModelBackedMineCellsMonsterRenderer(EntityRendererProvider.Context context, M model, float shadowRadius, ResourceLocation texture, @Nullable ResourceLocation glowTexture) {
        super(context, model, shadowRadius);
        this.texture = texture;
        if (glowTexture != null) {
            addLayer(new GlowEyesLayer<>(this, glowTexture));
        }
    }

    @Override
    public ResourceLocation getTextureLocation(MineCellsMonsterEntity entity) {
        return texture;
    }
}
