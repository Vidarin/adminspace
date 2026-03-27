package com.vidarin.adminspace.dimension.disposal;

import com.vidarin.adminspace.util.BlockHolder;
import com.vidarin.adminspace.worldgen.genblock.Cube;
import com.vidarin.adminspace.worldgen.genblock.GenBlockRuleSet;
import com.vidarin.adminspace.worldgen.grammar.Rule;
import com.vidarin.adminspace.worldgen.grammar.Shape;
import com.vidarin.adminspace.worldgen.grammar.Symbol;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.function.Function;

public final class TestGenBlockDefinition {
    public static final GenBlockRuleSet<IBlockState> RULES = new GenBlockRuleSet<>(Arrays.asList(Rules.values()));

    public enum Symbols implements Symbol {
        LargeCube(0, false),
        ;

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

    public enum Rules implements Rule<Cube, Pair<BlockHolder<IBlockState>, Cube>> {
        SplitLarge1(Symbols.LargeCube, (shape) -> {
            Cube cube1 = shape.shape().subCube(1, 1, 0.9, 0.6, 1, 0.9);
            Cube cube2 = shape.shape().subCube(0.4, 1, 0.8, 1, 0.8, 0.8);

            return Arrays.asList(
                    new Shape<>(Symbols.LargeCube, Pair.of(BlockHolder.of(
                            cube1.size(), Blocks.QUARTZ_BLOCK.getDefaultState()
                    ), cube1)),
                    new Shape<>(Symbols.LargeCube, Pair.of(BlockHolder.of(
                            cube2.size(), Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.ROUGH)
                    ), cube2))
            );
        }, 3),
        SplitLarge2(Symbols.LargeCube, (shape) -> {
            Cube cube1 = shape.shape().subCube(0.9, 1, 1, 0.9, 1, 0.6);
            Cube cube2 = shape.shape().subCube(0.8, 1, 0.4, 0.8, 0.8, 1);

            return Arrays.asList(
                    new Shape<>(Symbols.LargeCube, Pair.of(BlockHolder.of(
                            cube1.size(), Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.BRICKS)
                    ), cube1)),
                    new Shape<>(Symbols.LargeCube, Pair.of(BlockHolder.of(
                            cube2.size(), Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.DARK)
                    ), cube2))
            );
        }, 3),
        ;

        private final Symbol predecessor;
        private final Function<Shape<Cube>, Iterable<Shape<Pair<BlockHolder<IBlockState>, Cube>>>> successorFunc;
        private final int weight;

        Rules(Symbol predecessor, Function<Shape<Cube>, Iterable<Shape<Pair<BlockHolder<IBlockState>, Cube>>>> successorFunc, int weight) {
            this.predecessor = predecessor;
            this.successorFunc = successorFunc;
            this.weight = weight;
        }

        @Override
        public Symbol predecessor() {
            return predecessor;
        }

        @Override
        public Iterable<Shape<Pair<BlockHolder<IBlockState>, Cube>>> successor(Shape<Cube> predecessor) {
            return successorFunc.apply(predecessor);
        }

        @Override
        public int weight() {
            return weight;
        }
    }
}
