package org.iris.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContainerStatus {
    String name;
    String task;
    String status;
    Boolean success;
    String exception;

    public ContainerStatus(){

    }

    public ContainerStatus(String containerName, String task, Boolean success, String exception) {
        this.name = containerName;
        this.task = task;
        this.success = success;
        this.exception = exception;
    }

    public ContainerStatus(String containerName, String task, String status, Boolean success, String exception) {
        this.name = containerName;
        this.task = task;
        this.status = status;
        this.success = success;
        this.exception = exception;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("ContainerStatus{name=%s,task=%s,status=%s,success=%s, exception=%s}",
                name, task, status ,success, exception );
    }
}
