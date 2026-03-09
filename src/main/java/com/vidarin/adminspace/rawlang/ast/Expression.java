package com.vidarin.adminspace.rawlang.ast;

import com.vidarin.adminspace.rawlang.type.Type;
import org.jetbrains.annotations.NotNull;

public abstract class Expression {
    private Type type;

    public abstract <R> R accept(ExprVisitor<R> visitor);

    public void setType(@NotNull Type type) {
        this.type = type;
    }

    public Type type() {
        return this.type;
    }
}
