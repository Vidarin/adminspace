package com.vidarin.adminspace.dimension.skysector.generator;

import com.github.bsideup.jabel.Desugar;
import com.vidarin.adminspace.data.ReadOnlyCompactStructureData;
import com.vidarin.adminspace.util.MathUtil;
import com.vidarin.adminspace.util.blockholder.BlockHolder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

/// Contains helper methods for adding details and structures to the sky sector dimension
@SuppressWarnings("unused")
public final class SkySectorStructures {

    /* BOX STRUCTURES */

    public static final Structure TERMINAL_BALL = new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/box_terminal", o).rotate(EnumFacing.byHorizontalIndex(r.nextInt(4))), StructureType.BOX, 128, 151, 128);

    /* OUTSIDE STRUCTURES */

    public static final Structure OUTSIDE_1 = new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_1", o).rotate(EnumFacing.byHorizontalIndex(r.nextInt(4))), StructureType.OUTSIDE, 9, 10, 9);
    public static final Structure OUTSIDE_2Z = new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_2", o).rotate(r.nextBoolean() ? EnumFacing.NORTH : EnumFacing.SOUTH), StructureType.OUTSIDE, 9, 10, 18);
    public static final Structure OUTSIDE_2X = new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_2", o).rotate(r.nextBoolean() ? EnumFacing.WEST : EnumFacing.EAST), StructureType.OUTSIDE, 18, 10, 9);
    public static final Structure OUTSIDE_3Z = new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_3", o).rotate(r.nextBoolean() ? EnumFacing.WEST : EnumFacing.EAST), StructureType.OUTSIDE, 3, 3, 7);
    public static final Structure OUTSIDE_3X = new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_3", o).rotate(r.nextBoolean() ? EnumFacing.NORTH : EnumFacing.SOUTH), StructureType.OUTSIDE, 7, 3, 3);
    public static final Structure OUTSIDE_4Z = new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_4", o).rotate(r.nextBoolean() ? EnumFacing.NORTH : EnumFacing.SOUTH, MathUtil::rotateBlockState), StructureType.OUTSIDE, 7, 6, 16);
    public static final Structure OUTSIDE_4X = new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_4", o).rotate(r.nextBoolean() ? EnumFacing.WEST : EnumFacing.EAST, MathUtil::rotateBlockState), StructureType.OUTSIDE, 16, 6, 7);
    public static final Structure OUTSIDE_5 = new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_5", o).rotate(EnumFacing.byHorizontalIndex(r.nextInt(4))), StructureType.OUTSIDE, 9, 4, 9);
    public static final Structure OUTSIDE_6Z = new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_6", o).rotate(r.nextBoolean() ? EnumFacing.NORTH : EnumFacing.SOUTH), StructureType.OUTSIDE, 10, 7, 24);
    public static final Structure OUTSIDE_6X = new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_6", o).rotate(r.nextBoolean() ? EnumFacing.WEST : EnumFacing.EAST), StructureType.OUTSIDE, 24, 7, 10);
    public static final Structure OUTSIDE_7 = new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_7", o).rotate(EnumFacing.byHorizontalIndex(r.nextInt(4))), StructureType.OUTSIDE, 7, 7, 7);
    public static final Structure OUTSIDE_8Z = new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_8", o).rotate(r.nextBoolean() ? EnumFacing.NORTH : EnumFacing.SOUTH), StructureType.OUTSIDE, 11, 9, 13);
    public static final Structure OUTSIDE_8X = new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_8", o).rotate(r.nextBoolean() ? EnumFacing.WEST : EnumFacing.EAST), StructureType.OUTSIDE, 13, 9, 11);
    public static final Structure OUTSIDE_9Z = new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_9", o).rotate(r.nextBoolean() ? EnumFacing.NORTH : EnumFacing.SOUTH, MathUtil::rotateBlockState), StructureType.OUTSIDE, 7, 8, 28);
    public static final Structure OUTSIDE_9X = new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_9", o).rotate(r.nextBoolean() ? EnumFacing.WEST : EnumFacing.EAST, MathUtil::rotateBlockState), StructureType.OUTSIDE, 28, 8, 7);
    public static final Structure OUTSIDE_10 = new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_10", o).rotate(EnumFacing.byHorizontalIndex(r.nextInt(4)), MathUtil::rotateBlockState), StructureType.OUTSIDE, 14, 8, 14);
    public static final Structure OUTSIDE_11X = new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_11", o).rotate(r.nextBoolean() ? EnumFacing.NORTH : EnumFacing.SOUTH, MathUtil::rotateBlockState), StructureType.OUTSIDE, 12, 6, 7);
    public static final Structure OUTSIDE_11Z = new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_11", o).rotate(r.nextBoolean() ? EnumFacing.WEST : EnumFacing.EAST, MathUtil::rotateBlockState), StructureType.OUTSIDE, 7, 6, 12);
    public static final Structure OUTSIDE_12 = new Structure(
            (o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_12", o).rotate(EnumFacing.byHorizontalIndex(r.nextInt(4))),
            StructureType.OUTSIDE, 5, 25, 5
    );
    public static final Structure OUTSIDE_13X = new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_13", o).rotate(r.nextBoolean() ? EnumFacing.NORTH : EnumFacing.SOUTH, MathUtil::rotateBlockState), StructureType.OUTSIDE, 17, 10, 12);
    public static final Structure OUTSIDE_13Z = new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_13", o).rotate(r.nextBoolean() ? EnumFacing.WEST : EnumFacing.EAST, MathUtil::rotateBlockState), StructureType.OUTSIDE, 12, 10, 17);
    public static final Structure OUTSIDE_14X = new Structure(
            (o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_14", o).rotate(r.nextBoolean() ? EnumFacing.NORTH : EnumFacing.SOUTH, MathUtil::rotateBlockState),
            StructureType.OUTSIDE, 9, 6, 13
    );
    public static final Structure OUTSIDE_14Z = new Structure(
            (o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/outside_14", o).rotate(r.nextBoolean() ? EnumFacing.WEST : EnumFacing.EAST, MathUtil::rotateBlockState),
            StructureType.OUTSIDE, 13, 6, 9
    );

    /* FUNCTIONS */

    public static BlockHolder<IBlockState> getBestBoxStructure(int xSize, int ySize, int zSize, Vec3i offset, Random rand) {
        Structure best = null;
        List<Structure> list = new ArrayList<>(Structure.BOX_STRUCTURES);
        Collections.shuffle(list, rand);
        for (Structure structure : list) {
            if (structure.xSize <= xSize && structure.zSize <= zSize && (best == null || best.xSize * best.zSize < structure.xSize * structure.zSize))
                best = structure;
        }

        if (best == null) return BlockHolder.of(xSize, ySize, zSize, offset.getX(), offset.getY(), offset.getZ(), Blocks.AIR.getDefaultState());
         else if (xSize != best.xSize || zSize != best.zSize) return new BlockHolder<IBlockState>(xSize, ySize, zSize, offset.getX(), offset.getY(), offset.getZ()).copyFrom(
                        best.structureGenerator.apply(offset, rand),
                        Math.max((xSize - best.xSize) / 2, 0) + offset.getX(),
                        offset.getY(),
                        Math.max((zSize - best.zSize) / 2, 0) + offset.getZ()
                );
        else return best.structureGenerator.apply(offset, rand);
    }

    public static BlockHolder<IBlockState> getBestLargeOutsideStructure(int xSize, int ySize, int zSize, Vec3i offset, Random rand) {
        Structure best = null;
        List<Structure> list = new ArrayList<>(Structure.OUTSIDE_STRUCTURES);
        Collections.shuffle(list, rand);
        for (Structure structure : list) {
            if (structure.xSize <= xSize && structure.zSize <= zSize && (best == null || best.xSize * best.zSize < structure.xSize * structure.zSize))
                best = structure;
        }

        if (best == null) return BlockHolder.of(xSize, ySize, zSize, offset.getX(), offset.getY(), offset.getZ(), Blocks.AIR.getDefaultState());
        else if (xSize != best.xSize || zSize != best.zSize) return new BlockHolder<IBlockState>(xSize, ySize, zSize, offset.getX(), offset.getY(), offset.getZ()).copyFrom(
                best.structureGenerator.apply(offset, rand),
                (xSize > best.xSize ? rand.nextInt(xSize - best.xSize) : 0) + offset.getX(),
                offset.getY(),
                (zSize > best.zSize ? rand.nextInt(zSize - best.zSize) : 0) + offset.getZ()
        );
        else return best.structureGenerator.apply(offset, rand);
    }

    public enum StructureType {
        BOX, OUTSIDE
    }

    @Desugar
    public record Structure(BiFunction<Vec3i, Random, BlockHolder<IBlockState>> structureGenerator, StructureType type, int xSize, int ySize, int zSize) {
        public static final List<Structure> BOX_STRUCTURES = new ArrayList<>();
        public static final List<Structure> OUTSIDE_STRUCTURES = new ArrayList<>();

        public Structure(BiFunction<Vec3i, Random, BlockHolder<IBlockState>> structureGenerator, StructureType type, int xSize, int ySize, int zSize) {
            this.structureGenerator = structureGenerator;
            this.type = type;
            this.xSize = xSize;
            this.ySize = ySize;
            this.zSize = zSize;
            if (type == StructureType.BOX) BOX_STRUCTURES.add(this);
            else if (type == StructureType.OUTSIDE) OUTSIDE_STRUCTURES.add(this);
        }
    }
}
