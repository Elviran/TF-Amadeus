package com.iris.tfAmadeus.response;

public class ModelStatusResponse {
    String name;
    String clusterName;
    float percentage;
    boolean finished;

    public ModelStatusResponse(){

    }

    public ModelStatusResponse(String name, String clusterName, float percentage, boolean finished) {
        this.name = name;
        this.clusterName = clusterName;
        this.percentage = percentage;
        this.finished = finished;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
