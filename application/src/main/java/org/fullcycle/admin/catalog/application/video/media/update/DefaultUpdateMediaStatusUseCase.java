package org.fullcycle.admin.catalog.application.video.media.update;

import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.video.AudioVideoMedia;
import org.fullcycle.admin.catalog.domain.video.MediaStatus;
import org.fullcycle.admin.catalog.domain.video.MediaType;
import org.fullcycle.admin.catalog.domain.video.Video;
import org.fullcycle.admin.catalog.domain.video.VideoGateway;
import org.fullcycle.admin.catalog.domain.video.VideoID;

import java.util.Objects;
import java.util.Optional;

public class DefaultUpdateMediaStatusUseCase extends UpdateMediaStatusUseCase {

    private final VideoGateway videoGateway;

    public DefaultUpdateMediaStatusUseCase(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public void execute(final UpdateMediaStatusCommand command) {
        final var mediaStatus = command.mediaStatus();
        final var videoId = VideoID.from(command.videoId());
        final var resourceId = command.resourceId();
        final var folder = command.folder();
        final var filename = command.filename();

        final var video = this.videoGateway.findById(videoId)
            .orElseThrow(() -> notFound(videoId));

        final var encodedPath = "%s/%s".formatted(folder, filename);

        if (matches(resourceId, video.getVideo().orElse(null))) {
            update(encodedPath, video, mediaStatus, MediaType.VIDEO);
        }

        if (matches(resourceId, video.getTrailer().orElse(null))) {
            update(encodedPath, video, mediaStatus, MediaType.TRAILER);
        }
    }

    private void update(final String encodedPath, final Video video, final MediaStatus mediaStatus, final MediaType mediaType) {
        final Optional<Video> videoOptional = switch (mediaStatus) {
            case PENDING -> Optional.empty();
            case PROCESSING -> Optional.of(video.processing(mediaType));
            case COMPLETED -> Optional.of(video.completed(mediaType, encodedPath));
        };

        videoOptional.ifPresent(this.videoGateway::update);
    }

    private boolean matches(final String resourceId, final AudioVideoMedia audioVideoMedia) {
        return audioVideoMedia != null && audioVideoMedia.id().equals(resourceId);
    }

    private NotFoundException notFound(final VideoID videoId) {
        return NotFoundException.with(Video.class, videoId);
    }

}
