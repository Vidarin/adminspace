package com.vidarin.adminspace.rawlang.ast;

import com.vidarin.adminspace.rawlang.token.Token;

public final class PropertyGetExpression extends Expression {
    private final Expression property;
    private final Token name;

    public PropertyGetExpression(Expression property, Token name) {
        this.property = property;
        this.name = name;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.acceptPropertyGetExpr(this);
    }
    
    public Expression property() {
        return property;
    }

    public Token name() {
        return name;
    }

    @Override
    public String toString() {
        return property + "." + name.lexeme();
    }
}
