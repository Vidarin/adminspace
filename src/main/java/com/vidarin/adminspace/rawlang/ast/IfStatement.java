package com.vidarin.adminspace.rawlang.ast;

import com.github.bsideup.jabel.Desugar;
import com.vidarin.adminspace.rawlang.token.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Desugar
public record IfStatement(Token keyword, Expression condition, Statement thenBranch, @Nullable Statement elseBranch) implements Statement {
    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.acceptIfStmt(this);
    }

    @Override
    public @NotNull String toString() {
        return "if (" + condition.toString() + ") {" + thenBranch + (elseBranch == null ? "}" : ("} else {" + elseBranch + "}"));
    }
}
