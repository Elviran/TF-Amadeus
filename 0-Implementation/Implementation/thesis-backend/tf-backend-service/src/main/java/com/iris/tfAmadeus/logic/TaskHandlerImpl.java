package com.iris.tfAmadeus.logic;
import com.iris.tfAmadeus.components.PendingTasks;
import com.iris.tfAmadeus.components.Task;
import com.iris.tfAmadeus.configs.FileStorageProperties;

import org.apache.commons.io.FileUtils;
import org.iris.templates.ClusterJobDetail;
import org.iris.templates.Tensorboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

@Component
public class TaskHandlerImpl {

    @Autowired
    private PendingTasks pendingTasks;

    private final TFFileHandler fileHandler;
    private final PythonFileParser pythonParser;

    private final Path fileStorageLocation;
    private final Path externalStorageLocation;
    private final Path envStorageDirectoryLocation;
    private final Path tfConfDirectoryLocation;
    private final Path runnerScriptLocation;

    @Autowired
    public TaskHandlerImpl(FileStorageProperties fileStorageProperties){
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        this.externalStorageLocation = Paths.get(fileStorageProperties.getStorageDir()).toAbsolutePath().normalize();
        this.envStorageDirectoryLocation = Paths.get(fileStorageProperties.getConfigDir()).toAbsolutePath().normalize();
        this.tfConfDirectoryLocation = Paths.get(fileStorageProperties.getTfConfDir()).toAbsolutePath().normalize();
        this.runnerScriptLocation = Paths.get(fileStorageProperties.getRunnerDir()).toAbsolutePath().normalize();
        fileHandler = new TFFileHandler();
        pythonParser = new PythonFileParser();
    }

    /**
     * Prepares Task by creating a new directory into pending_tasks
     * and copies the env files, tfconfigs, user script and runnerscript.
     * @param filename The given user file name.
     * @param tfConfigs The Tensorflow json config files
     * @return
     * @throws Exception
     */
    public Task prepareTask(String filename, String tfConfigs) throws Exception {
        if(!TFFileHandler.checkFileName(filename))
            throw new IllegalArgumentException("Invalid file naming convention");

        Path scriptPath = fileStorageLocation.resolve(filename).normalize();

        // Set template
       ScriptTemplate template = pythonParser.ParseFile(scriptPath);

        ArrayList<ClusterJobDetail> jobDetails = prepareTaskDirectory(template.getModelNames(),filename, scriptPath, tfConfigs);

        String jobName = filename.substring(0,filename.indexOf("."));
        pendingTasks.enqueueTask(new Task(jobName, jobDetails, template));

        prepareTaskTensorBoards(pendingTasks.getCurrentTask());

        return pendingTasks.getCurrentTask();
    }

    public void deleteTask() throws IOException {
        Task task = pendingTasks.getCurrentTask();
        File file = new File(String.format("%s/pending_tasks/%s", externalStorageLocation, task.getName()));

        Boolean success = fileHandler.deleteDirectory(file);

        if(success){
            pendingTasks.dequeueTask();
        }
        else{
            throw new IOException("Unable to delete " + pendingTasks.getCurrentTask().getName() + "Directory");
        }

    }

    private ArrayList<ClusterJobDetail> prepareTaskDirectory(ArrayList<String> modelNames, String filename,
                                                             Path scriptPath, String tfConfigs) throws IOException {

        ArrayList<ClusterJobDetail> details = new ArrayList<>();
        String jobName = filename.substring(0,filename.indexOf("."));

        if(modelNames != null){
            for(int i = 0; i < modelNames.size(); i++){

                //Create Directories
                Path pendingDirectory = fileHandler.createDirectory("pending_tasks", externalStorageLocation);
                Path workDirectory = fileHandler.createDirectory(jobName, pendingDirectory);
                Path clusterPendingPath = fileHandler.createDirectory(String.format("c%d",i), workDirectory);
                Path modelDirectory = fileHandler.createDirectory("model", clusterPendingPath);

                //Get TfConfigPath TODO: future proof for when implementing multi-worker build as well.
                Path tfConfPath = Paths.get(
                        String.format("%s%s%s", tfConfDirectoryLocation , File.separator ,tfConfigs)
                );

                //Copy User Script to directory
                Files.copy( scriptPath,
                        clusterPendingPath.resolve("user.py"),
                        StandardCopyOption.REPLACE_EXISTING);

                //Copy env file to directory
                Files.copy( envStorageDirectoryLocation.resolve(String.format("c%d.env", i)),
                        clusterPendingPath.resolve(String.format("c%d.env", i)),
                        StandardCopyOption.REPLACE_EXISTING);

                //copy runner script to directory
                Files.copy( runnerScriptLocation,
                        clusterPendingPath.resolve("runner.py"),
                        StandardCopyOption.REPLACE_EXISTING);

                //Copy Tf-Configs directory
                FileUtils.copyDirectory(tfConfPath.toFile(), clusterPendingPath.resolve("tfConfigs").toFile());

                //Edit Paths for the docker files to be able to use. Remember that we will be mounting a directory
                // and connect to the samba server, so the pathing has to be different.
                String currPath = clusterPendingPath.toString();
                String workDir = currPath.substring(currPath.indexOf("pending_tasks"));
                String modelDir = String.format("%s%s%s", workDir, File.separator ,"model");
                String envFileName = String.format("c%d.env", i);
                String clusterName = String.format("cluster%d", i + 1);

                //Add in arraylist.
                details.add(new ClusterJobDetail("start", clusterName ,jobName, modelDir ,workDir, modelNames.get(i).trim(),
                        envFileName));
            }
        }

        return details;
    }

    private void prepareTaskTensorBoards(Task task){
        int port = 6006;

        for(ClusterJobDetail jobDetail : task.getClusterJobDetails()){

            String command = String.format("docker run -p %s:%s -d --name=%s -v shared_drive:/media/samba " +
                            "tensorflow/tensorflow tensorboard --bind_all --port=%s --logdir /media/samba/%s",
                    port, port, jobDetail.getModelName(),
                    port ,jobDetail.getModelDir());

            Tensorboard board = new Tensorboard(jobDetail.getModelName(), port, command, false);
            jobDetail.setTensorboard(board);
            port++;
        }
    }

}
