package com.vidarin.adminspace.rawlang.interpreter;

import com.github.bsideup.jabel.Desugar;
import com.vidarin.adminspace.rawlang.type.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Desugar
public record TypedObject(@Nullable Object value, @NotNull Type type) {
    public static final TypedObject VOID = new TypedObject(null, Type.ANY);

    public static TypedObject of(@NotNull Type type, @Nullable Object value) {
        return new TypedObject(value, type);
    }

    @Override
    public @NotNull String toString() {
        return "(" + value + "):" + type;
    }
}
