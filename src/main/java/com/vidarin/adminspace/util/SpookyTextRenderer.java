package com.vidarin.adminspace.util;

import net.minecraft.client.gui.FontRenderer;

import java.util.Random;

/**
 * Used for literally one thing, but I don't want to put extra stuff in {@link com.vidarin.adminspace.main.AdminspaceEventHandler}
 */
public final class SpookyTextRenderer {
    private static final Random rand = new Random();
    private static final FastNoiseLite noiseGen; // this is so stupid, but it works

    public static void doTheThing(String text, FontRenderer fontRenderer, int x, int y) {
        long time = System.currentTimeMillis() >> 1;

        int color;
        if (noiseGen.GetNoise(time, time) > 0.3) color = 0x6D6363;
        else color = 0xC00A16;

        fontRenderer.drawString(text, x, y, color, noiseGen.GetNoise(512 + time, time) > 0.5);
    }

    static {
        noiseGen = new FastNoiseLite(rand.nextInt());
        noiseGen.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        noiseGen.SetFrequency(0.001F);
    }
}
