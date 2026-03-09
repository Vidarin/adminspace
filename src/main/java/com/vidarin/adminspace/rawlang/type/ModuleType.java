package com.vidarin.adminspace.rawlang.type;

import com.github.bsideup.jabel.Desugar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

@Desugar
public record ModuleType(@NotNull String moduleName, @Nullable Type[] genericTypes, @Nullable ModuleType subModule) implements Type {
    @Override
    public byte getCode() {
        return Type.CODE_MODULE;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AnyType) return true;
        return obj instanceof ModuleType moduleType &&
                this.moduleName.equals(moduleType.moduleName) &&
                Arrays.equals(moduleType.genericTypes, this.genericTypes) &&
                Objects.equals(moduleType.subModule, this.subModule);
    }

    public ModuleType deepest() {
        if (subModule != null) return subModule.deepest();
        else return this;
    }

    @Override
    public int hashCode() {
        return getCode();
    }

    @Override
    public @NotNull String toString() {
        return moduleName + genericTypesString() + (subModule == null ? "" : "." + subModule);
    }

    private String genericTypesString() {
        if (genericTypes == null) return "";

        StringBuilder sb = new StringBuilder();

        sb.append('(');
        for (Type type : genericTypes) {
            sb.append(type);
            sb.append(',');
        }
        String s = sb.toString();
        return s.substring(0, s.length() - 1) + ")";
    }
}
