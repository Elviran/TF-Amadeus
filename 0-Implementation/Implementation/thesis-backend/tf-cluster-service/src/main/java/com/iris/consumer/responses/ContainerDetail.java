package com.iris.consumer.responses;

public class ContainerDetail {
    private String envFile;
    private String dockerDir;
    private String cluster;
    private String name;

    public ContainerDetail(String envFile, String dockerDir, String containerName, String cluster){
        this.envFile = envFile;
        this.dockerDir = dockerDir;
        this.name = containerName;
        this.cluster = cluster;
    }

    public String getEnvFile() {
        return envFile;
    }

    public void setEnvFile(String envFile) {
        this.envFile = envFile;
    }

    public String getDockerDir() {
        return dockerDir;
    }

    public void setDockerDir(String dockerDir) {
        this.dockerDir = dockerDir;
    }

    public String getCluster() {
        return cluster;
    }

    public String getName() {
        return name;
    }

    private void setCluster(String cluster){
        this.cluster = cluster;
    }

    private void setName(String name){
        this.name = name;
    }
}
