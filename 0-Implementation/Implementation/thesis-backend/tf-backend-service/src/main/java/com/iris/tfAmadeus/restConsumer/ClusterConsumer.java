package com.iris.tfAmadeus.restConsumer;

import com.iris.tfAmadeus.components.PendingTasks;
import com.iris.tfAmadeus.components.Task;
import org.iris.templates.ClusterJobDetail;
import org.iris.templates.ClusterTaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class ClusterConsumer {

    RestTemplate template = new RestTemplate();

    @Value("#{${cluster.urls}}")
    public HashMap<String,String> urls;

    @Autowired
    PendingTasks pendingTasks;

    WebClient client;

    public ClusterTaskStatus stopCluster(String url,String clusterName){
        if(url == null)
            throw new IllegalArgumentException("Cannot stop cluster as url is null");

        try{
            Task task = pendingTasks.getCurrentTask();

            if(task == null)
                throw new IllegalArgumentException("Cannot do Delete cluster as there are no tasks at the moment");

            return runPostRequest(clusterName,"/Task/Containers/Stop", url, "stop",
                    task.getClusterJobDetailByClusterName(clusterName));
        } catch (Exception ex) {
            return new ClusterTaskStatus(clusterName,"stop cluster",false, ex.getMessage());
        }

    }

    public ClusterTaskStatus restartCluster(String url,String clusterName){
        if(url == null)
            throw new IllegalArgumentException("Cannot stop cluster as url is null");

        try{
            Task task = pendingTasks.getCurrentTask();

            if(task == null)
                throw new IllegalArgumentException("Cannot do Delete cluster as there are no tasks at the moment");

            return runPostRequest(clusterName,"/Task/Containers/Restart",  url,"restart",
                    task.getClusterJobDetailByClusterName(clusterName));
        } catch (Exception ex) {
            return new ClusterTaskStatus(clusterName,"restart cluster",false, ex.getMessage());
        }

    }

    public ClusterTaskStatus pauseCluster(String url,String clusterName){
        if(url == null)
            throw new IllegalArgumentException("Cannot stop cluster as url is null");

        try{
            Task task = pendingTasks.getCurrentTask();

            if(task == null)
                throw new IllegalArgumentException("Cannot do Delete cluster as there are no tasks at the moment");

            return runPostRequest(clusterName,"/Task/Containers/Pause", url,"pause",
                    task.getClusterJobDetailByClusterName(clusterName));
        } catch (Exception ex) {
            return new ClusterTaskStatus(clusterName,"pause cluster",false, ex.getMessage());
        }

    }

    public ClusterTaskStatus resumeCluster(String url,String clusterName) {
        if(url == null)
            throw new IllegalArgumentException("Cannot stop cluster as url is null");

        try{
            Task task = pendingTasks.getCurrentTask();

            if(task == null)
                throw new IllegalArgumentException("Cannot do Delete cluster as there are no tasks at the moment");

            return runPostRequest(clusterName,"/Task/Containers/Resume",  url, "resume",
                    task.getClusterJobDetailByClusterName(clusterName));
        } catch (Exception ex) {
            return new ClusterTaskStatus(clusterName,"resume cluster",false, ex.getMessage());
        }

    }

    public ClusterTaskStatus deleteCluster(String url,String clusterName){
        if(url == null)
            throw new IllegalArgumentException("Cannot stop cluster as url is null");

        try{
            Task task = pendingTasks.getCurrentTask();

            if(task == null)
                throw new IllegalArgumentException("Cannot do Delete cluster as there are no tasks at the moment");

            return runPostRequest(clusterName,"/Task/Containers/Delete",  url,"delete",
                    task.getClusterJobDetailByClusterName(clusterName));
        } catch (Exception ex) {
            return new ClusterTaskStatus(clusterName,"deleteCluster",false, ex.getMessage());
        }
    }

    public ClusterTaskStatus clusterStatus(String url,String clusterName){
        if(url == null)
            throw new IllegalArgumentException("Cannot stop cluster as url is null");

        try{
            Task task = pendingTasks.getCurrentTask();

            if(task == null)
                throw new IllegalArgumentException("Cannot do Delete cluster as there are no tasks at the moment");

            return runPostRequest(clusterName,"/Task/Containers/Status/",  url, "status",
                    task.getClusterJobDetailByClusterName(clusterName));
        } catch (Exception ex) {
            return new ClusterTaskStatus(clusterName,"status cluster",false, ex.getMessage());
        }
    }

//    ClusterTaskStatus runPostRequest(String clusterName, String requestUrl,
//                                     String url, String task, ClusterJobDetail detail) throws Exception {
//        int count = 0;
//        ArrayList<ClusterTaskStatus> responses = new ArrayList<>();
//
//        Disposable subscribe = Flux.merge(monoPostRequest(clusterName, requestUrl, url,task, detail))
//                        .log()
//                        .doOnError(System.out::println)
//                        .subscribe(responses::add);
//
//        while(!subscribe.isDisposed() && count < 50){
//            if(responses.size() != 0)
//                break;
//
//            Thread.sleep(1000);
//            count++;
//            System.out.println("Waiting.....");
//        }
//
//        subscribe.dispose();
//
//        return responses.get(0);
//    }


    private ClusterTaskStatus runPostRequest(String clusterName, String requestUrl, String url, String task, ClusterJobDetail detail) throws Exception{
        int count = 0;
        ArrayList<ClusterTaskStatus> responses = new ArrayList<>();

        Disposable subscribe =
                Flux.merge(monoPostRequest(clusterName, requestUrl, url,task, detail))
                        .parallel()
                        .log()
                        .runOn(Schedulers.elastic())
                        .doOnError(System.out::println)
                        .subscribe(responses::add);

        while(!subscribe.isDisposed() && count < 25){
            if(responses.size() != 0)
                break;

            Thread.sleep(1000);
            count++;
            System.out.println("Waiting.....");
        }

        subscribe.dispose();
        ClusterTaskStatus status = (responses.size() > 0) ? responses.get(0) : null;

        return status;
    }


    private ArrayList<Mono<ClusterTaskStatus>> monoPostRequest(String clusterName, String requestUrl, String url, String task,
                                                               ClusterJobDetail detail) throws Exception {

        ArrayList<Mono<ClusterTaskStatus>> clusterResponses = new ArrayList<>();

            client = WebClient.create(url);
            try {
                clusterResponses.add(client.post()
                        .uri(requestUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromPublisher(Mono.just(detail), ClusterJobDetail.class))
                        .retrieve()
                        .bodyToMono(ClusterTaskStatus.class)
                        .onErrorReturn(new ClusterTaskStatus(clusterName, task, false, "Error Connecting to cluster")));
            } catch (Exception ex) {
                throw new Exception("Error Processing Start Task:" + ex.getMessage());
            }

        return clusterResponses;
    }


//    Mono<ClusterTaskStatus> monoPostRequest(String clusterName, String requestUrl, String url ,
//                                            String task, ClusterJobDetail detail ) throws Exception {
//
//        client = WebClient.create(url);
//        try {
//            return client.post()
//                    .uri(requestUrl)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(BodyInserters.fromPublisher(Mono.just(detail), ClusterJobDetail.class))
//                    .retrieve()
//                    .bodyToMono(ClusterTaskStatus.class)
//                    .onErrorReturn(new ClusterTaskStatus(clusterName, task, false, "Error Connecting to cluster"));
//
//        } catch (Exception ex) {
//            throw new Exception("Error Processing Start Task:" + ex.getMessage());
//        }
//
//    }



}