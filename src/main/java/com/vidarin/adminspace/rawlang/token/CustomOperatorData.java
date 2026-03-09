package com.vidarin.adminspace.rawlang.token;

import com.github.bsideup.jabel.Desugar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static com.vidarin.adminspace.rawlang.token.TokenType.INFIX;
import static com.vidarin.adminspace.rawlang.token.TokenType.PREFIX;

@Desugar
public record CustomOperatorData(@NotNull TokenType fix, @Nullable List<Token> typeLeft, @Nullable List<Token> typeRight, @NotNull List<Token> returnType) {
    public @Nullable List<Token> type() {
        if (fix == INFIX) return null;
        else return fix == PREFIX ? typeRight : typeLeft;
    }

    @Override
    public @NotNull String toString() {
        return "CustomOperatorData[" + fix + ", " + typeLeft + ", " + typeRight + ", " + returnType + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof CustomOperatorData other)) return false;
        return other.fix == this.fix
            && Objects.equals(this.typeLeft, other.typeLeft)
            && Objects.equals(this.typeRight, other.typeRight)
            && this.returnType.equals(other.returnType);
    }
}
