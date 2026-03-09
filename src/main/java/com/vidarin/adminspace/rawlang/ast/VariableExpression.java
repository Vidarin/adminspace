package com.vidarin.adminspace.rawlang.ast;

import com.vidarin.adminspace.rawlang.token.Token;

public final class VariableExpression extends Expression {
    private final Token name;

    public VariableExpression(Token name) {
        this.name = name;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.acceptVariableExpr(this);
    }

    public Token name() {
        return name;
    }

    @Override
    public String toString() {
        return name.lexeme();
    }
}
