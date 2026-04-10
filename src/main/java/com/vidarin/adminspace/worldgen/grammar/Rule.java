package com.vidarin.adminspace.worldgen.grammar;

import com.vidarin.adminspace.util.WildcardMap;

import java.util.Random;

public interface Rule<P, S> {
    Symbol predecessor();

    Iterable<Shape<S>> successor(Shape<P> predecessor, Random rand, WildcardMap<String> globals);

    default int weight() {
        return 1;
    }
}
