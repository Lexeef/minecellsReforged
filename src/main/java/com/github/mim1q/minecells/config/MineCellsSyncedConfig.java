package com.github.mim1q.minecells.config;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

/**
 * Common config values that the server overrides on connected clients (Fabric owo {@code @Sync(OVERRIDE_CLIENT)}).
 * Gameplay code reads these getters instead of {@link MineCellsConfig#COMMON} so client-side prediction
 * (elevator movement, parry window, tentacle reach) matches the server.
 */
public final class MineCellsSyncedConfig {
    @Nullable
    private static volatile Values serverValues = null;

    private MineCellsSyncedConfig() {
    }

    public static int baseTentacleMaxDistance() {
        Values values = serverValues;
        return values != null ? values.baseTentacleMaxDistance : MineCellsConfig.COMMON.baseTentacleMaxDistance.get();
    }

    public static int additionalParryTime() {
        Values values = serverValues;
        return values != null ? values.additionalParryTime : MineCellsConfig.COMMON.additionalParryTime.get();
    }

    public static int elevatorMaxAssemblyHeight() {
        Values values = serverValues;
        return values != null ? values.elevatorMaxAssemblyHeight : MineCellsConfig.COMMON.elevatorMaxAssemblyHeight.get();
    }

    public static int elevatorMinAssemblyHeight() {
        Values values = serverValues;
        return values != null ? values.elevatorMinAssemblyHeight : MineCellsConfig.COMMON.elevatorMinAssemblyHeight.get();
    }

    public static float elevatorSpeed() {
        Values values = serverValues;
        return values != null ? values.elevatorSpeed : MineCellsConfig.COMMON.elevatorSpeed.get().floatValue();
    }

    public static float elevatorAcceleration() {
        Values values = serverValues;
        return values != null ? values.elevatorAcceleration : MineCellsConfig.COMMON.elevatorAcceleration.get().floatValue();
    }

    public static float elevatorDamage() {
        Values values = serverValues;
        return values != null ? values.elevatorDamage : MineCellsConfig.COMMON.elevatorDamage.get().floatValue();
    }

    public static Values captureLocal() {
        MineCellsConfig.Common common = MineCellsConfig.COMMON;
        return new Values(
            common.baseTentacleMaxDistance.get(),
            common.additionalParryTime.get(),
            common.elevatorMaxAssemblyHeight.get(),
            common.elevatorMinAssemblyHeight.get(),
            common.elevatorSpeed.get().floatValue(),
            common.elevatorAcceleration.get().floatValue(),
            common.elevatorDamage.get().floatValue()
        );
    }

    public static void applyServerValues(Values values) {
        serverValues = values;
    }

    public static void clearServerValues() {
        serverValues = null;
    }

    public record Values(
        int baseTentacleMaxDistance,
        int additionalParryTime,
        int elevatorMaxAssemblyHeight,
        int elevatorMinAssemblyHeight,
        float elevatorSpeed,
        float elevatorAcceleration,
        float elevatorDamage
    ) {
        public void write(FriendlyByteBuf buf) {
            buf.writeVarInt(baseTentacleMaxDistance);
            buf.writeVarInt(additionalParryTime);
            buf.writeVarInt(elevatorMaxAssemblyHeight);
            buf.writeVarInt(elevatorMinAssemblyHeight);
            buf.writeFloat(elevatorSpeed);
            buf.writeFloat(elevatorAcceleration);
            buf.writeFloat(elevatorDamage);
        }

        public static Values read(FriendlyByteBuf buf) {
            return new Values(
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat()
            );
        }
    }
}
