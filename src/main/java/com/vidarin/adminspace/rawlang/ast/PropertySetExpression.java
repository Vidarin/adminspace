package com.vidarin.adminspace.rawlang.ast;

import com.vidarin.adminspace.rawlang.token.Token;

public final class PropertySetExpression extends Expression {
    private final Expression property;
    private final Token name;
    private final Expression value;

    public PropertySetExpression(Expression property, Token name, Expression value) {
        this.property = property;
        this.name = name;
        this.value = value;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.acceptPropertySetExpr(this);
    }
    
    public Expression property() {
        return property;
    }

    public Token name() {
        return name;
    }

    public Expression value() {
        return value;
    }

    @Override
    public String toString() {
        return property + "."  + name.lexeme() + "=" + value;
    }
}
