package com.iris.consumer.services;

import com.iris.consumer.smb.SmbConnectionImpl;
import org.iris.templates.ClusterJobDetail;
import org.iris.templates.ClusterTaskStatus;
import org.iris.templates.ContainerStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class ClusterService {

    @Autowired
    private SmbConnectionImpl smbConnection;

    @Autowired
    private ClusterQueryImpl clusterQueryImpl;

    @Value("${destination.name}")
    private String clusterName;

    private ArrayList<ContainerStatus> containers;

    private static HashMap<String,String> urls;

    public ClusterService(){
        urls = new HashMap<>();
    }

    public ClusterTaskStatus startContainers(ClusterJobDetail detail) {
        if(detail == null)
            throw new IllegalArgumentException("Cluster details cannot be null");


        try{
            prepareDockerDirectory(detail);
            ArrayList<String> envFiles = getEnvFilesInPath(detail.getEnvDir());
            urls = prepareServicesUrls(String.format("%s/%s", detail.getWorkDir(), detail.getEnvFile()));
//            urls = new HashMap<String,String>();
//            urls.put("chief","http://localhost:8087");
            containers = clusterQueryImpl.startContainers(detail, envFiles, urls);

            return new ClusterTaskStatus(detail.getTask(),true, detail.getModelDir(), detail.getModelName(), containers,
                    clusterName, detail.getTensorboard(),null);

        } catch (Exception e) {
            return new ClusterTaskStatus(detail.getTask(),false, detail.getModelDir(), detail.getModelName(), containers,
                    clusterName, detail.getTensorboard(), e.getMessage());
        }

    }


    public ClusterTaskStatus stopContainers(ClusterJobDetail detail) throws IOException {
        if(detail == null)
            return new ClusterTaskStatus(clusterName, "stop", false, "Cannot Stop containers as there is no task..");

        if(urls.size() == 0)
            urls = prepareServicesUrls(String.format("%s/%s", detail.getWorkDir(), detail.getEnvFile()));

        try{
            containers = clusterQueryImpl.stopContainers(urls);
            return new ClusterTaskStatus(detail.getTask(),true, detail.getModelDir(), detail.getModelName(), containers,
                    clusterName, detail.getTensorboard(),null);

        } catch (Exception e) {
            return new ClusterTaskStatus(detail.getTask(),false, detail.getModelDir(), detail.getModelName(), containers,
                    clusterName, detail.getTensorboard(), e.getMessage());
        }

    }

    public ClusterTaskStatus pauseContainers(ClusterJobDetail detail) throws IOException {
        if(detail == null)
            return new ClusterTaskStatus(clusterName, "pause", false, "Cannot Pause containers as there is no task..");

        if(urls.size() == 0)
            urls = prepareServicesUrls(String.format("%s/%s", detail.getWorkDir(), detail.getEnvFile()));

        try{
            containers = clusterQueryImpl.pauseContainers(urls);

            return new ClusterTaskStatus(detail.getTask(),true, detail.getModelDir(), detail.getModelName(), containers,
                    clusterName, detail.getTensorboard(),null);

        } catch (Exception e) {
            return new ClusterTaskStatus(detail.getTask(),false, detail.getModelDir(), detail.getModelName(), containers,
                    clusterName, detail.getTensorboard(), e.getMessage());
        }

    }

    public ClusterTaskStatus resumeContainers(ClusterJobDetail detail) throws IOException {
        if(detail == null)
            return new ClusterTaskStatus(clusterName, "resume", false, "Cannot Resume containers as there is no task..");

        if(urls.size() == 0)
            urls = prepareServicesUrls(String.format("%s/%s", detail.getWorkDir(), detail.getEnvFile()));

        try{
            containers = clusterQueryImpl.resumeContainers(urls);

            return new ClusterTaskStatus(detail.getTask(),true, detail.getModelDir(), detail.getModelName(), containers,
                    clusterName, detail.getTensorboard(),null);

        } catch (Exception e) {
            return new ClusterTaskStatus(detail.getTask(),false, detail.getModelDir(), detail.getModelName(), containers,
                    clusterName, detail.getTensorboard(), e.getMessage());
        }
    }

    public ClusterTaskStatus restartContainers(ClusterJobDetail detail) throws IOException {

        if(detail == null)
            return new ClusterTaskStatus(clusterName, "restart", false, "Cannot Restart containers as there is no task..");

        if(urls.size() == 0)
            urls = prepareServicesUrls(String.format("%s/%s", detail.getWorkDir(), detail.getEnvFile()));

        try{
            containers = clusterQueryImpl.restartContainers(urls);

            return new ClusterTaskStatus(detail.getTask(),true, detail.getModelDir(), detail.getModelName(), containers,
                    clusterName, detail.getTensorboard(),null);

        } catch (Exception e) {
            return new ClusterTaskStatus(detail.getTask(),false, detail.getModelDir(), detail.getModelName(), containers,
                    clusterName, detail.getTensorboard(), e.getMessage());
        }
    }

    public ClusterTaskStatus statusContainers(ClusterJobDetail detail) throws IOException {
        //delete pending directory
        if(detail == null)
            return new ClusterTaskStatus(clusterName, "status", false, "Cannot Get status of containers as there is no task..");

        if(urls.size() == 0)
            urls = prepareServicesUrls(String.format("%s/%s", detail.getWorkDir(), detail.getEnvFile()));

        try{
            containers = clusterQueryImpl.containerStatus(urls);

            return new ClusterTaskStatus(detail.getTask(),true, detail.getModelDir(), detail.getModelName(), containers,
                    clusterName, detail.getTensorboard(),null);

        } catch (Exception e) {
            return new ClusterTaskStatus(detail.getTask(),false, detail.getModelDir(), detail.getModelName(), containers,
                    clusterName, detail.getTensorboard(), e.getMessage());
        }
    }

    public ClusterTaskStatus removeContainers(ClusterJobDetail detail) throws IOException {
        if(detail == null)
            return new ClusterTaskStatus(clusterName, "remove", false, "Cannot Remove containers as there is no task..");

        if(urls.size() == 0)
            urls = prepareServicesUrls(String.format("%s/%s", detail.getWorkDir(), detail.getEnvFile()));

        try{
            containers = clusterQueryImpl.deleteContainers(urls);

            return new ClusterTaskStatus(detail.getTask(),true, detail.getModelDir(), detail.getModelName(), containers,
                    clusterName, detail.getTensorboard(),null);

        } catch (Exception e) {
            return new ClusterTaskStatus(detail.getTask(),false, detail.getModelDir(), detail.getModelName(), containers,
                    clusterName, detail.getTensorboard(), e.getMessage());
        }
    }

    public Map.Entry<String,String> getUrlPair (String key) throws Exception {
        if(urls == null)
            throw new Exception("Task path is not set inorder to retrieve cluster urls");

        for(Map.Entry<String, String> entry: urls.entrySet()){
            if(entry.getKey().equals(key)){
                return entry;
            }
        }
        return null;
    }

    /**
     * This method prepares the work directory assignment to it and
     * tranfers the necessary docker directory and contents in the directory.
     * This docker directory will then be used by the docker-container-service
     * to perform docker commands.
     * @param detail
     */
    private void prepareDockerDirectory(ClusterJobDetail detail) {

        //Directory paths that will be created inside the created pending task.
        String dockerDir    = detail.getWorkDir() + "/docker/";
        String tfConfigsDir = detail.getWorkDir() + "/tfConfigs/";
        String envFilesDir = detail.getWorkDir() + "/docker/envFiles/";

        //Change JobClusterDetail to mention runnable script.
        String runnerScript = detail.getWorkDir() + "/runner.py";
        String userScript = detail.getWorkDir() + "/user.py";

        //TODO:choose docker dir appropriate if they are mw or ps (can take from tfConfigs)
        String dockerFilesDir = "docker/tf-ps-docker/";

        try{
            //Create docker directory and env file directory inside it
            smbConnection.createNewDirectory(dockerDir);
            smbConnection.createNewDirectory(envFilesDir);

            //Copy docker files to the selected work directory
            smbConnection.copyFile(dockerFilesDir, dockerDir);
            smbConnection.copyFile(runnerScript, dockerDir+"runner.py");
            smbConnection.copyFile(userScript, dockerDir+"user.py");

            ArrayList<String> tfFiles = smbConnection.getFileNamesInDirectory(tfConfigsDir);

            //For each tensorflow config file, copy template env file and add env configurations
            for(String fileName : tfFiles){
                String envDirName   = fileName.substring(0, fileName.indexOf("."));
                String envFile      = String.format("%s/%s", detail.getWorkDir(), detail.getEnvFile());

                //copy Template environment file in the Docker Path
               if(smbConnection.copyFile(envFile, String.format("%s%s.env", envFilesDir, envDirName))){
                   prepareEnvironmentFile(String.format("%s%s.env", envFilesDir, envDirName),
                           detail, tfConfigsDir + fileName);
               }else{
                   throw new Exception("Failed to prep env file as copying of original template file was not successful");
               }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        detail.setDockerDir(dockerDir);
        detail.setEnvDir(envFilesDir);
    }

    /**
     * Retrieves the ip's of the cluster and returns them in a form of a string
     * to be able to connect with each machine through the ip
     * @param path
     * @return
     * @throws IOException
     */
    private HashMap<String, String> prepareServicesUrls(String path) throws IOException {
        HashMap<String, String> clusterUrls = new HashMap<>();

        String contents = smbConnection.readFile(path);

        String[] split = contents.split("\r\n");

        for(String line : split){
            if(!line.contains("master")){
                clusterUrls.put(line.substring(0, line.indexOf("=")), String.format("http://%s:8086",line.substring(line.indexOf("=")+1).trim()));
            }
        }

        return clusterUrls;
    }

    private ArrayList<String> getEnvFilesInPath(String envPath) throws Exception {
        //return a list of env file names.
        return smbConnection.getFileNamesInDirectory(envPath);
    }

    private void prepareEnvironmentFile(String envFileDir, ClusterJobDetail detail, String tfConfig) throws Exception {
        ArrayList<String> contentsToWrite = new ArrayList<>();

        //Add these into the configuration file
        contentsToWrite.add(String.format("\r\nworkspace=%s\n",detail.getWorkDir()));
        contentsToWrite.add(String.format("model_fn=%s\n", detail.getModelName()));
        contentsToWrite.add(String.format("model_dir=%s\n", detail.getModelDir()));
        contentsToWrite.add(String.format("tf_config=%s\n", tfConfig));
        contentsToWrite.add(String.format("container_name=%s\n",
                tfConfig.substring(tfConfig.lastIndexOf("/") + 1, tfConfig.indexOf("."))));

        try{
            smbConnection.appendToFile(envFileDir, contentsToWrite);
        }catch (Exception ex){
            throw new Exception("Failed to prepare Environment file:" + ex);
        }

    }
}
