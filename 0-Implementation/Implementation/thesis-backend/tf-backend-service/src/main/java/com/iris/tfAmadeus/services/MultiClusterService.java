package com.iris.tfAmadeus.services;

import com.iris.tfAmadeus.components.PendingTasks;
import com.iris.tfAmadeus.components.Task;
import com.iris.tfAmadeus.logic.TaskHandlerImpl;
import com.iris.tfAmadeus.restConsumer.MultiClusterConsumer;
import org.iris.templates.ClusterTaskStatus;
import org.iris.templates.Tensorboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class MultiClusterService {

    private static final Logger logger = LoggerFactory.getLogger(MultiClusterService.class);

    @Autowired
    private TaskHandlerImpl taskHandlerImpl;

    @Autowired
    private MultiClusterConsumer restConsumer;

    @Autowired
    private TensorBoardService tensorBoardService;

    @Autowired
    PendingTasks pendingTasks;

    public ResponseEntity<ArrayList<ClusterTaskStatus>> startTask(String filename, String tfConfigs) {
        ArrayList<ClusterTaskStatus> response = new ArrayList<>();

        try{
            Task task = taskHandlerImpl.prepareTask(filename, tfConfigs);
//            task.setClusterJobDetailBoards(tensorBoardService.startTensorBoards());

            response = restConsumer.startTask(task.getClusterJobDetails());

            ArrayList<ClusterTaskStatus> failedResponses = responseValidator(response, filename);

            if(failedResponses != null){
                finalizeTask();
                return new ResponseEntity<>(failedResponses, HttpStatus.SERVICE_UNAVAILABLE);
            }

            logger.info("Clusters loaded successfully, Deploying Tensorboards");
            //Clusters successfully deployed! Start tensorboard for each one using the model directory.
            tensorBoardService.startTensorBoards(task);

            logger.info("Response from Start Task : " + response.toString());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception ex){
            logger.error("Error Processing Start task: " + ex);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ArrayList<ClusterTaskStatus>> stopTask() {
        ArrayList<ClusterTaskStatus> response = new ArrayList<>();
        try{
            response = restConsumer.stopTask(pendingTasks.getCurrentTask().getClusterJobDetails());
            logger.info("Response from Stop Task : " + response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception ex){
            logger.error("Error Processing Stop task: " + ex);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ArrayList<ClusterTaskStatus>> pauseTask() {
        ArrayList<ClusterTaskStatus> response = new ArrayList<>();
        try{
            response = restConsumer.pauseTask(pendingTasks.getCurrentTask().getClusterJobDetails());
            logger.info("Response from Pause Task : " + response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception ex){
            logger.error("Error Processing Pause task: " + ex);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ArrayList<ClusterTaskStatus>> resumeTask() {
        ArrayList<ClusterTaskStatus> response = new ArrayList<>();
        try{
            response = restConsumer.resumeTask(pendingTasks.getCurrentTask().getClusterJobDetails());
            logger.info("Response from Resume Task : " + response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception ex){
            logger.error("Error Processing Resume task: " + ex);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ArrayList<ClusterTaskStatus>> restartTask() {
        ArrayList<ClusterTaskStatus> response = new ArrayList<>();
        try{
            response = restConsumer.restartTask(pendingTasks.getCurrentTask().getClusterJobDetails());
            logger.info("Response from Restart Task : " + response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception ex){
            logger.error("Error Processing Restart task: " + ex);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ArrayList<ClusterTaskStatus>> getClusterStatus() {
        ArrayList<ClusterTaskStatus> response = new ArrayList<>();
        try{
            response = restConsumer.clusterStatus(pendingTasks.getCurrentTask().getClusterJobDetails());
            logger.info("Response from Get Cluster Status Task : " + response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception ex){
            logger.error("Error Processing Get Cluster Status task: " + ex);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ArrayList<ClusterTaskStatus>> finalizeTask() {
        ArrayList<ClusterTaskStatus> response = new ArrayList<>();
        try{

//            //Stop tensorboard services..
            tensorBoardService.stopTensorBoards();
//
//            //Command Clusters to delete containers
            response = restConsumer.finalizeTask(pendingTasks.getCurrentTask().getClusterJobDetails());
//
//            //Delete directory and queue of this task.
            taskHandlerImpl.deleteTask();

            logger.info("Response from Finalize Task : " + response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception ex){
            logger.error("Error Processing Finalize task: " + ex);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ArrayList<ClusterTaskStatus> responseValidator(ArrayList<ClusterTaskStatus> response, String filename){

        ArrayList<ClusterTaskStatus> failed =
                (ArrayList<ClusterTaskStatus>) response.stream()
                        .filter(status -> status.getStatus().equals(false))
                        .collect(Collectors.toList());

        if(failed.size() != 0){
            logger.info("One of more clusters failed to build.. Dropping task: " + filename);

            for(ClusterTaskStatus cluster : failed){

                response = new ArrayList<>();

                if(cluster.getException().contains("Error Connecting to cluster")
                        || cluster.getException().contains("Error connecting to container")) {
                    response.add(new ClusterTaskStatus(null, filename,false, "Error processing task at this time.. please try again later"));
                    return response;
                }
            }

            response.add(new ClusterTaskStatus(null, filename,false, "Clusters cannot process given task.."));
            return response;
        }

        return null;
    }
    
}
