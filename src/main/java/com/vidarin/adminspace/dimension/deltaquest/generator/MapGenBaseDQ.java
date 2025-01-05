package com.vidarin.adminspace.dimension.deltaquest.generator;

import java.util.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;

public class MapGenBaseDQ
{
    protected int field_1306_a;
    protected Random rand;
    
    public MapGenBaseDQ() {
        this.field_1306_a = 8;
        this.rand = new Random();
    }
    
    public void generate(final World world, final int chunkX, final int chunkZ, final ChunkPrimer chunk) {
        final int var6 = this.field_1306_a;
        this.rand.setSeed(world.getSeed());
        final long var7 = this.rand.nextLong() / 2L * 2L + 1L;
        final long var8 = this.rand.nextLong() / 2L * 2L + 1L;
        for (int x = chunkX - var6; x <= chunkX + var6; ++x) {
            for (int z = chunkZ - var6; z <= chunkZ + var6; ++z) {
                this.rand.setSeed(x * var7 + z * var8 ^ world.getSeed());
                this.func_868_a(world, x, z, chunkX, chunkZ, chunk);
            }
        }
    }
    
    protected void func_868_a(final World world, final int x, final int z, final int chunkX, final int chunkZ, final ChunkPrimer chunk) {
    }
}
