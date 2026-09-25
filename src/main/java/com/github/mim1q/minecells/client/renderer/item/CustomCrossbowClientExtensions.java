package com.github.mim1q.minecells.client.renderer.item;

import com.github.mim1q.minecells.item.weapon.bow.CustomCrossbowItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/**
 * Makes custom crossbows render like the vanilla crossbow (Fabric's {@code PlayerEntityRendererMixin} and
 * {@code HeldItemRendererMixin} add them to the vanilla {@code isOf(CROSSBOW)} checks).
 */
public final class CustomCrossbowClientExtensions implements IClientItemExtensions {
    public static final CustomCrossbowClientExtensions INSTANCE = new CustomCrossbowClientExtensions();

    private CustomCrossbowClientExtensions() {
    }

    @Override
    public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
        if (!entity.swinging && CrossbowItem.isCharged(stack)) {
            return HumanoidModel.ArmPose.CROSSBOW_HOLD;
        }
        return null;
    }

    @Override
    public boolean applyForgeHandTransform(
        PoseStack poseStack,
        LocalPlayer player,
        HumanoidArm arm,
        ItemStack stack,
        float partialTick,
        float equipProcess,
        float swingProcess
    ) {
        if (!(stack.getItem() instanceof CustomCrossbowItem crossbow)) {
            return false;
        }
        boolean rightArm = arm == HumanoidArm.RIGHT;
        int side = rightArm ? 1 : -1;
        InteractionHand hand = arm == player.getMainArm() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;

        if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && player.getUsedItemHand() == hand) {
            applyItemArmTransform(poseStack, side, equipProcess);
            poseStack.translate(side * -0.4785682F, -0.094387F, 0.05731531F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-11.935F));
            poseStack.mulPose(Axis.YP.rotationDegrees(side * 65.3F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(side * -9.785F));
            float usedTicks = stack.getUseDuration() - (player.getUseItemRemainingTicks() - partialTick + 1.0F);
            float progress = Math.min(1.0F, usedTicks / Math.max(1, crossbow.getDrawTime(player, stack)));
            if (progress > 0.1F) {
                float shake = Mth.sin((usedTicks - 0.1F) * 1.3F) * (progress - 0.1F);
                poseStack.translate(0.0F, shake * 0.004F, 0.0F);
            }
            poseStack.translate(0.0F, 0.0F, progress * 0.04F);
            poseStack.scale(1.0F, 1.0F, 1.0F + progress * 0.2F);
            poseStack.mulPose(Axis.YN.rotationDegrees(side * 45.0F));
            return true;
        }

        float swingRoot = Mth.sqrt(swingProcess);
        float x = -0.4F * Mth.sin(swingRoot * Mth.PI);
        float y = 0.2F * Mth.sin(swingRoot * Mth.TWO_PI);
        float z = -0.2F * Mth.sin(swingProcess * Mth.PI);
        poseStack.translate(side * x, y, z);
        applyItemArmTransform(poseStack, side, equipProcess);
        applyItemArmAttackTransform(poseStack, side, swingProcess);
        if (CrossbowItem.isCharged(stack) && swingProcess < 0.001F && hand == InteractionHand.MAIN_HAND) {
            poseStack.translate(side * -0.641864F, 0.0F, 0.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(side * 10.0F));
        }
        return true;
    }

    private static void applyItemArmTransform(PoseStack poseStack, int side, float equipProcess) {
        poseStack.translate(side * 0.56F, -0.52F + equipProcess * -0.6F, -0.72F);
    }

    private static void applyItemArmAttackTransform(PoseStack poseStack, int side, float swingProcess) {
        float swing = Mth.sin(swingProcess * swingProcess * Mth.PI);
        poseStack.mulPose(Axis.YP.rotationDegrees(side * (45.0F + swing * -20.0F)));
        float swingRoot = Mth.sin(Mth.sqrt(swingProcess) * Mth.PI);
        poseStack.mulPose(Axis.ZP.rotationDegrees(side * swingRoot * -20.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(swingRoot * -80.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(side * -45.0F));
    }
}
