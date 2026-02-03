package com.vidarin.adminspace.util;

import net.minecraft.util.EnumFacing;

public class BlockHolder<T> {
    private final int xSize, ySize, zSize;
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

    public void set(int x, int y, int z, T value) {
        checkBounds(x, y, z);
        blocks[x][y][z] = value;
    }

    private void checkBounds(int x, int y, int z) {
        if (x < 0 || x >= xSize || y < 0 || y >= ySize || z < 0 || z >= zSize) {
            throw new IndexOutOfBoundsException("Invalid position: (" + x + ", " + y + ", " + z + ")");
        }
    }

    /**
     * Rotates the entire cube so that the face that was facing NORTH now faces the target direction.
     * Only horizontal rotations are supported.
     */
    public void rotate(EnumFacing target) {
        if (target.getAxis().isVertical() || target == EnumFacing.NORTH) return;

        BlockHolder<T> copy = new BlockHolder<>(xSize, ySize, zSize);

        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                for (int z = 0; z < zSize; z++) {
                    T value = get(x, y, z);
                    int[] rotated = rotateCoords(x, z, target);
                    copy.set(rotated[0], y, rotated[1], value);
                }
            }
        }

        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                if (zSize >= 0) System.arraycopy(copy.blocks[x][y], 0, this.blocks[x][y], 0, zSize);
            }
        }
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
