package com.iris.consumer.controllers;

import com.iris.consumer.services.ContainerService;
import org.iris.templates.ContainerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContainerController {

    @Autowired
    private ContainerService service;

    private static final Logger logger = LoggerFactory.getLogger(ContainerController.class);

    @PostMapping("/Cluster/Container/Start")
    public ResponseEntity<ContainerStatus> startContainer(@RequestParam("containerName") String containerName) throws Exception {

        ContainerStatus status = service.startContainer(containerName);

        if(status.getException() == null){
            logger.info(String.format("Sending response to stop Container %s : %s", containerName, status));
            return new ResponseEntity<>(status, HttpStatus.OK);
        }else{
            logger.info("Error processing stopContainers : " + status);
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/Cluster/Container/Stop")
    public ResponseEntity<ContainerStatus> stopContainer(@RequestParam("containerName") String containerName) throws Exception {

        ContainerStatus status = service.stopContainer(containerName);

        if(status.getException() == null){
            logger.info(String.format("Sending response to stop Container %s : %s", containerName, status));
            return new ResponseEntity<>(status, HttpStatus.OK);
        }else{
            logger.info("Error processing stopContainers : " + status);
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/Cluster/Container/Pause")
    public ResponseEntity<ContainerStatus> pauseContainer(@RequestParam("containerName") String containerName) throws Exception {

        ContainerStatus status = service.pauseContainer(containerName);

        if(status.getException() == null){
            logger.info(String.format("Sending response to pause Container %s : %s", containerName, status));
            return new ResponseEntity<>(status, HttpStatus.OK);
        }else{
            logger.info("Error processing stopContainers : " + status);
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/Cluster/Container/Resume")
    public ResponseEntity<ContainerStatus> resumeContainer(@RequestParam("containerName") String containerName) throws Exception {

        ContainerStatus status = service.resumeContainer(containerName);

        if(status.getException() == null){
            logger.info(String.format("Sending response to resume Container %s : %s", containerName, status));
            return new ResponseEntity<>(status, HttpStatus.OK);
        }else{
            logger.info("Error processing stopContainers : " + status);
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/Cluster/Container/Restart")
    public ResponseEntity<ContainerStatus> restartContainer(@RequestParam("containerName") String containerName) throws Exception {

        ContainerStatus status = service.restartContainer(containerName);

        if(status.getException() == null){
            logger.info(String.format("Sending response to restart Container %s : %s", containerName, status));
            return new ResponseEntity<>(status, HttpStatus.OK);
        }else{
            logger.info("Error processing stopContainers : " + status);
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/Cluster/Container/Delete")
    public ResponseEntity<ContainerStatus> deleteContainer(@RequestParam("containerName") String containerName) throws Exception {

        ContainerStatus status = service.deleteContainer(containerName);

        if(status.getException() == null){
            logger.info(String.format("Sending response to Get Status Container %s : %s", containerName, status));
            return new ResponseEntity<>(status, HttpStatus.OK);
        }else{
            logger.info("Error processing stopContainers : " + status);
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/Cluster/Container/Status")
    public ResponseEntity<ContainerStatus> statusContainer(@RequestParam("containerName") String containerName) throws Exception {

        ContainerStatus status = service.containerStatus(containerName);

        if(status.getException() == null){
            logger.info(String.format("Sending response to Get Status Container %s : %s", containerName, status));
            return new ResponseEntity<>(status, HttpStatus.OK);
        }else{
            logger.info("Error processing stopContainers : " + status);
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
