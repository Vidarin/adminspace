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

    private static void read() {
        if (!FILE.exists()) return;

        try (DataInputStream in = new DataInputStream(new FileInputStream(FILE))) {
            visitedBeyond = in.readBoolean();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write() {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(FILE))) {
            out.writeBoolean(visitedBeyond);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // VARIABLES START //
    // Remember to call write() in all setters!

    @SideOnly(Side.CLIENT) private static boolean visitedBeyond;

    public static boolean hasVisitedBeyond() { return visitedBeyond; }
    public static void setVisitedBeyond(boolean v) { visitedBeyond = v; write(); }
}
