package com.iris.tfAmadeus.logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TFFileHandler {

    //pending directory.
    public Path createDirectory(String dir, Path storagePath) throws IOException {

        Path pathToFolder = Paths.get(storagePath + "/" + dir);

        try{

            if(!Files.exists(pathToFolder)){
                Files.createDirectory(pathToFolder);

                //Set Path Permissions.
                Set<PosixFilePermission> perms = new HashSet<>();
                perms.add(PosixFilePermission.OWNER_READ);
                perms.add(PosixFilePermission.OWNER_WRITE);
                perms.add(PosixFilePermission.OWNER_EXECUTE);

                perms.add(PosixFilePermission.GROUP_READ);
                perms.add(PosixFilePermission.GROUP_WRITE);
                perms.add(PosixFilePermission.GROUP_EXECUTE);

                perms.add(PosixFilePermission.OTHERS_READ);
                perms.add(PosixFilePermission.OTHERS_WRITE);
                perms.add(PosixFilePermission.OTHERS_EXECUTE);

                Files.setPosixFilePermissions(pathToFolder, perms);
            }
        }catch (Exception ex){
            throw new IOException("Cannot Create Directory");
        }

        return pathToFolder;
    }

    public static boolean checkFileName(String filename){
        String regex = "^[a-zA-Z0-9.-]+$";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(filename);

        return matcher.matches();
    }

    public boolean deleteDirectory(File directoryToBeDeleted){
        File[] fileContents = directoryToBeDeleted.listFiles();

        if(Files.exists(directoryToBeDeleted.toPath())){
            if (fileContents != null) {
                for (File file : fileContents) {
                    deleteDirectory(file);
                }
            }
            return directoryToBeDeleted.delete();
        }

        return true;

    }

}
