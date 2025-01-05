package com.vidarin.adminspace.dimension.deltaquest.generator;

public class MathHelperDQ
{
    private static float[] SIN_TABLE;
    
    public static final float sin(final float var0) {
        return MathHelperDQ.SIN_TABLE[(int)(var0 * 10430.378f) & 0xFFFF];
    }
    
    public static final float cos(final float var0) {
        return MathHelperDQ.SIN_TABLE[(int)(var0 * 10430.378f + 16384.0f) & 0xFFFF];
    }

    public static final float sqrt_float(final float var0) {
        return (float)Math.sqrt(var0);
    }

    public static final float sqrt_double(final double var0) {
        return (float)Math.sqrt(var0);
    }

    public static int floor_float(final float var0) {
        final int var = (int)var0;
        return (var0 < var) ? (var - 1) : var;
    }
    
    public static int floor_double(final double var0) {
        final int var = (int)var0;
        return (var0 < var) ? (var - 1) : var;
    }

    public static float abs(final float var0) {
        return (var0 >= 0.0f) ? var0 : (-var0);
    }

    public static double abs_max(double var0, double var2) {
        if (var0 < 0.0) {
            var0 = -var0;
        }
        if (var2 < 0.0) {
            var2 = -var2;
        }
        return (var0 > var2) ? var0 : var2;
    }

    public static int bucketInt(final int var0, final int var1) {
        return (var0 < 0) ? (-((-var0 - 1) / var1) - 1) : (var0 / var1);
    }
    
    static {
        MathHelperDQ.SIN_TABLE = new float[65536];
        for (int var0 = 0; var0 < 65536; ++var0) {
            MathHelperDQ.SIN_TABLE[var0] = (float)Math.sin(var0 * 3.141592653589793 * 2.0 / 65536.0);
        }
    }
}
