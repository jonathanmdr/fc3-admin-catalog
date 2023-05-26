package org.fullcycle.admin.catalog.infrastructure.configuration;

import org.fullcycle.admin.catalog.infrastructure.configuration.properties.storage.StorageProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfiguration {

    @Bean
    @ConfigurationProperties("storage.video-catalog")
    public StorageProperties storageProperties() {
        return new StorageProperties();
    }

}
