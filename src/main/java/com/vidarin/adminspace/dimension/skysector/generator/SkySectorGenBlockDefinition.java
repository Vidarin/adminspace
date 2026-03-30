package com.vidarin.adminspace.dimension.skysector.generator;

import com.vidarin.adminspace.util.BlockHolder;
import com.vidarin.adminspace.worldgen.genblock.Cube;
import com.vidarin.adminspace.worldgen.genblock.GenBlockRuleSet;
import com.vidarin.adminspace.worldgen.grammar.Rule;
import com.vidarin.adminspace.worldgen.grammar.Shape;
import com.vidarin.adminspace.worldgen.grammar.Symbol;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.function.BiFunction;

public enum SkySectorGenBlockDefinition implements Rule<Cube, Pair<BlockHolder<IBlockState>, Cube>> {
    /*
    * METADATA DEFINITION:
    * [0]: SectorInfo.ActivityLevel index
    * [1]: SkyIndo.SkyState index
    */

    /// Splits one EntryPoint into 4 IndeterminateSectors
    SplitEntryPoint4(Symbols.EntryPoint, (shape, rand) -> {
        double xPos = rand.nextDouble(); double zPos = rand.nextDouble();
        Cube c1 = shape.shape().subCube(1, 1, 1, xPos, 1, zPos);
        Cube c2 = shape.shape().subCube(xPos, 1, 1, 1, 1, zPos);
        Cube c3 = shape.shape().subCube(1, 1, zPos, xPos, 1, 1);
        Cube c4 = shape.shape().subCube(xPos, 1, zPos, 1, 1, 1);
        return Arrays.asList(
                new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c1), shape.metadata()), new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c2), shape.metadata()),
                new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c3), shape.metadata()), new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c4), shape.metadata())
        );
    }, 1),

    /// Splits one EntryPoint into 9 IndeterminateSectors
    SplitEntryPoint9(Symbols.EntryPoint, (shape, rand) -> {
        double x1Pos = rand.nextDouble(); double x2Pos = rand.nextDouble();
        double z1Pos = rand.nextDouble(); double z2Pos = rand.nextDouble();
        Cube c1 = shape.shape().subCube(1, 1, 1, x1Pos, 1, z1Pos);
        Cube c2 = shape.shape().subCube(x1Pos, 1, 1, x2Pos, 1, z1Pos);
        Cube c3 = shape.shape().subCube(x2Pos, 1, 1, 1, 1, z1Pos);
        Cube c4 = shape.shape().subCube(1, 1, z1Pos, x1Pos, 1, z2Pos);
        Cube c5 = shape.shape().subCube(x1Pos, 1, z1Pos, x2Pos, 1, z2Pos);
        Cube c6 = shape.shape().subCube(x2Pos, 1, z1Pos, 1, 1, z2Pos);
        Cube c7 = shape.shape().subCube(1, 1, z2Pos, x1Pos, 1, 1);
        Cube c8 = shape.shape().subCube(x1Pos, 1, z2Pos, x2Pos, 1, 1);
        Cube c9 = shape.shape().subCube(x2Pos, 1, z2Pos, 1, 1, 1);
        return Arrays.asList(
                new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c1), shape.metadata()), new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c2), shape.metadata()),
                new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c3), shape.metadata()), new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c4), shape.metadata()),
                new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c5), shape.metadata()), new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c6), shape.metadata()),
                new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c7), shape.metadata()), new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c8), shape.metadata()),
                new Shape<>(Symbols.IndeterminateSector, Pair.of(null, c9), shape.metadata())
        );
    }, 2),

    /// Converts an IndeterminateSector to a LargeInsideSector or LargeOutsideSector, with higher activity skies having more inside sectors
    ChooseIndeterminateSectorType(Symbols.IndeterminateSector, (shape, rand) -> {
        boolean outside = rand.nextInt(8) + 3 - MathHelper.clamp(shape.metadata()[0], 1, 6) > 5;
        return Collections.singleton(new Shape<>(outside ? Symbols.LargeOutsideSector : Symbols.LargeInsideSector, Pair.of(null, shape.shape()), shape.metadata()));
    }, 1),

    /// Converts a LargeInsideSector to a CakeStructure
    ConvertInsideSectorToCakeStructure(Symbols.LargeInsideSector, (shape, rand) ->
            Collections.singletonList(new Shape<>(Symbols.CakeStructure, Pair.of(null, shape.shape()), shape.metadata())),
            1),

    /// Converts a LargeInsideSector to a BoxStructure, and picks the best prebuilt box structure
    ConvertInsideSectorToBoxStructure(Symbols.LargeInsideSector, (shape, rand) -> {
        return Collections.emptyList(); //TODO
    }, 1),
    ;

    public static final GenBlockRuleSet<IBlockState> RULES = new GenBlockRuleSet<>(Arrays.asList(values()));

    public enum Symbols implements Symbol {
        EntryPoint(0, false),
        IndeterminateSector(1, false),

        LargeOutsideSector(2, false),
        EmptySector(11, true),
        SmallStructuresSector(12, false),
        LargeStructuresSector(13, false),
        Walkway(14, false),
        NormalWalkway(15, true),
        ElevatedWalkway(16, true),
        PrebuiltSmallStructureGrid(17, true),
        PrebuiltLargeStructure(18, true),

        LargeInsideSector(3, false),
        BoxStructure(4, true),
        CakeStructure(5, false),
        Layer(6, false),
        PrebuiltLayer(7, true),
        Tunnel(8, false),
        TerminalNormalTunnel(9, true),
        TerminalSpecialTunnel(10, true),
        TerminalLayer(19, true);

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
