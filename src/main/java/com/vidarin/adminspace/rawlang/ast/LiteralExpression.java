package com.vidarin.adminspace.rawlang.ast;

public final class LiteralExpression extends Expression {
    private final Object value;

    public LiteralExpression(Object value) {
        this.value = value;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.acceptLiteralExpr(this);
    }

    public Object value() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
