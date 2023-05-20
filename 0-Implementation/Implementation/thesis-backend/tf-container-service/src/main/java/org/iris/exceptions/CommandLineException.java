package org.iris.exceptions;

public class CommandLineException extends RuntimeException {
    public CommandLineException(String message) {
        super(message);
    }

    public CommandLineException(String message, Throwable cause) {
        super(message, cause);
    }
}

