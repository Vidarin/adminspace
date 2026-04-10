package com.vidarin.adminspace.dimension.disposal;

import com.vidarin.adminspace.util.WildcardMap;
import com.vidarin.adminspace.util.blockholder.BlockHolder;
import com.vidarin.adminspace.worldgen.genblock.Cube;
import com.vidarin.adminspace.worldgen.genblock.GenBlockRuleSet;
import com.vidarin.adminspace.worldgen.grammar.Rule;
import com.vidarin.adminspace.worldgen.grammar.Shape;
import com.vidarin.adminspace.worldgen.grammar.Symbol;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

public final class TestGenBlockDefinition {
    public static final GenBlockRuleSet<IBlockState> RULES = new GenBlockRuleSet<>(Arrays.asList(Rules.values()));

    public enum Symbols implements Symbol {
        LargeCube(0, false);

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
            Cube cube1 = shape.shape().subCube(0, 0, 0.1, 0.6, 1, 0.9);
            Cube cube2 = shape.shape().subCube(0.6, 0, 0.2, 1, 0.8, 0.8);
            BlockHolder<IBlockState> holder1 = BlockHolder.of(cube1.size(), cube1.from(), Blocks.QUARTZ_BLOCK.getDefaultState());
            BlockHolder<IBlockState> holder2 = BlockHolder.of(cube2.size(), cube2.from(), Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.ROUGH));

            return Arrays.asList(
                    new Shape<>(Symbols.LargeCube, Pair.of(holder1
                                    .fillDirs(1, 1, 0, 1, 2, 0, Blocks.GLASS.getDefaultState())
                                    .fill(holder1.east(0), holder1.down(1), holder1.south(0), holder1.west(0), holder1.down(0), holder1.north(0), Blocks.AIR.getDefaultState())
                                    .fill(holder1.east(1), holder1.down(1), holder1.south(1), holder1.west(1), holder1.down(0), holder1.north(1), Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.UP)),
                            cube1)),
                    new Shape<>(Symbols.LargeCube, Pair.of(holder2
                                    .fillDirs(1, 1, 0, 1, 1, 0, Blocks.GLASS.getDefaultState()), cube2))
            );
        }, 1),
        SplitLarge2(Symbols.LargeCube, (shape) -> {
            Cube cube1 = shape.shape().subCube(0.1, 0, 0, 0.9, 1, 0.6);
            Cube cube2 = shape.shape().subCube(0.2, 0, 0.6, 0.8, 0.8, 1);
            BlockHolder<IBlockState> holder1 = BlockHolder.of(cube1.size(), cube1.from(), Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.BRICKS));
            BlockHolder<IBlockState> holder2 = BlockHolder.of(cube2.size(), cube2.from(), Blocks.QUARTZ_BLOCK.getDefaultState());

            return Arrays.asList(
                    new Shape<>(Symbols.LargeCube, Pair.of(holder1
                                    .fillDirs(1, 1, 0, 1, 2, 0, Blocks.GLASS.getDefaultState())
                                    .fill(holder1.east(0), holder1.down(1), holder1.south(0), holder1.west(0), holder1.down(0), holder1.north(0), Blocks.AIR.getDefaultState())
                                    .fill(holder1.east(1), holder1.down(1), holder1.south(1), holder1.west(1), holder1.down(0), holder1.north(1), Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.UP)),
                            cube1)),
                    new Shape<>(Symbols.LargeCube, Pair.of(holder2, cube2))
            );
        }, 1);

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
        public Iterable<Shape<Pair<BlockHolder<IBlockState>, Cube>>> successor(Shape<Cube> predecessor, Random rand, WildcardMap<String> globals) {
            return successorFunc.apply(predecessor);
        }

        @Override
        public int weight() {
            return weight;
        }
    }
}
