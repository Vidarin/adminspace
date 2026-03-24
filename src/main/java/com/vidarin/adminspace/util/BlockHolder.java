package com.vidarin.adminspace.util;

import net.minecraft.util.EnumFacing;

public class BlockHolder<T> {
    private int xSize, zSize;
    private final int ySize;
    private final Object[][][] blocks;

    public BlockHolder(int xSize, int ySize, int zSize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        this.blocks = new Object[xSize][ySize][zSize];
    }

    @SuppressWarnings("unchecked")
    public T get(int x, int y, int z) {
        checkBounds(x, y, z);
        return (T) blocks[x][y][z];
    }

    @SuppressWarnings("unchecked")
    public T getOrElse(int x, int y, int z, T fallback) {
        if (x < 0 || x >= xSize || y < 0 || y >= ySize || z < 0 || z >= zSize) return fallback;
        else return (T) blocks[x][y][z];
    }

    public void set(int x, int y, int z, T value) {
        checkBounds(x, y, z);
        blocks[x][y][z] = value;
    }

    private void checkBounds(int x, int y, int z) {
        if (x < 0 || x >= xSize || y < 0 || y >= ySize || z < 0 || z >= zSize) {
            throw new IndexOutOfBoundsException("Invalid position: (" + x + ", " + y + ", " + z + ")");
        }
    }

    public void copy(BlockHolder<T> other, int xOff, int yOff, int zOff) {
        for (int x = 0; x < other.getXSize(); x++) {
            for (int y = 0; y < other.getYSize(); y++) {
                System.arraycopy(other.blocks[x][y], 0, this.blocks[x + xOff][y + yOff], zOff, other.zSize);
            }
        }
    }

    /**
     * Rotates the entire cube so that the face that was facing NORTH now faces the target direction.
     * Only horizontal rotations are supported.
     */
    public void rotate(EnumFacing target) {
        if (target.getAxis().isVertical() || target == EnumFacing.NORTH) return;

        boolean flipXZ = target == EnumFacing.WEST || target == EnumFacing.EAST;

        BlockHolder<T> copy = flipXZ ? new BlockHolder<>(zSize, ySize, xSize) : new BlockHolder<>(xSize, ySize, zSize);

        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                for (int z = 0; z < zSize; z++) {
                    T value = get(x, y, z);
                    int[] rotated = rotateCoords(x, z, target);
                    copy.set(rotated[0], y, rotated[1], value);
                }
            }
        }

        if (flipXZ) {
            int temp = xSize;
            xSize = zSize;
            zSize = temp;
        }

        this.copy(copy, 0, 0, 0);
    }

    private int[] rotateCoords(int x, int z, EnumFacing target) {
        return switch (target) {
            case SOUTH -> new int[]{xSize - 1 - x, zSize - 1 - z}; // 180°
            case WEST -> new int[]{z, xSize - 1 - x}; // 270°
            case EAST -> new int[]{zSize - 1 - z, x}; // 90°
            default -> new int[]{x, z}; // 0°
        };
    }

    public int getXSize() { return xSize; }
    public int getYSize() { return ySize; }
    public int getZSize() { return zSize; }
}
