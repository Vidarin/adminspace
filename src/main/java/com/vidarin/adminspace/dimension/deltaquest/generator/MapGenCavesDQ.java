package com.vidarin.adminspace.dimension.deltaquest.generator;

import net.minecraft.world.chunk.*;
import java.util.*;
import net.minecraft.init.*;
import net.minecraft.block.*;
import net.minecraft.world.*;

public class MapGenCavesDQ extends MapGenBaseDQ
{
    protected void func_870_a(final int chunkX, final int chunkZ, final ChunkPrimer chunk, final double var4, final double var6, final double var8) {
        this.func_869_a(chunkX, chunkZ, chunk, var4, var6, var8, 1.0f + this.rand.nextFloat() * 6.0f, 0.0f, 0.0f, -1, -1, 0.5);
    }
    
    protected void func_869_a(final int chunkX, final int chunkZ, final ChunkPrimer chunk, double var4, double var6, double var8, final float var10, float var11, float var12, int var13, int var14, final double var15) {
        final double var16 = chunkX * 16 + 8;
        final double var17 = chunkZ * 16 + 8;
        float var18 = 0.0f;
        float var19 = 0.0f;
        final Random random = new Random(this.rand.nextLong());
        if (var14 <= 0) {
            final int var20 = this.field_1306_a * 16 - 16;
            var14 = var20 - random.nextInt(var20 / 4);
        }
        boolean var21 = false;
        if (var13 == -1) {
            var13 = var14 / 2;
            var21 = true;
        }
        final int var22 = random.nextInt(var14 / 2) + var14 / 4;
        final boolean var23 = random.nextInt(6) == 0;
        while (var13 < var14) {
            final double var24 = 1.5 + MathHelperDQ.sin(var13 * 3.1415927f / var14) * var10 * 1.0f;
            final double var25 = var24 * var15;
            final float var26 = MathHelperDQ.cos(var12);
            final float var27 = MathHelperDQ.sin(var12);
            var4 += MathHelperDQ.cos(var11) * var26;
            var6 += var27;
            var8 += MathHelperDQ.sin(var11) * var26;
            if (var23) {
                var12 *= 0.92f;
            }
            else {
                var12 *= 0.7f;
            }
            var12 += var19 * 0.1f;
            var11 += var18 * 0.1f;
            var19 *= 0.9f;
            var18 *= 0.75f;
            var19 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f;
            var18 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f;
            if (!var21 && var13 == var22 && var10 > 1.0f) {
                this.func_869_a(chunkX, chunkZ, chunk, var4, var6, var8, random.nextFloat() * 0.5f + 0.5f, var11 - 1.5707964f, var12 / 3.0f, var13, var14, 1.0);
                this.func_869_a(chunkX, chunkZ, chunk, var4, var6, var8, random.nextFloat() * 0.5f + 0.5f, var11 + 1.5707964f, var12 / 3.0f, var13, var14, 1.0);
                return;
            }
            if (var21 || random.nextInt(4) != 0) {
                final double var28 = var4 - var16;
                final double var29 = var8 - var17;
                final double var30 = var14 - var13;
                final double var31 = var10 + 2.0f + 16.0f;
                if (var28 * var28 + var29 * var29 - var30 * var30 > var31 * var31) {
                    return;
                }
                if (var4 >= var16 - 16.0 - var24 * 2.0 && var8 >= var17 - 16.0 - var24 * 2.0 && var4 <= var16 + 16.0 + var24 * 2.0 && var8 <= var17 + 16.0 + var24 * 2.0) {
                    int var32 = MathHelperDQ.floor_double(var4 - var24) - chunkX * 16 - 1;
                    int var33 = MathHelperDQ.floor_double(var4 + var24) - chunkX * 16 + 1;
                    int var34 = MathHelperDQ.floor_double(var6 - var25) - 1;
                    int y = MathHelperDQ.floor_double(var6 + var25) + 1;
                    int var35 = MathHelperDQ.floor_double(var8 - var24) - chunkZ * 16 - 1;
                    int var36 = MathHelperDQ.floor_double(var8 + var24) - chunkZ * 16 + 1;
                    if (var32 < 0) {
                        var32 = 0;
                    }
                    if (var33 > 16) {
                        var33 = 16;
                    }
                    if (var34 < 1) {
                        var34 = 1;
                    }
                    if (y > 120) {
                        y = 120;
                    }
                    if (var35 < 0) {
                        var35 = 0;
                    }
                    if (var36 > 16) {
                        var36 = 16;
                    }
                    boolean var37 = false;
                    for (int x = var32; !var37 && x < var33; ++x) {
                        for (int z = var35; !var37 && z < var36; ++z) {
                            for (int y2 = y + 1; !var37 && y2 >= var34 - 1; --y2) {
                                if (y2 >= 0 && y2 < 128) {
                                    final Block block = chunk.getBlockState(x, y2, z).getBlock();
                                    if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                                        var37 = true;
                                    }
                                    if (y2 != var34 - 1 && x != var32 && x != var33 - 1 && z != var35 && z != var36 - 1) {
                                        y2 = var34;
                                    }
                                }
                            }
                        }
                    }
                    if (!var37) {
                        for (int x = var32; x < var33; ++x) {
                            final double var38 = (x + chunkX * 16 + 0.5 - var4) / var24;
                            for (int z2 = var35; z2 < var36; ++z2) {
                                final double var39 = (z2 + chunkZ * 16 + 0.5 - var8) / var24;
                                int y3 = y;
                                boolean var40 = false;
                                for (int var41 = y - 1; var41 >= var34; --var41) {
                                    final double var42 = (var41 + 0.5 - var6) / var25;
                                    if (var42 > -0.7 && var38 * var38 + var42 * var42 + var39 * var39 < 1.0) {
                                        final Block block2 = chunk.getBlockState(x, y3, z2).getBlock();
                                        if (block2 == Blocks.GRASS) {
                                            var40 = true;
                                        }
                                        if (block2 == Blocks.STONE || block2 == Blocks.DIRT || block2 == Blocks.GRASS) {
                                            if (var41 < 10) {
                                                chunk.setBlockState(x, y3, z2, Blocks.LAVA.getDefaultState());
                                            }
                                            else {
                                                chunk.setBlockState(x, y3, z2, Blocks.AIR.getDefaultState());
                                                if (var40 && chunk.getBlockState(x, y3 - 1, z2).getBlock() == Blocks.DIRT) {
                                                    chunk.setBlockState(x, y3 - 1, z2, Blocks.GRASS.getDefaultState());
                                                }
                                            }
                                        }
                                    }
                                    --y3;
                                }
                            }
                        }
                        if (var21) {
                            break;
                        }
                    }
                }
            }
            ++var13;
        }
    }
    
    protected void func_868_a(final World world, final int x, final int z, final int chunkX, final int chunkZ, final ChunkPrimer chunk) {
        int var7 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(40) + 1) + 1);
        if (this.rand.nextInt(15) != 0) {
            var7 = 0;
        }
        for (int var8 = 0; var8 < var7; ++var8) {
            final double var9 = x * 16 + this.rand.nextInt(16);
            final double var10 = this.rand.nextInt(this.rand.nextInt(120) + 8);
            final double var11 = z * 16 + this.rand.nextInt(16);
            int var12 = 1;
            if (this.rand.nextInt(4) == 0) {
                this.func_870_a(chunkX, chunkZ, chunk, var9, var10, var11);
                var12 += this.rand.nextInt(4);
            }
            for (int var13 = 0; var13 < var12; ++var13) {
                final float var14 = this.rand.nextFloat() * 3.1415927f * 2.0f;
                final float var15 = (this.rand.nextFloat() - 0.5f) * 2.0f / 8.0f;
                final float var16 = this.rand.nextFloat() * 2.0f + this.rand.nextFloat();
                this.func_869_a(chunkX, chunkZ, chunk, var9, var10, var11, var16, var14, var15, 0, 0, 1.0);
            }
        }
    }
}
