package com.vidarin.adminspace.rawlang.ast;

import com.vidarin.adminspace.rawlang.token.Token;
import com.vidarin.adminspace.rawlang.type.Type;
import org.jetbrains.annotations.Nullable;

public final class DeclarationExpression extends Expression {
    private final Type declaredType;
    private final Token name;
    private final Expression initializer;

    public DeclarationExpression(Type declaredType, Token name, @Nullable Expression initializer) {
        this.declaredType = declaredType;
        this.name = name;
        this.initializer = initializer;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.acceptDeclarationExpr(this);
    }

    public Type declaredType() {
        return declaredType;
    }

    public Token name() {
        return name;
    }

    public Expression initializer() {
        return initializer;
    }

    @Override
    public String toString() {
        return "<$" + declaredType + " " + name.lexeme() + "=" + initializer + ">";
    }
}
