package com.vidarin.adminspace.rawlang.ast;

import com.vidarin.adminspace.rawlang.token.Token;

import java.util.List;

public final class CallExpression extends Expression {
    private final Expression callee;
    private final Token semicolon;
    private final List<Expression> args;

    public CallExpression(Expression callee, Token semicolon, List<Expression> args) {
        this.callee = callee;
        this.semicolon = semicolon;
        this.args = args;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.acceptCallExpr(this);
    }

    public Expression callee() {
        return callee;
    }

    public Token semicolon() {
        return semicolon;
    }

    public List<Expression> args() {
        return args;
    }

    @Override
    public String toString() {
        if (args.isEmpty()) return "<" + callee + ">";
        StringBuilder sb = new StringBuilder();
        for (Expression arg : args) sb.append(arg).append(", ");
        return "<" + callee + "(" + sb.substring(0, sb.toString().length() - 2) + ")>";
    }
}
