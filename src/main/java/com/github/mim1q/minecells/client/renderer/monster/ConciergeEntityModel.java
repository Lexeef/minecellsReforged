package com.github.mim1q.minecells.client.renderer.monster;

import com.github.mim1q.minecells.entity.boss.ConciergeEntity;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import com.mojang.blaze3d.vertex.PoseStack;

import static com.github.mim1q.minecells.util.MathUtils.radians;
import static com.github.mim1q.minecells.client.renderer.monster.MineCellsModelAnimationUtils.*;
import static org.joml.Math.*;

public class ConciergeEntityModel extends EntityModel<ConciergeEntity> {
  private boolean entityAlive = true;

  private final ModelPart root;
  private final ModelPart torsoLower;
  private final ModelPart torsoUpper;
  private final ModelPart neck;
  private final ModelPart head;
  private final ModelPart rightArm;
  private final ModelPart leftArm;
  private final ModelPart rightLeg;
  private final ModelPart leftLeg;

  public ConciergeEntityModel(ModelPart root) {
    this.root = root;
    this.torsoLower = root.getChild("torso_lower");
    this.torsoUpper = torsoLower.getChild("torso_upper");
    this.neck = torsoUpper.getChild("neck");
    this.head = neck.getChild("head");
    this.rightArm = torsoUpper.getChild("right_arm");
    this.leftArm = torsoUpper.getChild("left_arm");
    this.rightLeg = root.getChild("right_leg");
    this.leftLeg = root.getChild("left_leg");
  }

  @Override
  public void setupAnim(ConciergeEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    entityAlive = entity.isAlive();
    root.getAllParts().forEach(ModelPart::resetPose);

    var idleProgress = 1 - limbDistance;
    animateIdle(animationProgress, idleProgress);
    if (limbDistance > 1.0E-5F) animateWalk(limbAngle, limbDistance);
    animateHead(headYaw, headPitch);

    animate(entity.leapChargeAnimation,   this::animateLeapCharge,       animationProgress);
    animate(entity.leapReleaseAnimation,  this::animateLeapRelease,      animationProgress);
    animate(entity.waveChargeAnimation,   this::animateShockwaveCharge,  animationProgress);
    animate(entity.waveReleaseAnimation,  this::animateShockwaveRelease, animationProgress);
    animate(entity.punchChargeAnimation,  this::animatePunchCharge,      animationProgress);
    animate(entity.punchReleaseAnimation, this::animatePunchRelease,     animationProgress);
    animate(entity.deathStartAnimation,   this::animateDeathStart,       animationProgress);
    animate(entity.deathFallAnimation,    this::animateDeathFall,        animationProgress);
    animate(entity.screamAnimation, delta -> animateShout(delta, animationProgress), animationProgress);
  }

  private void animateIdle(float animationProgress, float delta) {
    torsoUpper.xRot = radians(35F) + wobble(animationProgress, 0.1F, 5F * delta);
    torsoLower.xRot = radians(-5F) + wobble(animationProgress, 0.1F, 3F * delta, 15F);
    rightArm.xRot = radians(-30F) + wobble(animationProgress, 0.1F, -10F * delta, 30F);
    leftArm.xRot = rightArm.xRot;
    rightArm.yRot = radians(15F);
    leftArm.yRot = radians(-15F);
  }

  private void animateWalk(float limbAngle, float limbDistance) {
    rightArm.xRot -= wobble(limbAngle, -0.5F, 90F * limbDistance, -20F);
    leftArm.xRot -= wobble(limbAngle, 0.5F, 90F * limbDistance, -20F);
    rightLeg.xRot = wobble(limbAngle, -0.5F, 60F * limbDistance);
    rightLeg.y -= max(0, sin(limbAngle * 0.5F + radians(80F))) * 4 * limbDistance;
    leftLeg.xRot = wobble(limbAngle, 0.5F, 60F * limbDistance);
    leftLeg.y -= max(0, sin(limbAngle * 0.5F - radians(100F))) * 4 * limbDistance;
    torsoLower.xRot += wobble(limbAngle, 1F, 10F * limbDistance);
    torsoLower.yRot = wobble(limbAngle, 0.5F, 20F * limbDistance);
    torsoLower.y += abs(sin(limbAngle * 0.5F + radians(180F))) * 3 * limbDistance;
    torsoUpper.xRot += wobble(limbAngle, 1F, 10F * limbDistance);
    torsoUpper.yRot = wobble(limbAngle, 0.5F, 20F * limbDistance, 15F);
    torsoUpper.zRot = wobble(limbAngle, 0.5F, 10F * limbDistance, 30F);
  }

  private void animateHead(float headYaw, float headPitch) {
    neck.xRot = -0.8F * (torsoLower.xRot + torsoUpper.xRot);
    neck.yRot = -0.9F * torsoUpper.yRot;
    head.yRot = radians(headYaw);
    head.xRot = radians(headPitch);
  }

  private void animateLeapCharge(float delta) {
    lerpAngles(torsoLower, 30, 0, 20, delta);
    torsoLower.y += 7 * delta;
    lerpAngles(torsoUpper, 30, 0, -10, delta);
    lerpAngles(leftArm, -60, 0, -30, delta);
    lerpAngles(rightArm, -30, 0, 20, delta);
    rightArm.y -= 2 * delta;
    lerpAngles(rightLeg, 15, 0, 0, delta);
    rightLeg.z -= 10 * delta;
    lerpAngles(leftLeg, 60, 20, 10, delta);
    leftLeg.y += 8 * delta;
    leftLeg.z -= 10 * delta;
    head.xRot += radians(20) * delta;
  }

  private void animateLeapRelease(float delta) {
    var leapReleaseLimbs = 1 - delta * 0.5;

    torsoLower.xRot += radians(5) * delta;
    torsoUpper.xRot -= radians(5) * delta;
    leftArm.zRot -= radians(25) * delta;
    rightArm.zRot += radians(25) * delta;
    head.xRot += radians(40) * delta;
    rightLeg.xRot *= leapReleaseLimbs;
    leftLeg.xRot *= leapReleaseLimbs;
    leftArm.xRot *= leapReleaseLimbs;
    rightArm.xRot *= leapReleaseLimbs;
  }

  private void animateShockwaveCharge(float delta) {
    lerpAngles(torsoLower, -10, 30, -25, delta);
    torsoLower.y += 2 * delta;
    lerpAngles(torsoUpper, -15, 10, -25, delta);
    torsoUpper.y += 3 * delta;
    lerpAngles(rightArm, 0, 20, 60, delta);
    lerpAngles(leftArm, 15, 0, 0, delta);
    leftLeg.z -= 3 * delta;
    lerpAngles(neck, 0, -45, 30, delta);
  }

  private void animateShockwaveRelease(float delta) {
    lerpAngles(torsoLower, 0, 15, 0, delta);
    lerpAngles(torsoUpper, 15, -20, 0, delta);
    lerpAngles(rightArm, -90, -30, 60, delta);
    lerpAngles(leftArm, 40, 0, 0, delta);
  }

  private void animatePunchCharge(float delta) {
    lerpAngles(torsoLower, 0, 15, 0, delta);
    lerpAngles(torsoUpper, -10, 10, 0, delta);
    lerpAngles(rightArm, -45, 0, 30, delta);
    rightArm.y -= 4 * delta;
    rightArm.z += 4 * delta;
    rightLeg.z += 2 * delta;
    leftLeg.z -= 2 * delta;
    neck.yRot -= radians(10F) * delta;
    neck.xRot += radians(15F) * delta;
  }

  private void animatePunchRelease(float delta) {
    lerpAngles(torsoLower, 20, -10, 0, delta);
    torsoLower.y += 3 * delta;
    lerpAngles(torsoUpper, 15, -10, 0, delta);
    lerpAngles(rightArm, -80, 0, -10, delta);
    rightArm.y += 2 * delta;
    rightArm.z -= 4 * delta;
    lerpAngles(leftArm, 20, 0, -20, delta);
    rightLeg.z -= 2 * delta;
    lerpAngles(leftLeg, 25, 0, 0, delta);
    leftLeg.y += 2 * delta;
    leftLeg.z += 2 * delta;
  }

  private void animateDeathStart(float delta) {
    var bounceDelta = MathUtils.easeOutBounce(0, 1, delta);
    var spedUpDelta = MathUtils.easeInOutQuad(0, 1, min(1, delta * 2));
    lerpAngles(torsoLower, -10, 0, 0, delta);
    torsoLower.y += 17 * bounceDelta;
    lerpAngles(torsoUpper, 40, 0, 0, bounceDelta);
    lerpAngles(leftArm, -50, 0, -15, delta);
    lerpAngles(rightArm, -50, 0, 15, delta);
    lerpAngles(leftLeg, 85, 15, 0, spedUpDelta);
    leftLeg.y += 17 * bounceDelta;
    leftLeg.z -= 4 * delta;
    lerpAngles(rightLeg, 85, -15, 0, spedUpDelta);
    rightLeg.y += 17 * bounceDelta;
    rightLeg.z -= 4 * delta;
  }

  private void animateDeathFall(float delta) {
    var bounceDelta = MathUtils.easeOutBounce(0, 1, delta);
    var spedUpDelta = MathUtils.easeInOutQuad(0, 1, min(1, delta * 2.5F));
    lerpAngles(torsoLower, 80, 0, 0, bounceDelta);
    torsoLower.z -= 2 * delta;
    lerpAngles(torsoUpper, 10, 0, 0, bounceDelta);
    lerpAngles(leftArm, -5, 0, -40, spedUpDelta);
    lerpAngles(rightArm, -5, 0, 40, spedUpDelta);
    lerpAngles(head, -10, 110, 10, bounceDelta);
    lerpAngles(neck, 0, 0, 0, delta);
    head.x += 2 * spedUpDelta;
    head.y -= 5 * spedUpDelta;
    head.z += 1 * spedUpDelta;
  }

  private void animateShout(float delta, float animationProgress) {
    var torsoYaw = 5 * sin(animationProgress * 0.2F - 0.1F);
    var upperTorsoYaw = 10 * sin(animationProgress * 0.2F);
    lerpAngles(torsoLower, 30, torsoYaw, 0, delta);
    lerpAngles(torsoUpper, -10, upperTorsoYaw, 0, delta);
    lerpAngles(rightArm, 25, 0, 25, delta);
    lerpAngles(leftArm, 25, 0, -25, delta);
    head.yRot += radians(3) * sin(animationProgress * 6) * delta;
    head.xRot += radians(2) * sin(animationProgress * 4) * delta;
  }

  @Override
  public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
    var overlayUv = entityAlive ? overlay : OverlayTexture.NO_OVERLAY;
    this.root.render(matrices, vertices, light, overlayUv, red, green, blue, alpha);
  }

  public static LayerDefinition createLayer() {
    MeshDefinition modelData = new MeshDefinition();
    PartDefinition modelPartData = modelData.getRoot();modelPartData.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(24, 65).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 24.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
    var torsoLower = modelPartData.addOrReplaceChild("torso_lower", CubeListBuilder.create().texOffs(0, 42).addBox(-7.0F, -18.0F, -4.0F, 14.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
      .texOffs(0, 26).addBox(-8.0F, -8.0F, -5.0F, 16.0F, 6.0F, 10.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 2.0F, 0.0F));
    var torsoUpper = torsoLower.addOrReplaceChild("torso_upper", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -14.0F, -9.5F, 22.0F, 14.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -16.0F, 2.5F));
    torsoUpper.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(67, 36).addBox(-2.0F, -4.0F, 0.0F, 2.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -10.0F, 2.5F, 0.5236F, -0.2618F, 0.0F));
    torsoUpper.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(67, 36).mirror().addBox(0.0F, -4.0F, 0.0F, 2.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(4.0F, -10.0F, 2.5F, 0.5236F, 0.2618F, 0.0F));
    var neck = torsoUpper.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -11.0F, -8.5F));
    neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(59, 17).addBox(-3.5F, -8.0F, -8.0F, 7.0F, 10.0F, 9.0F, new CubeDeformation(0.0F))
      .texOffs(68, 0).addBox(-2.5F, -14.0F, -8.0F, 5.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
    torsoUpper.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(66, 63).addBox(-6.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
      .texOffs(0, 60).addBox(-5.0F, 2.0F, -3.0F, 6.0F, 28.0F, 6.0F, new CubeDeformation(0.0F))
      .texOffs(44, 42).addBox(-6.0F, 13.0F, -3.5F, 8.0F, 22.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-11.0F, -11.0F, -2.5F));
    torsoUpper.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(66, 63).mirror().addBox(-2.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false)
      .texOffs(0, 60).mirror().addBox(-1.0F, 2.0F, -3.0F, 6.0F, 28.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
      .texOffs(44, 42).mirror().addBox(-2.0F, 13.0F, -3.5F, 8.0F, 22.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(11.0F, -11.0F, -2.5F));
    modelPartData.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(24, 65).mirror().addBox(-3.0F, -2.0F, -3.0F, 6.0F, 24.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));
    return LayerDefinition.create(modelData, 128, 128);
  }
}
