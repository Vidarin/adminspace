package com.vidarin.adminspace.worldgen.grammar;

import com.github.bsideup.jabel.Desugar;
import org.jetbrains.annotations.NotNull;

@Desugar
public record Shape<T>(@NotNull Symbol symbol, @NotNull T shape, int @NotNull [] meta) {
    public Shape(@NotNull Symbol symbol, @NotNull T shape) {
        this(symbol, shape, new int[0]);
    }
}
