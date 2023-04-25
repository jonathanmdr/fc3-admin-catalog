package org.fullcycle.admin.catalog.application.video.retrieve.get;

import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.video.Video;
import org.fullcycle.admin.catalog.domain.video.VideoGateway;
import org.fullcycle.admin.catalog.domain.video.VideoID;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultGetVideoByIdUseCase extends GetVideoByIdUseCase {

    private final VideoGateway videoGateway;

    public DefaultGetVideoByIdUseCase(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public GetVideoByIdOutput execute(final GetVideoByIdCommand command) {
        final var id = VideoID.from(command.id());

        return this.videoGateway.findById(id)
            .map(GetVideoByIdOutput::from)
            .orElseThrow(videoNotFound(id));
    }

    private Supplier<NotFoundException> videoNotFound(final VideoID id) {
        return () -> NotFoundException.with(Video.class, id);
    }

}
