package com.vidarin.adminspace.data;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.*;

public class AdminspaceGlobalData {
    public static File FILE;

    public static void init() {
        File dir = Minecraft.getMinecraft().gameDir;
        FILE = new File(dir, "adminspace.dat");
        read();
    }

    /// NOTE: Update this before committing any changes to read() or write()!
    private static final int VERSION = 1;

    private static void read() {
        if (!FILE.exists()) return;

        try (DataInputStream in = new DataInputStream(new FileInputStream(FILE))) {
            if (in.readInt() != VERSION) { FILE.deleteOnExit(); throw new IllegalStateException("Data file version mismatch; Please relaunch your game."); }
            shownResourcePackNotice = in.readBoolean();
            visitedBeyond = in.readBoolean();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write() {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(FILE))) {
            out.writeInt(VERSION);
            out.writeBoolean(shownResourcePackNotice);
            out.writeBoolean(visitedBeyond);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // VARIABLES START //
    // Remember to call write() in all setters!

    @SideOnly(Side.CLIENT) private static boolean shownResourcePackNotice = false;
    @SideOnly(Side.CLIENT) private static boolean visitedBeyond = false;

    public static boolean hasShownResourcePackNotice() { return shownResourcePackNotice; }
    public static void setShownResourcePackNotice(boolean v) { shownResourcePackNotice = v; write(); }

    public static boolean hasVisitedBeyond() { return visitedBeyond; }
    public static void setVisitedBeyond(boolean v) { visitedBeyond = v; write(); }
}
