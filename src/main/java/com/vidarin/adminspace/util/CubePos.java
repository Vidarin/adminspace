package com.vidarin.adminspace.util;

import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;

public class CubePos extends Vec3i {
    public CubePos(int xIn, int yIn, int zIn) {
        super(xIn, yIn, zIn);
    }

    public CubePos(Vec3i vec) {
        super(vec.getX(), vec.getY(), vec.getZ());
    }

    public CubePos add(int x, int y, int z) {
        return new CubePos(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    public CubePos subtract(int x, int y, int z) {
        return add(-x, -y, -z);
    }

    public CubePos setX(int x) { return new CubePos(x, this.getY(), this.getZ()); }
    public CubePos setY(int y) { return new CubePos(this.getX(), y, this.getZ()); }
    public CubePos setZ(int z) { return new CubePos(this.getX(), this.getY(), z); }
    public CubePos setX(@NotNull Vec3i other) { return setX(other.getX()); }
    public CubePos setY(@NotNull Vec3i other) { return setY(other.getY()); }
    public CubePos setZ(@NotNull Vec3i other) { return setZ(other.getZ()); }
}
