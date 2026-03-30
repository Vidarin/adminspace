package com.vidarin.adminspace.data;

import com.vidarin.adminspace.util.BlockHolder;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.Vec3i;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class ReadOnlyCompactStructureData {
    public static BlockHolder<IBlockState> readCSDStructure(String structureName, Vec3i offset) {
        return readCSDAsBlockHolder(ReadOnlyCompactStructureData.class.getResourceAsStream("/assets/adminspace/structures/" + structureName), offset);
    }

    @SuppressWarnings("deprecation")
    public static BlockHolder<IBlockState> readCSDAsBlockHolder(InputStream stream, Vec3i offset) {
        try (DataInputStream is = new DataInputStream(stream)) {
            int paletteSize = is.readInt();
            int xSize = is.readInt();
            int ySize = is.readInt();
            int zSize = is.readInt();

            Byte2ObjectMap<IBlockState> palette = new Byte2ObjectOpenHashMap<>(paletteSize);
            for (int i = 0; i < paletteSize; i++) {
                int nameLength = is.readShort() & 0xFFFF;
                byte[] nameBytes = new byte[nameLength];
                is.readFully(nameBytes);
                int meta = is.readByte() & 0xFF;
                byte key = is.readByte();

                Block block = Block.getBlockFromName(new String(nameBytes, StandardCharsets.UTF_8));
                if (block == null) palette.put(key, Blocks.AIR.getDefaultState());
                else palette.put(key, block.getStateFromMeta(meta));
            }

            int xOff = offset.getX();
            int yOff = offset.getY();
            int zOff = offset.getZ();
            BlockHolder<IBlockState> holder = new BlockHolder<>(xSize, ySize, zSize, xOff, yOff, zOff);
            for (int x = xOff; x < xSize + xOff; x++) {
                for (int y = yOff; y < ySize + yOff; y++) {
                    for (int z = zOff; z < zSize + zOff; z++) {
                        byte key = is.readByte();
                        holder.set(x, y, z, palette.get(key));
                    }
                }
            }
            return holder;
        } catch (IOException e) {
            throw new RuntimeException("Error while reading CSD", e);
        }
    }
}
