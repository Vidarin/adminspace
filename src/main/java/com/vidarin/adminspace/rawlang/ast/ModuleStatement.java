package com.vidarin.adminspace.rawlang.ast;

import com.github.bsideup.jabel.Desugar;
import com.vidarin.adminspace.rawlang.token.Token;
import com.vidarin.adminspace.rawlang.type.ModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Desugar
public record ModuleStatement(Token name, @Nullable ModuleType supermodule, List<Statement> body) implements Statement {
    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.acceptModuleStmt(this);
    }

    @Override
    public @NotNull String toString() {
        return "module " + name + " {\n" + body + "\n}";
    }
}
