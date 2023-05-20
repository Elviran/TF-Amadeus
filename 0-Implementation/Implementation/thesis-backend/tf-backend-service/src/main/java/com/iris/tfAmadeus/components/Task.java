package com.iris.tfAmadeus.components;

import com.iris.tfAmadeus.logic.ScriptTemplate;
import org.iris.templates.ClusterJobDetail;
import org.iris.templates.Tensorboard;

import java.util.ArrayList;

public class Task {
    String name;
    ArrayList<ClusterJobDetail> clusterJobDetails;
    ScriptTemplate scriptTemplate;

    public Task(String name, ArrayList<ClusterJobDetail> clusterJobDetails, ScriptTemplate template){
        this.name = name;
        this.clusterJobDetails = clusterJobDetails;
        this.scriptTemplate = template;
    }

    public String getName() {
        return name;
    }

    public ClusterJobDetail getClusterJobDetailByClusterName(String clusterName){
        return getClusterJobDetails()
                .stream()
                .filter(detail -> detail.getClusterName().equals(clusterName))
                .findAny()
                .orElse(null);
    }

    public ClusterJobDetail getClusterJobDetailByModelName(String modelName){
        return getClusterJobDetails()
                .stream()
                .filter(detail -> detail.getModelName().equals(modelName))
                .findAny()
                .orElse(null);
    }

    public ArrayList<Tensorboard> getClusterJobDetailBoard(){
        ArrayList<Tensorboard> board = new ArrayList<>();
        for(ClusterJobDetail detail : clusterJobDetails){
            board.add(detail.getTensorboard());
        }
        return board;
    }

    public void setClusterJobDetailBoards(ArrayList<Tensorboard> tensorboards) throws Exception {
        if(tensorboards == null){
            throw new IllegalArgumentException("Tensorboards cannot be set to null");
        }

        try{
            for(Tensorboard boards : tensorboards){
                getClusterJobDetailByModelName(boards.getContainerName()).setTensorboard(boards);
            }
        }catch (Exception ex){
            throw new Exception("Cannot set tensorboards" + ex.getMessage());
        }

    }

    public ScriptTemplate getScriptTemplate() {
        return scriptTemplate;
    }

    public ArrayList<ClusterJobDetail> getClusterJobDetails() {
        return clusterJobDetails;
    }
}
