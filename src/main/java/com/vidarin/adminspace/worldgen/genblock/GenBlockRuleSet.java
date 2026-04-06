package com.vidarin.adminspace.worldgen.genblock;

import com.vidarin.adminspace.util.blockholder.BlockHolder;
import com.vidarin.adminspace.worldgen.grammar.Rule;
import com.vidarin.adminspace.worldgen.grammar.SimpleRuleSet;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;

public class GenBlockRuleSet<T> extends SimpleRuleSet<Cube, Pair<BlockHolder<T>, Cube>> {
    public GenBlockRuleSet() {
        super();
    }

    public GenBlockRuleSet(Collection<Rule<Cube, Pair<BlockHolder<T>, Cube>>> rules) {
        super(rules);
    }
}
