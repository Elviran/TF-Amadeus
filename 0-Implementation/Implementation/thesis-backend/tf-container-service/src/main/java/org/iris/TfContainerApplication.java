package org.iris;

import org.iris.properties.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class
})
public class TfContainerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TfContainerApplication.class, args);
    }
}
