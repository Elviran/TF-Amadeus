package com.iris.tfAmadeus.controllers;

import com.iris.tfAmadeus.components.PendingTasks;
import com.iris.tfAmadeus.logic.ZipFileHandler;
import com.iris.tfAmadeus.response.ModelStatusResponse;
import com.iris.tfAmadeus.services.ModelService;
import org.iris.templates.ClusterJobDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
public class ModelController {

    @Autowired
    ModelService service;

    @Value("${file.storage-dir}")
    String storageDir;

    @Autowired
    PendingTasks tasks;

    @Autowired
    ZipFileHandler zipFileHandler;

    private static final Logger logger = LoggerFactory.getLogger(ModelController.class);

    @GetMapping("/Model/Progress")
    public ResponseEntity<ModelStatusResponse> getModelProgress(@RequestParam("clusterName") String clusterName) throws IOException {
        return service.checkModelProgress(clusterName);
    }

    @GetMapping("/Model/Download")
    public void DownloadTrainedModel(@RequestParam("clusterName") String clusterName, HttpServletResponse response) throws Exception {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=download.zip");
        response.setStatus(HttpServletResponse.SC_OK);

        logger.info(String.format("Started Download model for:%s", clusterName));

        ClusterJobDetail detail = tasks.getCurrentTask().getClusterJobDetailByClusterName(clusterName);
        if(detail == null)
            throw new Exception("Invalid Cluster name given");

        String modelPath = detail.getModelDir();

        if(modelPath == null){
            logger.info(String.format("Error for Download model: Invalid Cluster name given :%s", clusterName));
            throw new Exception("Invalid Cluster name given!!");
        }

        try{
            zipFileHandler.zipDirectory(modelPath, response);
        }catch (Exception ex){
            throw new Exception("Invalid model Filepath given!!");
        }

    }

    @GetMapping("/Model/EvaluateModel")
    public void EvaluateTrainedModel(@RequestParam("modelPath") String modelPath){
        //starts tensorboard for chosen model.
    }

}
