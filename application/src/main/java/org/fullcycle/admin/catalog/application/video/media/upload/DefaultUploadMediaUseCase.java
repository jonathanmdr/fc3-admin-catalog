package org.fullcycle.admin.catalog.application.video.media.upload;

import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.video.MediaResourceGateway;
import org.fullcycle.admin.catalog.domain.video.Video;
import org.fullcycle.admin.catalog.domain.video.VideoGateway;
import org.fullcycle.admin.catalog.domain.video.VideoID;

import java.util.Objects;

public class DefaultUploadMediaUseCase extends UploadMediaUseCase {

    private final MediaResourceGateway mediaResourceGateway;
    private final VideoGateway videoGateway;

    public DefaultUploadMediaUseCase(final MediaResourceGateway mediaResourceGateway, final VideoGateway videoGateway) {
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public UploadMediaOutput execute(final UploadMediaCommand command) {
        final var videoId = VideoID.from(command.videoId());
        final var resource = command.videoResource();

        final var video = this.videoGateway.findById(videoId)
            .orElseThrow(() -> notFoundException(videoId));

        switch (resource.type()) {
            case VIDEO -> video.updateVideoMedia(this.mediaResourceGateway.storeAudioVideo(videoId, resource));
            case TRAILER -> video.updateTrailerMedia(this.mediaResourceGateway.storeAudioVideo(videoId, resource));
            case BANNER -> video.updateBannerMedia(this.mediaResourceGateway.storeImage(videoId, resource));
            case THUMBNAIL -> video.updateThumbnailMedia(this.mediaResourceGateway.storeImage(videoId, resource));
            case THUMBNAIL_HALF -> video.updateThumbnailHalfMedia(this.mediaResourceGateway.storeImage(videoId, resource));
        }

        return UploadMediaOutput.with(this.videoGateway.update(video), resource.type());
    }

    private NotFoundException notFoundException(final VideoID videoId) {
        return NotFoundException.with(Video.class, videoId);
    }

}
