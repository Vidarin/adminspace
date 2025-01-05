package com.vidarin.adminspace.dimension.deltaquest.generator;

import java.util.*;

public class NoiseGeneratorPerlinDQ extends NoiseGeneratorDQ
{
    private int[] permutations;
    public double xCoord;
    public double yCoord;
    public double zCoord;

    public NoiseGeneratorPerlinDQ() {
        this(new Random());
    }

    public NoiseGeneratorPerlinDQ(final Random random) {
        this.permutations = new int[512];
        this.xCoord = random.nextDouble() * 256.0;
        this.yCoord = random.nextDouble() * 256.0;
        this.zCoord = random.nextDouble() * 256.0;
        for (int var2 = 0; var2 < 256; this.permutations[var2] = var2++) {}
        for (int var3 = 0; var3 < 256; ++var3) {
            final int var4 = random.nextInt(256 - var3) + var3;
            final int var5 = this.permutations[var3];
            this.permutations[var3] = this.permutations[var4];
            this.permutations[var4] = var5;
            this.permutations[var3 + 256] = this.permutations[var3];
        }
    }
    
    public double generateNoise(final double px, final double py, final double pz) {
        double dx = px + this.xCoord;
        double dy = py + this.yCoord;
        double dz = pz + this.zCoord;
        int ix = (int)dx;
        int iy = (int)dy;
        int iz = (int)dz;
        if (dx < ix) {
            --ix;
        }
        if (dy < iy) {
            --iy;
        }
        if (dz < iz) {
            --iz;
        }
        final int var16 = ix & 0xFF;
        final int var17 = iy & 0xFF;
        final int var18 = iz & 0xFF;
        dx -= ix;
        dy -= iy;
        dz -= iz;
        final double var19 = dx * dx * dx * (dx * (dx * 6.0 - 15.0) + 10.0);
        final double var20 = dy * dy * dy * (dy * (dy * 6.0 - 15.0) + 10.0);
        final double var21 = dz * dz * dz * (dz * (dz * 6.0 - 15.0) + 10.0);
        final int var22 = this.permutations[var16] + var17;
        final int var23 = this.permutations[var22] + var18;
        final int var24 = this.permutations[var22 + 1] + var18;
        final int var25 = this.permutations[var16 + 1] + var17;
        final int var26 = this.permutations[var25] + var18;
        final int var27 = this.permutations[var25 + 1] + var18;
        return this.lerp(var21, this.lerp(var20, this.lerp(var19, this.grad(this.permutations[var23], dx, dy, dz), this.grad(this.permutations[var26], dx - 1.0, dy, dz)), this.lerp(var19, this.grad(this.permutations[var24], dx, dy - 1.0, dz), this.grad(this.permutations[var27], dx - 1.0, dy - 1.0, dz))), this.lerp(var20, this.lerp(var19, this.grad(this.permutations[var23 + 1], dx, dy, dz - 1.0), this.grad(this.permutations[var26 + 1], dx - 1.0, dy, dz - 1.0)), this.lerp(var19, this.grad(this.permutations[var24 + 1], dx, dy - 1.0, dz - 1.0), this.grad(this.permutations[var27 + 1], dx - 1.0, dy - 1.0, dz - 1.0))));
    }
    
    public double lerp(final double var1, final double var3, final double var5) {
        return var3 + var1 * (var5 - var3);
    }
    
    public double grad(final int var1, final double var2, final double var4, final double var6) {
        final int var7 = var1 & 0xF;
        final double var8 = (var7 < 8) ? var2 : var4;
        final double var9 = (var7 < 4) ? var4 : ((var7 != 12 && var7 != 14) ? var6 : var2);
        return (((var7 & 0x1) == 0x0) ? var8 : (-var8)) + (((var7 & 0x2) == 0x0) ? var9 : (-var9));
    }
    
    public double func_801_a(final double var1, final double var3) {
        return this.generateNoise(var1, var3, 0.0);
    }
    
    public void func_805_a(final double[] var1, final double var2, final double var4, final double var6, final int var8, final int var9, final int var10, final double var11, final double var13, final double var15, final double var17) {
        int var18 = 0;
        final double var19 = 1.0 / var17;
        int var20 = -1;
        int var21 = 0;
        int var22 = 0;
        int var23 = 0;
        int var24 = 0;
        int var25 = 0;
        int var26 = 0;
        double var27 = 0.0;
        double var28 = 0.0;
        double var29 = 0.0;
        double var30 = 0.0;
        for (int var31 = 0; var31 < var8; ++var31) {
            double var32 = (var2 + var31) * var11 + this.xCoord;
            int var33 = (int)var32;
            if (var32 < var33) {
                --var33;
            }
            final int var34 = var33 & 0xFF;
            var32 -= var33;
            final double var35 = var32 * var32 * var32 * (var32 * (var32 * 6.0 - 15.0) + 10.0);
            for (int var36 = 0; var36 < var10; ++var36) {
                double var37 = (var6 + var36) * var15 + this.zCoord;
                int var38 = (int)var37;
                if (var37 < var38) {
                    --var38;
                }
                final int var39 = var38 & 0xFF;
                var37 -= var38;
                final double var40 = var37 * var37 * var37 * (var37 * (var37 * 6.0 - 15.0) + 10.0);
                for (int var41 = 0; var41 < var9; ++var41) {
                    double var42 = (var4 + var41) * var13 + this.yCoord;
                    int var43 = (int)var42;
                    if (var42 < var43) {
                        --var43;
                    }
                    final int var44 = var43 & 0xFF;
                    var42 -= var43;
                    final double var45 = var42 * var42 * var42 * (var42 * (var42 * 6.0 - 15.0) + 10.0);
                    if (var41 == 0 || var44 != var20) {
                        var20 = var44;
                        var21 = this.permutations[var34] + var44;
                        var22 = this.permutations[var21] + var39;
                        var23 = this.permutations[var21 + 1] + var39;
                        var24 = this.permutations[var34 + 1] + var44;
                        var25 = this.permutations[var24] + var39;
                        var26 = this.permutations[var24 + 1] + var39;
                        var27 = this.lerp(var35, this.grad(this.permutations[var22], var32, var42, var37), this.grad(this.permutations[var25], var32 - 1.0, var42, var37));
                        var28 = this.lerp(var35, this.grad(this.permutations[var23], var32, var42 - 1.0, var37), this.grad(this.permutations[var26], var32 - 1.0, var42 - 1.0, var37));
                        var29 = this.lerp(var35, this.grad(this.permutations[var22 + 1], var32, var42, var37 - 1.0), this.grad(this.permutations[var25 + 1], var32 - 1.0, var42, var37 - 1.0));
                        var30 = this.lerp(var35, this.grad(this.permutations[var23 + 1], var32, var42 - 1.0, var37 - 1.0), this.grad(this.permutations[var26 + 1], var32 - 1.0, var42 - 1.0, var37 - 1.0));
                    }
                    final double var46 = this.lerp(var45, var27, var28);
                    final double var47 = this.lerp(var45, var29, var30);
                    final double var48 = this.lerp(var40, var46, var47);
                    final int n;
                    final int var49 = n = var18++;
                    var1[n] += var48 * var19;
                }
            }
        }
    }
}
