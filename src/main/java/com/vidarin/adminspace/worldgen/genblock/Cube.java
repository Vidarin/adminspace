package com.vidarin.adminspace.worldgen.genblock;

import com.github.bsideup.jabel.Desugar;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;

@Desugar
public record Cube(@NotNull Vec3i from, @NotNull Vec3i to) {
    public @NotNull Cube shrunkCube(double xRatio, double yRatio, double zRatio) {
        return subCube(1 - (xRatio / 2), 1 - (yRatio / 2), 1 - (zRatio / 2), 1 - (xRatio / 2), 1 - (yRatio / 2), 1 - (zRatio / 2));
    }

    public @NotNull Cube subCube(double x1Ratio, double y1Ratio, double z1Ratio, double x2Ratio, double y2Ratio, double z2Ratio) {
        if (x1Ratio < 0 || x1Ratio > 1 || y1Ratio < 0 || y1Ratio > 1 || z1Ratio < 0 || z1Ratio > 1 ||
                x2Ratio < 0 || x2Ratio > 1 || y2Ratio < 0 || y2Ratio > 1 || z2Ratio < 0 || z2Ratio > 1)
            throw new IllegalArgumentException("Ratio must be between 0 and 1!");

        if (1 - (x1Ratio) > x2Ratio || 1 - (y1Ratio) > y2Ratio || 1 - (z1Ratio) > z2Ratio) throw new IllegalArgumentException("Cannot create negative space");

        int x1 = (int) Math.floor((double) sizeX() * (1.0 - x1Ratio)) + from.getX();
        int y1 = (int) Math.floor((double) sizeY() * (1.0 - y1Ratio)) + from.getY();
        int z1 = (int) Math.floor((double) sizeZ() * (1.0 - z1Ratio)) + from.getZ();
        int x2 = (int) Math.ceil((double) sizeX() * x2Ratio) + from.getX();
        int y2 = (int) Math.ceil((double) sizeY() * y2Ratio) + from.getY();
        int z2 = (int) Math.ceil((double) sizeZ() * z2Ratio) + from.getZ();
        return new Cube(new Vec3i(x1, y1, z1), new Vec3i(x2, y2, z2));
    }

    public Vec3i size() {
        return new Vec3i(sizeX(), sizeY(), sizeZ());
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
}
