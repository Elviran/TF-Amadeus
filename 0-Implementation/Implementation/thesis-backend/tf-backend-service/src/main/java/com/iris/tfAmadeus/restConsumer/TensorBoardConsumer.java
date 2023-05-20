package com.iris.tfAmadeus.restConsumer;

import com.iris.tfAmadeus.components.PendingTasks;
import com.iris.tfAmadeus.components.Task;
import org.iris.templates.ClusterJobDetail;
import org.iris.templates.ClusterTaskStatus;
import org.iris.templates.ContainerStatus;
import org.iris.templates.Tensorboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TensorBoardConsumer {

    @Value("${container.service-url}")
    String url;

    @Value("${file.storage-dir}")
    String storageDir;

    @Value("${docker.external-volume}")
    String dockerVolume;

    @Autowired
    PendingTasks tasks;

    WebClient client;

    public TensorBoardConsumer(){
    }

    public ArrayList<ContainerStatus> startTensorBoards(Task task) throws Exception{

       if(task == null)
           throw new IllegalArgumentException("task cannot be null");


        try{
            return parallelRunStartTask(prepareMultiMonoResponse(task.getClusterJobDetailBoard(),
                    "Start Board", "/Container/StartWithCommand", true));
        }catch (Exception ex){
            throw new Exception("Error Starting tensorboard for each cluster model.." + ex.getMessage());
        }
    }

    public ArrayList<ContainerStatus> deleteTensorboards(Task currentTask) throws Exception{
        if(currentTask == null)
            throw new IllegalArgumentException("Task cannot be null");

        try{
            ArrayList<Tensorboard> boards = currentTask.getClusterJobDetailBoard();

            if(boards == null){
                return null;
            }

            ArrayList<Tensorboard> boardsToDelete = (ArrayList<Tensorboard>) boards.stream()
                                                    .filter(Tensorboard::isExecuted)
                                                    .collect(Collectors.toList());



            return parallelRunStartTask(prepareMultiMonoResponse(boardsToDelete,"Delete Boards","/Container/Cleanup", false));
        }catch (Exception ex){
            throw new Exception("Cannot delete tensorboards");
        }
    }

    //Place below in a class on its own to handle these things..

    private ArrayList<ContainerStatus> parallelRunStartTask(ArrayList<Mono<ContainerStatus>> monoRequests) throws Exception {
        int count = 0;
        ArrayList<ContainerStatus> responses = new ArrayList<>();

        if(monoRequests == null)
            throw new IllegalArgumentException("Requests list cannot be set null");

        if(monoRequests.size() == 0){
            return responses;
        }

        Disposable subscribe =
                Flux.merge(monoRequests)
                        .parallel()
                        .log()
                        .runOn(Schedulers.elastic())
                        .doOnError(System.out::println)
                        .subscribe(responses::add);

        while(!subscribe.isDisposed() && count < 100){
            if(responses.size() == monoRequests.size())
                break;

            Thread.sleep(2000);
            count++;
            System.out.println("Waiting.....");
        }
        subscribe.dispose();

        return responses;
    }

    private ArrayList<Mono<ContainerStatus>> prepareMultiMonoResponse(ArrayList<Tensorboard> tensorboards, String task,
                                                                      String requestUrl, boolean command) throws Exception {

        ArrayList<Mono<ContainerStatus>> clusterResponse = new ArrayList<>();

        for(Tensorboard board : tensorboards){
            try {
                if(command)
                    clusterResponse.add(clientPostRequest(requestUrl, board.getCommand(), board.getContainerName(), task));
                else
                    clusterResponse.add(clientPostRequest(requestUrl, board.getContainerName(), task));
            } catch (HttpClientErrorException ex) {
                throw new Exception(String.format("Error Processing %s Task: %s",task ,ex.getMessage()));
            }

        }

        return clusterResponse;
    }

    private Mono<ContainerStatus> clientPostRequest(String requestUrl, String command, String containerName, String task){
        client = WebClient.create(url);

        return client.post()
                .uri(uriBuilder -> uriBuilder
                        .path(requestUrl)
                        .queryParam("command", command)
                        .queryParam("containerName", containerName)
                        .build())
                .retrieve()
                .bodyToMono(ContainerStatus.class)
                .onErrorReturn(new ContainerStatus(containerName, task, false, "Error Connecting to container"));
    }

    private Mono<ContainerStatus> clientPostRequest(String requestUrl, String containerName, String task){
        return client.post()
                .uri(uriBuilder -> uriBuilder
                        .path(requestUrl)
                        .queryParam("containerName", containerName)
                        .build())
                .retrieve()
                .bodyToMono(ContainerStatus.class)
                .onErrorReturn(new ContainerStatus(containerName, task, false, "Error Connecting to container"));
    }

    private HttpEntity<MultiValueMap<String, String>> postRequest(String command, String containerName){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("command", "");
        map.add("containerName", containerName);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        return request;
    }

    private HttpEntity<MultiValueMap<String, String>> postRequest(String containerName){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("containerName", containerName);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        return request;
    }
}
