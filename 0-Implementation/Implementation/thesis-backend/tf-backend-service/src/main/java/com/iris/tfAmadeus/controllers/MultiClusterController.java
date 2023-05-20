package com.iris.tfAmadeus.controllers;

import com.iris.tfAmadeus.services.MultiClusterService;
import org.iris.templates.ClusterTaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class MultiClusterController {

    private static final Logger logger = LoggerFactory.getLogger(MultiClusterController.class);

    @Autowired
    private MultiClusterService multiClusterService;

    @PostMapping("/Task/Start")
    public ResponseEntity<ArrayList<ClusterTaskStatus>> startTask(@RequestParam("filename") String fileName) throws Exception {
        logger.info("Received request for Start Task with filename:" + fileName);
        return multiClusterService.startTask(fileName, "ps");
    }

    @PostMapping("/Task/Stop")
    public ResponseEntity<ArrayList<ClusterTaskStatus>> stopTask() {
        logger.info("Received request for Stop Task ");
        return multiClusterService.stopTask();
    }

    @PostMapping("/Task/Pause")
    public ResponseEntity<ArrayList<ClusterTaskStatus>> pauseTask(){
        logger.info("Received request for Pause Task ");
        return multiClusterService.pauseTask();
    }

    @PostMapping("/Task/Resume")
    public ResponseEntity<ArrayList<ClusterTaskStatus>> resumeTask() {
        logger.info("Received request for Resume Task ");
        return multiClusterService.resumeTask();
    }

    @PostMapping("/Task/Restart")
    public ResponseEntity<ArrayList<ClusterTaskStatus>> restartTask() {
        logger.info("Received request for Restart Task ");
        return multiClusterService.restartTask();
    }

    @PostMapping("/Task/Delete")
    public ResponseEntity<ArrayList<ClusterTaskStatus>> deleteTask() {
        logger.info("Received request for Delete Task ");
        return multiClusterService.finalizeTask();
    }

    @PostMapping("/Task/Status")
    public ResponseEntity<ArrayList<ClusterTaskStatus>> checkClustersStatus() {
        logger.info("Received request for Cluster Status ");
        return multiClusterService.getClusterStatus();
    }

}
