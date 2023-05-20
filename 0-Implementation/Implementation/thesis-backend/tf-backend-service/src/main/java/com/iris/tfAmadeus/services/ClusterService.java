package com.iris.tfAmadeus.services;

import com.iris.tfAmadeus.restConsumer.ClusterConsumer;
import org.iris.templates.ClusterTaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ClusterService {

    @Autowired
    private ClusterConsumer restConsumer;

    @Value("#{${cluster.urls}}")
    public HashMap<String,String> urls;

    private static final Logger logger = LoggerFactory.getLogger(ClusterService.class);

    public ResponseEntity<ClusterTaskStatus> stopCluster(String clusterName) {
        ClusterTaskStatus response = new ClusterTaskStatus();
        try{
            response = restConsumer.stopCluster(retrieveUrl(clusterName), clusterName);
            logger.info("Sending Response for stopCluster : " + response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception ex){
            logger.info("Error processing stopCluster : " + response);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ClusterTaskStatus> pauseCluster(String clusterName) {
        ClusterTaskStatus response = new ClusterTaskStatus();
        try{
            response = restConsumer.pauseCluster(retrieveUrl(clusterName), clusterName);
            logger.info("Sending Response for pauseCluster : " + response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception ex){
            logger.info("Error processing pauseCluster : " + response);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ClusterTaskStatus> resumeCluster(String clusterName) {
        ClusterTaskStatus response = new ClusterTaskStatus();
        try{
            response = restConsumer.resumeCluster(retrieveUrl(clusterName), clusterName);
            logger.info("Sending Response for resumeCluster : " + response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception ex){
            logger.info("Error processing resumeCluster : " + response);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ClusterTaskStatus> restartCluster(String clusterName) {
        ClusterTaskStatus response = new ClusterTaskStatus();

        try{
            response = restConsumer.restartCluster(retrieveUrl(clusterName), clusterName);
            logger.info("Sending Response for restartCluster : " + response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception ex){
            logger.info("Error processing restartCluster : " + response);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ClusterTaskStatus> clusterStatus(String clusterName){
        ClusterTaskStatus response = new ClusterTaskStatus();
        try{
            response = restConsumer.clusterStatus(retrieveUrl(clusterName), clusterName);
            logger.info("Sending Response for clusterStatus : " + response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception ex){
            logger.info("Error processing clusterStatus : " + response);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ClusterTaskStatus> ClearClusterTask(String clusterName) {
        ClusterTaskStatus response = new ClusterTaskStatus();
        try{
            response = restConsumer.deleteCluster(retrieveUrl(clusterName), clusterName);
            logger.info("Sending Response for clearCluster : " + response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception ex){
            logger.info("Error processing clerClusterTask : " + response);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String retrieveUrl(String clusterName) {
        if(clusterName == null)
            throw new IllegalArgumentException("Cluster name cannot be null");

        String url = urls.get(clusterName);

        if(url == null)
            throw new IllegalArgumentException("Wrong cluster name given");
        else
            return url;

    }
}
