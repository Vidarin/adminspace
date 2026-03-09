package com.vidarin.adminspace.rawlang.ast;

import com.github.bsideup.jabel.Desugar;
import com.vidarin.adminspace.rawlang.token.Token;
import org.jetbrains.annotations.NotNull;

@Desugar
public record WhileStatement(Token keyword, Expression condition, Statement body) implements Statement {
    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.acceptWhileStmt(this);
    }

    @Override
    public @NotNull String toString() {
        return "while (" + condition + ") {" + body + "}";
    }
}
