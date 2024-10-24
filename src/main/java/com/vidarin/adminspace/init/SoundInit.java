package com.vidarin.adminspace.init;

import com.vidarin.adminspace.main.Adminspace;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SoundInit {
    public static SoundEvent VOID_DOOR_OPEN, VOID_DOOR_CLOSE;
    public static SoundEvent DEATH_EASTER_EGG;
    public static SoundEvent CORRIDOR_MUSIC, SKY_SECTOR_MUSIC;
    public static SoundEvent RECORD_CALM_5;

    public static void registerSounds() {
        VOID_DOOR_OPEN = registerSound("block.void_door_open");
        VOID_DOOR_CLOSE = registerSound("block.void_door_close");

        DEATH_EASTER_EGG = registerSound("entity.death_easter_egg");

        CORRIDOR_MUSIC = registerSound("music.corridors.corridor_music");
        SKY_SECTOR_MUSIC = registerSound("music.skysector.sky_sector_music");

        RECORD_CALM_5 = registerSound("music.disc.calm_5");
    }

    private static SoundEvent registerSound(String name) {
        ResourceLocation location = new ResourceLocation(Adminspace.MODID, name);
        SoundEvent event = new SoundEvent(location);
        event.setRegistryName(name);
        ForgeRegistries.SOUND_EVENTS.register(event);
        return event;
    }
}
