package org.fullcycle.admin.catalog.application.video.delete;

import org.fullcycle.admin.catalog.domain.video.VideoGateway;
import org.fullcycle.admin.catalog.domain.video.VideoID;

import java.util.Objects;

public class DefaultDeleteVideoUseCase extends DeleteVideoUseCase {

    private final VideoGateway videoGateway;

    public DefaultDeleteVideoUseCase(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public void execute(final String id) {
        this.videoGateway.deleteById(VideoID.from(id));
    }

}
