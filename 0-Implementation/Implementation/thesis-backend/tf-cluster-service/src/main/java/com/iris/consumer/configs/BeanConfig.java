package com.iris.consumer.configs;

import com.iris.consumer.smb.SmbConnectionImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public SmbConnectionImpl smbConnection(){
        return new SmbConnectionImpl();
    }

}
