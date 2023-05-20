package com.iris.tfAmadeus.components;

import java.util.LinkedList;

public class PendingTasks {

    LinkedList<Task> pendingTasks;

    public PendingTasks(){
        this.pendingTasks = new LinkedList<>();
    }

    public int getPendingTasks(){
        return pendingTasks.size();
    }

    public void enqueueTask(Task task){
        pendingTasks.addLast(task);
    }

    public Task dequeueTask(){
       return pendingTasks.removeFirst();
    }

    public Task getCurrentTask(){
        return pendingTasks.getFirst();
    }

}
