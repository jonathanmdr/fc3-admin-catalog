package org.fullcycle.admin.catalog.application.video.media.get;

import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.validation.Error;
import org.fullcycle.admin.catalog.domain.video.MediaResourceGateway;
import org.fullcycle.admin.catalog.domain.video.MediaType;
import org.fullcycle.admin.catalog.domain.video.VideoID;

import java.util.Objects;

public class DefaultGetMediaUseCase extends GetMediaUseCase {

    private final MediaResourceGateway mediaResourceGateway;

    public DefaultGetMediaUseCase(final MediaResourceGateway mediaResourceGateway) {
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    @Override
    public GetMediaOutput execute(final GetMediaCommand command) {
        final var videoId = VideoID.from(command.videoId());
        final var mediaType = MediaType.of(command.mediaType())
            .orElseThrow(() -> mediaTypeNotFound(command.mediaType()));

        final var resource = this.mediaResourceGateway.getResource(videoId, mediaType)
            .orElseThrow(() -> notFound(command.videoId(), command.mediaType()));

        return GetMediaOutput.with(resource);
    }

    private static NotFoundException notFound(final String videoId, final String mediaType) {
        return NotFoundException.with(new Error("Resource %s not found for video %s".formatted(mediaType, videoId)));
    }

    private static NotFoundException mediaTypeNotFound(final String mediaType) {
        return NotFoundException.with(new Error("Media type %s doesn't exists".formatted(mediaType)));
    }

}
