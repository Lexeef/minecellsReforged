package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class MineCellsSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MineCells.MOD_ID);

    public static final RegistryObject<SoundEvent> LEAPING_ZOMBIE_CHARGE = register("leaping_zombie.leap.charge");
    public static final RegistryObject<SoundEvent> LEAPING_ZOMBIE_RELEASE = register("leaping_zombie.leap.release");
    public static final RegistryObject<SoundEvent> LEAPING_ZOMBIE_DEATH = register("leaping_zombie.death");
    public static final RegistryObject<SoundEvent> SHOCKER_DEATH = register("shocker.death");
    public static final RegistryObject<SoundEvent> SHOCKER_CHARGE = register("shocker.charge");
    public static final RegistryObject<SoundEvent> SHOCKER_RELEASE = register("shocker.release");
    public static final RegistryObject<SoundEvent> GRENADIER_CHARGE = register("grenadier.charge");
    public static final RegistryObject<SoundEvent> DISGUSTING_WORM_ATTACK = register("disgusting_worm.attack");
    public static final RegistryObject<SoundEvent> DISGUSTING_WORM_DEATH = register("disgusting_worm.death");
    public static final RegistryObject<SoundEvent> INQUISITOR_CHARGE = register("inquisitor.charge");
    public static final RegistryObject<SoundEvent> INQUISITOR_RELEASE = register("inquisitor.release");
    public static final RegistryObject<SoundEvent> KAMIKAZE_WAKE = register("kamikaze.wake");
    public static final RegistryObject<SoundEvent> KAMIKAZE_CHARGE = register("kamikaze.charge");
    public static final RegistryObject<SoundEvent> KAMIKAZE_DEATH = register("kamikaze.death");
    public static final RegistryObject<SoundEvent> SHIELDBEARER_CHARGE = register("shieldbearer.charge");
    public static final RegistryObject<SoundEvent> SHIELDBEARER_RELEASE = register("shieldbearer.release");
    public static final RegistryObject<SoundEvent> MUTATED_BAT_CHARGE = register("mutated_bat.charge");
    public static final RegistryObject<SoundEvent> MUTATED_BAT_RELEASE = register("mutated_bat.release");
    public static final RegistryObject<SoundEvent> MUTATED_BAT_WAKE = register("mutated_bat.wake");
    public static final RegistryObject<SoundEvent> RANCID_RAT_CHARGE = register("rancid_rat.charge");
    public static final RegistryObject<SoundEvent> RANCID_RAT_RELEASE = register("rancid_rat.release");
    public static final RegistryObject<SoundEvent> SCORPION_CHARGE = register("scorpion.charge");
    public static final RegistryObject<SoundEvent> SEWERS_TENTACLE_DEATH = register("sewers_tentacle.death");
    public static final RegistryObject<SoundEvent> FLY_CHARGE = register("fly.charge");
    public static final RegistryObject<SoundEvent> FLY_FLY = register("fly.fly");
    public static final RegistryObject<SoundEvent> FLY_RELEASE = register("fly.release");
    public static final RegistryObject<SoundEvent> SWEEPER_CHARGE = register("sweeper.charge");
    public static final RegistryObject<SoundEvent> SWEEPER_RELEASE = register("sweeper.release");
    public static final RegistryObject<SoundEvent> CONJUNCTIVIUS_DASH_CHARGE = register("conjunctivius.dash.charge");
    public static final RegistryObject<SoundEvent> CONJUNCTIVIUS_DASH_RELEASE = register("conjunctivius.dash.release");
    public static final RegistryObject<SoundEvent> CONJUNCTIVIUS_DYING = register("conjunctivius.dying");
    public static final RegistryObject<SoundEvent> CONJUNCTIVIUS_DEATH = register("conjunctivius.death");
    public static final RegistryObject<SoundEvent> CONJUNCTIVIUS_HIT = register("conjunctivius.hit");
    public static final RegistryObject<SoundEvent> CONJUNCTIVIUS_SHOT = register("conjunctivius.shot");
    public static final RegistryObject<SoundEvent> CONJUNCTIVIUS_SHOUT = register("conjunctivius.shout");
    public static final RegistryObject<SoundEvent> CONJUNCTIVIUS_MOVE = register("conjunctivius.move");
    public static final RegistryObject<SoundEvent> CONCIERGE_LEAP_CHARGE = register("concierge.leap.charge");
    public static final RegistryObject<SoundEvent> CONCIERGE_LEAP_LAND = register("concierge.leap.land");
    public static final RegistryObject<SoundEvent> CONCIERGE_SHOCKWAVE_CHARGE = register("concierge.shockwave.charge");
    public static final RegistryObject<SoundEvent> CONCIERGE_SHOCKWAVE_RELEASE = register("concierge.shockwave.release");
    public static final RegistryObject<SoundEvent> CONCIERGE_AURA_CHARGE = register("concierge.aura.charge");
    public static final RegistryObject<SoundEvent> CONCIERGE_AURA_RELEASE = register("concierge.aura.release");
    public static final RegistryObject<SoundEvent> CONCIERGE_PUNCH_CHARGE = register("concierge.punch.charge");
    public static final RegistryObject<SoundEvent> CONCIERGE_PUNCH_RELEASE = register("concierge.punch.release");
    public static final RegistryObject<SoundEvent> CONCIERGE_SHOUT = register("concierge.shout");
    public static final RegistryObject<SoundEvent> CONCIERGE_STEP = register("concierge.step");
    public static final RegistryObject<SoundEvent> BOW_CHARGE = register("weapon.bow.charge");
    public static final RegistryObject<SoundEvent> BOW_RELEASE = register("weapon.bow.release");
    public static final RegistryObject<SoundEvent> SWIPE = register("weapon.swipe");
    public static final RegistryObject<SoundEvent> HIT_FLOOR = register("weapon.hit_floor");
    public static final RegistryObject<SoundEvent> TENTACLE_CHARGE = register("weapon.tentacle.charge");
    public static final RegistryObject<SoundEvent> TENTACLE_RELEASE = register("weapon.tentacle.release");
    public static final RegistryObject<SoundEvent> KATANA_CHARGE = register("weapon.katana.charge");
    public static final RegistryObject<SoundEvent> KATANA_RELEASE = register("weapon.katana.release");
    public static final RegistryObject<SoundEvent> FROST_BLAST = register("weapon.frost_blast.release");
    public static final RegistryObject<SoundEvent> FLINT_CHARGE = register("weapon.flint.charge");
    public static final RegistryObject<SoundEvent> FLINT_RELEASE = register("weapon.flint.release");
    public static final RegistryObject<SoundEvent> PORTAL_ACTIVATE = register("portal.activate");
    public static final RegistryObject<SoundEvent> PORTAL_USE = register("portal.use");
    public static final RegistryObject<SoundEvent> CRIT = register("crit");
    public static final RegistryObject<SoundEvent> SHOCK = register("shock");
    public static final RegistryObject<SoundEvent> FREEZE = register("freeze");
    public static final RegistryObject<SoundEvent> EXPLOSION = register("explosion");
    public static final RegistryObject<SoundEvent> ELEVATOR_START = register("elevator_start");
    public static final RegistryObject<SoundEvent> ELEVATOR_STOP = register("elevator_stop");
    public static final RegistryObject<SoundEvent> BUZZ = register("buzz");
    public static final RegistryObject<SoundEvent> TELEPORT_CHARGE = register("teleport.charge");
    public static final RegistryObject<SoundEvent> TELEPORT_RELEASE = register("teleport.release");
    public static final RegistryObject<SoundEvent> RISE = register("rise");
    public static final RegistryObject<SoundEvent> CHARGE = register("charge");
    public static final RegistryObject<SoundEvent> CELL_ABSORB = register("cell_absorb");
    public static final RegistryObject<SoundEvent> CURSE_DEATH = register("curse_death");
    public static final RegistryObject<SoundEvent> OBELISK = register("obelisk");
    public static final RegistryObject<SoundEvent> PRISONERS_QUARTERS_SOUND = register("music.prisoners_quarters");
    public static final RegistryObject<SoundEvent> PROMENADE_SOUND = register("music.promenade");
    public static final RegistryObject<SoundEvent> RAMPARTS_SOUND = register("music.ramparts");
    public static final RegistryObject<SoundEvent> INSUFFERABLE_CRYPT_SOUND = register("music.insufferable_crypt");
    public static final RegistryObject<SoundEvent> BLACK_BRIDGE_SOUND = register("music.black_bridge");

    private MineCellsSounds() {
    }

    public static void register(IEventBus eventBus) {
        SOUNDS.register(eventBus);
    }

    public static Music music(RegistryObject<SoundEvent> sound) {
        return new Music(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound.get()), 0, 0, true);
    }

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(MineCells.id(name)));
    }
}
