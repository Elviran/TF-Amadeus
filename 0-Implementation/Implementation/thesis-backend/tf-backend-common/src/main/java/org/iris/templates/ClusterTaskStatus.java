package org.iris.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClusterTaskStatus {
    private String task;
    private Boolean status;
    private String modelDir;
    private String modelName;
    private String clusterName;
    private ArrayList<ContainerStatus> containers;
    private Tensorboard tensorboard;
    private String exception;

    public ClusterTaskStatus() {

    }

    public ClusterTaskStatus(String task, Boolean status, String modelDir, String modelName,
                             ArrayList<ContainerStatus> containers, String clusterName, Tensorboard board,
                             String exception) {
        this.task = task;
        this.status = status;
        this.modelDir = modelDir;
        this.modelName = modelName;
        this.clusterName = clusterName;
        this.containers = containers;
        this.tensorboard = board;
        this.exception = exception;
    }

    public ClusterTaskStatus(String clusterName, String task, Boolean status, String exception){
        this.clusterName = clusterName;
        this.task = task;
        this.status = status;
        this.exception = exception;
    }

    public ArrayList<ContainerStatus> getContainerBySuccess(Boolean success){
        return (ArrayList<ContainerStatus>) getContainers()
                .stream()
                .filter(status -> status != null ? status.getSuccess().equals(success) : null)
                .collect(Collectors.toList());
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getModelDir() {
        return modelDir;
    }

    public void setModelDir(String modelDir) {
        this.modelDir = modelDir;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public ArrayList<ContainerStatus> getContainers() {
        return containers;
    }

    public void setContainers(ArrayList<ContainerStatus> containers) {
        this.containers = containers;
    }

    public Tensorboard getTensorboard() {
        return tensorboard;
    }

    public void setTensorboard(Tensorboard tensorboard) {
        this.tensorboard = tensorboard;
    }

    @Override
    public String toString() {
        return "ClusterTaskStatus: {" +
                task + " ," +
                status + " ," +
                modelDir + " ," +
                modelName + " ," +
                clusterName + " ," +
                containers + " ," +
                tensorboard + " ," +
                exception + " }";
    }

}
