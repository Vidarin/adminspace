package com.vidarin.adminspace.rawlang.ast;

import com.vidarin.adminspace.rawlang.token.Token;

public final class UnaryPostfixExpression extends Expression {
    private final Expression left;
    private final Token operator;

    public UnaryPostfixExpression(Expression left, Token operator) {
        this.left = left;
        this.operator = operator;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.acceptUnaryPostfixExpr(this);
    }

    public Expression left() {
        return left;
    }

    public Token operator() {
        return operator;
    }

    @Override
    public String toString() {
        return "<" + left + operator + ">";
    }
}
