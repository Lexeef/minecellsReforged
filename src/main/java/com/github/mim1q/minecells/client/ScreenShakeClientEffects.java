package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.config.MineCellsConfig;
import com.github.mim1q.minecells.MineCells;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, value = Dist.CLIENT)
public final class ScreenShakeClientEffects {
    private static float intensity;
    private static int remainingTicks;

    private ScreenShakeClientEffects() {
    }

    public static void apply(float newIntensity, int durationTicks) {
        apply(newIntensity, durationTicks, "");
    }

    public static void apply(float newIntensity, int durationTicks, String modifier) {
        float scaled = newIntensity * getModifier(modifier);
        if (scaled <= 0.0F) {
            return;
        }
        intensity = Math.max(intensity, scaled);
        remainingTicks = Math.max(remainingTicks, durationTicks);
    }

    /**
     * Global multiplier times the per-source multiplier, relative to that source's default value:
     * default config keeps the tuned shake, 0 disables it, 2x default doubles it.
     */
    private static float getModifier(String modifier) {
        MineCellsConfig.Client config = MineCellsConfig.CLIENT;
        float global = config.screenShakeGlobal.get().floatValue();
        ForgeConfigSpec.DoubleValue value = switch (modifier) {
            case "minecells:weapon_flint" -> config.screenShakeWeaponFlint;
            case "minecells:weapon_lightning_bolt" -> config.screenShakeWeaponLightningBolt;
            case "minecells:shield_block" -> config.screenShakeShieldBlock;
            case "minecells:shield_parry" -> config.screenShakeShieldParry;
            case "minecells:conjunctivius_smash" -> config.screenShakeConjunctiviusSmash;
            case "minecells:conjunctivius_roar" -> config.screenShakeConjunctiviusRoar;
            case "minecells:conjunctivius_death" -> config.screenShakeConjunctiviusDeath;
            case "minecells:concierge_leap" -> config.screenShakeConciergeLeap;
            case "minecells:concierge_step" -> config.screenShakeConciergeStep;
            case "minecells:concierge_roar" -> config.screenShakeConciergeRoar;
            case "minecells:concierge_death" -> config.screenShakeConciergeDeath;
            case "minecells:explosion" -> config.screenShakeExplosion;
            default -> null;
        };
        if (value == null) {
            return global;
        }
        double defaultValue = value.getDefault();
        if (defaultValue <= 0.0D) {
            return global * value.get().floatValue();
        }
        return global * (float) (value.get() / defaultValue);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (remainingTicks > 0) {
            remainingTicks--;
            if (remainingTicks <= 0) {
                intensity = 0.0F;
            }
        }
    }

    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (remainingTicks <= 0 || intensity <= 0.0F) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        float time = minecraft.player.tickCount + (float) event.getPartialTick();
        float shake = intensity * 2.0F;
        event.setYaw(event.getYaw() + Mth.sin(time * 1.7F) * shake);
        event.setPitch(event.getPitch() + Mth.cos(time * 1.9F) * shake * 0.6F);
        event.setRoll(event.getRoll() + Mth.sin(time * 2.3F) * shake * 0.3F);
    }
}
