package com.github.mim1q.minecells.client.renderer.projectile;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.GrenadeProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class GrenadeProjectileRenderer<T extends GrenadeProjectileEntity> extends EntityRenderer<T> {
    public static final ModelLayerLocation GRENADE_LAYER = new ModelLayerLocation(MineCells.id("grenade"), "main");
    public static final ModelLayerLocation BIG_GRENADE_LAYER = new ModelLayerLocation(MineCells.id("big_grenade"), "main");
    public static final ModelLayerLocation DISGUSTING_WORM_EGG_LAYER = new ModelLayerLocation(MineCells.id("disgusting_worm_egg"), "main");

    private static final ResourceLocation GRENADE_TEXTURE = MineCells.id("textures/entity/grenades/grenade.png");
    private static final ResourceLocation BIG_GRENADE_TEXTURE = MineCells.id("textures/entity/grenades/big_grenade.png");
    private static final ResourceLocation DISGUSTING_WORM_EGG_TEXTURE = MineCells.id("textures/entity/grenades/disgusting_worm_egg.png");

    private final ModelPart body;
    private final ResourceLocation texture;
    @Nullable
    private final ResourceLocation glowTexture;

    public GrenadeProjectileRenderer(
        EntityRendererProvider.Context context,
        ModelLayerLocation layer,
        ResourceLocation texture,
        @Nullable ResourceLocation glowTexture
    ) {
        super(context);
        this.body = context.bakeLayer(layer).getChild("body");
        this.texture = texture;
        this.glowTexture = glowTexture;
    }

    public static <E extends GrenadeProjectileEntity> GrenadeProjectileRenderer<E> grenade(EntityRendererProvider.Context context) {
        return new GrenadeProjectileRenderer<>(context, GRENADE_LAYER, GRENADE_TEXTURE, GRENADE_TEXTURE);
    }

    public static <E extends GrenadeProjectileEntity> GrenadeProjectileRenderer<E> bigGrenade(EntityRendererProvider.Context context) {
        return new GrenadeProjectileRenderer<>(context, BIG_GRENADE_LAYER, BIG_GRENADE_TEXTURE, BIG_GRENADE_TEXTURE);
    }

    public static <E extends GrenadeProjectileEntity> GrenadeProjectileRenderer<E> disgustingWormEgg(EntityRendererProvider.Context context) {
        return new GrenadeProjectileRenderer<>(context, DISGUSTING_WORM_EGG_LAYER, DISGUSTING_WORM_EGG_TEXTURE, null);
    }

    public static LayerDefinition createGrenadeLayer() {
        return createLayer(8.0F);
    }

    public static LayerDefinition createBigGrenadeLayer() {
        return createLayer(12.0F);
    }

    public static LayerDefinition createDisgustingWormEggLayer() {
        return createLayer(6.0F);
    }

    private static LayerDefinition createLayer(float size) {
        MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(0, 0).addBox(-size / 2.0F, 0.0F, -size / 2.0F, size, size, size),
            PartPose.ZERO
        );
        return LayerDefinition.create(mesh, (int) size * 4, (int) size * 2);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        int overlay = OverlayTexture.NO_OVERLAY;
        if (entity.getFuse() < 10 && entity.getFuse() / 2 % 2 == 0) {
            overlay = OverlayTexture.pack(OverlayTexture.u(1.0F), 10);
        }
        body.render(poseStack, buffer.getBuffer(RenderType.entityCutoutNoCull(texture)), packedLight, overlay);
        if (glowTexture != null) {
            body.render(poseStack, buffer.getBuffer(RenderType.eyes(glowTexture)), packedLight, overlay);
        }
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return texture;
    }
}
