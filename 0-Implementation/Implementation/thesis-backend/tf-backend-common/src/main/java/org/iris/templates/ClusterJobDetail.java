package org.iris.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClusterJobDetail {
    private String task;
    private String clusterName;
    private String jobName;
    private String modelDir;
    private String workDir;
    private String modelName;
    private String envFile;
    private String envDir;
    private String dockerDir;
    private Tensorboard tensorboard;

    public ClusterJobDetail(){
        // Default constructor..
    }

    public ClusterJobDetail(String task, String jobName, String modelDir, String workDir, String modelName, String envFile) {
        this.task = task;
        this.jobName = jobName;
        this.modelDir = modelDir;
        this.workDir = workDir;
        this.modelName = modelName;
        this.envFile = envFile;
    }

    public ClusterJobDetail(String task, String clusterName ,String jobName, String modelDir, String workDir,
                            String modelName, String envFile) {
        this.task = task;
        this.clusterName = clusterName;
        this.jobName = jobName;
        this.modelDir = modelDir;
        this.workDir = workDir;
        this.modelName = modelName;
        this.envFile = envFile;
        this.tensorboard = tensorboard;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getModelDir() {
        return modelDir;
    }

    public void setModelDir(String modelDir) {
        this.modelDir = modelDir;
    }

    public String getWorkDir() {
        return workDir;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getEnvFile() {
        return envFile;
    }

    public void setEnvFile(String envFile) {
        this.envFile = envFile;
    }

    public String getEnvDir() {
        return envDir;
    }

    public void setEnvDir(String envDir) {
        this.envDir = envDir;
    }

    public String getDockerDir() {
        return dockerDir;
    }

    public void setDockerDir(String dockerDir) {
        this.dockerDir = dockerDir;
    }

    public Tensorboard getTensorboard() {
        return tensorboard;
    }

    public void setTensorboard(Tensorboard tensorboard) {
        this.tensorboard = tensorboard;
    }

    @Override
    public String toString() {
        return String.format("ClusterJobDetail{task=%s,clusterName=%s,jobName=%s,modelDir=%s, workDir=%s ,modelName=%s, " +
                        "envFile=%s, envDir=%s, dockerDir=%s, tensorboard=%s}",
                task, clusterName, jobName ,modelDir, workDir ,modelName, envFile, envDir, dockerDir, tensorboard);
    }
}