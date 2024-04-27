package org.fullcycle.admin.catalog.infrastructure.configuration.usecases;

import org.fullcycle.admin.catalog.application.video.media.update.DefaultUpdateMediaStatusUseCase;
import org.fullcycle.admin.catalog.application.video.media.update.UpdateMediaStatusUseCase;
import org.fullcycle.admin.catalog.domain.video.VideoGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class VideoUseCaseConfiguration {

    private final VideoGateway videoGateway;

    public VideoUseCaseConfiguration(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Bean
    public UpdateMediaStatusUseCase updateMediaStatusUseCase() {
        return new DefaultUpdateMediaStatusUseCase(this.videoGateway);
    }

}
