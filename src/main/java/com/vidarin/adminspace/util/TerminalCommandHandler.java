package com.vidarin.adminspace.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.Objects;


public class TerminalCommandHandler {
    private Logger logger;

    public void runCommand(String command) {
        if (Objects.equals(command, "test"))
            logger.log(Level.DEBUG, "test message");
    }
}
