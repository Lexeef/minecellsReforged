package com.github.mim1q.minecells.client.renderer.monster;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public final class MineCellsModelAnimationUtils {
    private MineCellsModelAnimationUtils() {
    }

    public static void bipedWalk(float limbSwing, float limbSwingAmount, ModelPart root, ModelPart rightLeg, ModelPart leftLeg, ModelPart rightArm, ModelPart leftArm, ModelPart lowerTorso, ModelPart upperTorso) {
        float rightLegPitch = Mth.sin(limbSwing * 0.5F) * limbSwingAmount;
        rightLeg.xRot = rightLegPitch;
        leftLeg.xRot = -rightLegPitch;
        leftArm.xRot = -rightLegPitch;
        rightArm.xRot = rightLegPitch;
        root.y = 24.0F - Math.abs(Mth.sin((limbSwing + Mth.PI) * 0.5F)) * limbSwingAmount;

        float torsoPitch = Mth.sin(limbSwing + Mth.PI) * limbSwingAmount * Mth.DEG_TO_RAD;
        if (upperTorso != null) {
            upperTorso.xRot = torsoPitch;
        }
        if (lowerTorso != null) {
            lowerTorso.xRot = torsoPitch;
        }
    }

    public static void rotateHead(float headYaw, float headPitch, ModelPart head) {
        head.yRot = headYaw * Mth.DEG_TO_RAD;
        head.xRot = headPitch * Mth.DEG_TO_RAD;
    }
}
