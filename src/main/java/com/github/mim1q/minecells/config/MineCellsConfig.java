package com.github.mim1q.minecells.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class MineCellsConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final Common COMMON;
    public static final Client CLIENT;

    static {
        ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();
        COMMON = new Common(commonBuilder);
        COMMON_SPEC = commonBuilder.build();

        ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();
        CLIENT = new Client(clientBuilder);
        CLIENT_SPEC = clientBuilder.build();
    }

    private MineCellsConfig() {
    }

    public enum ForceServerThreadMode {
        ALWAYS,
        NEVER,
        DEFAULT
    }

    public static final class Common {
        public final ForgeConfigSpec.BooleanValue unlockedBossEntry;
        public final ForgeConfigSpec.EnumValue<ForceServerThreadMode> teleportForceMainThread;
        public final ForgeConfigSpec.BooleanValue autoWipeData;
        public final ForgeConfigSpec.BooleanValue disableFallProtection;
        public final ForgeConfigSpec.IntValue baseTentacleMaxDistance;
        public final ForgeConfigSpec.IntValue additionalParryTime;
        public final ForgeConfigSpec.IntValue elevatorMaxAssemblyHeight;
        public final ForgeConfigSpec.IntValue elevatorMinAssemblyHeight;
        public final ForgeConfigSpec.DoubleValue elevatorSpeed;
        public final ForgeConfigSpec.DoubleValue elevatorAcceleration;
        public final ForgeConfigSpec.DoubleValue elevatorDamage;

        private Common(ForgeConfigSpec.Builder builder) {
            builder.push("common");
            unlockedBossEntry = builder.define("unlockedBossEntry", false);
            teleportForceMainThread = builder.defineEnum("teleportForceMainThread", ForceServerThreadMode.DEFAULT);
            autoWipeData = builder.define("autoWipeData", false);
            disableFallProtection = builder.define("disableFallProtection", false);
            baseTentacleMaxDistance = builder.defineInRange("baseTentacleMaxDistance", 24, 0, 64);
            additionalParryTime = builder.defineInRange("additionalParryTime", 0, 0, 200);
            builder.push("elevator");
            elevatorMaxAssemblyHeight = builder.defineInRange("maxAssemblyHeight", 256, 64, 320);
            elevatorMinAssemblyHeight = builder.defineInRange("minAssemblyHeight", 1, 1, 10);
            elevatorSpeed = builder.defineInRange("speed", 1.0, 0.1, 2.5);
            elevatorAcceleration = builder.defineInRange("acceleration", 0.01, 0.001, 0.1);
            elevatorDamage = builder.defineInRange("damage", 10.0, 0.0, 100.0);
            builder.pop();
            builder.pop();
        }
    }

    public static final class Client {
        public final ForgeConfigSpec.BooleanValue keepOriginalGuiModels;
        public final ForgeConfigSpec.BooleanValue showCritIndicator;
        public final ForgeConfigSpec.BooleanValue experimentalMusicLooping;
        public final ForgeConfigSpec.BooleanValue customBossBars;
        public final ForgeConfigSpec.DoubleValue screenShakeGlobal;
        public final ForgeConfigSpec.DoubleValue screenShakeWeaponFlint;
        public final ForgeConfigSpec.DoubleValue screenShakeWeaponLightningBolt;
        public final ForgeConfigSpec.DoubleValue screenShakeShieldBlock;
        public final ForgeConfigSpec.DoubleValue screenShakeShieldParry;
        public final ForgeConfigSpec.DoubleValue screenShakeConjunctiviusSmash;
        public final ForgeConfigSpec.DoubleValue screenShakeConjunctiviusRoar;
        public final ForgeConfigSpec.DoubleValue screenShakeConjunctiviusDeath;
        public final ForgeConfigSpec.DoubleValue screenShakeConciergeLeap;
        public final ForgeConfigSpec.DoubleValue screenShakeConciergeStep;
        public final ForgeConfigSpec.DoubleValue screenShakeConciergeRoar;
        public final ForgeConfigSpec.DoubleValue screenShakeConciergeDeath;
        public final ForgeConfigSpec.DoubleValue screenShakeExplosion;
        public final ForgeConfigSpec.BooleanValue opaqueParticles;
        public final ForgeConfigSpec.BooleanValue shockerGlow;
        public final ForgeConfigSpec.BooleanValue grenadierGlow;
        public final ForgeConfigSpec.BooleanValue leapingZombieGlow;
        public final ForgeConfigSpec.BooleanValue disgustingWormGlow;
        public final ForgeConfigSpec.BooleanValue protectorGlow;
        public final ForgeConfigSpec.BooleanValue rancidRatGlow;
        public final ForgeConfigSpec.BooleanValue scorpionGlow;

        private Client(ForgeConfigSpec.Builder builder) {
            builder.push("client");
            keepOriginalGuiModels = builder.define("keepOriginalGuiModels", false);
            showCritIndicator = builder.define("showCritIndicator", true);
            experimentalMusicLooping = builder.define("experimentalMusicLooping", true);
            customBossBars = builder.define("customBossBars", true);
            builder.push("screenShake");
            screenShakeGlobal = builder.defineInRange("global", 1.0, 0.0, 10.0);
            screenShakeWeaponFlint = builder.defineInRange("weaponFlint", 1.5, 0.0, 10.0);
            screenShakeWeaponLightningBolt = builder.defineInRange("weaponLightningBolt", 0.5, 0.0, 10.0);
            screenShakeShieldBlock = builder.defineInRange("shieldBlock", 0.4, 0.0, 10.0);
            screenShakeShieldParry = builder.defineInRange("shieldParry", 0.8, 0.0, 10.0);
            screenShakeConjunctiviusSmash = builder.defineInRange("conjunctiviusSmash", 1.0, 0.0, 10.0);
            screenShakeConjunctiviusRoar = builder.defineInRange("conjunctiviusRoar", 1.0, 0.0, 10.0);
            screenShakeConjunctiviusDeath = builder.defineInRange("conjunctiviusDeath", 2.0, 0.0, 10.0);
            screenShakeConciergeLeap = builder.defineInRange("conciergeLeap", 2.0, 0.0, 10.0);
            screenShakeConciergeStep = builder.defineInRange("conciergeStep", 0.25, 0.0, 10.0);
            screenShakeConciergeRoar = builder.defineInRange("conciergeRoar", 1.0, 0.0, 10.0);
            screenShakeConciergeDeath = builder.defineInRange("conciergeDeath", 2.0, 0.0, 10.0);
            screenShakeExplosion = builder.defineInRange("explosion", 0.75, 0.0, 10.0);
            builder.pop();
            builder.push("rendering");
            opaqueParticles = builder.define("opaqueParticles", false);
            shockerGlow = builder.define("shockerGlow", true);
            grenadierGlow = builder.define("grenadierGlow", true);
            leapingZombieGlow = builder.define("leapingZombieGlow", true);
            disgustingWormGlow = builder.define("disgustingWormGlow", true);
            protectorGlow = builder.define("protectorGlow", true);
            rancidRatGlow = builder.define("rancidRatGlow", true);
            scorpionGlow = builder.define("scorpionGlow", true);
            builder.pop();
            builder.pop();
        }
    }
}
