package com.vidarin.adminspace.dimension.deltaquest.generator;

import java.util.*;

public class NoiseGeneratorOctavesDQ extends NoiseGeneratorDQ
{
    private NoiseGeneratorPerlinDQ[] generatorCollection;
    private int field_1191_b;
    
    public NoiseGeneratorOctavesDQ(final Random random, final int var2) {
        this.field_1191_b = var2;
        this.generatorCollection = new NoiseGeneratorPerlinDQ[var2];
        for (int var3 = 0; var3 < var2; ++var3) {
            this.generatorCollection[var3] = new NoiseGeneratorPerlinDQ(random);
        }
    }

    public double generateTreeNoise(final double var1, final double var3) {
        double var4 = 0.0;
        double var5 = 1.0;
        for (int var6 = 0; var6 < this.field_1191_b; ++var6) {
            var4 += this.generatorCollection[var6].func_801_a(var1 * var5, var3 * var5) / var5;
            var5 /= 2.0;
        }
        return var4;
    }
    
    public double[] generateNoiseOctaves(double[] var1, final double var2, final double var4, final double var6, final int var8, final int var9, final int var10, final double var11, final double var13, final double var15) {
        if (var1 == null) {
            var1 = new double[var8 * var9 * var10];
        }
        else {
            for (int var16 = 0; var16 < var1.length; ++var16) {
                var1[var16] = 0.0;
            }
        }
        double var17 = 1.0;
        for (int var18 = 0; var18 < this.field_1191_b; ++var18) {
            this.generatorCollection[var18].func_805_a(var1, var2, var4, var6, var8, var9, var10, var11 * var17, var13 * var17, var15 * var17, var17);
            var17 /= 2.0;
        }
        return var1;
    }
}
