package com.vidarin.adminspace.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;

/**
 * 3D Array of any element. Can be rotated
 * @param <T> The type of this BlockHolder's element
 */
public class BlockHolder<T> {
    private int xSize, zSize;
    private final int ySize;

    private int xOff, zOff;
    private final int yOff;

    private final Object[][][] elements;

    /**
     * Constructs a new BlockHolder with the specified size and offsets
     */
    public BlockHolder(int xSize, int ySize, int zSize, int xOff, int yOff, int zOff) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        this.xOff = xOff;
        this.yOff = yOff;
        this.zOff = zOff;
        this.elements = new Object[xSize][ySize][zSize];
    }

    /**
     * Constructs a new BlockHolder with the specified size, and offsets 0
     */
    public BlockHolder(int xSize, int ySize, int zSize) {
        this(xSize, ySize, zSize, 0, 0 ,0);
    }

    /**
     * Creates a new BlockHolder of type T with the specified size and offsets, and fills it with the specified value.
     */
    public static <T> BlockHolder<T> of(Vec3i size, Vec3i offsets, T value) {
        return of(size.getX(), size.getY(), size.getZ(), offsets.getX(), offsets.getY(), offsets.getZ(), value);
    }

    /**
     * Creates a new BlockHolder of type T with the specified size, and fills it with the specified value.
     */
    public static <T> BlockHolder<T> of(int xSize, int ySize, int zSize, T value) {
        return of(xSize, ySize, zSize, 0, 0, 0, value);
    }

    /**
     * Creates a new BlockHolder of type T with the specified size and offsets, and fills it with the specified value.
     */
    public static <T> BlockHolder<T> of(int xSize, int ySize, int zSize, int xOff, int yOff, int zOff, T value) {
        return new BlockHolder<T>(xSize, ySize, zSize, xOff, yOff, zOff)
                .fill(xOff, yOff, zOff, xSize + xOff, ySize + yOff, zSize + zOff, value);
    }

    /**
     * Creates an identical copy of the specified BlockHolder.
     */
    public static <T> BlockHolder<T> copyOf(BlockHolder<T> holder) {
        BlockHolder<T> result = new BlockHolder<>(holder.xSize, holder.ySize, holder.zSize, holder.xOff, holder.yOff, holder.zOff);
        result.copyFrom(holder, holder.xOff, holder.yOff, holder.zOff);
        return result;
    }

    /**
     * @return The element at the specified position
     */
    @SuppressWarnings("unchecked")
    public T get(int x, int y, int z) {
        checkBounds(x, y, z);
        return (T) elements[x - xOff][y - yOff][z - zOff];
    }

    /**
     * @return The element at the specified position, or fallback if out of bounds or the gotten value is null
     */
    @SuppressWarnings("unchecked")
    public T getOrElse(int x, int y, int z, T fallback) {
        if (x < xOff || x >= xSize + xOff || y < yOff || y >= ySize + yOff || z < zOff || z >= zSize + zOff) return fallback;
        T result = (T) elements[x - xOff][y - yOff][z - zOff];
        return result == null ? fallback : result;
    }

    /**
     * Sets the value at the specified position to the specified value
     */
    public void set(int x, int y, int z, T value) {
        checkBounds(x, y, z);
        elements[x - xOff][y - yOff][z - zOff] = value;
    }

    private void checkBounds(int x, int y, int z) {
        if (x < xOff || x >= xSize + xOff || y < yOff || y >= ySize + yOff || z < zOff || z >= zSize + zOff) {
            throw new IndexOutOfBoundsException("Invalid position: (" + x + ", " + y + ", " + z + "), size is (" + xSize + ", " + ySize + ", " + zSize + "), offsets are (" + xOff + ", " + yOff + ", " + zOff + ")");
        }
    }

    /**
     * Copies all elements from the other BlockHolder into this one, starting at the specified position
     */
    public void copyFrom(BlockHolder<T> other, int xPos, int yPos, int zPos) {
        checkBounds(xPos, yPos, zPos);
        checkBounds(xPos + other.xSize - 1, yPos + other.ySize - 1, zPos + other.zSize - 1);
        for (int x = 0; x < other.xSize; x++) {
            for (int y = 0; y < other.ySize; y++) {
                System.arraycopy(other.elements[x][y], 0, this.elements[x - this.xOff + xPos][y - this.yOff + yPos], zPos - this.zOff, other.zSize);
            }
        }
    }

    public BlockHolder<T> fillDirs(int east, int up, int south, int west, int down, int north, T value) {
        return fill(east(east), up(up), south(south), west(west), down(down), north(north), value);
    }

    /**
     * Fills this BlockHolder from x1, y1, z1 (inclusive) to x2, y2, z2 (exclusive) with the specified value
     */
    public BlockHolder<T> fill(int x1, int y1, int z1, int x2, int y2, int z2, T value) {
        if (x1 > x2 || y1 > y2 || z1 > z2) return this;
        checkBounds(x1, y1, z1);
        checkBounds(x2 - 1, y2 - 1, z2 - 1);
        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                for (int z = z1; z < z2; z++) {
                    this.set(x, y, z, value);
                }
            }
        }
        return this;
    }

    /**
     * Rotates the entire cube so that the face that was facing NORTH now faces the target direction.
     * Only horizontal rotations are supported.
     */
    public void rotate(EnumFacing target) {
        if (target.getAxis().isVertical() || target == EnumFacing.NORTH) return;

        boolean flipXZ = target == EnumFacing.WEST || target == EnumFacing.EAST;

        BlockHolder<T> copy = flipXZ ? new BlockHolder<>(zSize, ySize, xSize, zOff, yOff, xOff) : new BlockHolder<>(xSize, ySize, zSize, xOff, yOff, zOff);

        int origXSize = xSize, origZSize = zSize;
        int origXOff = xOff, origZOff = zOff;

        if (flipXZ) {
            xSize = zSize;
            zSize = origXSize;
            xOff = zOff;
            zOff = origXOff;
        }

        for (int x = origXOff; x < origXSize + origXOff; x++) {
            for (int y = yOff; y < ySize + yOff; y++) {
                for (int z = origZOff; z < origZSize + origZOff; z++) {
                    T value = get(x, y, z);
                    int[] rotated = rotateCoords(x, z, target);
                    copy.set(rotated[0], y, rotated[1], value);
                }
            }
        }

        this.copyFrom(copy, xOff, yOff, zOff);
    }

    private int[] rotateCoords(int x, int z, EnumFacing target) {
        return switch (target) {
            case SOUTH -> new int[]{xSize - 1 - x, zSize - 1 - z}; // 180°
            case WEST -> new int[]{z, xSize - 1 - x}; // 270°
            case EAST -> new int[]{zSize - 1 - z, x}; // 90°
            default -> new int[]{x, z}; // 0°
        };
    }

    /**
     * Performs an operation for every element in this BlockHolder
     * @param operation A lambda that takes x, y, z and an element and does something with it
     */
    @SuppressWarnings("unchecked")
    public BlockHolder<T> forEach(BlockHolderOperation<T> operation) {
        for (int x = xOff; x < xSize + xOff; x++) {
            for (int y = yOff; y < ySize + yOff; y++) {
                for (int z = zOff; z < zSize + zOff; z++) {
                    operation.call(x, y, z, (T) elements[x - xOff][y - yOff][z - zOff]);
                }
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return "BlockHolder{" +
                "xSize=" + xSize +
                ", zSize=" + zSize +
                ", ySize=" + ySize +
                ", xOff=" + xOff +
                ", zOff=" + zOff +
                ", yOff=" + yOff +
                '}';
    }

    /** Towards positive Y */
    public int up(int i) { return yOff + i; }
    /** Towards negative Y */
    public int down(int i) { return yOff + ySize - i; }
    /** Towards negative Z */
    public int north(int i) { return zOff + zSize - i; }
    /** Towards positive Z */
    public int south(int i) { return zOff + i; }
    /** Towards positive X */
    public int east(int i) { return xOff + i; }
    /** Towards negative X */
    public int west(int i) { return xOff + xSize - i; }

    public int getXSize() { return xSize; }
    public int getYSize() { return ySize; }
    public int getZSize() { return zSize; }

    public int getXOff() { return xOff; }
    public int getYOff() { return yOff; }
    public int getZOff() { return zOff; }
}
