package com.iris.consumer.services;

import org.iris.templates.ContainerStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ContainerService {

    @Autowired
    private ClusterService service;

    private final RestTemplate template;

    public ContainerService(){
        template = new RestTemplate();
    }

    public ContainerStatus startContainer(String containerName) throws Exception{

        if(containerName == null || containerName.isEmpty())
            throw new IllegalArgumentException("Container Name cannot be empty of null");

        Map.Entry<String, String> entry = service.getUrlPair(containerName);

        if(entry == null)
            throw new IllegalArgumentException("Invalid Container name given");

        try{
            ContainerStatus status = template.postForObject(entry.getValue() + "/Container/Start",
                    postRequest(containerName), ContainerStatus.class);
            status.setName(entry.getKey());

            return status;
        }catch (HttpClientErrorException ex){
            return new ContainerStatus(entry.getKey(),"start Container",false, ex.getMessage());
        }
    }

    public ContainerStatus stopContainer(String containerName) throws Exception{

        if(containerName == null || containerName.isEmpty())
            throw new IllegalArgumentException("Container Name cannot be empty of null");

        Map.Entry<String, String> entry = service.getUrlPair(containerName);

        if(entry == null)
            throw new IllegalArgumentException("Invalid Container name given");

        try{
            ContainerStatus status = template.postForObject(entry.getValue() + "/Container/Stop",
                    postRequest(containerName), ContainerStatus.class);
            status.setName(entry.getKey());

            return status;
        }catch (HttpClientErrorException ex){
            return new ContainerStatus(entry.getKey(),"stop container",false, ex.getMessage());
        }
    }

    public ContainerStatus pauseContainer(String containerName) throws Exception{

        if(containerName == null || containerName.isEmpty())
            throw new IllegalArgumentException("Container Name cannot be empty of null");

        Map.Entry<String, String> entry = service.getUrlPair(containerName);

        if(entry == null)
            throw new IllegalArgumentException("Invalid Container name given");

        try{
            ContainerStatus status = template.postForObject(entry.getValue() + "/Container/Pause",
                    postRequest(containerName), ContainerStatus.class);
            status.setName(entry.getKey());

            return status;
        }catch (HttpClientErrorException ex){
            return new ContainerStatus(entry.getKey(),"pause container",false, ex.getMessage());
        }
    }

    public ContainerStatus restartContainer(String containerName) throws Exception{

        if(containerName == null || containerName.isEmpty())
            throw new IllegalArgumentException("Container Name cannot be empty of null");

        Map.Entry<String, String> entry = service.getUrlPair(containerName);

        if(entry == null)
            throw new IllegalArgumentException("Invalid Container name given");

        try{
            ContainerStatus status = template.postForObject(entry.getValue() + "/Container/Restart",
                    postRequest(containerName), ContainerStatus.class);
            status.setName(entry.getKey());

            return status;
        }catch (HttpClientErrorException ex){
            return new ContainerStatus(entry.getKey(),"restart container",false, ex.getMessage());
        }
    }

    public ContainerStatus resumeContainer(String containerName) throws Exception{

        if(containerName == null || containerName.isEmpty())
            throw new IllegalArgumentException("Container Name cannot be empty of null");

        Map.Entry<String, String> entry = service.getUrlPair(containerName);

        if(entry == null)
            throw new IllegalArgumentException("Invalid Container name given");

        try{
            ContainerStatus status = template.postForObject(entry.getValue() + "/Container/Resume",
                    postRequest(containerName), ContainerStatus.class);
            status.setName(entry.getKey());

            return status;
        }catch (HttpClientErrorException ex){
            return new ContainerStatus(entry.getKey(),"resume container",false, ex.getMessage());
        }
    }

    public ContainerStatus containerStatus(String containerName) throws Exception{

        if(containerName == null || containerName.isEmpty())
            throw new IllegalArgumentException("Container Name cannot be empty of null");

        Map.Entry<String, String> entry = service.getUrlPair(containerName);

        if(entry == null)
            throw new IllegalArgumentException("Invalid Container name given");

        try{
            ContainerStatus status = template.getForObject(String.format("%s/Container/Status/?clusterName=%s",
                        entry.getValue(), containerName),
                        ContainerStatus.class);
            status.setName(entry.getKey());

            return status;
        }catch (HttpClientErrorException ex){
            return new ContainerStatus(entry.getKey(),"stop container",false, ex.getMessage());
        }
    }

    public ContainerStatus deleteContainer(String containerName) throws Exception{

        if(containerName == null || containerName.isEmpty())
            throw new IllegalArgumentException("Container Name cannot be empty of null");

        Map.Entry<String, String> entry = service.getUrlPair(containerName);

        if(entry == null)
            throw new IllegalArgumentException("Invalid Container name given");

        try{
            ContainerStatus status = template.postForObject(entry.getValue() + "/Container/Delete",
                    postRequest(containerName), ContainerStatus.class);
            status.setName(entry.getKey());

            return status;
        }catch (HttpClientErrorException ex){
            return new ContainerStatus(entry.getKey(),"stop container",false, ex.getMessage());
        }
    }

    private HttpEntity<MultiValueMap<String, String>> postRequest(String containerName){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("containerName", containerName);

        return new HttpEntity<>(map, headers);
    }
}
