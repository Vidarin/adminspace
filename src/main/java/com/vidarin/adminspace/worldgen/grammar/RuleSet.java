package com.vidarin.adminspace.worldgen.grammar;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public interface RuleSet<P, S> extends Set<Rule<P, S>> {
    @Override boolean add(Rule<P, S> rule);

    Shape<S> get(Shape<P> predecessor, Random rand);

    default Shape<S> get(Shape<P> predecessor) {
        return get(predecessor, new Random());
    }

    default Shape<S> get(Shape<P> predecessor, long seed) {
        return get(predecessor, new Random(seed));
    }

    @Override
    default boolean isEmpty() {
        return size() == 0;
    }

    @Override
    default Object @NotNull [] toArray() {
        Iterator<Rule<P, S>> iterator = iterator();
        Object[] arr = new Object[size()];
        int i = 0;
        while (iterator.hasNext()) {
            arr[i++] = iterator.next();
        }
        return arr;
    }

    @Override
    @SuppressWarnings("unchecked")
    default <A> A @NotNull [] toArray(A @NotNull [] a) {
        return (A[]) toArray();
    }

    @Override
    default boolean containsAll(@NotNull Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) return false;
        }
        return true;
    }

    @Override
    default boolean addAll(@NotNull Collection<? extends Rule<P, S>> c) {
        boolean changed = false;
        for (Rule<P, S> rule : c) {
            if (add(rule)) changed = true;
        }
        return changed;
    }

    @Override
    default boolean removeAll(@NotNull Collection<?> c) {
        boolean changed = false;
        for (Object o : c) {
            if (remove(o)) changed = true;
        }
        return changed;
    }

    @Override
    default boolean retainAll(@NotNull Collection<?> c) {
        Iterator<Rule<P, S>> iterator = iterator();
        boolean changed = false;
        while (iterator().hasNext()) {
            Rule<P, S> rule = iterator.next();
            if (!c.contains(rule)) {
                if (remove(rule)) changed = true;
            }
        }
        return changed;
    }
}
