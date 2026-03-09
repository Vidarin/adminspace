package com.vidarin.adminspace.rawlang.type;

import com.github.bsideup.jabel.Desugar;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Desugar
public record ExprType(@NotNull Type[] paramTypes, @NotNull Type returnType) implements Type {
    @Override
    public byte getCode() {
        return Type.CODE_EXPR;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AnyType) return true;
        return obj instanceof ExprType exprType && Arrays.equals(exprType.paramTypes, this.paramTypes);
    }

    @Override
    public int hashCode() {
        return getCode();
    }

    @Override
    public @NotNull String toString() {
        return "EXPR" + paramTypesString() + "->" + returnType;
    }

    private String paramTypesString() {
        if (paramTypes.length == 0) return "";

        StringBuilder sb = new StringBuilder();

        sb.append('(');
        for (Type type : paramTypes) {
            sb.append(type);
            sb.append(',');
        }
        String s = sb.toString();
        return s.substring(0, s.length() - 1) + ")";
    }

    public static ExprType of(Type returnType, Type... paramTypes) {
        return new ExprType(paramTypes, returnType);
    }
}
