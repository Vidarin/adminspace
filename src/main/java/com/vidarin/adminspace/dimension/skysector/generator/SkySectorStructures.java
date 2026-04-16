package com.vidarin.adminspace.dimension.skysector.generator;

import com.github.bsideup.jabel.Desugar;
import com.vidarin.adminspace.data.ReadOnlyCompactStructureData;
import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.util.CubePos;
import com.vidarin.adminspace.util.FastNoiseLite;
import com.vidarin.adminspace.util.MathUtil;
import com.vidarin.adminspace.util.WildcardMap;
import com.vidarin.adminspace.util.blockholder.BlockHolder;
import com.vidarin.adminspace.worldgen.genblock.Cube;
import com.vidarin.adminspace.worldgen.genblock.GenBlock;
import com.vidarin.adminspace.worldgen.genblock.GenBlockRuleSet;
import com.vidarin.adminspace.worldgen.genblock.GenBlockRuleSuccessorFunc;
import com.vidarin.adminspace.worldgen.grammar.Rule;
import com.vidarin.adminspace.worldgen.grammar.Shape;
import com.vidarin.adminspace.worldgen.grammar.Symbol;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;

/// Contains helper methods for adding details and structures to the sky sector dimension
public final class SkySectorStructures {
    /* STRUCTURE REGISTRY */

    static {
        Structure.registerBoxStructure("terminal", Rotation.ANY, true, 128, 151, 128);

        Structure.registerOutsideStructure(1, Rotation.ANY, false, 9, 10, 9, false);
        Structure.registerOutsideStructure(2, Rotation.XZ, false, 9, 10, 18, false);
        Structure.registerOutsideStructure(3, Rotation.XZ, false, 7, 3, 3, false);
        Structure.registerOutsideStructure(4, Rotation.XZ, true, 7, 6, 16, false);
        Structure.registerOutsideStructure(5, Rotation.ANY, false, 9, 4, 9, false);
        Structure.registerOutsideStructure(6, Rotation.XZ, false, 10, 7, 24, false);
        Structure.registerOutsideStructure(7, Rotation.ANY, false, 7, 7, 7, false);
        Structure.registerOutsideStructure(8, Rotation.XZ, false, 11, 9, 13, false);
        Structure.registerOutsideStructure(9, Rotation.XZ, true, 7, 8, 28, false);
        Structure.registerOutsideStructure(10, Rotation.ANY, true, 14, 8, 14, false);
        Structure.registerOutsideStructure(11, Rotation.XZ, true, 12, 6, 7, true);
        Structure.registerOutsideStructure(12, Rotation.ANY, false, 5, 25, 5, false);
        Structure.registerOutsideStructure(13, Rotation.XZ, true, 17, 10, 12, false);
        Structure.registerOutsideStructure(14, Rotation.XZ, true, 9, 6, 13, false);
    }

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
                        offset.getX() + Math.max((xSize - best.xSize) / 2, 0),
                        offset.getY(),
                        offset.getZ() + Math.max((zSize - best.zSize) / 2, 0)
                );
        else return best.structureGenerator.apply(offset, rand);
    }

    public static BlockHolder<IBlockState> chooseBestOutsideStructure(Vec3i size, Vec3i offset, Random rand) {
        Structure best = null;
        List<Structure> list = new ArrayList<>(Structure.OUTSIDE_STRUCTURES);
        Collections.shuffle(list, rand);
        for (Structure structure : list) {
            if (structure.xSize <= size.getX() && structure.ySize <= size.getY() && structure.zSize <= size.getZ() &&
                    (best == null || best.xSize * best.ySize * best.zSize < structure.xSize * structure.ySize * structure.zSize))
                best = structure;
        }

        if (best == null) return BlockHolder.of(size, offset, Blocks.AIR.getDefaultState());
        else if (size.getX() != best.xSize || size.getZ() != best.zSize) return new BlockHolder<IBlockState>(size, offset).copyFrom(
                best.structureGenerator.apply(offset, rand),
                (size.getX() > best.xSize ? rand.nextInt(size.getX() - best.xSize) : 0) + offset.getX(),
                offset.getY(),
                (size.getZ() > best.zSize ? rand.nextInt(size.getZ() - best.zSize) : 0) + offset.getZ()
        );
        else return best.structureGenerator.apply(offset, rand);
    }

    public static @Nullable BlockHolder<IBlockState> chooseFoundationReplacement(Vec3i size, Vec3i offset, Random rand) {
        Structure best = null;
        List<Structure> list = new ArrayList<>(Structure.FOUNDATION_REPLACEMENTS);
        Collections.shuffle(list, rand);
        for (Structure structure : list) {
            if (structure.xSize <= size.getX() && structure.ySize <= size.getY() && structure.zSize <= size.getZ() &&
                    (best == null || best.xSize * best.zSize < structure.xSize * structure.zSize))
                best = structure;
        }

        if (best == null) return null;
        else if (size.getX() != best.xSize || size.getZ() != best.zSize) return BlockHolder.of(size, offset, BlockInit.voidTile.getDefaultState()).copyFrom(
                best.structureGenerator.apply(offset, rand), offset.getX(), offset.getY(), offset.getZ()
        );
        else return best.structureGenerator.apply(offset, rand);
    }

    public static BlockHolder<IBlockState> createLargeOutsideStructure(Vec3i size, Vec3i offset, Random rand, boolean elevated) {
        GenBlock<IBlockState> structureGen = new GenBlock<>(new Shape<>(OutsideStructureGenBlockDefinition.Symbols.Structure, Cube.fromSizeAndOffset(size, offset), new int[]{elevated ? 1 : 0}), rand);
        structureGen.applyRules(OutsideStructureGenBlockDefinition.RULES, Blocks.AIR.getDefaultState(), 20);
        return structureGen.extractAll();
    }

    public enum Rotation { ANY, X, Z, XZ }

    @Desugar
    public record Structure(BiFunction<Vec3i, Random, BlockHolder<IBlockState>> structureGenerator, int xSize, int ySize, int zSize) {
        public static final List<Structure> BOX_STRUCTURES = new ArrayList<>();
        public static final List<Structure> OUTSIDE_STRUCTURES = new ArrayList<>();
        public static final List<Structure> FOUNDATION_REPLACEMENTS = new ArrayList<>();

        private static void registerOutsideStructure(int id, Rotation rotation, boolean rotateBlocks, int xSize, int ySize, int zSize, boolean canReplaceFoundations) {
            if (canReplaceFoundations && ySize != 4 && ySize != 6) throw new IllegalArgumentException("ySize must be 4 or 6 to replace foundations");
            Structure[] structures = registerStructure(String.valueOf(id), rotation, rotateBlocks, xSize, ySize, zSize, OUTSIDE_STRUCTURES, "outside");
            if (canReplaceFoundations) FOUNDATION_REPLACEMENTS.addAll(Arrays.asList(structures));
        }

        private static void registerBoxStructure(String id, Rotation rotation, boolean rotateBlocks, int xSize, int ySize, int zSize) {
            registerStructure(id, rotation, rotateBlocks, xSize, ySize, zSize, BOX_STRUCTURES, "box");
        }

        private static Structure[] registerStructure(String id, Rotation rotation, boolean rotateBlocks, int xSize, int ySize, int zSize, List<Structure> list, String name) {
            if (rotation == Rotation.XZ) {
                return new Structure[]{
                        registerStructure(id, Rotation.Z, rotateBlocks, xSize, ySize, zSize, list, name)[0],
                        registerStructure(id, Rotation.X, rotateBlocks, zSize, ySize, xSize, list, name)[0]
                };
            } else if (rotateBlocks) {
                Structure s = switch (rotation) {
                    case ANY -> new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/" + name + "_" + id, o).rotate(EnumFacing.byHorizontalIndex(r.nextInt(4)), MathUtil::rotateBlockState), xSize, ySize, zSize);
                    case X -> new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/" + name + "_" + id, o).rotate(r.nextBoolean() ? EnumFacing.WEST : EnumFacing.EAST, MathUtil::rotateBlockState), xSize, ySize, zSize);
                    case Z -> new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/" + name + "_" + id, o).rotate(r.nextBoolean() ? EnumFacing.NORTH : EnumFacing.SOUTH, MathUtil::rotateBlockState), xSize, ySize, zSize);
                    default -> throw new IllegalStateException("Unexpected value: " + rotation);
                };
                list.add(s);
                return new Structure[]{s};
            } else {
                Structure s = switch (rotation) {
                    case ANY -> new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/" + name + "_" + id, o).rotate(EnumFacing.byHorizontalIndex(r.nextInt(4))), xSize, ySize, zSize);
                    case X -> new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/" + name + "_" + id, o).rotate(r.nextBoolean() ? EnumFacing.WEST : EnumFacing.EAST), xSize, ySize, zSize);
                    case Z -> new Structure((o, r) -> ReadOnlyCompactStructureData.readCSDStructure("skysector/" + name + "_" + id, o).rotate(r.nextBoolean() ? EnumFacing.NORTH : EnumFacing.SOUTH), xSize, ySize, zSize);
                    default -> throw new IllegalStateException("Unexpected value: " + rotation);
                };
                list.add(s);
                return new Structure[]{s};
            }
        }
    }

    public enum OutsideStructureGenBlockDefinition implements Rule<Cube, Pair<BlockHolder<IBlockState>, Cube>> {
        SplitStructureHorizontal(Symbols.Structure, (shape, rand, globals) -> {
            Cube cube = shape.shape();
            if (cube.sizeX() < 7 || cube.sizeZ() < 7 || rand.nextFloat() < 0.15) return finalizeStructure(shape, rand);
            int diff = Math.abs(cube.sizeX() - cube.sizeZ());
            boolean axis = cube.sizeX() > cube.sizeZ();
            if (diff > 1 && rand.nextInt((int) Math.sqrt(diff)) == 0) axis = !axis;
            double pos = (rand.nextDouble() * 0.4) + 0.3;
            int gap = (int) Math.round(Math.sqrt(rand.nextDouble() * 25));
            if (gap == 0 || (axis ? cube.sizeX() < 16 : cube.sizeZ() < 16)) {
                return Arrays.asList(
                        new Shape<>(Symbols.Structure, Pair.of(null, cube.subCube(0, 0, 0, axis ? pos : 1, 1, axis ? 1 : pos)), shape.meta()),
                        new Shape<>(Symbols.Structure, Pair.of(null, cube.subCube(axis ? pos : 0, 0, axis ? 0 : pos, 1, 1, 1)), shape.meta())
                );
            } else {
                Cube cube1 = cube.subCube(0, 0, 0, axis ? pos : 1, 1, axis ? 1 : pos).indentTo(axis ? gap : 0, 0, axis ? 0 : gap);
                CubePos cube2from = axis ? cube1.from().setX(cube1.to()) : cube1.from().setZ(cube1.to());
                Cube cube2 = new Cube(cube2from, cube2from.add(axis ? gap : cube1.sizeX(), cube1.sizeY(), axis ? cube1.sizeZ() : gap));
                Cube cube3 = cube.subCube(axis ? pos : 0, 0, axis ? 0 : pos, 1, 1, 1).indentFrom(axis ? gap : 0, 0, axis ? 0 : gap);
                return Arrays.asList(
                        new Shape<>(Symbols.Structure, Pair.of(null, cube1), shape.meta()),
                        new Shape<>(Symbols.FinalStructure, Pair.of(BlockHolder.of(cube2.size(), cube2.from(), Blocks.AIR.getDefaultState()), cube2), shape.meta()),
                        new Shape<>(Symbols.Structure, Pair.of(null, cube3), shape.meta())
                );
            }
        }, 5),

        @SuppressWarnings("SuspiciousNameCombination")
        SplitStructureVertical(Symbols.Structure, (shape, rand, globals) -> {
            Cube cube = shape.shape();
            int height = 4 + 2 * shape.meta()[0]; // 4 tall if normal, 6 tall if elevated
            if (cube.sizeY() <= height + 1) return finalizeStructure(shape, rand);

            int topYOff = Math.max(Math.min(rand.nextInt(cube.sizeY() - height), rand.nextInt(cube.sizeY() - height)) - 1, 0);

            Cube bottomCube = new Cube(cube.from(), cube.to().setY(cube.from().getY() + height));
            Cube topCube = cube.indent(0, height, 0, 0, topYOff, 0)
                    .shrunkCube((rand.nextDouble() * 0.25) + 0.75, (rand.nextDouble() * 0.25) + 0.75);

            if (rand.nextFloat() < 0.75) {
                int diffX = topCube.from().getX() - bottomCube.from().getX();
                int diffZ = topCube.from().getZ() - bottomCube.from().getZ();
                if (diffX > 0 && diffZ > 0)
                    topCube = topCube.move(rand.nextInt(diffX * 2) - diffX, 0, rand.nextInt(diffZ * 2) - diffZ);
            }

            return Arrays.asList(
                    new Shape<>(Symbols.Foundation, Pair.of(null, bottomCube), shape.meta()),
                    new Shape<>(Symbols.Structure, Pair.of(null, topCube), new int[]{0})
            );
        }, 1),

        SplitFoundation(Symbols.Foundation, (shape, rand, globals) -> {
            Cube cube = shape.shape();
            if (cube.sizeX() < 8 || cube.sizeZ() < 8 || rand.nextFloat() < 0.2) return finalizeFoundation(shape, rand);
            int diff = Math.abs(cube.sizeX() - cube.sizeZ());
            boolean axis = cube.sizeX() > cube.sizeZ();
            if (diff > 1 && rand.nextInt((int) Math.sqrt(diff)) == 0) axis = !axis;
            double pos = (rand.nextDouble() * 0.4) + 0.3;

            return Arrays.asList(
                    new Shape<>(Symbols.Foundation, Pair.of(null, cube.subCube(0, 0, 0, axis ? pos : 1, 1, axis ? 1 : pos).indentTo(axis ? -1 : 0, 0, axis ? 0 : -1)), shape.meta()),
                    new Shape<>(Symbols.Foundation, Pair.of(null, cube.subCube(axis ? pos : 0, 0, axis ? 0 : pos, 1, 1, 1)), shape.meta())
            );
        }, 1),
        ;

        private static @NotNull Iterable<Shape<Pair<BlockHolder<IBlockState>, Cube>>> finalizeFoundation(Shape<Cube> shape, Random rand) {
            Cube cube = shape.shape();
            int elevatedHeightDiff = 2 * shape.meta()[0];
            boolean elevated = shape.meta()[0] == 1;
            if (rand.nextFloat() < 0.2) {
                BlockHolder<IBlockState> structure = chooseFoundationReplacement(cube.size(), cube.from(), rand);
                if (structure != null) return Collections.singleton(new Shape<>(Symbols.FinalStructure, Pair.of(structure, cube), shape.meta()));
            }
            BlockHolder<IBlockState> holder = BlockHolder.of(cube.size(), cube.from(), BlockInit.voidTile.getDefaultState());
            if (rand.nextFloat() < 0.7) {
                holder.set(holder.east(0), holder.down(1), holder.south(0), BlockInit.voidLamp.getDefaultState());
                holder.set(holder.west(1), holder.down(1), holder.south(0), BlockInit.voidLamp.getDefaultState());
                holder.set(holder.east(0), holder.down(1), holder.north(1), BlockInit.voidLamp.getDefaultState());
                holder.set(holder.west(1), holder.down(1), holder.north(1), BlockInit.voidLamp.getDefaultState());
            }
            if (rand.nextFloat() > 0.2 && cube.sizeX() >= 5 && cube.sizeZ() >= 5 && cube.sizeY() >= 4 + elevatedHeightDiff) {
                holder.fillDirs(1, 0, 1, 1, 1, 1, Blocks.AIR.getDefaultState());
                for (int i = 3; i < rand.nextInt((int) (Math.sqrt(cube.sizeX() * cube.sizeZ()))) + 3; i++) {
                    switch (rand.nextInt(9)) {
                        case 0, 1, 2 -> { // Small door
                           EnumFacing side = EnumFacing.byHorizontalIndex(rand.nextInt(4));
                           int posX = side.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE ? 0 : cube.sizeX() - 1;
                           int posZ = side.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE ? 0 : cube.sizeZ() - 1;
                           boolean zAxis = side.getAxis() == EnumFacing.Axis.Z;
                           if (zAxis) posX = rand.nextInt(cube.sizeX() - 1) + 1;
                           else posZ = rand.nextInt(cube.sizeZ() - 1) + 1;
                           if (elevated) {
                               holder.set(holder.east(posX), holder.up(1 + elevatedHeightDiff), holder.south(posZ), Blocks.AIR.getDefaultState());
                               holder.set(holder.east(posX), holder.up(2 + elevatedHeightDiff), holder.south(posZ), Blocks.AIR.getDefaultState());
                               EnumFacing ladderSide = side.getOpposite();
                               holder.fill(
                                       holder.east(posX + ladderSide.getXOffset()), holder.up(0), holder.south(posZ + ladderSide.getZOffset()),
                                       holder.east(posX + ladderSide.getXOffset() * (zAxis ? 1 : 2) + (zAxis ? 1 : 0)), holder.up(1 + elevatedHeightDiff), holder.south(posZ + ladderSide.getZOffset() * (zAxis ? 2 : 1) + (zAxis ? 0 : 1)),
                                       BlockInit.voidLadder.getDefaultState().withProperty(BlockLadder.FACING, ladderSide)
                               );
                           } else {
                               holder.set(holder.east(posX), holder.up(elevatedHeightDiff), holder.south(posZ), Blocks.AIR.getDefaultState());
                               holder.set(holder.east(posX), holder.up(1 + elevatedHeightDiff), holder.south(posZ), Blocks.AIR.getDefaultState());
                           }
                        }
                        case 3, 4 -> { // Large door
                            EnumFacing side = EnumFacing.byHorizontalIndex(rand.nextInt(4));
                            int posX = side.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE ? 0 : cube.sizeX() - 1;
                            int posZ = side.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE ? 0 : cube.sizeZ() - 1;
                            boolean zAxis = side.getAxis() == EnumFacing.Axis.Z;
                            if (zAxis) posX = rand.nextInt(cube.sizeX() - 4) + 1;
                            else posZ = rand.nextInt(cube.sizeZ() - 4) + 1;
                            holder.fill(holder.east(posX), holder.up(elevatedHeightDiff), holder.south(posZ), holder.east(posX + (zAxis ? 3 : 1)), holder.up(elevated ? 5 : 3), holder.south(posZ + (zAxis ? 1 : 3)), Blocks.AIR.getDefaultState());
                            if (elevated) {
                                EnumFacing stairSide = side.getOpposite();
                                holder.fill(
                                        holder.east(posX + stairSide.getXOffset()), holder.up(1), holder.south(posZ + stairSide.getZOffset()),
                                        holder.east(posX + stairSide.getXOffset() * (zAxis ? 1 : 2) + (zAxis ? 3 : 0)), holder.up(2), holder.south(posZ + stairSide.getZOffset() * (zAxis ? 2 : 1) + (zAxis ? 0 : 3)),
                                        BlockInit.voidTile.getDefaultState()
                                );
                                holder.fill(
                                        holder.east(posX + stairSide.getXOffset()), holder.up(0), holder.south(posZ + stairSide.getZOffset()),
                                        holder.east(posX + stairSide.getXOffset() * (zAxis ? 2 : 3) + (zAxis ? 3 : 0)), holder.up(1), holder.south(posZ + stairSide.getZOffset() * (zAxis ? 3 : 2) + (zAxis ? 0 : 3)),
                                        BlockInit.voidTile.getDefaultState()
                                );
                            }
                        }
                        case 5, 6 -> { // Large window
                            EnumFacing side = EnumFacing.byHorizontalIndex(rand.nextInt(4));
                            int posX = side.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE ? 0 : cube.sizeX() - 1;
                            int posZ = side.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE ? 0 : cube.sizeZ() - 1;
                            boolean xAxis = side.getAxis() == EnumFacing.Axis.X;
                            if (xAxis) posX = rand.nextInt(cube.sizeX() - 4) + 1;
                            else posZ = rand.nextInt(cube.sizeZ() - 4) + 1;
                            holder.fill(holder.east(posX), holder.up(1), holder.south(posZ), holder.east(posX + (xAxis ? 3 : 1)), holder.up(2 + elevatedHeightDiff), holder.south(posZ + (xAxis ? 1 : 3)), BlockInit.voidGlass.getDefaultState());
                        }
                        case 7 -> { // Staircase / Ladder
                            int posX = rand.nextInt(cube.sizeX() - 4) + 1;
                            int posZ = rand.nextInt(cube.sizeZ() - 4) + 1;
                            if (elevated) {
                                holder.fill(holder.east(posX), holder.up(5), holder.south(posZ), holder.east(posX + 3), holder.up(6), holder.south(posZ + 2), Blocks.AIR.getDefaultState());
                                holder.fill(holder.east(posX + 1), holder.up(0), holder.south(posZ + 1), holder.east(posX + 2), holder.up(6), holder.south(posZ + 2), BlockInit.voidTile.getDefaultState());
                                holder.fill(holder.east(posX + 1), holder.up(0), holder.south(posZ), holder.east(posX + 2), holder.up(6), holder.south(posZ + 1), BlockInit.voidLadder.getDefaultState());
                            } else {
                                boolean axis = rand.nextBoolean();
                                int zOff1 = axis ? 1 : 0, xOff1 = axis ? 0 : 1;
                                int zOff2 = axis ? 2 : 0, xOff2 = axis ? 0 : 2;
                                int xOff3 = axis ? 3 : 1, zOff3 = axis ? 1 : 3;
                                holder.fill(holder.east(posX), holder.up(0), holder.south(posZ), holder.east(posX + xOff3), holder.up(1), holder.south(posZ + zOff3), BlockInit.voidTile.getDefaultState());
                                holder.fill(holder.east(posX + xOff1), holder.up(1), holder.south(posZ + zOff1), holder.east(posX + xOff3 + xOff1), holder.up(2), holder.south(posZ + zOff3 + zOff1), BlockInit.voidTile.getDefaultState());
                                holder.fill(holder.east(posX + xOff2), holder.up(2), holder.south(posZ + zOff2), holder.east(posX + xOff3 + xOff2), holder.up(3), holder.south(posZ + zOff3 + zOff2), BlockInit.voidTile.getDefaultState());
                                holder.fill(holder.east(posX), holder.up(3), holder.south(posZ), holder.east(posX + 3), holder.up(4), holder.south(posZ + 3), Blocks.AIR.getDefaultState());
                            }
                        }
                        case 8 -> { // Wall lights
                            EnumFacing side = EnumFacing.byHorizontalIndex(rand.nextInt(4));
                            int posX = side.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE ? 0 : cube.sizeX() - 1;
                            int posZ = side.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE ? 0 : cube.sizeZ() - 1;
                            boolean xAxis = side.getAxis() == EnumFacing.Axis.X;
                            int length = rand.nextInt((xAxis ? cube.sizeX() : cube.sizeZ()) - 3) + 2;
                            if (xAxis) posX = rand.nextInt(cube.sizeX() - length - 1) + 1;
                            else posZ = rand.nextInt(cube.sizeZ() - length - 1) + 1;
                            holder.fill(holder.east(posX), holder.up(1), holder.south(posZ), holder.east(posX + (xAxis ? length : 1)), holder.up(2), holder.south(posZ + (xAxis ? 1 : length)), BlockInit.voidLamp.getDefaultState());
                            if (elevated) {
                                holder.fill(holder.east(posX), holder.up(3), holder.south(posZ), holder.east(posX + (xAxis ? length : 1)), holder.up(4), holder.south(posZ + (xAxis ? 1 : length)), BlockInit.voidLamp.getDefaultState());
                                holder.fill(holder.east(posX), holder.up(5), holder.south(posZ), holder.east(posX + (xAxis ? length : 1)), holder.up(6), holder.south(posZ + (xAxis ? 1 : length)), BlockInit.voidLamp.getDefaultState());
                            }
                        }
                    }
                }
            }
            if (rand.nextFloat() < 0.6 && cube.sizeZ() > 2 && cube.sizeX() > 1) {
                BlockHolder<IBlockState> ridgeHolder1 = BlockHolder
                        .of(cube.size().getX(), 1, 1, cube.from().getX(), cube.to().getY(), cube.from().getZ(), BlockInit.voidLamp.getDefaultState())
                        .fillDirs(1, 0, 0, 1, 0, 0, BlockInit.voidTile.getDefaultState());
                Cube ridgeCube1 = new Cube(cube.from().getX(), cube.to().getY(), cube.from().getZ(), cube.to().getX(), cube.to().getY() + 1, cube.from().getZ() + 1);

                BlockHolder<IBlockState> ridgeHolder2 = BlockHolder
                        .of(cube.size().getX(), 1, 1, cube.from().getX(), cube.to().getY(), cube.to().getZ() - 1, BlockInit.voidLamp.getDefaultState())
                        .fillDirs(1, 0, 0, 1, 0, 0, BlockInit.voidTile.getDefaultState());
                Cube ridgeCube2 = new Cube(cube.from().getX(), cube.to().getY(), cube.to().getZ() - 1, cube.to().getX(), cube.to().getY() + 1, cube.to().getZ());

                BlockHolder<IBlockState> ridgeHolder3 = BlockHolder
                        .of(1, 1, cube.sizeZ() - 2, cube.from().getX(), cube.to().getY(), cube.from().getZ() + 1, BlockInit.voidTile.getDefaultState());
                Cube ridgeCube3 = new Cube(cube.from().getX(), cube.to().getY(), cube.from().getZ() + 1, cube.from().getX() + 1, cube.to().getY() + 1, cube.to().getZ() - 1);

                BlockHolder<IBlockState> ridgeHolder4 = BlockHolder
                        .of(1, 1, cube.sizeZ() - 2, cube.to().getX() - 1, cube.to().getY(), cube.from().getZ() + 1, BlockInit.voidTile.getDefaultState());
                Cube ridgeCube4 = new Cube(cube.to().getX() - 1, cube.to().getY(), cube.from().getZ() + 1, cube.to().getX(), cube.to().getY() + 1, cube.to().getZ() - 1);

                return Arrays.asList(
                        new Shape<>(Symbols.FinalStructure, Pair.of(holder, cube), shape.meta()),
                        new Shape<>(Symbols.FinalStructure, Pair.of(ridgeHolder1, ridgeCube1), shape.meta()),
                        new Shape<>(Symbols.FinalStructure, Pair.of(ridgeHolder2, ridgeCube2), shape.meta()),
                        new Shape<>(Symbols.FinalStructure, Pair.of(ridgeHolder3, ridgeCube3), shape.meta()),
                        new Shape<>(Symbols.FinalStructure, Pair.of(ridgeHolder4, ridgeCube4), shape.meta())
                );
            }
            return Collections.singleton(new Shape<>(Symbols.FinalStructure, Pair.of(holder, cube), shape.meta()));
        }

        public static @NotNull Iterable<Shape<Pair<BlockHolder<IBlockState>, Cube>>> finalizeStructure(Shape<Cube> shape, Random rand) {
            Cube cube = shape.shape();
            if (rand.nextFloat() < 0.08 || cube.sizeX() <= 2 || cube.sizeZ() <= 2)
                return Collections.singleton(new Shape<>(Symbols.FinalStructure, Pair.of(BlockHolder.of(new CubePos(1, 1, 1), cube.from(), Blocks.AIR.getDefaultState()), cube), shape.meta()));
            if (cube.sizeX() < 16 && cube.sizeZ() < 16 && rand.nextFloat() - 0.5 > noiseGen.GetNoise(cube.from().getX(), cube.from().getZ())) {
                BlockHolder<IBlockState> holder = BlockHolder.of(cube.size().subtract(2, 0, 2), cube.from().add(1, 0, 1), BlockInit.voidTile.getDefaultState());
                holder.set(holder.east(0), holder.down(1), holder.south(0), BlockInit.voidLamp.getDefaultState());
                holder.set(holder.west(1), holder.down(1), holder.south(0), BlockInit.voidLamp.getDefaultState());
                holder.set(holder.east(0), holder.down(1), holder.north(1), BlockInit.voidLamp.getDefaultState());
                holder.set(holder.west(1), holder.down(1), holder.north(1), BlockInit.voidLamp.getDefaultState());
                return Collections.singleton(new Shape<>(Symbols.FinalStructure, Pair.of(holder, cube.indent(1, 0, 1, 1, 0, 1)), shape.meta()));
            }

            BlockHolder<IBlockState> structure = chooseBestOutsideStructure(cube.size().subtract(2, 0, 2), cube.from().add(1, 0, 1), rand);
            return Collections.singleton(new Shape<>(Symbols.FinalStructure, Pair.of(structure, new Cube(cube.from().add(1, 0, 1), cube.to().setY(cube.from().getY() + structure.getYSize()))), shape.meta()));
        }

        private static final FastNoiseLite noiseGen;

        static {
            noiseGen = new FastNoiseLite(152);
            noiseGen.SetFrequency(0.004F);
        }

        public static final GenBlockRuleSet<IBlockState> RULES = new GenBlockRuleSet<>(Arrays.asList(values()));

        public enum Symbols implements Symbol {
            Structure(0, false),
            Foundation(1, false),
            FinalStructure(2, true);

            private final int id;
            private final boolean terminal;

            Symbols(int id, boolean terminal) {
                this.id = id;
                this.terminal = terminal;
            }

            @Override public int identifier() { return id; }
            @Override public boolean isTerminal() { return terminal; }
        }

        private final Symbol predecessor;
        private final GenBlockRuleSuccessorFunc successorFunc;
        private final int weight;

        OutsideStructureGenBlockDefinition(Symbol predecessor, GenBlockRuleSuccessorFunc successorFunc, int weight) {
            this.predecessor = predecessor;
            this.successorFunc = successorFunc;
            this.weight = weight;
        }

        @Override public Symbol predecessor() { return predecessor; }
        @Override public Iterable<Shape<Pair<BlockHolder<IBlockState>, Cube>>> successor(Shape<Cube> predecessor, Random rand, WildcardMap<String> globals) { return successorFunc.successor(predecessor, rand, globals); }
        @Override public int weight() { return weight; }
    }
}
