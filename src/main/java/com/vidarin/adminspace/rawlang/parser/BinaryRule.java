package com.vidarin.adminspace.rawlang.parser;

import com.github.bsideup.jabel.Desugar;
import com.vidarin.adminspace.rawlang.type.Type;
import org.jetbrains.annotations.Nullable;

@Desugar
public record BinaryRule(String operator, Type left, Type right, @Nullable Type returnType) {
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof BinaryRule other)) return false;
        return this.operator.equals(other.operator)
            && this.left.equals(other.left)
            && this.right.equals(other.right); // Ignores returnType for a reason
    }
}
