package com.vidarin.adminspace.worldgen.genblock;

import com.vidarin.adminspace.util.BlockHolder;
import com.vidarin.adminspace.worldgen.grammar.SimpleRuleSet;
import org.apache.commons.lang3.tuple.Pair;

public class GenBlockRuleSet<T> extends SimpleRuleSet<Cube, Pair<BlockHolder<T>, Cube>> {}
