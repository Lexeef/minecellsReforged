package com.github.mim1q.minecells.network;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.resources.ResourceLocation;

public final class PacketIdentifiers {
    public static final ResourceLocation CRIT = MineCells.id("crit");
    public static final ResourceLocation EXPLOSION = MineCells.id("explosion");
    public static final ResourceLocation CONNECT = MineCells.id("connect");
    public static final ResourceLocation ELEVATOR_DESTROYED = MineCells.id("elevator_destroyed");
    public static final ResourceLocation USE_TENTACLE = MineCells.id("use_tentacle_c2s");

    private PacketIdentifiers() {
    }
}
