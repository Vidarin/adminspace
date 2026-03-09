package com.vidarin.adminspace.rawlang;

import com.vidarin.adminspace.rawlang.token.Token;

public class RawLangRuntimeError extends RuntimeException {
    public final Token token;

    public RawLangRuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
