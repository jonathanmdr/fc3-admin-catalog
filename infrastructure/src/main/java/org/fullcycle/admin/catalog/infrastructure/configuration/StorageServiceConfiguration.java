package org.fullcycle.admin.catalog.infrastructure.configuration;

import com.google.cloud.storage.Storage;
import org.fullcycle.admin.catalog.infrastructure.configuration.properties.google.GoogleCloudStorageProperties;
import org.fullcycle.admin.catalog.infrastructure.services.StorageService;
import org.fullcycle.admin.catalog.infrastructure.services.impl.GoogleCloudStorageService;
import org.fullcycle.admin.catalog.infrastructure.services.local.InMemoryStorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static org.fullcycle.admin.catalog.infrastructure.utils.ProfileUtils.DEVELOPMENT;
import static org.fullcycle.admin.catalog.infrastructure.utils.ProfileUtils.PRODUCTION;

@Configuration
public class StorageServiceConfiguration {

    private static final String STORAGE_SERVICE_BEAN_NAME = "storageService";

    @Bean(STORAGE_SERVICE_BEAN_NAME)
    @Profile(
        value = {
            DEVELOPMENT,
            PRODUCTION
        }
    )
    public StorageService googleCloudStorageService(
        final GoogleCloudStorageProperties googleCloudStorageProperties,
        final Storage storage
    ) {
        return new GoogleCloudStorageService(googleCloudStorageProperties.getBucket(), storage);
    }

    @Bean(STORAGE_SERVICE_BEAN_NAME)
    @ConditionalOnMissingBean
    public StorageService inMemoryStorageService() {
        return new InMemoryStorageService();
    }

}
