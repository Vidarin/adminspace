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
    public static SoundEvent ONE_BUILDUP_ERRORS, ONE_CERTITUDES, ONE_CLIFFSIDE_HINSON, ONE_CLUMSINESS_AND_INNOVATION,
            ONE_DANNY_MAKES_CHIPTUNE, ONE_DISKDANCE, ONE_DRUNKEN_CARBONI, ONE_FAUX_VIDEO_PRODUCTION, ONE_FOR_THE_SAKE_OF_MAKING_GAMES,
            ONE_IMPOSTOR_SYNDROME, ONE_INDEPENDENT_ACCIDENT, ONE_JAYSON_GLOVE, ONE_LAWYER_CAGE_FIGHT, ONE_LOST_COUSINS,
            ONE_NO_PRESSURE, ONE_ONE, ONE_ONE_LAST_GAME, ONE_POST_SUCCESS_DEPRESSION, ONE_PR_DEPARTMENT, ONE_PRELIMINARY_ART_FORM,
            ONE_SOCIAL_LEGO, ONE_SURFACE_PENSION, ONE_SWARMS, ONE_THE_FIRST_MILLION, ONE_THE_WEIRDEST_YEAR_OF_YOUR_LIFE,
            ONE_THIS_DOESNT_WORK, ONE_TOTAL_DRAG, ONE_WOODEN_LOVE;
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

        ONE_BUILDUP_ERRORS = registerSound("music.one.buildup_errors");
        ONE_CERTITUDES = registerSound("music.one.certitudes");
        ONE_CLIFFSIDE_HINSON = registerSound("music.one.cliffside_hinson");
        ONE_CLUMSINESS_AND_INNOVATION = registerSound("music.one.clumsiness_and_innovation");
        ONE_DANNY_MAKES_CHIPTUNE = registerSound("music.one.danny_makes_chiptune");
        ONE_DISKDANCE = registerSound("music.one.diskdance");
        ONE_DRUNKEN_CARBONI = registerSound("music.one.drunken_carboni");
        ONE_FAUX_VIDEO_PRODUCTION = registerSound("music.one.faux_video_production");
        ONE_FOR_THE_SAKE_OF_MAKING_GAMES = registerSound("music.one.for_the_sake_of_making_games");
        ONE_IMPOSTOR_SYNDROME = registerSound("music.one.impostor_syndrome");
        ONE_INDEPENDENT_ACCIDENT = registerSound("music.one.independent_accident");
        ONE_JAYSON_GLOVE = registerSound("music.one.jayson_glove");
        ONE_LAWYER_CAGE_FIGHT = registerSound("music.one.lawyer_cage_fight");
        ONE_LOST_COUSINS = registerSound("music.one.lost_cousins");
        ONE_NO_PRESSURE = registerSound("music.one.no_pressure");
        ONE_ONE = registerSound("music.one.one");
        ONE_ONE_LAST_GAME = registerSound("music.one.one_last_game");
        ONE_POST_SUCCESS_DEPRESSION = registerSound("music.one.post_success_depression");
        ONE_PR_DEPARTMENT = registerSound("music.one.pr_department");
        ONE_PRELIMINARY_ART_FORM = registerSound("music.one.preliminary_art_form");
        ONE_SOCIAL_LEGO = registerSound("music.one.social_lego");
        ONE_SURFACE_PENSION = registerSound("music.one.surface_pension");
        ONE_SWARMS = registerSound("music.one.swarms");
        ONE_THE_FIRST_MILLION = registerSound("music.one.the_first_million");
        ONE_THE_WEIRDEST_YEAR_OF_YOUR_LIFE = registerSound("music.one.the_weirdest_year_of_your_life");
        ONE_THIS_DOESNT_WORK = registerSound("music.one.this_doesnt_work");
        ONE_TOTAL_DRAG = registerSound("music.one.total_drag");
        ONE_WOODEN_LOVE = registerSound("music.one.wooden_love");

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
