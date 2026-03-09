package com.vidarin.adminspace.rawlang;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@FunctionalInterface
public interface ErrorPrinter {
    void print(String s);

    static ErrorPrinter syserr() {
        return System.err::println;
    }

    static ErrorPrinter log4j(Logger logger, Level level) {
        return s -> logger.log(level, s);
    }

    static ErrorPrinter none() {
        return s -> {};
    }
}
