package com.iris.tfAmadeus.restConsumer;

import org.iris.templates.ClusterJobDetail;
import org.iris.templates.ClusterTaskStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
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
public class MultiClusterConsumer {

    RestTemplate template = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();



    @Value("#{${cluster.urls}}")
    public HashMap<String,String> urls;

    WebClient client;

    public ArrayList<ClusterTaskStatus> startTask(ArrayList<ClusterJobDetail> detail) throws Exception {
        try{
            return parallelRunStartTask(detail, "start", "/Task/Containers/Start");
        }catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public ArrayList<ClusterTaskStatus> stopTask(ArrayList<ClusterJobDetail> detail) throws Exception {
        try{
            return parallelRunStartTask(detail, "stop", "/Task/Containers/Stop");
        }catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public ArrayList<ClusterTaskStatus> pauseTask(ArrayList<ClusterJobDetail> detail) throws Exception {
        try{
            return parallelRunStartTask(detail, "pause", "/Task/Containers/Pause");
        }catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public ArrayList<ClusterTaskStatus> resumeTask(ArrayList<ClusterJobDetail> detail) throws Exception {
        try{
            return parallelRunStartTask(detail, "resume", "/Task/Containers/Resume");
        }catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public ArrayList<ClusterTaskStatus> restartTask(ArrayList<ClusterJobDetail> detail) throws Exception {
        try{
            return parallelRunStartTask(detail, "restart", "/Task/Containers/Restart");
        }catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public ArrayList<ClusterTaskStatus> clusterStatus(ArrayList<ClusterJobDetail> detail) throws Exception {
        try{
            return parallelRunGetTask(detail, "status", "/Task/Containers/Status");
        }catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public ArrayList<ClusterTaskStatus> finalizeTask(ArrayList<ClusterJobDetail> detail) throws Exception {
        try{
            return parallelRunStartTask(detail, "delete", "/Task/Containers/Delete");
        }catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    private ArrayList<ClusterTaskStatus> parallelRunStartTask(ArrayList<ClusterJobDetail> details, String task, String requestUrl) throws Exception{
        int count = 0;
        ArrayList<ClusterTaskStatus> responses = new ArrayList<>();

        Disposable subscribe =
                Flux.merge(prepareMultiMonoResponse(details, task, requestUrl))
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

    private ArrayList<ClusterTaskStatus> parallelRunGetTask(ArrayList<ClusterJobDetail> details, String task, String requestUrl) throws Exception{
        int count = 0;
        ArrayList<ClusterTaskStatus> responses = new ArrayList<>();

        Disposable subscribe =
                Flux.merge(prepareMultiMonoGetRequest(requestUrl, task, details))
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

    private ArrayList<Mono<ClusterTaskStatus>> prepareMultiMonoResponse(ArrayList<ClusterJobDetail> jobDetail, String task,
                                                                        String requestUrl) throws Exception {

        ArrayList<Mono<ClusterTaskStatus>> clusterResponses = new ArrayList<>();

        int i = 0;
        for(Map.Entry<String, String> kv : urls.entrySet()){
            client = WebClient.create(kv.getValue());
            try {
                clusterResponses.add(client.post()
                        .uri(requestUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromPublisher(Mono.just(jobDetail.get(i)), ClusterJobDetail.class))
                        .retrieve()
                        .bodyToMono(ClusterTaskStatus.class)
                        .onErrorReturn(new ClusterTaskStatus(kv.getKey(), task, false, "Error Connecting to cluster"))
                );
                i++;
            } catch (Exception ex) {
                throw new Exception("Error Processing Start Task:" + ex.getMessage());
            }
        }

        return clusterResponses;
    }

    private ArrayList<Mono<ClusterTaskStatus>> prepareMultiMonoGetRequest(String requestUrl, String task, ArrayList<ClusterJobDetail> detail) throws Exception {

        ArrayList<Mono<ClusterTaskStatus>> clusterResponse = new ArrayList<>();
            int i = 0;
            for (Map.Entry<String, String> kv : urls.entrySet()) {
                client = WebClient.create(kv.getValue());
                try {
                    clusterResponse.add(client.post()
                            .uri(requestUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(BodyInserters.fromPublisher(Mono.just(detail.get(i)), ClusterJobDetail.class))
                            .retrieve()
                            .bodyToMono(ClusterTaskStatus.class)
                            .onErrorReturn(new ClusterTaskStatus(kv.getKey(), task, false, "Error Connecting to Machine"))
                    );
                i++;
                } catch (HttpClientErrorException ex) {
                    throw new Exception(String.format("Error Processing %s Task: %s",task ,ex.getMessage()));
                }
            }

            return clusterResponse;
        }

}
