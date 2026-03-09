package com.vidarin.adminspace.rawlang.ast;

import com.github.bsideup.jabel.Desugar;
import com.vidarin.adminspace.rawlang.token.Token;
import com.vidarin.adminspace.rawlang.type.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Intellij hates this specific class for some reason
@Desugar
public record FieldStatement(Token keyword, Type type, Token name, @Nullable Expression initializer) implements Statement {
    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.acceptFieldStmt(this); // <-- there is apparently an error here, according to intellij
    }

    @Override
    public @NotNull String toString() {
        if (initializer == null) return keyword.lexeme() + " " + type + " " + name.lexeme();
        return keyword.lexeme() + " " + type + " " + name.lexeme() + " = " + initializer;
    }
}
