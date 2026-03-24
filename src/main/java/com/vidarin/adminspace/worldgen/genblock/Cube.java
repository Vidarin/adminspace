package com.vidarin.adminspace.worldgen.genblock;

import com.github.bsideup.jabel.Desugar;
import net.minecraft.util.math.Vec3i;

@Desugar
public record Cube(Vec3i from, Vec3i to) {}
