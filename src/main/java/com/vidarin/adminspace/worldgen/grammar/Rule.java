package com.vidarin.adminspace.worldgen.grammar;

public interface Rule<P, S> {
    Symbol predecessor();

    Iterable<Shape<S>> successor(Shape<P> predecessor);

    default int weight() {
        return 1;
    }
}
