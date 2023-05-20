package com.iris.tfAmadeus.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
    private String uploadDir;
    private String storageDir;
    private String configDir;
    private String tfConfDir;
    private String runnerDir;
    private String sambaDir;

    public String[] getPaths(){
        String[] paths = {uploadDir, storageDir, configDir,  tfConfDir, runnerDir};
        return paths;
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getStorageDir() {
        return storageDir;
    }

    public void setStorageDir(String storageDir) {
        this.storageDir = storageDir;
    }

    public String getConfigDir() {
        return configDir;
    }

    public void setConfigDir(String configDir) {
        this.configDir = configDir;
    }

    public String getTfConfDir() {
        return tfConfDir;
    }

    public void setTfConfDir(String tfConfDir) {
        this.tfConfDir = tfConfDir;
    }

    public String getRunnerDir() {
        return runnerDir;
    }

    public void setRunnerDir(String runnerDir) {
        this.runnerDir = runnerDir;
    }
}
