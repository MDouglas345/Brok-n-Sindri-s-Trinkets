package net.fabricmc.Sounds;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SoundRegistry {
    public static final Identifier THROWSOUNDID = new Identifier("bns:throw");
    public static SoundEvent THROW_SOUND = new SoundEvent(THROWSOUNDID);

    public static final Identifier IMPACTSOUNDID = new Identifier("bns:impact");
    public static SoundEvent IMPACT_SOUND = new SoundEvent(IMPACTSOUNDID);

    public static void registerSounds(){
        Registry.register(Registry.SOUND_EVENT, THROWSOUNDID,THROW_SOUND);
        Registry.register(Registry.SOUND_EVENT, IMPACTSOUNDID,IMPACT_SOUND);
    }
}
