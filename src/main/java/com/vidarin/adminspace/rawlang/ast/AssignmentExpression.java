package com.vidarin.adminspace.rawlang.ast;

import com.vidarin.adminspace.rawlang.token.Token;

public final class AssignmentExpression extends Expression {
    private final Token name;
    private final Expression value;

    public AssignmentExpression(Token name, Expression value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.acceptAssignmentExpr(this);
    }
    
    public Token name() {
        return name;
    }

    public Expression value() {
        return value;
    }

    @Override
    public String toString() {
        return "<$" + name.lexeme() + "=" + value + ">";
    }
}
