package com.vidarin.adminspace.dimension.skysector.generator;

import com.github.bsideup.jabel.Desugar;
import com.vidarin.adminspace.data.ReadOnlyCompactStructureData;
import com.vidarin.adminspace.util.blockholder.BlockHolder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class SkySectorStructures {
    public static final Structure TERMINAL_BALL = new Structure("sky_box_terminal", StructureType.BOX, 128, 151, 128);

    public static BlockHolder<IBlockState> getBestBoxStructure(int xSize, int ySize, int zSize, Vec3i offset) {
        Structure best = null;
        for (Structure structure : Structure.BOX_STRUCTURES) {
            if (structure.xSize <= xSize && structure.zSize <= zSize && (best == null || best.xSize * best.zSize < structure.xSize * structure.zSize))
                best = structure;
        }

        if (best == null) return BlockHolder.of(xSize, ySize, zSize, offset.getX(), offset.getY(), offset.getZ(), Blocks.AIR.getDefaultState());
         else if (xSize != best.xSize || zSize != best.zSize) return new BlockHolder<IBlockState>(xSize, ySize, zSize, offset.getX(), offset.getY(), offset.getZ()).copyFrom(
                        ReadOnlyCompactStructureData.readCSDStructure("skysector/" + best.location + ".csd", offset),
                        Math.max((xSize - best.xSize) / 2, 0) + offset.getX(),
                        offset.getY(),
                        Math.max((zSize - best.zSize) / 2, 0) + offset.getZ()
                );
        else return ReadOnlyCompactStructureData.readCSDStructure("skysector/" + best.location + ".csd", offset);
    }

    public enum StructureType {
        BOX,
    }

    @Desugar
    public record Structure(String location, StructureType type, int xSize, int ySize, int zSize) {
        public static final List<Structure> BOX_STRUCTURES = new ArrayList<>();

        public Structure(String location, StructureType type, int xSize, int ySize, int zSize) {
            this.location = location;
            this.type = type;
            this.xSize = xSize;
            this.ySize = ySize;
            this.zSize = zSize;
            if (type == StructureType.BOX) BOX_STRUCTURES.add(this);
        }
    }
}
