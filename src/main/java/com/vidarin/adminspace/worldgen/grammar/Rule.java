package com.vidarin.adminspace.worldgen.grammar;

import java.util.Random;

public interface Rule<P, S> {
    Symbol predecessor();

    Iterable<Shape<S>> successor(Shape<P> predecessor, Random rand);

    default int weight() {
        return 1;
    }
}
