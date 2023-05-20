package org.iris.components;

import org.iris.enums.ContainerState;
import org.iris.exceptions.CommandLineException;
import org.iris.properties.FileStorageProperties;
import org.iris.services.ContainerService;
import org.iris.templates.CommandOutput;
import org.iris.templates.ContainerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class QueryContainers {
    private final Path fileStorageLocation;

    private final Logger logger = LoggerFactory.getLogger(QueryContainers.class);

    @Autowired
    public QueryContainers(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getStorageDir()).toAbsolutePath().normalize();
    }

    /**
     * Starts the given docker container
     * @param containerName
     * @return
     */
    public ContainerStatus startContainer(String dockerDir, String envFile, String containerName) throws InterruptedException {

        String command = "";

        if(envFile == null){
            command = String.format("docker-compose -f %s/docker-compose.yml up -d --build",
                    fileStorageLocation.resolve(dockerDir).toString(),
                    containerName);
        }else{
            command = String.format("docker-compose -f %s/docker-compose.yml --env-file=%s up -d --build",
                    fileStorageLocation.resolve(dockerDir).toString(),
                    fileStorageLocation.resolve(envFile).toString(),
                    containerName);
        }

        return _startContainer(command, containerName);
    }

    public ContainerStatus startContainer(String command, String containerName) throws InterruptedException {
        return _startContainer(command, containerName);
    }

    /**
     * Stops the given docker container
     * @param containerName
     * @return
     */
    public ContainerStatus stopContainer(String containerName){
        if(!doesContainerExist(containerName)){
            logger.error("Cannot stop an non-existing container");
            return new ContainerStatus(containerName, "stop", false, "Cannot stop an non-existing container");
        }

        if(isDead(containerName)){
            logger.warn("Container is already stopped");
            return new ContainerStatus(containerName, "stop", true, "Container is already stopped");
        }

        if(isPaused(containerName))
            resumeContainer(containerName);

        changeContainerState(containerName, "stop");
        return new ContainerStatus(containerName, "stop", isDead(containerName),null);
    }

    /**
     * Pauses the given docker container
     * @param containerName
     * @return
     */
    public ContainerStatus pauseContainer(String containerName){

        if(!doesContainerExist(containerName)){
            logger.error("Cannot stop an non-existing container");
            return new ContainerStatus(containerName, "pause", false, "Cannot stop an non-existing container");
        }

        if(isPaused(containerName)) {
            logger.warn("Container already paused");
            return new ContainerStatus(containerName, "pause", true, "Container already paused");
        }

        if(isDead(containerName)){
            logger.warn("Container is stopped");
            return new ContainerStatus(containerName, "pause", false, "Container is stopped");
        }

        if(isRunning(containerName))
            changeContainerState(containerName, "pause");


        return new ContainerStatus(containerName, "pause", isPaused(containerName), null);
    }

    /**
     * Checks the state of the given docker container
     * @param containerName the container name
     * @param state State ContainerState enum
     * @return
     */
    public CommandOutput checkContainerStatus(String containerName, ContainerState state){

        CommandOutput result;

        try{
            String command = String.format("docker inspect -f '{{.State.%s}}' %s", state, containerName);
            result = executeCommandWithOutput(command);

            return result;

        }catch (IllegalArgumentException ex){
            throw new IllegalArgumentException("Wrong Container Status State given");
        }catch (CommandLineException ex){
            throw new CommandLineException("Could not query command");
        }
    }

    /**
     * Resumes a paused docker container
     * @param containerName the container name
     * @return
     */
    public ContainerStatus resumeContainer(String containerName){
        if(!doesContainerExist(containerName)){
            logger.warn("Cannot resume an non-existing container");
            return new ContainerStatus(containerName, "resume", false, "Cannot resume an non-existing container");
        }

        if (!isPaused(containerName)){
            logger.warn("Cannot resume an unpaused container");
            return new ContainerStatus(containerName, "resume", false, "Cannot resume an unpaused container");
        }

        changeContainerState(containerName, "unpause");
        return new ContainerStatus(containerName, "resume", isRunning(containerName), null);
    }

    /**
     * Restarts a docker container
     * @param containerName the container name
     * @return
     */
    public ContainerStatus restartContainer(String containerName) throws InterruptedException {

        if(!doesContainerExist(containerName)){
            logger.warn("Cannot restart an non-existing container");
            return new ContainerStatus(containerName, "restart", false, "Cannot restart an non-existing container");
        }

        changeContainerState(containerName, "restart");

        Thread.sleep(10000);
        return new ContainerStatus(containerName, "restart", isRunning(containerName), null);
    }

    /**
     * Resumes a paused docker container
     * @param containerName the container name
     * @return
     */
    public ContainerStatus deleteContainer(String containerName){

        try{
            if(!doesContainerExist(containerName)){
                logger.warn("Cannot delete an non-existing container");
                return new ContainerStatus(containerName, "delete", false, "Cannot delete an non-existing container");
            }

            //If container is currently running..
            if(isPaused(containerName) || isRunning(containerName)){
                stopContainer(containerName); //stop container
            }

            String command = String.format("docker rm %s", containerName);

            executeDockerCommand(command);
        }catch (CommandLineException ex){
            logger.error("Cannot delete an non-existing container");
            return new ContainerStatus(containerName, "delete", false, "Cannot delete a container that does not exist");
        }

        //check if container is deleted
        return new ContainerStatus(containerName, "delete", !isContainerDeleted(containerName), null);
    }

    private Boolean isContainerDeleted(String containerName){
        String command = String.format("docker rm %s", containerName);

        CommandOutput result = executeCommandWithOutput(command);

        return result.getLine().contains("No such container");
    }

    /**
     * Checks if a docker container exists
     * @param containerName the container name
     * @return
     */
    private Boolean doesContainerExist(String containerName){
        String command = String.format("docker ps -a -f name=%s", containerName);

        CommandOutput result = executeCommandWithOutput(command);

        return result.getLine().contains(containerName);
    }

    /**
     * Changes the state of a docker container
     * @param containerName the container name
     * @return
     */
    private void changeContainerState(String containerName, String state){
        String command = String.format("docker %s %s", state,containerName);

        CommandOutput result = checkContainerStatus(containerName, ContainerState.Status);

        if(result.isError())
            throw new CommandLineException("Container does not exist to change its state");

        try{
            executeDockerCommand(command);
        }catch (CommandLineException ex){
            throw new CommandLineException("Could not query command at this time..");
        }
    }

    /**
     * Starts a container using a specified docker command
     * @param command
     * @param containerName
     * @return
     * @throws InterruptedException
     */
    private ContainerStatus _startContainer(String command, String containerName) throws InterruptedException {

        CommandOutput output;

        if(command.isEmpty() || containerName.isEmpty()){
            logger.error("Command or Container name cannot be empty");
            return new ContainerStatus(containerName, "start", false, "Command or Container name cannot be empty");
        }

        if(doesContainerExist(containerName))
            deleteContainer(containerName);

        try{
            output = executeCommandWithOutput(command);
        }catch (CommandLineException ex){
            return new ContainerStatus(containerName, "start", false, "Could not query command at this time..");
        }

        Thread.sleep(10000);
        if(!isRunning(containerName)){
            return new ContainerStatus(containerName, "start", false, output.getMessage());
        }

        return new ContainerStatus(containerName, "start", true, null);
    }

    // Check container status..

    /**
     * Checks if a docker container is running
     * @param containerName the container name
     * @return
     */
    private Boolean isRunning(String containerName){
        CommandOutput result = checkContainerStatus(containerName, ContainerState.Running);

        return result.getLine().contains("true");
    }

    /**
     * Checks if a docker container is paused
     * @param containerName the container name
     * @return
     */
    private Boolean isPaused(String containerName){
        CommandOutput result = checkContainerStatus(containerName, ContainerState.Paused);

        return result.getLine().contains("true");
    }

    /**
     * Checks if a docker container is dead
     * @param containerName the container name
     * @return
     */
    private Boolean isDead(String containerName){
        CommandOutput result = checkContainerStatus(containerName, ContainerState.Status);

        return result.getLine().contains("exited");
    }

    // Command line functions

    /**
     * Executes a command line argument
     * @return
     */
    private void executeDockerCommand(String command){
        System.out.println("Executing Docker command:\n   " + command);
        Runtime r = Runtime.getRuntime();
        StringBuilder builder = new StringBuilder();

        try {
            Process p = r.exec(command);
            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            System.out.println("Executing Command.....");
            p.waitFor();
            b.close();
            System.out.println("Command Finished.....");
        } catch (CommandLineException | IOException | InterruptedException e) {
            throw new CommandLineException("Failed to execute command");
        }
    }

    /**
     * Executes a command line argument and returns an  output
     * @return
     */
    private CommandOutput executeCommandWithOutput(String command){
        System.out.println("Executing Docker command:\n   " + command);
        Runtime r = Runtime.getRuntime();
        StringBuilder inputBuilder = new StringBuilder();
        StringBuilder errorBuilder = new StringBuilder();

        try {
            Process p = r.exec(command);
            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String line = "";

            while ((line = b.readLine()) != null) {
                System.out.println(line);
                inputBuilder.append(line);
                if(inputBuilder.length() > 10)
                    inputBuilder.delete(0, 5);
            }

            String error = "";
            while((error = err.readLine()) != null){
                errorBuilder.append(error);
                if(errorBuilder.length() > 10)
                    errorBuilder.delete(0, 5);
                System.out.println(error);
            }

            p.waitFor();
            b.close();
            err.close();
        } catch (CommandLineException | IOException | InterruptedException e) {
            throw new CommandLineException("Failed to execute command");
        }

        if(errorBuilder.toString().isBlank())
            return new CommandOutput(inputBuilder.toString(), false, null);
        else
            return new CommandOutput(inputBuilder.toString(), true, errorBuilder.toString());
    }

}
