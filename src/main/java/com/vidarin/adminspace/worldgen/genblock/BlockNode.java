package com.vidarin.adminspace.worldgen.genblock;

import com.github.bsideup.jabel.Desugar;
import com.vidarin.adminspace.worldgen.grammar.Shape;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

@Desugar
public record BlockNode(@NotNull Shape<Cube> cube, @NotNull Collection<BlockNode> children) {
    public @NotNull List<BlockNode> lowestChildren() {
        List<BlockNode> result = new ObjectArrayList<>();
        if (this.children.isEmpty()) result.add(this);
        else for (BlockNode node : children) result.addAll(node.lowestChildren());
        return result;
    }
}
