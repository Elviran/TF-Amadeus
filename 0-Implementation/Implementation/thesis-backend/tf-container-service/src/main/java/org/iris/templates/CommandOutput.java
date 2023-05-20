package org.iris.templates;

public class CommandOutput {
    String line;
    boolean error;
    String message;

    public CommandOutput(String line, boolean error, String message) {
        this.line = line;
        this.error = error;
        this.message = message;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
