package com.iris.tfAmadeus.controllers;

import com.iris.tfAmadeus.services.ClusterService;
import org.iris.templates.ClusterTaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClusterController {

    @Autowired
    private ClusterService clusterService;

    private static final Logger logger = LoggerFactory.getLogger(ClusterController.class);

    @PostMapping("/Task/Cluster/Stop")
    public ResponseEntity<ClusterTaskStatus> stopCluster(@RequestParam("clusterName") String clusterName) {
        logger.info("Received request for Task/Cluster/Stop with clusterName " + clusterName);
        return clusterService.stopCluster(clusterName);
    }

    @PostMapping("/Task/Cluster/Pause")
    public ResponseEntity<ClusterTaskStatus> pauseCluster(@RequestParam("clusterName") String clusterName) {
        logger.info("Received request for Task/Cluster/Pause with clusterName " + clusterName);
        return clusterService.pauseCluster(clusterName);
    }

    @PostMapping("/Task/Cluster/Resume")
    public ResponseEntity<ClusterTaskStatus> resumeCluster(@RequestParam("clusterName") String clusterName) {
        logger.info("Received request for Task/Cluster/Resume with clusterName " + clusterName);
        return clusterService.resumeCluster(clusterName);
    }

    @PostMapping("/Task/Cluster/Restart")
    public ResponseEntity<ClusterTaskStatus> restartCluster(@RequestParam("clusterName") String clusterName) {
        logger.info("Received request for Task/Cluster/Restart with clusterName " + clusterName);
        return clusterService.restartCluster(clusterName);
    }

    @PostMapping("/Task/Cluster/Delete")
    public ResponseEntity<ClusterTaskStatus> deleteCluster(@RequestParam("clusterName") String clusterName) {
        logger.info("Received request for Task/Cluster/Delete with clusterName " + clusterName);
        return clusterService.ClearClusterTask(clusterName);
    }

    @PostMapping("/Task/Cluster/Status")
    public ResponseEntity<ClusterTaskStatus> clusterStatus(@RequestParam("clusterName") String clusterName) {
        logger.info("Received request for Task/Cluster/Status with clusterName " + clusterName);
        return clusterService.clusterStatus(clusterName);
    }

}
