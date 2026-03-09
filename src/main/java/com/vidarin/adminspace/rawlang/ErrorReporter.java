package com.vidarin.adminspace.rawlang;

public interface ErrorReporter {
    void error(int line, String message);

    default void error(int line, String message, Object... args) {
        error(line, String.format(message, args));
    }

    void runtimeError(RawLangRuntimeError error);
}
