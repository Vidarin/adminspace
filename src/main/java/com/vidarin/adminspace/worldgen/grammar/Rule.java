package com.vidarin.adminspace.worldgen.grammar;

public interface Rule<P, S> {
    Shape<P> predecessor();

    Iterable<Shape<S>> successor(Shape<P> predecessor);

    default int weight() {
        return 1;
    }
}
