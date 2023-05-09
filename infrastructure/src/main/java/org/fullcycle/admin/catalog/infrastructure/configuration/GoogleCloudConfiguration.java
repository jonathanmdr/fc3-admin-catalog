package org.fullcycle.admin.catalog.infrastructure.configuration;

import org.fullcycle.admin.catalog.infrastructure.configuration.properties.GoogleCloudProperties;
import org.fullcycle.admin.catalog.infrastructure.configuration.properties.GoogleCloudStorageProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile(
    value = {
        "dev",
        "prod"
    }
)
public class GoogleCloudConfiguration {

    @Bean
    @ConfigurationProperties("google.cloud")
    public GoogleCloudProperties googleCloudProperties() {
        return new GoogleCloudProperties();
    }

    @Bean
    @ConfigurationProperties("google.cloud.storage.video-catalog")
    public GoogleCloudStorageProperties googleCloudStorageProperties() {
        return new GoogleCloudStorageProperties();
    }

}
