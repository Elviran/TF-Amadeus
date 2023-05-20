package com.iris.consumer.services;

import org.iris.templates.ClusterJobDetail;
import org.iris.templates.ContainerStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.MediaType;

import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class ClusterQueryImpl {

    @Value("${destination.name}")
    private String clusterName;

    WebClient client;

    public ArrayList<ContainerStatus> startContainers(ClusterJobDetail detail, ArrayList<String> envFiles, Map<String, String> urls) throws Exception{

        if(urls == null || detail == null || envFiles == null)
            throw new IllegalArgumentException("Params cannot be null");

        try{
            ArrayList<Mono<ContainerStatus>> requests = prepareMultiMonoPostRequest(detail, envFiles, urls);
            return parallelRunPostTask(requests);
        }catch (Exception ex) {
            throw new Exception("Error Running Task StartContainers: " + ex.getMessage());
        }
    }

    //Post
    public ArrayList<ContainerStatus> stopContainers(HashMap<String,String> urls) throws Exception {

        if(urls == null)
            throw new IllegalArgumentException("Urls cannot be null");

        try{
            return parallelRunPostTask("/Container/Stop", "stop", urls);
        }catch (Exception ex) {
            throw new Exception("Error Running Task StopContainers: " + ex.getMessage());
        }
    }

    //Post
    public ArrayList<ContainerStatus> pauseContainers(HashMap<String,String> urls) throws Exception {
        if(urls == null)
            throw new IllegalArgumentException("Urls cannot be null");

        try{
            return parallelRunPostTask("/Container/Pause", "pause", urls);
        }catch (Exception ex) {
            throw new Exception("Error Running Task PauseContainers: " + ex.getMessage());
        }
    }

    //Post
    public ArrayList<ContainerStatus> resumeContainers(HashMap<String,String> urls) throws Exception {
        if(urls == null)
            throw new IllegalArgumentException("Urls cannot be null");

        try{
            return parallelRunPostTask("/Container/Resume", "resume", urls);
        }catch (Exception ex) {
            throw new Exception("Error Running Task ResumeContainers: " + ex.getMessage());
        }
    }

    //Post
    public ArrayList<ContainerStatus> restartContainers(HashMap<String,String> urls) throws Exception {
        if(urls == null)
            throw new IllegalArgumentException("Urls cannot be null");

        try{
            return parallelRunPostTask("/Container/Restart", "resume", urls);
        }catch (Exception ex) {
            throw new Exception("Error Running Task RestartContainers: " + ex.getMessage());
        }
    }

    //Post
    public ArrayList<ContainerStatus> deleteContainers(HashMap<String,String> urls) throws Exception {

        if(urls == null)
            throw new IllegalArgumentException("Urls cannot be null");

        try{
            return parallelRunPostTask("/Container/Cleanup", "delete", urls);
        }catch (Exception ex) {
            throw new Exception("Error Running Task DeleteContainers: " + ex.getMessage());
        }
    }

    //Get
    public ArrayList<ContainerStatus> containerStatus(HashMap<String,String> urls) throws Exception {

        if(urls == null)
            throw new IllegalArgumentException("Urls cannot be null");

        try{
            return parallelRunGetTask("/Container/Status", "Status", urls);
        }catch (Exception ex) {
            throw new Exception("Error Running Task ContainerStatus: " + ex.getMessage());
        }
    }


    private ArrayList<ContainerStatus> parallelRunPostTask(ArrayList<Mono<ContainerStatus>> monoRequests)
            throws Exception {

        if(monoRequests == null)
            throw new IllegalAccessException("Mono Requests cannot be null");

        int count = 0;
        ArrayList<ContainerStatus> responses = new ArrayList<>();

        //Parallel send using Mono Merge of multiple requests and sending through flux and parallelism
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

            Thread.sleep(1000);
            count++;
            System.out.println("Waiting.....");
        }
        subscribe.dispose();

        if(responses.size() != 5)
            return null;

        return responses;
    }

    private ArrayList<ContainerStatus> parallelRunPostTask(String url, String task, HashMap<String, String> urls)
            throws Exception{
        int count = 0;
        ArrayList<ContainerStatus> responses = new ArrayList<>();

        Disposable subscribe =
                Flux.merge(prepareMultiMonoPostRequest(url, task, urls))
                        .parallel()
                        .log()
                        .runOn(Schedulers.elastic())
                        .doOnError(System.out::println)
                        .subscribe(responses::add);

        while(!subscribe.isDisposed() && count < 100){
            if(responses.size() == urls.size())
                break;

            Thread.sleep(1000);
            count++;
            System.out.println("Waiting.....");
        }
        subscribe.dispose();

        return responses;
    }

    private ArrayList<ContainerStatus> parallelRunGetTask(String url, String task, HashMap<String, String> urls)
            throws Exception{
        int count = 0;
        ArrayList<ContainerStatus> responses = new ArrayList<>();

        Disposable subscribe =
                Flux.merge(prepareMultiMonoGetRequest(url, task, urls))
                        .parallel()
                        .log()
                        .runOn(Schedulers.elastic())
                        .doOnError(System.out::println)
                        .subscribe(responses::add);

        while(!subscribe.isDisposed() && count < 100){
            if(responses.size() == urls.size())
                break;

            Thread.sleep(1000);
            count++;
            System.out.println("Waiting.....");
        }
        subscribe.dispose();

        return responses;
    }

    private ArrayList<Mono<ContainerStatus>> prepareMultiMonoPostRequest(ClusterJobDetail jobDetail, ArrayList<String> envFiles,
                                                                         Map<String, String> urls) throws Exception {
        if(urls == null)
            throw new IllegalArgumentException("Url Map cannot be null!");

        ArrayList<Mono<ContainerStatus>> responses = new ArrayList<>();

        for(String filename : envFiles){
            String containerName = "";

            try{
                containerName = filename.substring(0, filename.indexOf("."));
                String url = urls.get(containerName);
                client = WebClient.create(url);

                responses.add(client.post()
                        .uri("/Container/Start")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .body(BodyInserters.fromMultipartData(postHeaders(jobDetail, filename, containerName).build()))
                        .retrieve()
                        .bodyToMono(ContainerStatus.class)
                        .onErrorReturn(new ContainerStatus(containerName, "Start", false, "Error connecting to container"))
                );

            }catch (Exception ex){
                throw new Exception("Error Sending Start to clusters : " + ex);
            }

        }
        return responses;
    }

    private ArrayList<Mono<ContainerStatus>> prepareMultiMonoPostRequest(String requestUrl, String task,
                                                                         HashMap<String, String> urls) throws Exception {

        ArrayList<Mono<ContainerStatus>> clusterResponse = new ArrayList<>();

        for (Map.Entry<String, String> kv : urls.entrySet()) {
            client = WebClient.create(kv.getValue());
            try {
                clusterResponse.add(client.post()
                        .uri(uriBuilder -> uriBuilder
                                .path(requestUrl)
                                .queryParam("containerName", kv.getKey())
                                .build())
                        .retrieve()
                        .bodyToMono(ContainerStatus.class)
                        .onErrorReturn(new ContainerStatus(kv.getKey(), task, false, "Error Connecting to Machine"))
                );
            } catch (HttpClientErrorException ex) {
                throw new Exception(String.format("Error Processing %s Task: %s",task ,ex.getMessage()));
            }
        }

        return clusterResponse;
    }

    private ArrayList<Mono<ContainerStatus>> prepareMultiMonoGetRequest(String requestUrl, String task,
                                                                        HashMap<String, String> urls) throws Exception {

        ArrayList<Mono<ContainerStatus>> clusterResponse = new ArrayList<>();

        for (Map.Entry<String, String> kv : urls.entrySet()) {
            client = WebClient.create(kv.getValue());
            try {
                clusterResponse.add(client.get()
                        .uri(uriBuilder -> uriBuilder
                                .path(requestUrl)
                                .queryParam("containerName", kv.getKey())
                                .build())
                        .retrieve()
                        .bodyToMono(ContainerStatus.class)
                        .onErrorReturn(new ContainerStatus(kv.getKey(), task, false, "Error Connecting to Machine"))
                );
            } catch (HttpClientErrorException ex) {
                throw new Exception(String.format("Error Processing %s Task: %s",task ,ex.getMessage()));
            }
        }

        return clusterResponse;
    }

    private MultipartBodyBuilder postHeaders(ClusterJobDetail detail, String envFile, String containerName){

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

        bodyBuilder.part("dockerDir", detail.getDockerDir(), MediaType.TEXT_PLAIN)
                .header("Content-Disposition", "form-data; name=dockerDir")
                .header("Content-type", "text/plain");
        bodyBuilder.part("envFile", String.format("%s%s",detail.getEnvDir(), envFile), MediaType.TEXT_PLAIN)
                .header("Content-Disposition", "form-data; name=envFile")
                .header("Content-type", "text/plain");
        bodyBuilder.part("containerName", containerName, MediaType.TEXT_PLAIN)
                .header("Content-Disposition", "form-data; name=containerName")
                .header("Content-type", "text/plain");
        bodyBuilder.part("cluster", clusterName, MediaType.TEXT_PLAIN)
                .header("Content-Disposition", "form-data; name=cluster")
                .header("Content-type", "text/plain");

        return bodyBuilder;
    }

    private MultipartBodyBuilder postHeaders(String containerName){
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

        bodyBuilder.part("containerName", containerName, MediaType.TEXT_PLAIN)
                .header("Content-Disposition", "form-data; name=containerName")
                .header("Content-type", "text/plain");

        return bodyBuilder;
    }
}
