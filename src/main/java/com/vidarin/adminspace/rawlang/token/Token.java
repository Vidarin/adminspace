package com.vidarin.adminspace.rawlang.token;

import com.github.bsideup.jabel.Desugar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Desugar
public record Token(TokenType type, String lexeme, @Nullable Object literal, int line) {
    @Override
    public @NotNull String toString() {
        return "RawLang Token[" + type + ", " + lexeme + ", " + literal + "]";
    }
}
