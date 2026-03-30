package com.vidarin.adminspace.dimension.skysector.generator;

import com.vidarin.adminspace.util.Vec2i;
import com.vidarin.adminspace.worldgen.genblock.GenBlock;
import net.minecraft.block.state.IBlockState;

public class SkyInfo { // 3200x3200 blocks large (200x200 chunks)
    public final Vec2i position;
    public final int id;
    public SkyState state;
    public GenBlock<IBlockState> genBlock;

    public SkyInfo(Vec2i position, SkyState state, GenBlock<IBlockState> genBlock) {
        this.position = position;
        this.id = (position.x * 8) + position.y;
        this.state = state;
        this.genBlock = genBlock;
    }

    protected SkyInfo(Vec2i position, SkyState state, int id, GenBlock<IBlockState> genBlock) {
        this.position = position;
        this.id = id;
        this.state = state;
        this.genBlock = genBlock;
    }

    public enum SkyState {
        OPERATIONAL, ABANDONED, DANGEROUS, DESTROYED, ABSENT
    }
}
