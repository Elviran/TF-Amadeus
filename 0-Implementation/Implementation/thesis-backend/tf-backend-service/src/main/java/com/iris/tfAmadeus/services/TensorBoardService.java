package com.iris.tfAmadeus.services;

import com.iris.tfAmadeus.components.PendingTasks;
import com.iris.tfAmadeus.components.Task;
import com.iris.tfAmadeus.restConsumer.TensorBoardConsumer;
import org.iris.templates.ContainerStatus;
import org.iris.templates.Tensorboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TensorBoardService {

    @Autowired
    PendingTasks tasks;

    @Autowired
    TensorBoardConsumer containerConsumer;

    private static final Logger logger = LoggerFactory.getLogger(TensorBoardService.class);

    public ArrayList<Tensorboard> startTensorBoards(Task task){
        ArrayList<ContainerStatus> response = new ArrayList<>();

        if(tasks.getCurrentTask() == null)
            throw new IllegalArgumentException("There are no pending tasks..");

        try{
            response = containerConsumer.startTensorBoards(task);

            ArrayList<Tensorboard> tensorboards = new ArrayList<>();

            for(ContainerStatus stats : response){
                Tensorboard tensorboard = tasks.getCurrentTask().getClusterJobDetailByModelName(stats.getName()).getTensorboard();

                if(!stats.getSuccess()){
                    tensorboard.setSuccess(false);
                    tensorboard.setException(stats.getException());
                    tensorboard.setExecuted(false);
                }else{
                    tensorboard.setExecuted(true);
                }
                tensorboard.setSuccess(true);
                tensorboards.add(tensorboard);
            }

            logger.info("Response from Start TensorBoards :" + response);
            return tensorboards;
        }catch (Exception ex){
            logger.error("Error Processing Start TensorBoards: " + ex);
            return null;
        }
    }

    public ArrayList<Tensorboard> stopTensorBoards(){

        ArrayList<ContainerStatus> response = new ArrayList<>();

        try{
            Task task = tasks.getCurrentTask();

            response = containerConsumer.deleteTensorboards(task);

            if(response == null){
                logger.info("No Tensorboards were initialized so non were deleted");
                return null;
            }

            ArrayList<Tensorboard> tensorboards = new ArrayList<>();

            for(ContainerStatus stats : response){
                Tensorboard tensorboard = task.getClusterJobDetailByModelName(stats.getName()).getTensorboard();

                if(!stats.getSuccess()){
                    tensorboard.setSuccess(false);
                    tensorboard.setException(stats.getException());
                }else{
                    tensorboard.setSuccess(true);
                }
                tensorboards.add(tensorboard);
            }

            logger.info("Response from Stop Tensorboard Task : " + response);
            return tensorboards;
        }catch (Exception ex){
            logger.error("Error Processing Start task: " + ex);
            return null;
        }
    }


}
