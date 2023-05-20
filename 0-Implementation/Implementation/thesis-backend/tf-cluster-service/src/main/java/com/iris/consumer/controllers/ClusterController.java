package com.iris.consumer.controllers;

import com.iris.consumer.services.ClusterService;
import org.iris.templates.ClusterJobDetail;
import org.iris.templates.ClusterTaskStatus;
import org.iris.templates.ContainerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;

@RestController
public class ClusterController {

    @Autowired
    private ClusterService service;

    private static final Logger logger = LoggerFactory.getLogger(ClusterController.class);

    @PostMapping("/Task/Containers/Start")
    public ResponseEntity<ClusterTaskStatus> startContainers(@RequestBody ClusterJobDetail jobDetail) throws Exception {

        if(jobDetail == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        logger.info("Starting Task with provided details : " + jobDetail.toString());

        ClusterTaskStatus status = service.startContainers(jobDetail);

        if(status.getException() == null){
            logger.info("Sending response of Start Containers : " + status);
            return new ResponseEntity<>(checkContainerResponses(status, status.getTask()), HttpStatus.OK);
        }else{
            logger.info("Error processing Start Containers : " + status);
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/Task/Containers/Stop")
    public ResponseEntity<ClusterTaskStatus> stopContainers(@RequestBody ClusterJobDetail jobDetail) throws IOException {

        if(jobDetail == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ClusterTaskStatus status = service.stopContainers(jobDetail);

        if(status.getException() == null){

            logger.info("Sending response of stopContainers : " + status);
            return new ResponseEntity<>(checkContainerResponses(status, status.getTask()), HttpStatus.OK);
        }else{
            logger.info("Error processing stopContainers : " + status);
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/Task/Containers/Pause")
    public ResponseEntity<ClusterTaskStatus> pauseContainers(@RequestBody ClusterJobDetail jobDetail) throws IOException {

        if(jobDetail == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ClusterTaskStatus status = service.pauseContainers(jobDetail);

        if(status.getException() == null){
            logger.info("Sending response of pauseContainers : " + status);
            return new ResponseEntity<>(checkContainerResponses(status, status.getTask()), HttpStatus.OK);
        }else{
            logger.info("Error processing pauseContainers : " + status);
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/Task/Containers/Resume")
    public ResponseEntity<ClusterTaskStatus> ResumeContainers(@RequestBody ClusterJobDetail jobDetail) throws IOException {

        if(jobDetail == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ClusterTaskStatus status = service.resumeContainers(jobDetail);

        if(status.getException() == null){
            logger.info("Sending response of resumeContainers : " + status);
            return new ResponseEntity<>(checkContainerResponses(status, status.getTask()), HttpStatus.OK);
        }else{
            logger.info("Error processing resumeContainers : " + status);
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/Task/Containers/Delete")
    public ResponseEntity<ClusterTaskStatus> deleteContainers(@RequestBody ClusterJobDetail jobDetail) throws IOException {

        if(jobDetail == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ClusterTaskStatus status = service.removeContainers(jobDetail);

        if(status.getException() == null){
            logger.info("Sending response of deleteContainers : " + status);
            return new ResponseEntity<>(checkContainerResponses(status, status.getTask()), HttpStatus.OK);
        }else{
            logger.info("Error processing deleteContainers : " + status);
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/Task/Containers/Restart")
    public ResponseEntity<ClusterTaskStatus> restartContainers(@RequestBody ClusterJobDetail jobDetail) throws IOException {

        if(jobDetail == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ClusterTaskStatus status = service.restartContainers(jobDetail);

        if(status.getException() == null){
            logger.info("Sending response of restartContainers : " + status);
            return new ResponseEntity<>(checkContainerResponses(status, status.getTask()), HttpStatus.OK);
        }else{
            logger.info("Error processing restartContainers : " + status);
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/Task/Containers/Status")
    public ResponseEntity<ClusterTaskStatus> getContainerStatus(@RequestBody ClusterJobDetail jobDetail) throws IOException {

        if(jobDetail == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ClusterTaskStatus status = service.statusContainers(jobDetail);

        if(status.getException() == null){
            logger.info("Sending response of getContainerStatus : " + status);
            return new ResponseEntity<>(checkContainerResponses(status, status.getTask()), HttpStatus.OK);
        }else{
            logger.info("Error processing getContainerStatus : " + status);
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ClusterTaskStatus checkContainerResponses(ClusterTaskStatus status, String task){
        ArrayList<ContainerStatus> failedContainers = status.getContainerBySuccess(false);

        if(failedContainers == null)
            return status;

        if(failedContainers.size() != 0){
            String message = String.format("One or more containers have failed %s due to: ", task);

            try{
                for(ContainerStatus stats : failedContainers){
                    if(!message.contains(stats.getException())){
                        message += stats.getException();
                    }
                }
            }catch (Exception ex ){
                logger.error("Error parsing array " + ex.getMessage());
            }


            status.setStatus(false);
            status.setException(message);
        }

        return status;
    }


}
