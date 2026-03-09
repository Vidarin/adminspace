package com.vidarin.adminspace.rawlang.ast;

import com.vidarin.adminspace.rawlang.token.Token;
import com.vidarin.adminspace.rawlang.type.Type;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public final class FunctionTypeExpression extends Expression {
    private final List<Pair<Type, Token>> params;
    private final Type returnType;
    private final List<Statement> body;

    public FunctionTypeExpression(List<Pair<Type, Token>> params, Type returnType, List<Statement> body) {
        this.params = params;
        this.returnType = returnType;
        this.body = body;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.acceptFunctionTypeExpr(this);
    }

    public List<Pair<Type, Token>> params() {
        return params;
    }

    public Type returnType() {
        return returnType;
    }

    public List<Statement> body() {
        return body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(returnType + "-> (");
        for (Pair<Type, Token> param : params) sb.append('$').append(param.getLeft()).append(' ').append(param.getRight().lexeme()).append(' ');
        sb.append(") {\n");
        for (Statement statement : body) sb.append(statement).append('\n');
        sb.append('}');
        return sb.toString();
    }
}
