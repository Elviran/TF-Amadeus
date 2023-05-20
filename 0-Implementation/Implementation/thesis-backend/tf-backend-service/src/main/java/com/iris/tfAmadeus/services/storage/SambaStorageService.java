package com.iris.tfAmadeus.services.storage;

import com.iris.tfAmadeus.components.Task;
import com.iris.tfAmadeus.configs.FileStorageProperties;
import com.iris.tfAmadeus.controllers.MultiClusterController;
import com.iris.tfAmadeus.customExceptions.CustomFileNotFoundException;
import com.iris.tfAmadeus.customExceptions.FileStorageException;
import com.iris.tfAmadeus.logic.TFFileHandler;
import com.iris.tfAmadeus.response.ModelStatusResponse;
import org.iris.templates.ClusterJobDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class SambaStorageService {

    private final Path sambaDirectory;

    private static final Logger logger = LoggerFactory.getLogger(SambaStorageService.class);

    @Autowired
    public SambaStorageService(FileStorageProperties fileStorageProperties) {
        this.sambaDirectory = Paths.get(fileStorageProperties.getStorageDir())
                .toAbsolutePath().normalize();
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.sambaDirectory.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new CustomFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new CustomFileNotFoundException("File not found " + fileName, ex);
        }
    }

    public String getClusterModelPath(String clusterName,  Task currentTask){
        if(currentTask == null){
            logger.warn("GetClusterModelPath was called when there is no task available");
            throw new IllegalArgumentException("Task cannot be null");
        }
        return currentTask.getClusterJobDetailByClusterName(clusterName).getModelDir();
    }

    public ModelStatusResponse retrieveModelProgress(String clusterName, Task currentTask) throws IOException {

        if(currentTask == null){
            logger.warn("RetrieveModelProgress was called when there is no task available");
            return null;
        }

        try{
            logger.info("Started retrieve ModelProgress");
            ClusterJobDetail jobDetail = currentTask.getClusterJobDetailByClusterName(clusterName);

            File modelPath = retrieveModelPath(jobDetail.getModelDir());
            int currProgress = readCheckpointModelFile(modelPath);
            int stepsToReach = currentTask.getScriptTemplate().getModelSteps().get(jobDetail.getModelName().trim());
            float percentage = (float) (currProgress * 100 / stepsToReach);
            boolean finished = currProgress >= stepsToReach;
            logger.info(String.format("Successfully retrieved Model Percentage %s:%s",jobDetail.getModelName(), percentage));
            return new ModelStatusResponse(jobDetail.getModelName(), clusterName, percentage, finished);
        }catch (IOException ex){
            logger.error("Error retrieving Model Percentage " + ex.getMessage());
            throw new IOException("Error Retrieving Model Progress" + ex.getMessage());
        }

    }

    private File retrieveModelPath(String filepath) throws IOException {
        File temp = new File(String.format("%s/%s",sambaDirectory, filepath));
        if(!Files.exists(temp.toPath()))
            throw new IOException("File given does not exist");

        File[] files = temp.listFiles();

        String cpPath = "";

        cpPath = String.format("%s/%s/checkpoint", sambaDirectory, filepath);
        logger.info("MODEL PATH:" + cpPath);

//        for(File file : files){
//            if(file.getName().contains("keras")){
//                cpPath = String.format("%s/%s/keras/checkpoint", sambaDirectory, filepath);
//            }else{
//
//            }
//        }

        return new File(cpPath);
    }

    private Integer readCheckpointModelFile(File checkPointFile) throws IOException {
        try {

            if (!Files.exists(checkPointFile.toPath())) {
                logger.error("No Checkpoint file was found for path " + checkPointFile.toPath());
                throw new IOException("Checkpoint file does not exist");
            }

            BufferedReader bufferedReader = new BufferedReader(new FileReader(checkPointFile));

            String text = bufferedReader.readLine();

            if (text.isEmpty())
                return 0;

            bufferedReader.close();
            text = text.substring(text.lastIndexOf("-") + 1, text.lastIndexOf('"'));
            logger.info("TEXT LINE:" + text);

            return Integer.parseInt(text);
        } catch (IOException ex) {
            logger.error("Error parsing checkpoint file " + ex.getMessage());
            throw new IOException("Unable to read or parse checkpoint file");
        }

    }
}
