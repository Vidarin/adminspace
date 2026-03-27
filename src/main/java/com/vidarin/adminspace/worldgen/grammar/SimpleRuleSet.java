package com.vidarin.adminspace.worldgen.grammar;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SimpleRuleSet<P, S> implements RuleSet<P, S> {
    private final Int2ObjectMap<List<Rule<P, S>>> rules;
    private int size = 0;

    public SimpleRuleSet() {
        this.rules = new Int2ObjectOpenHashMap<>();
    }

    public SimpleRuleSet(Collection<Rule<P, S>> rules) {
        this.rules = new Int2ObjectOpenHashMap<>();
        this.addAll(rules);
    }

    @Override
    public @Nullable Iterable<Shape<S>> get(Shape<P> predecessor, Random rand) {
        if (rules.containsKey(predecessor.symbol().identifier())) {
            List<Rule<P, S>> list = rules.get(predecessor.symbol().identifier());

            int totalWeight = 0;
            for (Rule<P, S> rule : list) totalWeight += rule.weight();

            int r = rand.nextInt(totalWeight);
            int cumulative = 0;

            for (Rule<P, S> rule : list) {
                cumulative += rule.weight();
                if (r < cumulative) {
                    return rule.successor(predecessor);
                }
            }
        }
        return null;
    }

    @Override
    public boolean add(Rule<P, S> rule) {
        if (rule.predecessor().isTerminal()) throw new IllegalArgumentException("Rules cannot have terminal predecessors");
        if (rules.containsKey(rule.predecessor().identifier())) {
            List<Rule<P, S>> list = rules.get(rule.predecessor().identifier());
            if (!list.contains(rule)) {
                list.add(rule);
                size++;
                return true;
            } else return false;
        } else {
            List<Rule<P, S>> list = new ObjectArrayList<>();
            list.add(rule);
            size++;
            rules.put(rule.predecessor().identifier(), list);
            return true;
        }
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof Symbol symbol) {
            if (rules.containsKey(symbol.identifier())) {
                size -= rules.remove(symbol.identifier()).size();
                return true;
            } else return false;
        } else if (o instanceof Rule<?,?> rule) {
            if (rules.containsKey(rule.predecessor().identifier())) {
                List<Rule<P, S>> list = rules.get(rule.predecessor().identifier());
                if (list.remove(rule)) {
                    size--;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof Symbol symbol) {
            return rules.containsKey(symbol.identifier());
        } else if (o instanceof Rule<?,?> rule) {
            if (rules.containsKey(rule.predecessor().identifier())) {
                return rules.get(rule.predecessor().identifier()).contains(rule);
            }
        }
        return false;
    }

    @Override
    public @NotNull Iterator<Rule<P, S>> iterator() {
        return new RuleSetIterator(rules.values());
    }

    private class RuleSetIterator implements Iterator<Rule<P, S>> {
        private final Iterator<List<Rule<P, S>>> valuesIterator;
        private Iterator<Rule<P, S>> current = null;

        private RuleSetIterator(Collection<List<Rule<P, S>>> values) {
            this.valuesIterator = values.iterator();
        }

        @Override
        public boolean hasNext() {
            if (!valuesIterator.hasNext()) {
                if (current == null) return false;
                return current.hasNext();
            } return true;
        }

        @Override
        public Rule<P, S> next() {
            if (current == null || !current.hasNext()) {
                if (!valuesIterator.hasNext()) return null;
                current = valuesIterator.next().iterator();
            }
            return current.next();
        }

        @Override
        public void remove() {
            if (hasNext()) SimpleRuleSet.this.remove(next());
        }
    }

    @Override
    public void clear() {
        rules.clear();
        size = 0;
    }
}
