package com.vidarin.adminspace.worldgen.genblock;

import com.vidarin.adminspace.util.WildcardMap;
import com.vidarin.adminspace.util.blockholder.BlockHolder;
import com.vidarin.adminspace.worldgen.grammar.Shape;
import net.minecraft.block.state.IBlockState;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Random;

@FunctionalInterface
public interface GenBlockRuleSuccessorFunc {
    Iterable<Shape<Pair<BlockHolder<IBlockState>, Cube>>> successor(Shape<Cube> shape, Random rand, WildcardMap<String> globals);
}
