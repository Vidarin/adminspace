package com.vidarin.adminspace.data;

import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class WriteOnlyCompactStructureData {
    public static File outputDir = null;
    public final File outputFile;
    private final Vec3i from;
    private final Vec3i to;
    private final World world;

    private byte currentPaletteIndex = 0;
    private final Object2ByteMap<IBlockState> palette = new Object2ByteOpenHashMap<>();
    private final byte[][][] values;

    public static void init() {
        if (outputDir != null) return;
        File dir = Minecraft.getMinecraft().gameDir;
        outputDir = new File(dir, "csd_out");
        if (!outputDir.exists()) outputDir.mkdir();
    }

    public WriteOnlyCompactStructureData(String structureName, Vec3i from, Vec3i to, World world) {
        try {
            this.outputFile = new File(outputDir, structureName + ".csd");
            if (!outputFile.exists()) outputFile.createNewFile();
            this.from = from;
            this.to = to;
            this.world = world;
            this.values = new byte[to.getX() - from.getX()][to.getY() - from.getY()][to.getZ() - from.getZ()];
        } catch (IOException e) {
            throw new RuntimeException("Failed to create write only CSD object", e);
        }
    }

    public void write() {
        int x1 = from.getX(), x2 = to.getX();
        int y1 = from.getY(), y2 = to.getY();
        int z1 = from.getZ(), z2 = to.getZ();
        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                for (int z = z1; z < z2; z++) {
                    IBlockState state = world.getBlockState(new BlockPos(x, y, z));
                    if (!palette.containsKey(state)) {
                        if (currentPaletteIndex == -1) throw new IllegalStateException("Palette too large, max 255 different elements are allowed");
                        palette.put(state, currentPaletteIndex);
                        values[x - x1][y - y1][z - z1] = currentPaletteIndex++;
                    } else values[x - x1][y - y1][z - z1] = palette.get(state);
                }
            }
        }

        try (DataOutputStream os = new DataOutputStream(new FileOutputStream(outputFile))) {
            os.writeInt(palette.size());
            os.writeInt(values.length);
            os.writeInt(values[0].length);
            os.writeInt(values[0][0].length);

            for (Object2ByteMap.Entry<IBlockState> entry : palette.object2ByteEntrySet()) {
                IBlockState state = entry.getKey();
                byte[] name = Objects.requireNonNull(state.getBlock().getRegistryName()).toString().getBytes(StandardCharsets.UTF_8);
                os.writeShort(name.length);
                os.write(name);
                os.writeByte((byte) state.getBlock().getMetaFromState(state));
                os.writeByte(entry.getByteValue());
            }

            for (byte[][] xBytes : values) {
                for (byte[] yBytes : xBytes) {
                    for (byte zByte : yBytes) {
                        os.writeByte(zByte);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while writing CSD", e);
        }
    }
}
