package com.github.mim1q.minecells.client.renderer.monster;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.client.model.geom.ModelLayerLocation;

public final class MineCellsMonsterModelLayers {
    public static final ModelLayerLocation LEAPING_ZOMBIE = layer("leaping_zombie");
    public static final ModelLayerLocation GRENADIER = layer("grenadier");
    public static final ModelLayerLocation SHOCKER = layer("shocker");
    public static final ModelLayerLocation UNDEAD_ARCHER = layer("undead_archer");
    public static final ModelLayerLocation RUNNER = layer("runner");
    public static final ModelLayerLocation PROTECTOR = layer("protector");
    public static final ModelLayerLocation SHIELDBEARER = layer("shieldbearer");
    public static final ModelLayerLocation RANCID_RAT = layer("rancid_rat");
    public static final ModelLayerLocation MUTATED_BAT = layer("mutated_bat");
    public static final ModelLayerLocation SCORPION = layer("scorpion");

    private MineCellsMonsterModelLayers() {
    }

    private static ModelLayerLocation layer(String path) {
        return new ModelLayerLocation(MineCells.id(path), "main");
    }
}
