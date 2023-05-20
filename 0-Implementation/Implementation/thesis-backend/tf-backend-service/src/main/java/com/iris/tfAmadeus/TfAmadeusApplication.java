package com.iris.tfAmadeus;

import com.iris.tfAmadeus.configs.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
@EnableJms
public class TfAmadeusApplication {

	public static void main(String[] args) {
		SpringApplication.run(TfAmadeusApplication.class, args);
	}

}
