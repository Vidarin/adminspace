package com.vidarin.adminspace.rawlang.type;

import com.github.bsideup.jabel.Desugar;
import org.jetbrains.annotations.NotNull;

@Desugar
public record ArrayType(@NotNull Type elementType, @NotNull Type keyType) implements Type {
    @Override
    public byte getCode() {
        return Type.CODE_ARRAY;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AnyType) return true;
        return obj instanceof ArrayType arrayType && arrayType.elementType.equals(this.elementType) && arrayType.keyType.equals(this.keyType);
    }

    @Override
    public int hashCode() {
        return getCode();
    }

    @Override
    public @NotNull String toString() {
        return elementType + "[" + keyType + "]";
    }
}
