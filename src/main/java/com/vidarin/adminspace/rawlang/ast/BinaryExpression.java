package com.vidarin.adminspace.rawlang.ast;

import com.vidarin.adminspace.rawlang.token.Token;

public final class BinaryExpression extends Expression {
    private final Expression left;
    private final Token operator;
    private final Expression right;

    public BinaryExpression(Expression left, Token operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.acceptBinaryExpr(this);
    }

    public Expression left() {
        return left;
    }

    public Token operator() {
        return operator;
    }

    public Expression right() {
        return right;
    }

    @Override
    public String toString() {
        return "<" + left + operator.lexeme() + right + ">";
    }
}
