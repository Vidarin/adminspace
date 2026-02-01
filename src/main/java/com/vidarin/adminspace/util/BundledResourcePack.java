package com.vidarin.adminspace.util;

import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public final class BundledResourcePack {
    public static File resourcePackFolder;
    public static File bundledPackFile;

    public static void init() {
        resourcePackFolder = Minecraft.getMinecraft().gameDir.toPath().resolve("resourcepacks").toFile();
        if (!resourcePackFolder.exists())
            if (!resourcePackFolder.mkdirs()) throw new RuntimeException("Unable to create or find resource packs folder");

        bundledPackFile = new File(resourcePackFolder, "AlphaPack.zip");
        if (bundledPackFile.exists()) return;

        try (InputStream in = BundledResourcePack.class.getResourceAsStream("/extra/AlphaPack.zip")) {
            if (in == null) throw new NullPointerException("Could not find bundled resource pack");
            Files.copy(in, bundledPackFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
