package com.github.mim1q.minecells.client.renderer.monster;

import com.github.mim1q.minecells.util.animation.AnimationProperty;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

public final class MineCellsModelAnimationUtils {
    private static final float EPSILON = 1.0E-5F;

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

    public static void lerpModelPartRotation(ModelPart part, float pitchDeg, float yawDeg, float rollDeg, float delta) {
        part.xRot = Mth.lerp(delta, part.xRot, pitchDeg * Mth.DEG_TO_RAD);
        part.yRot = Mth.lerp(delta, part.yRot, yawDeg * Mth.DEG_TO_RAD);
        part.zRot = Mth.lerp(delta, part.zRot, rollDeg * Mth.DEG_TO_RAD);
    }

    public static float wobble(float progress, float speed, float scale, float offset) {
        return (float) Math.sin(offset * Mth.DEG_TO_RAD + progress * speed) * scale * Mth.DEG_TO_RAD;
    }

    public static float wobble(float progress, float speed, float scale) {
        return wobble(progress, speed, scale, 0.0F);
    }

    public static float wobble(float progress, float speed) {
        return wobble(progress, speed, 1.0F);
    }

    public static void lerpAngles(ModelPart bone, Float pitchDeg, Float yawDeg, Float rollDeg, float delta) {
        if (pitchDeg != null) {
            bone.xRot = Mth.lerp(delta, bone.xRot, pitchDeg * Mth.DEG_TO_RAD);
        }
        if (yawDeg != null) {
            bone.yRot = Mth.lerp(delta, bone.yRot, yawDeg * Mth.DEG_TO_RAD);
        }
        if (rollDeg != null) {
            bone.zRot = Mth.lerp(delta, bone.zRot, rollDeg * Mth.DEG_TO_RAD);
        }
    }

    public static void lerpAngles(ModelPart bone, float pitchDeg, float yawDeg, float rollDeg, float delta) {
        bone.xRot = Mth.lerp(delta, bone.xRot, pitchDeg * Mth.DEG_TO_RAD);
        bone.yRot = Mth.lerp(delta, bone.yRot, yawDeg * Mth.DEG_TO_RAD);
        bone.zRot = Mth.lerp(delta, bone.zRot, rollDeg * Mth.DEG_TO_RAD);
    }

    public static void animate(AnimationProperty animation, Consumer<Float> animationPlayer, float animationProgress) {
        float value = animation.update(animationProgress);
        if (value > EPSILON) {
            animationPlayer.accept(value);
        }
    }
}
