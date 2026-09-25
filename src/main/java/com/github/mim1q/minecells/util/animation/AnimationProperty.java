package com.github.mim1q.minecells.util.animation;

import net.minecraft.util.Mth;

/**
 * Time-based tween matching Fabric {@code AnimationProperty}.
 */
public final class AnimationProperty {
    private float value;
    private float lastValue;
    private float targetValue;
    private float lastTime;
    private float time;
    private float duration = 10.0F;
    private EasingFunction easingFunction;

    @FunctionalInterface
    public interface EasingFunction {
        float ease(float start, float end, float delta);
    }

    public AnimationProperty(float value) {
        this(value, AnimationProperty::easeInOutQuad);
    }

    public AnimationProperty(float value, EasingFunction easingFunction) {
        this.value = value;
        this.lastValue = value;
        this.targetValue = value;
        this.easingFunction = easingFunction != null ? easingFunction : AnimationProperty::easeInOutQuad;
    }

    public void setupTransitionTo(float targetValue, float duration, EasingFunction easingFunction) {
        if (setupTransitionTo(targetValue, duration) && easingFunction != null) {
            this.easingFunction = easingFunction;
        }
    }

    public boolean setupTransitionTo(float targetValue, float duration) {
        if (targetValue == this.targetValue) {
            return false;
        }
        this.lastValue = this.value;
        this.lastTime = this.time;
        this.duration = duration;
        this.targetValue = targetValue;
        return true;
    }

    public float update(float time) {
        this.time = time;
        float progress = Mth.clamp(this.duration <= 0.0F ? 1.0F : (this.time - this.lastTime) / this.duration, 0.0F, 1.0F);
        this.value = this.easingFunction.ease(this.lastValue, this.targetValue, progress);
        return this.value;
    }

    public float getProgress() {
        return Mth.clamp(this.duration <= 0.0F ? 1.0F : (this.time - this.lastTime) / this.duration, 0.0F, 1.0F);
    }

    public float getValue() {
        return this.value;
    }

    public static float easeOutQuad(float start, float end, float delta) {
        float t = 1.0F - delta;
        return start + (end - start) * (1.0F - t * t);
    }

    public static float easeInOutQuad(float start, float end, float delta) {
        float t = delta < 0.5F ? 2.0F * delta * delta : 1.0F - (float) Math.pow(-2.0F * delta + 2.0F, 2.0D) / 2.0F;
        return start + (end - start) * t;
    }
}
