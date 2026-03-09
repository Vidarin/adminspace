package com.vidarin.adminspace.rawlang.ast;

import com.github.bsideup.jabel.Desugar;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Desugar
public record BlockStatement(List<Statement> statements) implements Statement {
    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.acceptBlockStatement(this);
    }

    @Override
    public @NotNull String toString() {
        StringBuilder sb = new StringBuilder("{\n");
        for (Statement statement : statements) sb.append(statement).append("\n");
        sb.append('}');
        return sb.toString();
    }
}
