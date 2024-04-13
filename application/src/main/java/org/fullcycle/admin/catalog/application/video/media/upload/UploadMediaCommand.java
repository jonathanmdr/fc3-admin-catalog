package org.fullcycle.admin.catalog.application.video.media.upload;

import org.fullcycle.admin.catalog.domain.video.VideoResource;

public record UploadMediaCommand(
    String videoId,
    VideoResource videoResource
) {

    public static UploadMediaCommand with(final String videoId, final VideoResource videoResource) {
        return new UploadMediaCommand(videoId, videoResource);
    }

}
