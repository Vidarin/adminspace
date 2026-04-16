package com.vidarin.adminspace.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class MathUtil {
    public static final BlockPos[] SQUARE_OFFSET_DIRECTIONS = {
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

    // Not to be confused with larp
    public static Vec3d lerp(Vec3d a, Vec3d b, float t) {
        double d1 = a.x * (1.0F - t) + b.x * t;
        double d2 = a.y * (1.0F - t) + b.y * t;
        double d3 = a.z * (1.0F - t) + b.z * t;
        return new Vec3d(d1, d2, d3);
    }

    public static IBlockState rotateBlockState(IBlockState state, EnumFacing target) {
        if (target.getAxis().isVertical() || target == EnumFacing.NORTH) return state;
        return state.withRotation(rotationFromFacing(target));
    }

    public static Rotation rotationFromFacing(EnumFacing facing) {
        return switch (facing) {
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.COUNTERCLOCKWISE_90;
            case EAST -> Rotation.CLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }

    public static int[] rotateCoords(int x, int z, EnumFacing target, int xSize, int zSize) {
        return switch (target) {
            case SOUTH -> new int[]{xSize - 1 - x, zSize - 1 - z}; // 180°
            case WEST -> new int[]{z, xSize - 1 - x}; // 270°
            case EAST -> new int[]{zSize - 1 - z, x}; // 90°
            default -> new int[]{x, z}; // 0°
        };
    }
}
