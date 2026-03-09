package com.vidarin.adminspace.rawlang.ast;

import com.github.bsideup.jabel.Desugar;
import com.vidarin.adminspace.rawlang.token.Token;
import org.jetbrains.annotations.NotNull;

@Desugar
public record FactoryStatement(Token keyword, FunctionTypeExpression function) implements Statement {
    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.acceptFactoryStmt(this);
    }

    @Override
    public @NotNull String toString() {
        return "factory " + function;
    }
}
