package com.vidarin.adminspace.worldgen.grammar;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record Shape<T>(Symbol symbol, T shape, int[] metadata) {}
