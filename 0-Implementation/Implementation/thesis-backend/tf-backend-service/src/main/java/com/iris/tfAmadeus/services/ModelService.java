package com.iris.tfAmadeus.services;

import com.iris.tfAmadeus.components.PendingTasks;
import com.iris.tfAmadeus.response.ModelStatusResponse;
import com.iris.tfAmadeus.services.storage.SambaStorageService;
import org.iris.templates.ContainerStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class ModelService {

    @Autowired
    public PendingTasks tasks;

    @Autowired
    ClusterService service;

    @Autowired
    SambaStorageService sambaStorage;

    private static final Logger logger = LoggerFactory.getLogger(ModelService.class);

    public ResponseEntity<ModelStatusResponse> checkModelProgress(String clusterName) {

        ModelStatusResponse progress = new ModelStatusResponse();

        try{
            progress = sambaStorage.retrieveModelProgress(clusterName, tasks.getCurrentTask());
            return new ResponseEntity<>(progress, HttpStatus.OK);
        }catch (Exception ex){
            logger.info("Error processing clusterStatus : " + progress.toString());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
