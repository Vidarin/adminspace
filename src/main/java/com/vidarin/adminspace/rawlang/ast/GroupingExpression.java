package com.vidarin.adminspace.rawlang.ast;

public final class GroupingExpression extends Expression {
    private final Expression expression;

    public GroupingExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.acceptGroupingExpr(this);
    }

    public Expression expression() {
        return expression;
    }

    @Override
    public String toString() {
        return "(" + expression + ")";
    }
}
