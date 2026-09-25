package com.github.mim1q.minecells.client.renderer.monster;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.MineCellsMonsterEntity;
import com.github.mim1q.minecells.entity.SewersTentacleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class SewersTentacleRenderer extends ModelBackedMineCellsMonsterRenderer<SewersTentacleModel> {
    private static final ResourceLocation TEXTURE_BLUE = MineCells.id("textures/entity/sewers_tentacle/blue.png");
    private static final ResourceLocation TEXTURE_RED = MineCells.id("textures/entity/sewers_tentacle/red.png");
    private static final ResourceLocation TEXTURE_PURPLE = MineCells.id("textures/entity/sewers_tentacle/purple.png");

    public SewersTentacleRenderer(EntityRendererProvider.Context context) {
        super(context, new SewersTentacleModel(context.bakeLayer(MineCellsMonsterModelLayers.SEWERS_TENTACLE)), 0.0F, TEXTURE_BLUE, null);
    }

    @Override
    public ResourceLocation getTextureLocation(MineCellsMonsterEntity entity) {
        if (!(entity instanceof SewersTentacleEntity tentacle)) {
            return TEXTURE_BLUE;
        }
        return switch (tentacle.getVariant()) {
            case 1 -> TEXTURE_RED;
            case 2 -> TEXTURE_PURPLE;
            default -> TEXTURE_BLUE;
        };
    }

    @Override
    public void render(MineCellsMonsterEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        if (entity.isInvisible()) {
            return;
        }
        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
    }

    @Override
    protected float getFlipDegrees(MineCellsMonsterEntity entity) {
        return 0.0F;
    }
}
