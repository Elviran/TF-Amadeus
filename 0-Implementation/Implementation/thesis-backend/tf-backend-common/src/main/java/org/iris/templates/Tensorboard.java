package org.iris.templates;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tensorboard {
    String containerName;
    int port;
    boolean success;
    String command;
    boolean executed;
    String exception;

    public Tensorboard(){

    }

    public Tensorboard(String containerName, int port, String command, boolean executed) {
        this.containerName = containerName;
        this.port = port;
        this.command = command;
        this.executed = executed;
    }

    public Tensorboard(String containerName, int port, boolean success ,String exception, boolean executed) {
        this.containerName = containerName;
        this.port = port;
        this.success = success;
        this.exception = exception;
        this.executed = executed;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return String.format("Tensorboard{name=%s, port=%s, command=%s, executed=%s, success=%s, exception=%s}",
                containerName, port, command,executed ,success, exception );
    }
}
