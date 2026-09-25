package com.github.mim1q.minecells.effect;

public enum MineCellsEffectFlags {
    PROTECTED(0),
    BLEEDING(1),
    AWAKENED(2),
    CURSED(3),
    DISARMED(4),
    FROZEN(5),
    STUNNED(6);

    private final int offset;

    MineCellsEffectFlags(int bit) {
        this.offset = 1 << bit;
    }

    public int getOffset() {
        return offset;
    }

    public boolean isSet(int flags) {
        return (flags & offset) != 0;
    }

    public static int compute(net.minecraft.world.entity.LivingEntity entity) {
        int flags = 0;
        for (net.minecraft.world.effect.MobEffectInstance instance : entity.getActiveEffects()) {
            if (instance.getEffect() instanceof MineCellsMobEffect effect) {
                MineCellsEffectFlags flag = effect.flag();
                if (flag != null) {
                    flags |= flag.getOffset();
                }
            }
        }
        return flags;
    }
}
