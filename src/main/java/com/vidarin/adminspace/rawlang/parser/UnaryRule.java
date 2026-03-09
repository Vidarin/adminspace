package com.vidarin.adminspace.rawlang.parser;

import com.github.bsideup.jabel.Desugar;
import com.vidarin.adminspace.rawlang.type.Type;
import org.jetbrains.annotations.Nullable;

@Desugar
public record UnaryRule(String operator, Type type, @Nullable Type returnType) {
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof UnaryRule other)) return false;
        return this.operator.equals(other.operator)
            && this.type.equals(other.type); // Ignores returnType for a reason
    }
}
