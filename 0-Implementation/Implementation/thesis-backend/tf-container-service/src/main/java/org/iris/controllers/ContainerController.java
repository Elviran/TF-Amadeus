package org.iris.controllers;

import org.iris.services.ContainerService;
import org.iris.templates.ContainerStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ContainerController {

    @Autowired
    ContainerService service;

    @GetMapping("/Container/Status")
    public ResponseEntity<ContainerStatus> containerStatus(@RequestParam("containerName") String containerName) {
        return service.getContainerStatus(containerName);
    }

    @PostMapping("/Container/Start")
    public ResponseEntity<ContainerStatus> startContainer(@RequestParam("dockerDir") String dockerDir,
                                                          @RequestParam("envFile") String envFile,
                                                          @RequestParam("containerName") String containerName,
                                                          @RequestParam("cluster") String cluster) {

        return service.startContainer(dockerDir, envFile, containerName, cluster);
    }

    @PostMapping("/Container/StartWithCommand")
    public ResponseEntity<ContainerStatus> startContainer(@RequestParam("command") String command,
                                                          @RequestParam("containerName") String containerName) {

        return service.startContainer(command, containerName);
    }

    @PostMapping("/Container/Stop")
    public ResponseEntity<ContainerStatus> stopContainer(String containerName){
        try{
            return service.stopContainer(containerName);
        }catch (IllegalArgumentException ex){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/Container/Pause")
    public ResponseEntity<ContainerStatus> pauseContainer(@RequestParam("containerName") String containerName){
        return service.pauseContainer(containerName);
    }

    @PostMapping("/Container/Resume")
    public ResponseEntity<ContainerStatus> resumeContainer(@RequestParam("containerName") String containerName){
        return service.resumeContainer(containerName);
    }

    @PostMapping("/Container/Restart")
    public ResponseEntity<ContainerStatus> restartContainer(@RequestParam("containerName") String containerName){
        return service.restartContainer(containerName);
    }

    @PostMapping("/Container/Cleanup")
    public ResponseEntity<ContainerStatus> deleteContainer(@RequestParam("containerName") String containerName){
        return service.deleteContainer(containerName);
    }

}
