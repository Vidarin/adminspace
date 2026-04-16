package com.vidarin.adminspace.worldgen.genblock;

import com.github.bsideup.jabel.Desugar;
import com.vidarin.adminspace.util.CubePos;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Desugar
public record Cube(@NotNull CubePos from, @NotNull CubePos to) {
    public Cube(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        this(new CubePos(fromX, fromY, fromZ), new CubePos(toX, toY, toZ));
    }

    @Contract(pure = true)
    public @NotNull Cube shrunkCube(double xRatio, double zRatio) {
        return subCube(1 - xRatio, 0, 1 - zRatio, xRatio, 1, zRatio);
    }

    @Contract(pure = true)
    public @NotNull Cube subCube(double x1Ratio, double y1Ratio, double z1Ratio, double x2Ratio, double y2Ratio, double z2Ratio) {
        if (x1Ratio < 0 || x1Ratio > 1 || y1Ratio < 0 || y1Ratio > 1 || z1Ratio < 0 || z1Ratio > 1 ||
                x2Ratio < 0 || x2Ratio > 1 || y2Ratio < 0 || y2Ratio > 1 || z2Ratio < 0 || z2Ratio > 1)
            throw new IllegalArgumentException("Ratio must be between 0 and 1!");

        if (x1Ratio > x2Ratio || y1Ratio > y2Ratio || z1Ratio > z2Ratio) throw new IllegalArgumentException("Cannot create negative space");

        int x1 = (int) Math.floor((double) sizeX() * x1Ratio) + from.getX();
        int y1 = (int) Math.floor((double) sizeY() * y1Ratio) + from.getY();
        int z1 = (int) Math.floor((double) sizeZ() * z1Ratio) + from.getZ();
        int x2 = (int) Math.ceil((double) sizeX() * x2Ratio) + from.getX();
        int y2 = (int) Math.ceil((double) sizeY() * y2Ratio) + from.getY();
        int z2 = (int) Math.ceil((double) sizeZ() * z2Ratio) + from.getZ();
        return new Cube(new CubePos(x1, y1, z1), new CubePos(x2, y2, z2));
    }

    @Contract(pure = true)
    public @NotNull Cube indent(int x1, int y1, int z1, int x2, int y2, int z2) {
        return new Cube(this.from.add(x1, y1, z1), this.to.subtract(x2, y2, z2));
    }

    @Contract(pure = true)
    public @NotNull Cube indentFrom(int x, int y, int z) {
        return new Cube(this.from.add(x, y, z), this.to);
    }

    @Contract(pure = true)
    public @NotNull Cube indentTo(int x, int y, int z) {
        return new Cube(this.from, this.to.subtract(x, y, z));
    }

    @Contract(pure = true)
    public @NotNull Cube move(int x, int y, int z) {
        return indent(x, y, z, -x, -y, -z);
    }

    public @NotNull CubePos size() {
        return new CubePos(sizeX(), sizeY(), sizeZ());
    }

    public int sizeX() {
        return to.getX() - from.getX();
    }

    public int sizeY() {
        return to.getY() - from.getY();
    }

    public int sizeZ() {
        return to.getZ() - from.getZ();
    }

    public boolean intersects(Cube other) {
        return contains(other.from) || contains(other.to);
    }

    public boolean contains(Vec3i pos) {
        return pos.getX() >= from.getX() &&
                pos.getY() >= from.getY() &&
                pos.getZ() >= from.getZ() &&
                pos.getX() < to.getX() &&
                pos.getY() < to.getY() &&
                pos.getZ() < to.getZ();
    }

    public static @NotNull Cube fromSizeAndOffset(Vec3i size, Vec3i offset) {
        return new Cube(new CubePos(offset), new CubePos(offset.getX() + size.getX(), offset.getY() + size.getY(), offset.getZ() + size.getZ()));
    }
}
