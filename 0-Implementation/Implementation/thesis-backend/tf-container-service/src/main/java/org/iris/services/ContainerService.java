package org.iris.services;

import org.iris.components.QueryContainers;
import org.iris.enums.ContainerState;
import org.iris.templates.CommandOutput;
import org.iris.templates.ContainerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ContainerService {

    @Autowired
    QueryContainers containers;

    private final Logger logger = LoggerFactory.getLogger(ContainerService.class);

    public ResponseEntity<ContainerStatus> startContainer(String dockerDir, String envFile, String containerName, String cluster){

        try{
            ContainerStatus result = containers.startContainer(dockerDir, envFile, containerName);
            logger.info(String.format("Sending Response for StartContainer: %s", result.toString()));
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception ex){
            logger.error("Error Processing Start Container: " + ex.getMessage());
            return new ResponseEntity<>(new ContainerStatus(containerName, "start", false,
                    "Error Processing Start Container"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ContainerStatus> startContainer(String command, String containerName){
        try{
            ContainerStatus result = containers.startContainer(command, containerName);

            logger.info(String.format("Sending Response for StartContainer: %s", result.toString()));
            return new ResponseEntity<>(result, HttpStatus.OK);

        }catch (Exception ex){
            logger.error("Error Processing Start Container: " + ex.getMessage());
            return new ResponseEntity<>(new ContainerStatus(containerName, "start", false,
                    "Error Processing Start Container"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ContainerStatus> stopContainer(String containerName){
        try{
            ContainerStatus result = containers.stopContainer(containerName);

            logger.info(String.format("Sending Response for Stop Container : %s", result.toString()));
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception ex){
            logger.error("Error Processing Stop Container: " + ex.getMessage());
            return new ResponseEntity<>(new ContainerStatus(containerName, "stop", false,
                    "Error Processing stop Container"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ContainerStatus> pauseContainer(String containerName){
        try{
            ContainerStatus result = containers.pauseContainer(containerName);

            logger.info(String.format("Sending Response for Pause Container:  %s", result.toString()));
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception ex){
            logger.error("Error Processing Pause Container: " + ex.getMessage());
            return new ResponseEntity<>(new ContainerStatus(containerName, "pause", false,
                    "Error Processing pause Container"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ContainerStatus> resumeContainer(String containerName){
        try{
            ContainerStatus result = containers.resumeContainer(containerName);

            logger.info(String.format("Sending Response for Resume Container:  %s", result.toString()));
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception ex){
            logger.error("Error Processing Resume Container: " + ex.getMessage());
            return new ResponseEntity<>(new ContainerStatus(containerName, "resume", false,
                    "Error Processing resume Container"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ContainerStatus> restartContainer(String containerName){
        try{
            ContainerStatus result = containers.restartContainer(containerName);

            logger.info(String.format("Sending Response for Restart Container:  %s", result.toString()));
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception ex){
            logger.error("Error Processing Restart Container : " + ex.getMessage());
            return new ResponseEntity<>(new ContainerStatus(containerName, "restart", false,
                    "Error Processing restart Container"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ContainerStatus> deleteContainer(String containerName){
        try{
            ContainerStatus result = containers.deleteContainer(containerName);
            logger.info("Sending Response for delete Container:" + result.toString());
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception ex){
            logger.error("Error Processing delete Container: " + ex.getMessage());
            return new ResponseEntity<>(new ContainerStatus(containerName, "delete", false,
                    "Error Processing delete Container"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ContainerStatus> getContainerStatus(String containerName){
        try{
            CommandOutput result = containers.checkContainerStatus(containerName, ContainerState.Status);

            if(result.isError()){
                logger.warn("ContainerStatus has encountered an error:" + result.getMessage());
                return new ResponseEntity<>(new ContainerStatus(containerName,"status", result.getMessage(),
                        false, result.getMessage()), HttpStatus.OK);
            }

            logger.info("Sending Response for ContainerStatus: Container Get Status:" + result.toString());
            return new ResponseEntity<>(new ContainerStatus(containerName,"Status", result.getLine(),true,null), HttpStatus.OK);
        }catch (Exception ex){
            logger.error("Error Processing ContainerStatus Container: " + ex.getMessage());
            return new ResponseEntity<>(new ContainerStatus(containerName,"Status", "",false, ex.getMessage()), HttpStatus.OK);
        }
    }

}
