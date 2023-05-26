package org.fullcycle.admin.catalog.infrastructure.configuration;

import com.google.api.gax.retrying.RetrySettings;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.http.HttpTransportOptions;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.fullcycle.admin.catalog.infrastructure.configuration.properties.google.GoogleCloudProperties;
import org.fullcycle.admin.catalog.infrastructure.configuration.properties.google.GoogleCloudStorageProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.threeten.bp.Duration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import static org.fullcycle.admin.catalog.infrastructure.utils.ProfileUtils.DEVELOPMENT;
import static org.fullcycle.admin.catalog.infrastructure.utils.ProfileUtils.PRODUCTION;

@Configuration
@Profile(
    value = {
        DEVELOPMENT,
        PRODUCTION
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

    @Bean
    public Credentials credentials(final GoogleCloudProperties googleCloudProperties) {
        final var jsonContent = Base64.getDecoder()
            .decode(googleCloudProperties.getCredentials());

        try (final var byteArrayInputStream = new ByteArrayInputStream(jsonContent)) {
            return GoogleCredentials.fromStream(byteArrayInputStream);
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Bean
    public Storage storage(
        final GoogleCloudStorageProperties googleCloudStorageProperties,
        final Credentials credentials
    ) {
        final var transportOptions = HttpTransportOptions.newBuilder()
            .setConnectTimeout(googleCloudStorageProperties.getConnectTimeout())
            .setReadTimeout(googleCloudStorageProperties.getReadTimeout())
            .build();

        final var retrySettings = RetrySettings.newBuilder()
            .setInitialRetryDelay(Duration.ofMillis(googleCloudStorageProperties.getRetryDelay()))
            .setMaxAttempts(googleCloudStorageProperties.getRetryMaxAttempts())
            .setMaxRetryDelay(Duration.ofMillis(googleCloudStorageProperties.getRetryMaxDelay()))
            .setRetryDelayMultiplier(googleCloudStorageProperties.getRetryMultiplier())
            .build();

        final var storageOptions = StorageOptions.newBuilder()
            .setCredentials(credentials)
            .setTransportOptions(transportOptions)
            .setRetrySettings(retrySettings)
            .build();

        return storageOptions.getService();
    }

}
