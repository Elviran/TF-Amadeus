package com.iris.consumer.smb;

import jcifs.smb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;

@Component
public class SmbConnectionImpl {

    @Value("${smb.username}")
    private String username;

    @Value("${smb.password}")
    private String password;

    @Value("${smb.domain}")
    private String domain;

    @Value("${smb.url}")
    private String url;

    private static final Logger logger = LoggerFactory.getLogger(SmbConnectionImpl.class);

    private NtlmPasswordAuthentication auth;

    public SmbConnectionImpl(){
    }

    @PostConstruct
    private void setAuth(){
        this.auth = new NtlmPasswordAuthentication(null, username, password);
    }

    public String getUrl() {
        return url;
    }

    public NtlmPasswordAuthentication getAuth(){
        return this.auth;
    }

    //TODO: Place the below in another class

    public void doRecursiveLookup(SmbFile smb){
        try {
            if (smb.isDirectory()) {
                System.out.println(smb.getName());
                for (SmbFile f : smb.listFiles()) {
                    if (f.isDirectory()) {
                        doRecursiveLookup(f);
                    } else {
                        System.out.println("\t:" + f.getName());
                    }
                }
            } else {
                System.out.println("\t:" + smb.getName());
            }
        } catch (SmbException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getFileNamesInDirectory(String path) throws Exception{
        ArrayList<String> fileNames = new ArrayList<>();
        try {
            SmbFile file = new SmbFile(String.format("%s%s", this.url, path), auth);
            if (file.isDirectory()) {
                System.out.println(file.getName());
                for (SmbFile f : file.listFiles()) {
                    fileNames.add(f.getName());
                }
            }
        } catch (Exception e) {
            logger.error("SMB Error when getting filenames in a directory :" + e.getMessage());
           throw new Exception("Error going through file directory: "  + e);
        }
        return fileNames;
    }

    public void downloadFile(String filePath, String pathDir) throws Exception {
        if(doesFileExist(filePath)){
            SmbFile file = new SmbFile(String.format("%s%s", this.url, filePath), auth);

            try{
                InputStream inputStream = file.getInputStream();
                OutputStream outputStream = new FileOutputStream(pathDir);

                byte[] buf = new byte[1024];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0, len);
                }

                outputStream.close();
            }catch (Exception ex){
                throw new Exception("Cannot download given file" + ex);
            }
        }
    }

    public void createNewDirectory(String path) throws Exception {
        SmbFile file = new SmbFile(String.format("%s%s", this.url, path), auth);

        try{
            if(!doesDirectoryExist(path)){
                file.mkdir();
            }
        }catch (Exception ex){
            throw new Exception("Cannot create directory on samba server");
        }
    }

    public void deleteDirectoryFromServer(String path) throws MalformedURLException {
        SmbFile file = new SmbFile(String.format("%s%s", this.url, path), auth);
        //TODO: Implement delete directory from server.
    }

    public void createNewFile(String path) throws SmbException, MalformedURLException {
        SmbFile file = new SmbFile(String.format("%s%s", this.url, path), auth);

        if(!doesFileExist(path)){
            file.createNewFile();
        }
    }

    public void appendToFile(String path, ArrayList<String> contents) throws Exception {

        SmbFile file = new SmbFile(String.format("%s%s", this.url, path), auth);

        if(file == null || contents == null){
            throw new IllegalArgumentException("Smb or contents cannot be set to null");
        }

        try{
            SmbFileOutputStream smbfos = new SmbFileOutputStream(file, true);
            if(doesFileExist(path)){
                for(String content : contents){
                    smbfos.write(content.getBytes());
                }
                smbfos.close();
            }
        }catch (SmbException ex){
            logger.error("SMB Error when getting filenames in a directory :" + ex.getMessage());
            throw new Exception("SMB Error: Error when appending to file");
        }

    }

    public String readFile(String path) throws IOException{
        SmbFile file = new SmbFile(String.format("%s%s", this.url, path), auth);

        String contents = "";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new SmbFileInputStream(file)))) {
            String line = reader.readLine();
            while (line != null) {
                contents += line +"\r\n";
                line = reader.readLine();
            }

            return contents;
        }catch (IOException ex){
            logger.error("SMB Error when reading file :" + ex.getMessage());
            throw new IOException("SMB Error when reading file" + ex.getMessage());
        }
    }

    public Boolean copyFile(String srcFilePath, String destFilePath) throws Exception {
        SmbFile src = new SmbFile(String.format("%s%s", this.url, srcFilePath), auth);
        SmbFile dest = new SmbFile(String.format("%s%s", this.url, destFilePath), auth);

        try{
            if(doesFileExist(srcFilePath)){
                src.copyTo(dest);
            }

            return doesFileExist(destFilePath);

        }catch (Exception ex){
            logger.error("SMB Error when copying files in path : " + ex.getMessage());
            throw new IOException("SMB Error when copying Files to destination: "+ ex.getMessage());
        }
    }

    private String getFilename(String name) throws MalformedURLException {
        SmbFile file = new SmbFile(String.format("%s%s", this.url, name), auth);
        return file.getName();
    }

    private Boolean doesDirectoryExist(String path) throws Exception {
        SmbFile file = new SmbFile(String.format("%s%s", this.url, path), auth);

        try{
            return file.isDirectory();
        }catch (Exception ex){
            logger.error("SMB Error when checking path if exists : " + ex.getMessage());
            throw new IOException("SMB Error when checking path if exists: "+ ex.getMessage());
        }

    }

    private Boolean doesFileExist(String path) throws MalformedURLException, SmbException {
        SmbFile file = new SmbFile(String.format("%s%s", this.url, path), auth);
        return file.exists();
    }
}
