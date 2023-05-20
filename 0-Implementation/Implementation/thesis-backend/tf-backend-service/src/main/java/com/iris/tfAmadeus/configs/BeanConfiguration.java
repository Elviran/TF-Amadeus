package com.iris.tfAmadeus.configs;

import com.iris.tfAmadeus.components.PendingTasks;
import com.iris.tfAmadeus.components.Task;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public PendingTasks pendingTasks(){
        return new PendingTasks();
    }

}
