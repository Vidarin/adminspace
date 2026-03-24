package com.vidarin.adminspace.worldgen.genblock;

import com.github.bsideup.jabel.Desugar;
import com.vidarin.adminspace.worldgen.grammar.Shape;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

@Desugar
public record BlockNode<T>(Shape<Cube> cube, List<BlockNode<T>> children) {
    public List<BlockNode<T>> lowestChildren() {
        List<BlockNode<T>> result = new ObjectArrayList<>();
        if (this.children.isEmpty()) result.add(this);
        else for (BlockNode<T> node : children) result.addAll(node.lowestChildren());
        return result;
    }
}
