package com.vidarin.adminspace.init;

import com.vidarin.adminspace.main.Adminspace;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SoundInit {
    public static SoundEvent VOID_DOOR_OPEN, VOID_DOOR_CLOSE;
    public static SoundEvent DEATH_EASTER_EGG;
    public static SoundEvent CORRIDOR_MUSIC, SKY_SECTOR_MUSIC, DISPOSAL_MUSIC;
    public static SoundEvent SIMULATION_RUMBLING, BEYOND_ENTRANCE;
    public static SoundEvent RECORD_CALM_5, RECORD_CALM_6, RECORD_ELSEWHERE, RECORD_MOONWALK;
    public static SoundEvent DISMANTLER_DASH, DISMANTLER_RECHARGE;

    public static void registerSounds() {
        VOID_DOOR_OPEN = registerSound("block.void_door_open");
        VOID_DOOR_CLOSE = registerSound("block.void_door_close");

        DEATH_EASTER_EGG = registerSound("entity.death_easter_egg");

        CORRIDOR_MUSIC = registerSound("music.corridors.corridor_music");
        SKY_SECTOR_MUSIC = registerSound("music.skysector.sky_sector_music");
        DISPOSAL_MUSIC = registerSound("music.disposal.disposal_music");
        SIMULATION_RUMBLING = registerSound("music.beyond.simulation_rumbling");
        BEYOND_ENTRANCE = registerSound("music.beyond.beyond_entrance");

        RECORD_CALM_5 = registerSound("music.disc.calm_5");
        RECORD_CALM_6 = registerSound("music.disc.calm_6");
        RECORD_ELSEWHERE = registerSound("music.disc.elsewhere");
        RECORD_MOONWALK = registerSound("music.disc.moonwalk");

        DISMANTLER_DASH = registerSound("item.dismantler.dash");
        DISMANTLER_RECHARGE = registerSound("item.dismantler.recharge");
    }

    private static SoundEvent registerSound(String name) {
        SoundEvent event = new SoundEvent(new ResourceLocation(Adminspace.MODID, name));
        event.setRegistryName(name);
        ForgeRegistries.SOUND_EVENTS.register(event);
        return event;
    }
}
