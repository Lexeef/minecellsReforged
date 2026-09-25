package com.github.mim1q.minecells.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class ClientUtil {
    private ClientUtil() {
    }

    public static Vec3 getClientCameraPos() {
        Entity camera = Minecraft.getInstance().getCameraEntity();
        if (camera == null) {
            return Vec3.ZERO;
        }
        return camera.position();
    }
}
