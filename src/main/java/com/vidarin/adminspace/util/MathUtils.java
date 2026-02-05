package com.vidarin.adminspace.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class MathUtils {
    public static final BlockPos[] DIRECTIONS = {
            new BlockPos(1, 0, 0), new BlockPos(1, 0, 1),
            new BlockPos(0, 0, 1), new BlockPos(-1, 0, 1),
            new BlockPos(-1, 0, 0), new BlockPos(-1, 0, -1),
            new BlockPos(0, 0, -1), new BlockPos(1, 0, -1)
    };

    public static int spiralIndex(Vec2i vec) {
        int x = vec.x;
        int y = vec.y;

        if (vec.equals(Vec2i.ZERO)) return 0;

        int layer = Math.max(Math.abs(x), Math.abs(y));
        int layerStart = (2 * layer - 1) * (2 * layer - 1);
        int edgeLength = 2 * layer;

        int dx = x + layer;
        int dy = y + layer;

        int index;

        if (y == -layer) {
            index = dx;
        } else if (x == layer) {
            index = edgeLength + dy;
        } else if (y == layer) {
            index = 2 * edgeLength + (edgeLength - dx);
        } else { // x == -layer
            index = 3 * edgeLength + (edgeLength - dy);
        }

        return layerStart + index;
    }

    public static Vec3d lerp(Vec3d a, Vec3d b, float t) {
        double d1 = a.x * (1.0F - t) + b.x * t;
        double d2 = a.y * (1.0F - t) + b.y * t;
        double d3 = a.z * (1.0F - t) + b.z * t;
        return new Vec3d(d1, d2, d3);
    }
}
