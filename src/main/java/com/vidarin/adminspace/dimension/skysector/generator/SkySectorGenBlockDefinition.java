package com.vidarin.adminspace.dimension.skysector.generator;

import com.vidarin.adminspace.init.BlockInit;
import com.vidarin.adminspace.util.FastNoiseLite;
import com.vidarin.adminspace.util.blockholder.BlockHolder;
import com.vidarin.adminspace.util.blockholder.BlockHolderSequence;
import com.vidarin.adminspace.worldgen.genblock.Cube;
import com.vidarin.adminspace.worldgen.genblock.GenBlockRuleSet;
import com.vidarin.adminspace.worldgen.grammar.Rule;
import com.vidarin.adminspace.worldgen.grammar.Shape;
import com.vidarin.adminspace.worldgen.grammar.Symbol;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.function.BiFunction;

public enum SkySectorGenBlockDefinition implements Rule<Cube, Pair<BlockHolder<IBlockState>, Cube>> {
    /*
    * METADATA DEFINITION:
    * [0]: SectorInfo.ActivityLevel index
    * [1]: SkyIndo.SkyState index
    * [2]: Layer -> Height level
    *      Tunnel -> Layer height level
    *      OutsideSector -> Use elevated walkways (1) or normal ones (0)
    *      Walkway -> Become elevated (1) or not (0)
    */

    /* INITIAL HANDLING */

    /// Splits one EntryPoint into 4 IndeterminateSectors
    SplitEntryPoint4(Symbols.EntryPoint, (shape, rand) -> {
        double xPos = (rand.nextDouble() / 2) + 0.25, zPos = (rand.nextDouble() / 2) + 0.25;
        Cube cube = shape.shape();
        Cube c1 = cube.subCube(0, 0, 0, xPos, 1, zPos).indentFrom(0, 1, 0);
        Cube c2 = cube.subCube(xPos, 0, 0, 1, 1, zPos).indentFrom(0, 1, 0);
        Cube c3 = cube.subCube(0, 0, zPos, xPos, 1, 1).indentFrom(0, 1, 0);
        Cube c4 = cube.subCube(xPos, 0, zPos, 1, 1, 1).indentFrom(0, 1, 0);
        return Arrays.asList(
                new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c1), shape.meta()), new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c2), shape.meta()),
                new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c3), shape.meta()), new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c4), shape.meta()),
                new Shape<>(Symbols.SolidWall, Pair.of(
                        BlockHolder.of(cube.size().setY(1), cube.from(), BlockInit.voidTile.getDefaultState()),
                        new Cube(cube.from(), cube.to().setY(cube.from().getY() + 1))), shape.meta())
        );
    }, 1),

    /// Splits one EntryPoint into 9 IndeterminateSectors
    SplitEntryPoint9(Symbols.EntryPoint, (shape, rand) -> {
        double x1Pos = (rand.nextDouble() / 3) + 0.16, x2Pos = (rand.nextDouble() / 3) + 0.51;
        double z1Pos = (rand.nextDouble() / 3) + 0.16, z2Pos = (rand.nextDouble() / 3) + 0.51;
        Cube cube = shape.shape();
        Cube c1 = cube.subCube(0, 0, 0, x1Pos, 1, z1Pos).indentFrom(0, 1, 0);
        Cube c2 = cube.subCube(x1Pos, 0, 0, x2Pos, 1, z1Pos).indentFrom(0, 1, 0);
        Cube c3 = cube.subCube(x2Pos, 0, 0, 1, 1, z1Pos).indentFrom(0, 1, 0);
        Cube c4 = cube.subCube(0, 0, z1Pos, x1Pos, 1, z2Pos).indentFrom(0, 1, 0);
        Cube c5 = cube.subCube(x1Pos, 0, z1Pos, x2Pos, 1, z2Pos).indentFrom(0, 1, 0);
        Cube c6 = cube.subCube(x2Pos, 0, z1Pos, 1, 1, z2Pos).indentFrom(0, 1, 0);
        Cube c7 = cube.subCube(0, 0, z2Pos, x1Pos, 1, 1).indentFrom(0, 1, 0);
        Cube c8 = cube.subCube(x1Pos, 0, z2Pos, x2Pos, 1, 1).indentFrom(0, 1, 0);
        Cube c9 = cube.subCube(x2Pos, 0, z2Pos, 1, 1, 1).indentFrom(0, 1, 0);
        return Arrays.asList(
                new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c1), shape.meta()), new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c2), shape.meta()),
                new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c3), shape.meta()), new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c4), shape.meta()),
                new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c5), shape.meta()), new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c6), shape.meta()),
                new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c7), shape.meta()), new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c8), shape.meta()),
                new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c9), shape.meta()), new Shape<>(Symbols.SolidWall, Pair.of(
                        BlockHolder.of(cube.size().setY(1), cube.from(), BlockInit.voidTile.getDefaultState()),
                        new Cube(cube.from(), cube.to().setY(cube.from().getY() + 1))), shape.meta())
        );
    }, 2),

    /// Converts an IndeterminateSector to a InsideSector or OutsideSector, with higher activity skies having more inside sectors
    ChooseIndeterminateSectorType(Symbols.IndeterminateSector, (shape, rand) -> {
        boolean outside = rand.nextInt(8) + 3 - MathHelper.clamp(shape.meta()[0], 1, 6) > 5;
        return Collections.singleton(new Shape<>(
                        outside ? Symbols.OutsideSector : Symbols.InsideSector,
                        Pair.of(null, shape.shape()), outside ?
                new int[]{shape.meta()[0], shape.meta()[1], rand.nextFloat() < 0.25 ? 1 : 0} : shape.meta()
        ));
    }, 1),

    /* OUTSIDE SECTOR HANDLING */

    /// Splits an OutsideSector into 2 smaller OutsideSectors separated by a straight Walkway. If the sector is small enough, it is turned into a SmallStructuresSector or a LargeStructureSector
    SplitOutsideSector2(Symbols.OutsideSector, (shape, rand) -> split2(shape, rand, false), 3),

    /// Splits an OutsideSector into 3 smaller OutsideSectors separated by a Walkway T-junction. If the sector is small enough, it is turned into a SmallStructuresSector or a LargeStructureSector
    SplitOutsideSector3T(Symbols.OutsideSector, (shape, rand) -> split3T(shape, rand, false), 4),

    /// Splits an OutsideSector into 3 smaller OutsideSectors separated by 2 straight Walkway. If the sector is small enough, it is turned into a SmallStructuresSector or a LargeStructureSector
    SplitOutsideSector3I(Symbols.OutsideSector, (shape, rand) -> split3I(shape, rand, false), 2),

    /// Splits an OutsideSector into 4 smaller OutsideSectors, separated by 2 straight Walkway. If the sector is small enough, it is turned into a SmallStructuresSector or a LargeStructureSector
    SplitOutsideSector4(Symbols.OutsideSector, (shape, rand) -> split4(shape, rand, false), 3),

    /// Splits an OutsideSector into 9 smaller OutsideSectors, separated by 4 straight Walkway. If the sector is small enough, it is turned into a SmallStructuresSector or a LargeStructureSector
    SplitOutsideSector9(Symbols.OutsideSector, (shape, rand) -> split9(shape, rand, false), 1),

    /// Converts a Walkway into a terminal NormalWalkway or a terminal ElevatedWalkway
    FinalizeWalkway(Symbols.Walkway, (shape, rand) -> {
        Cube cube = shape.shape();
        boolean xAxis = cube.sizeX() > 3;
        BlockHolder<IBlockState> holder = BlockHolder.of(cube.size().add(0, 1, 0), cube.from().subtract(0, 1, 0), Blocks.AIR.getDefaultState());
        holder = holder.fill(holder.east(0), holder.up(0), holder.south(0), holder.west(0), holder.up(1), holder.north(0), BlockInit.voidTile.getDefaultState());
        return Collections.singleton(
                new Shape<>(Symbols.NormalWalkway, Pair.of(rand.nextFloat() < 0.3 ? holder : holder
                        .sequence(cube.from().add(1, -1, 1), (pos, value) ->
                                BlockHolderSequence.result(pos.add(xAxis ? 3 : 0, 0, xAxis ? 0 : 3), BlockInit.voidLamp.getDefaultState())),
                        cube.indentFrom(0, -1, 0)), shape.meta())
        );
    }, 1),

    /* INSIDE SECTOR HANDLING */

    /// Converts a InsideSector to a CakeStructure
    ConvertInsideSectorToCakeStructure(Symbols.InsideSector, (shape, rand) -> Collections.singletonList(
            new Shape<>(Symbols.CakeStructure, Pair.of(null, shape.shape()), shape.meta())
    ), 1),

    /// Converts a InsideSector to a BoxStructure, and picks the best prebuilt box structure
    ConvertInsideSectorToBoxStructure(Symbols.InsideSector, (shape, rand) -> {
        Cube cube = shape.shape();
        BlockHolder<IBlockState> structure = SkySectorStructures.getBestBoxStructure(cube.sizeX(), cube.sizeY(), cube.sizeZ(), cube.from(), rand);
        return Collections.singletonList(
                new Shape<>(Symbols.BoxStructure, Pair.of(structure, new Cube(cube.from(), cube.to().setY(cube.from().getY() + structure.getYSize()))), shape.meta())
        );
    }, 2),

    /// Surrounds a CakeStructure with 4 three block thick walls and cuts it into 2, 3, 4 or 5 Layers
    CutCakeStructure(Symbols.CakeStructure, (shape, rand) -> {
        float r = rand.nextFloat();
        Cube cube = shape.shape();

        int height = r > 0.9 ? 30 :
                     r > 0.55 ? 24 :
                     r > 0.2 ? 18 : 12;
        Shape<Pair<BlockHolder<IBlockState>, Cube>> wall1 = new Shape<>(Symbols.SolidWall, Pair.of(
                BlockHolder.of(new Vec3i(3, height, cube.sizeZ()), cube.from(), BlockInit.voidTile.getDefaultState()),
                new Cube(cube.from(), cube.from().add(3, height, cube.sizeZ()))
        ), shape.meta());
        Shape<Pair<BlockHolder<IBlockState>, Cube>> wall2 = new Shape<>(Symbols.SolidWall, Pair.of(
                BlockHolder.of(new Vec3i(3, height, cube.sizeZ()), cube.from().add(cube.sizeX() - 3, 0, 0), BlockInit.voidTile.getDefaultState()),
                new Cube(cube.from().add(cube.sizeX() - 3, 0, 0), cube.to().setY(height))
        ), shape.meta());
        Shape<Pair<BlockHolder<IBlockState>, Cube>> wall3 = new Shape<>(Symbols.SolidWall, Pair.of(
                BlockHolder.of(new Vec3i(cube.sizeX(), height, 3), cube.from(), BlockInit.voidTile.getDefaultState()),
                new Cube(cube.from(), cube.from().add(cube.sizeX(), height, 3))
        ), shape.meta());
        Shape<Pair<BlockHolder<IBlockState>, Cube>> wall4 = new Shape<>(Symbols.SolidWall, Pair.of(
                BlockHolder.of(new Vec3i(cube.sizeX(), height, 3), cube.from().add(0, 0, cube.sizeZ() - 3), BlockInit.voidTile.getDefaultState()),
                new Cube(cube.from().add(0, 0, cube.sizeZ() - 3), cube.to().setY(height))
        ), shape.meta());

        Shape<Pair<BlockHolder<IBlockState>, Cube>> floor1 = new Shape<>(Symbols.Layer,
                Pair.of(null, new Cube(cube.from().add(3, 0, 3), cube.to().setY(cube.from().getY() + 6).subtract(3, 0, 3))),
                new int[]{shape.meta()[0], shape.meta()[1], 1});
        Shape<Pair<BlockHolder<IBlockState>, Cube>> floor2 = new Shape<>(Symbols.Layer,
                Pair.of(null, new Cube(cube.from().setY(cube.from().getY() + 6).add(3, 0, 3), cube.to().setY(cube.from().getY() + 12).subtract(3, 0, 3))),
                new int[]{shape.meta()[0], shape.meta()[1], 2});

        if (r > 0.2) {
            Shape<Pair<BlockHolder<IBlockState>, Cube>> floor3 = new Shape<>(Symbols.Layer,
                    Pair.of(null, new Cube(cube.from().setY(cube.from().getY() + 12).add(3, 0, 3), cube.to().setY(cube.from().getY() + 18).subtract(3, 0, 3))),
                    new int[]{shape.meta()[0], shape.meta()[1], 3});

            if (r > 0.55) {
                Shape<Pair<BlockHolder<IBlockState>, Cube>> floor4 = new Shape<>(Symbols.Layer,
                        Pair.of(null, new Cube(cube.from().setY(cube.from().getY() + 18).add(3, 0, 3), cube.to().setY(cube.from().getY() + 24).subtract(3, 0, 3))),
                        new int[]{shape.meta()[0], shape.meta()[1], 4});

                if (r > 0.9) {
                    return Arrays.asList(wall1, wall2, wall3, wall4, floor1, floor2, floor3, floor4, new Shape<>(Symbols.Layer,
                            Pair.of(null, new Cube(cube.from().setY(cube.from().getY() + 24).add(3, 0, 3), cube.to().setY(cube.from().getY() + 30).subtract(3, 0, 3))),
                            new int[]{shape.meta()[0], shape.meta()[1], 5}));
                } return Arrays.asList(wall1, wall2, wall3, wall4, floor1, floor2, floor3, floor4);
            } return Arrays.asList(wall1, wall2, wall3, wall4, floor1, floor2, floor3);
        } return Arrays.asList(wall1, wall2, wall3, wall4, floor1, floor2);
    }, 1),

    /// Splits a Layer into 2 smaller Layers separated by a straight Tunnel
    SplitLayer2(Symbols.Layer, (shape, rand) -> split2(shape, rand, true), 4),

    /// Splits a Layer into 3 smaller Layers separated by a Tunnels T-junction
    SplitLayer3T(Symbols.Layer, (shape, rand) -> split3T(shape, rand, true), 4),

    /// Splits a Layer into 3 smaller Layers separated by 2 straight Tunnels
    SplitLayer3I(Symbols.Layer, (shape, rand) -> split3I(shape, rand, true), 2),

    /// Splits a Layer into 4 smaller Layers, separated by 2 straight Tunnels
    SplitLayer4(Symbols.Layer, (shape, rand) -> split4(shape, rand, true), 3),

    /// Splits a Layer into 9 smaller Layers, separated by 4 straight Tunnels
    SplitLayer9(Symbols.Layer, (shape, rand) -> split9(shape, rand, true), 1),

    /// Turns a Tunnel into a TerminalNormalTunnel
    CreateNormalTunnel(Symbols.Tunnel, (shape, rand) -> {
        Cube cube = shape.shape();
        boolean xAxis = cube.sizeX() > 3;
        if (rand.nextFloat() < 0.3) cube = new Cube( // Sometimes extends the tunnel so it can go through the walls of the cake structure
                rand.nextBoolean() ? cube.from() : cube.from().subtract(xAxis ? 3 : 0, 0, xAxis ? 0 : 3),
                rand.nextBoolean() ? cube.to() : cube.to().add(xAxis ? 3 : 0, 0, xAxis ? 0 : 3)
        );
        return Collections.singleton(
                new Shape<>(Symbols.TerminalNormalTunnel, Pair.of(
                        BlockHolder.of(cube.size(), cube.from(), Blocks.AIR.getDefaultState())
                                .fillDirs(0, 3, 0, 0, 0, 0, BlockInit.voidTile.getDefaultState())
                                .sequence(cube.from().add(1, 3, 1), (pos, value) ->
                                        BlockHolderSequence.result(pos.add(xAxis ? 3 : 0, 0, xAxis ? 0 : 3), BlockInit.voidLamp.getDefaultState())),
                        cube), shape.meta())
        );
    }, 3),
    ;

    private static @NotNull Iterable<Shape<Pair<BlockHolder<IBlockState>, Cube>>> split2(Shape<Cube> shape, Random rand, boolean layer) {
        double pos = (rand.nextDouble() * 0.5) + 0.25;
        boolean axis = rand.nextBoolean();
        Cube cube = shape.shape();
        if (cube.sizeX() < 15 || cube.sizeZ() < 15) return finalize(shape, rand, layer);
        else if (axis) {
            Cube cube1 = cube.subCube(0, 0, 0, 1, 1, pos).indentTo(0, 0, 3);
            return Arrays.asList(
                    new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, cube1), shape.meta()),
                    new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, cube.subCube(0, 0, pos, 1, 1, 1)), shape.meta()),
                    new Shape<>(layer ? Symbols.Tunnel : Symbols.Walkway, Pair.of(null, new Cube(cube1.from().setZ(cube1.to()), cube1.to().add(0, 0, 3))), shape.meta())
            );
        } else {
            Cube cube1 = cube.subCube(0, 0, 0, pos, 1, 1).indentTo(3, 0, 0);
            return Arrays.asList(
                    new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, cube1), shape.meta()),
                    new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, cube.subCube(pos, 0, 0, 1, 1, 1)), shape.meta()),
                    new Shape<>(layer ? Symbols.Tunnel : Symbols.Walkway, Pair.of(null, new Cube(cube1.from().setX(cube1.to()), cube1.to().add(3, 0, 0))), shape.meta())
            );
        }
    }

    private static @NotNull Iterable<Shape<Pair<BlockHolder<IBlockState>, Cube>>> split3T(Shape<Cube> shape, Random rand, boolean layer) {
        double pos1 = (rand.nextDouble() * 0.5) + 0.25, pos2 = (rand.nextDouble() * 0.5) + 0.25;
        boolean axis = rand.nextBoolean(), side = rand.nextBoolean();
        Cube cube = shape.shape();
        if (cube.sizeX() < 18 || cube.sizeZ() < 18) return finalize(shape, rand, layer);
        else if (axis) {
            Cube cube1 = cube.subCube(0, 0, side ? 0 : pos1, 1, 1, side ? pos1 : 1).indent(0, 0, side ? 0 : 3, 0, 0, side ? 3 : 0);
            Cube cube2 = cube.subCube(0, 0, side ? pos1 : 0, pos2, 1, side ? 1 : pos1).indentTo(3, 0, 0);
            Cube cube3 = cube.subCube(pos2, 0, side ? pos1 : 0, 1, 1, side ? 1 : pos1);
            return Arrays.asList(
                    new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, cube1), shape.meta()),
                    new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, cube2), shape.meta()),
                    new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, cube3), shape.meta()),
                    new Shape<>(
                            layer ? Symbols.Tunnel : Symbols.Walkway,
                            Pair.of(null, new Cube(
                                    side ? cube1.from().setZ(cube1.to()) : cube1.from().subtract(0, 0, 3),
                                    side ? cube1.to().add(0, 0, 3) : cube1.to().setZ(cube1.from()))),
                            shape.meta()),
                    new Shape<>(layer ? Symbols.Tunnel : Symbols.Walkway, Pair.of(null, new Cube(cube2.from().setX(cube2.to()), cube2.to().add(3, 0, 0))), shape.meta())
            );
        } else {
            Cube cube1 = cube.subCube(side ? 0 : pos1, 0, 0, side ? pos1 : 1, 1, 1).indent(side ? 0 : 3, 0, 0, side ? 3 : 0, 0, 0);
            Cube cube2 = cube.subCube(side ? pos1 : 0, 0, 0, side ? 1 : pos1, 1, pos2).indentTo(0, 0, 3);
            Cube cube3 = cube.subCube(side ? pos1 : 0, 0, pos2, side ? 1 : pos1, 1, 1);
            return Arrays.asList(
                    new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, cube1), shape.meta()),
                    new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, cube2), shape.meta()),
                    new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, cube3), shape.meta()),
                    new Shape<>(
                            layer ? Symbols.Tunnel : Symbols.Walkway,
                            Pair.of(null, new Cube(
                                    side ? cube1.from().setX(cube1.to()) : cube1.from().subtract(3, 0, 0),
                                    side ? cube1.to().add(3, 0, 0) : cube1.to().setX(cube1.from()))),
                            shape.meta()),
                    new Shape<>(layer ? Symbols.Tunnel : Symbols.Walkway, Pair.of(null, new Cube(cube2.from().setZ(cube2.to()), cube2.to().add(0, 0, 3))), shape.meta())
            );
        }
    }

    private static @NotNull Iterable<Shape<Pair<BlockHolder<IBlockState>, Cube>>> split3I(Shape<Cube> shape, Random rand, boolean layer) {
        double pos1 = (rand.nextDouble() / 3) + 0.16, pos2 = (rand.nextDouble() / 3) + 0.51;
        boolean axis = rand.nextBoolean();
        Cube cube = shape.shape();
        if (cube.sizeX() < 27 || cube.sizeZ() < 27) return finalize(shape, rand, layer);
        else if (axis) {
            Cube cube1 = cube.subCube(0, 0, 0, 1, 1, pos1).indentTo(0, 0, 3);
            Cube cube2 = cube.subCube(0, 0, pos1, 1, 1, pos2).indentTo(0, 0, 3);
            return Arrays.asList(
                    new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, cube1), shape.meta()),
                    new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, cube2), shape.meta()),
                    new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, cube.subCube(0, 0, pos2, 1, 1, 1)), shape.meta()),
                    new Shape<>(layer ? Symbols.Tunnel : Symbols.Walkway, Pair.of(null, new Cube(cube1.from().setZ(cube1.to()), cube1.to().add(0, 0, 3))), shape.meta()),
                    new Shape<>(layer ? Symbols.Tunnel : Symbols.Walkway, Pair.of(null, new Cube(cube2.from().setZ(cube2.to()), cube2.to().add(0, 0, 3))), shape.meta())
            );
        } else {
            Cube cube1 = cube.subCube(0, 0, 0, pos1, 1, 1).indentTo(3, 0, 0);
            Cube cube2 = cube.subCube(pos1, 0, 0, pos2, 1, 1).indentTo(3, 0, 0);
            return Arrays.asList(
                    new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, cube1), shape.meta()),
                    new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, cube2), shape.meta()),
                    new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, cube.subCube(pos2, 0, 0, 1, 1, 1)), shape.meta()),
                    new Shape<>(layer ? Symbols.Tunnel : Symbols.Walkway, Pair.of(null, new Cube(cube1.from().setX(cube1.to()), cube1.to().add(3, 0, 0))), shape.meta()),
                    new Shape<>(layer ? Symbols.Tunnel : Symbols.Walkway, Pair.of(null, new Cube(cube2.from().setX(cube2.to()), cube2.to().add(3, 0, 0))), shape.meta())
            );
        }
    }

    private static @NotNull Iterable<Shape<Pair<BlockHolder<IBlockState>, Cube>>> split4(Shape<Cube> shape, Random rand, boolean layer) {
        double xPos = (rand.nextDouble() / 2) + 0.25, zPos = (rand.nextDouble() / 2) + 0.25;
        Cube cube = shape.shape();
        if (cube.sizeX() < 18 || cube.sizeZ() < 18) return finalize(shape, rand, layer);
        Cube c1 = cube.subCube(0, 0, 0, xPos, 1, zPos).indentTo(3, 0, 3);
        Cube c2 = cube.subCube(xPos, 0, 0, 1, 1, zPos).indentTo(0, 0, 3);
        Cube c3 = cube.subCube(0, 0, zPos, xPos, 1, 1).indentTo(3, 0, 0);
        Cube c4 = cube.subCube(xPos, 0, zPos, 1, 1, 1);
        return Arrays.asList(
                new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, c1), shape.meta()),
                new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, c2), shape.meta()),
                new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, c3), shape.meta()),
                new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, c4), shape.meta()),
                new Shape<>(layer ? Symbols.Tunnel : Symbols.Walkway, Pair.of(null, new Cube(c1.from().setX(c1.to()), c3.to().add(3, 0, 0))), shape.meta()),
                new Shape<>(layer ? Symbols.Tunnel : Symbols.Walkway, Pair.of(null, new Cube(c1.from().setZ(c1.to()), c2.to().setX(c4.to()).add(0, 0, 3))), shape.meta())
        );
    }

    private static Iterable<Shape<Pair<BlockHolder<IBlockState>, Cube>>> split9(Shape<Cube> shape, Random rand, boolean layer) {
        double x1Pos = (rand.nextDouble() / 3) + 0.16, x2Pos = (rand.nextDouble() / 3) + 0.51;
        double z1Pos = (rand.nextDouble() / 3) + 0.16, z2Pos = (rand.nextDouble() / 3) + 0.51;
        Cube cube = shape.shape();
        if (cube.sizeX() < 32 || cube.sizeZ() < 32) return finalize(shape, rand, layer);
        Cube c1 = cube.subCube(0, 0, 0, x1Pos, 1, z1Pos).indentTo(3, 0, 3);
        Cube c2 = cube.subCube(x1Pos, 0, 0, x2Pos, 1, z1Pos).indentTo(3, 0, 3);
        Cube c3 = cube.subCube(x2Pos, 0, 0, 1, 1, z1Pos).indentTo(0, 0, 3);
        Cube c4 = cube.subCube(0, 0, z1Pos, x1Pos, 1, z2Pos).indentTo(3, 0, 3);
        Cube c5 = cube.subCube(x1Pos, 0, z1Pos, x2Pos, 1, z2Pos).indentTo(3, 0, 3);
        Cube c6 = cube.subCube(x2Pos, 0, z1Pos, 1, 1, z2Pos).indentTo(0, 0, 3);
        Cube c7 = cube.subCube(0, 0, z2Pos, x1Pos, 1, 1).indentTo(3, 0, 0);
        Cube c8 = cube.subCube(x1Pos, 0, z2Pos, x2Pos, 1, 1).indentTo(3, 0, 0);
        Cube c9 = cube.subCube(x2Pos, 0, z2Pos, 1, 1, 1);
        return Arrays.asList(
                new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, c1), shape.meta()),
                new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, c2), shape.meta()),
                new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, c3), shape.meta()),
                new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, c4), shape.meta()),
                new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, c5), shape.meta()),
                new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, c6), shape.meta()),
                new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, c7), shape.meta()),
                new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, c8), shape.meta()),
                new Shape<>(layer ? Symbols.Layer : Symbols.OutsideSector, Pair.of(null, c9), shape.meta()),
                new Shape<>(layer ? Symbols.Tunnel : Symbols.Walkway, Pair.of(null, new Cube(c1.from().setX(c1.to()), c7.to().add(3, 0, 0))), shape.meta()),
                new Shape<>(layer ? Symbols.Tunnel : Symbols.Walkway, Pair.of(null, new Cube(c2.from().setX(c2.to()), c8.to().add(3, 0, 0))), shape.meta()),
                new Shape<>(layer ? Symbols.Tunnel : Symbols.Walkway, Pair.of(null, new Cube(c1.from().setZ(c1.to()), c3.to().add(0, 0, 3))), shape.meta()),
                new Shape<>(layer ? Symbols.Tunnel : Symbols.Walkway, Pair.of(null, new Cube(c4.from().setZ(c4.to()), c6.to().add(0, 0, 3))), shape.meta())
        );
    }

    private static final FastNoiseLite noiseGen;

    static {
        noiseGen = new FastNoiseLite(System.nanoTime());
        noiseGen.SetFrequency(0.004F);
    }

    public static Iterable<Shape<Pair<BlockHolder<IBlockState>, Cube>>> finalize(Shape<Cube> shape, Random rand, boolean layer) {
        if (layer) return finalizeLayer(shape);
        else return finalizeOuter(shape, rand);
    }

    /// Called when trying to split an OutsideSector that's too small.Turns the OutsideSector into a SmallStructuresSector or a LargeStructureSector, or None if the sector's size is 0 or less.
    private static Iterable<Shape<Pair<BlockHolder<IBlockState>, Cube>>> finalizeOuter(Shape<Cube> shape, Random rand) {
        Cube cube = shape.shape();
        if (cube.sizeX() <= 0 || cube.sizeY() <= 0 || cube.sizeZ() <= 0)
            return Collections.singleton(new Shape<>(Symbols.None, Pair.of(new BlockHolder<>(0, 0, 0), cube), shape.meta()));
        if (rand.nextFloat() + 0.5 < noiseGen.GetNoise(cube.from().getX(), cube.from().getZ())) return Collections.singleton(
                new Shape<>(Symbols.EmptySector, Pair.of(BlockHolder.of(cube.size(), cube.from(), Blocks.AIR.getDefaultState()), cube), shape.meta())
        );
        else {
            BlockHolder<IBlockState> structure = SkySectorStructures.getBestLargeOutsideStructure(cube.sizeX(), cube.sizeY(), cube.sizeZ(), cube.from(), rand);
            return Collections.singleton(
                    new Shape<>(Symbols.LargeStructureSector, Pair.of(structure, new Cube(cube.from(), cube.to().setY(cube.from().getY() + structure.getYSize()))), shape.meta())
            );
        }
    }

    /// Called when trying to split a Layer that's too small. Turns the Layer into a SolidWall or PrebuiltLayer, or None if the Layer's size is 0 or less.
    public static Iterable<Shape<Pair<BlockHolder<IBlockState>, Cube>>> finalizeLayer(Shape<Cube> shape) {
        Cube cube = shape.shape();
        if (cube.sizeX() <= 0 || cube.sizeY() <= 0 || cube.sizeZ() <= 0)
            return Collections.singleton(new Shape<>(Symbols.None, Pair.of(new BlockHolder<>(0, 0, 0), cube), shape.meta()));
        return Collections.singleton(
                new Shape<>(Symbols.SolidWall, Pair.of(BlockHolder.of(cube.size(), cube.from(), BlockInit.voidTile.getDefaultState()), cube), shape.meta())
        );
    }

    public static final GenBlockRuleSet<IBlockState> RULES = new GenBlockRuleSet<>(Arrays.asList(values()));

    public enum Symbols implements Symbol {
        EntryPoint(0, false),
        IndeterminateSector(1, false),

        OutsideSector(2, false),
        EmptySector(11, true),
        SmallStructuresSector(12, true),
        LargeStructureSector(13, true),
        Walkway(14, false),
        NormalWalkway(15, true),
        ElevatedWalkway(16, true),

        InsideSector(3, false),
        BoxStructure(4, true),
        CakeStructure(5, false),
        Layer(6, false),
        PrebuiltLayer(7, true),
        Tunnel(8, false),
        TerminalNormalTunnel(9, true),
        TerminalSpecialTunnel(10, true),
        SolidWall(17, true),

        None(18, true);

        private final int id;
        private final boolean terminal;

        Symbols(int id, boolean terminal) {
            this.id = id;
            this.terminal = terminal;
        }

        @Override
        public int identifier() {
            return id;
        }

        @Override
        public boolean isTerminal() {
            return terminal;
        }
    }

    private final Symbol predecessor;
    private final BiFunction<Shape<Cube>, Random, Iterable<Shape<Pair<BlockHolder<IBlockState>, Cube>>>> successorFunc;
    private final int weight;

    SkySectorGenBlockDefinition(Symbol predecessor, BiFunction<Shape<Cube>, Random, Iterable<Shape<Pair<BlockHolder<IBlockState>, Cube>>>> successorFunc, int weight) {
        this.predecessor = predecessor;
        this.successorFunc = successorFunc;
        this.weight = weight;
    }

    @Override
    public Symbol predecessor() {
        return predecessor;
    }

    @Override
    public Iterable<Shape<Pair<BlockHolder<IBlockState>, Cube>>> successor(Shape<Cube> predecessor, Random rand) {
        return successorFunc.apply(predecessor, rand);
    }

    @Override
    public int weight() {
        return weight;
    }
}
