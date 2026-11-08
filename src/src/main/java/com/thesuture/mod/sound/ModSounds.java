package com.thesuture.mod.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final Identifier AMBIENT_ID = new Identifier("the_suture", "entity.suture.ambient");
    public static final SoundEvent SUTURE_AMBIENT = SoundEvent.of(AMBIENT_ID);

    public static final Identifier HURT_ID = new Identifier("the_suture", "entity.suture.hurt");
    public static final SoundEvent SUTURE_HURT = SoundEvent.of(HURT_ID);

    public static final Identifier DEATH_ID = new Identifier("the_suture", "entity.suture.death");
    public static final SoundEvent SUTURE_DEATH = SoundEvent.of(DEATH_ID);

    public static final Identifier GHOST_STEP_ID = new Identifier("the_suture", "effect.ghost_step");
    public static final SoundEvent GHOST_STEP = SoundEvent.of(GHOST_STEP_ID);

    public static final Identifier BONE_SNAP_ID = new Identifier("the_suture", "sound.bone_snap");
    public static final SoundEvent BONE_SNAP = SoundEvent.of(BONE_SNAP_ID);

    public static final Identifier DEMONIC_WHISPER_ID = new Identifier("the_suture", "sound.demonic_whisper");
    public static final SoundEvent DEMONIC_WHISPER = SoundEvent.of(DEMONIC_WHISPER_ID);

    public static final Identifier HEARTBEAT_PANIC_ID = new Identifier("the_suture", "sound.heartbeat_panic");
    public static final SoundEvent HEARTBEAT_PANIC = SoundEvent.of(HEARTBEAT_PANIC_ID);

    public static final Identifier TINNITUS_SCREECH_ID = new Identifier("the_suture", "sound.tinnitus_screech");
    public static final SoundEvent TINNITUS_SCREECH = SoundEvent.of(TINNITUS_SCREECH_ID);

    public static final Identifier PROXIMITY_DRONE_ID = new Identifier("the_suture", "sound.proximity_drone");
    public static final SoundEvent PROXIMITY_DRONE = SoundEvent.of(PROXIMITY_DRONE_ID);

    public static final Identifier CHASE_SCREECH_ID = new Identifier("the_suture", "sound.chase_screech");
    public static final SoundEvent CHASE_SCREECH = SoundEvent.of(CHASE_SCREECH_ID);

    public static final Identifier JUMPSCARE_STINGER_ID = new Identifier("the_suture", "jumpscare.stinger");
    public static final SoundEvent JUMPSCARE_STINGER = SoundEvent.of(JUMPSCARE_STINGER_ID);

    public static void registerSounds() {
        Registry.register(Registries.SOUND_EVENT, AMBIENT_ID, SUTURE_AMBIENT);
        Registry.register(Registries.SOUND_EVENT, HURT_ID, SUTURE_HURT);
        Registry.register(Registries.SOUND_EVENT, DEATH_ID, SUTURE_DEATH);
        Registry.register(Registries.SOUND_EVENT, GHOST_STEP_ID, GHOST_STEP);
        Registry.register(Registries.SOUND_EVENT, BONE_SNAP_ID, BONE_SNAP);
        Registry.register(Registries.SOUND_EVENT, DEMONIC_WHISPER_ID, DEMONIC_WHISPER);
        Registry.register(Registries.SOUND_EVENT, HEARTBEAT_PANIC_ID, HEARTBEAT_PANIC);
        Registry.register(Registries.SOUND_EVENT, TINNITUS_SCREECH_ID, TINNITUS_SCREECH);
        Registry.register(Registries.SOUND_EVENT, PROXIMITY_DRONE_ID, PROXIMITY_DRONE);
        Registry.register(Registries.SOUND_EVENT, CHASE_SCREECH_ID, CHASE_SCREECH);
        Registry.register(Registries.SOUND_EVENT, JUMPSCARE_STINGER_ID, JUMPSCARE_STINGER);
    }
}
