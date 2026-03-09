package com.vidarin.adminspace.rawlang.ast;

import com.github.bsideup.jabel.Desugar;
import org.jetbrains.annotations.NotNull;

@Desugar
public record ExpressionStatement(Expression expression) implements Statement {
    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.acceptExpressionStmt(this);
    }

    @Override
    public @NotNull String toString() {
        return expression.toString();
    }
}
