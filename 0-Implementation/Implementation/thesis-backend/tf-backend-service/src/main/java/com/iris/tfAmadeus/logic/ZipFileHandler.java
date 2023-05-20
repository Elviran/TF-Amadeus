package com.iris.tfAmadeus.logic;

import com.iris.tfAmadeus.controllers.ModelController;
import com.iris.tfAmadeus.services.storage.SambaStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ZipFileHandler {

    @Autowired
    private SambaStorageService sambaStorageService;

    private static final Logger logger = LoggerFactory.getLogger(ZipFileHandler.class);

    public void zipDirectory(String modelPath, final HttpServletResponse response) throws Exception {
        try (ZipOutputStream zippedOut = new ZipOutputStream(response.getOutputStream())) {
            Resource resource = sambaStorageService.loadFileAsResource(modelPath);
            logger.info(String.format("File size for cluster model:%s", resource.contentLength()));

            for(File file : Objects.requireNonNull(resource.getFile().listFiles())){
                FileSystemResource fileSystemResource = new FileSystemResource(file);

                if(!fileSystemResource.exists())
                    throw new RuntimeException("Error, Model Directory not found...: " + modelPath);

                if(file.listFiles() != null)
                    addDirRecursively(file.getName(), file.getAbsolutePath(), file, zippedOut);
                else
                    addFileToZip(file, zippedOut);
            }

            zippedOut.finish();
        } catch (Exception e) {
            throw new Exception("Invalid model Filepath given!!");
        }
    }

    private static String fileToRelativePath (File file, String baseDir) {
        return file.getAbsolutePath()
                .substring(baseDir.length() + 1);
    }

    private static void addFileToZip(File file, final ZipOutputStream zippedOut) throws IOException {
        FileSystemResource fileSystemResource = new FileSystemResource(file);

        ZipEntry zipEntry = new ZipEntry(fileSystemResource.getFilename());
        // Configure the zip entry, the properties of the file
        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        zipEntry.setLastModifiedTime(attr.lastModifiedTime());
        zipEntry.setCreationTime(attr.creationTime());
        zipEntry.setLastAccessTime(attr.lastAccessTime());
        zipEntry.setTime(attr.lastModifiedTime().toMillis());
        zippedOut.putNextEntry(zipEntry);
        // And the content of the resource:
        StreamUtils.copy(fileSystemResource.getInputStream(), zippedOut);
        zippedOut.closeEntry();
    }

    private static void addDirRecursively(String baseDirName, String baseDir, File dirFile, final ZipOutputStream out) throws IOException{
        File[] files = dirFile.listFiles();

        if(files != null){
            for (File file : files) {
                if (file.isDirectory()) {
                    addDirRecursively(baseDirName, baseDir, file, out);
                    continue;
                }

                ZipEntry zipEntry = new ZipEntry(baseDirName + File.separatorChar +
                        fileToRelativePath(file, baseDir));
                BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                zipEntry.setLastModifiedTime(attr.lastModifiedTime());
                zipEntry.setCreationTime(attr.creationTime());
                zipEntry.setLastAccessTime(attr.lastAccessTime());
                zipEntry.setTime(attr.lastModifiedTime().toMillis());

                out.putNextEntry(zipEntry);
                try (BufferedInputStream in = new BufferedInputStream(new
                        FileInputStream(file))) {
                    byte[] b = new byte[1024];
                    int count;
                    while ((count = in.read(b)) > 0) {
                        out.write(b, 0, count);
                    }
                    out.closeEntry();
                }
            }
        }
    }
}
