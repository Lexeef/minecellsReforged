package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.item.weapon.LightningBoltItem;
import com.github.mim1q.minecells.item.weapon.TentacleItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class MineCellsTargetHighlightRenderer {
    private static final int TENTACLE_COLOR = 0xAA25EB;

    private MineCellsTargetHighlightRenderer() {
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        PoseStack poseStack = event.getPoseStack();
        Vec3 camera = event.getCamera().getPosition();
        float partialTick = event.getPartialTick();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();

        if (stack.getItem() instanceof TentacleItem tentacle && !player.getCooldowns().isOnCooldown(tentacle)) {
            HitResult hit = tentacle.getClientHitResult();
            if (hit instanceof EntityHitResult entityHit) {
                drawEntity(poseStack, buffers, camera, entityHit.getEntity(), partialTick, TENTACLE_COLOR);
            } else if (hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK) {
                drawBlock(poseStack, buffers, camera, minecraft, blockHit.getBlockPos(), TENTACLE_COLOR);
            }
        } else if (stack.getItem() instanceof LightningBoltItem) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("targetId")) {
                LivingEntity target = LightningBoltItem.getTargetedEntity(stack, minecraft.level);
                if (target != null) {
                    int color = LightningBoltItem.getLightningColor(player.getTicksUsingItem());
                    drawEntity(poseStack, buffers, camera, target, partialTick, color);
                }
            }
        }
        buffers.endBatch(RenderType.lines());
    }

    private static void drawEntity(PoseStack poseStack, MultiBufferSource buffers, Vec3 camera, Entity entity, float partialTick, int color) {
        if (!entity.isAlive()) {
            return;
        }
        double dx = Mth.lerp(partialTick, entity.xOld, entity.getX()) - entity.getX();
        double dy = Mth.lerp(partialTick, entity.yOld, entity.getY()) - entity.getY();
        double dz = Mth.lerp(partialTick, entity.zOld, entity.getZ()) - entity.getZ();
        AABB box = entity.getBoundingBox().move(dx - camera.x, dy - camera.y, dz - camera.z).inflate(0.02D);
        drawBox(poseStack, buffers.getBuffer(RenderType.lines()), box, color);
    }

    private static void drawBlock(PoseStack poseStack, MultiBufferSource buffers, Vec3 camera, Minecraft minecraft, BlockPos pos, int color) {
        VoxelShape shape = minecraft.level.getBlockState(pos).getShape(minecraft.level, pos);
        if (shape.isEmpty()) {
            return;
        }
        VertexConsumer consumer = buffers.getBuffer(RenderType.lines());
        for (AABB part : shape.toAabbs()) {
            drawBox(poseStack, consumer, part.move(pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z).inflate(0.002D), color);
        }
    }

    private static void drawBox(PoseStack poseStack, VertexConsumer consumer, AABB box, int color) {
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        LevelRenderer.renderLineBox(poseStack, consumer, box, r, g, b, 1.0F);
    }
}
