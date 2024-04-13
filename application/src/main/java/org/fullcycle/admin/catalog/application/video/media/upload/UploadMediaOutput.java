package org.fullcycle.admin.catalog.application.video.media.upload;

import org.fullcycle.admin.catalog.domain.video.MediaType;
import org.fullcycle.admin.catalog.domain.video.Video;

public record UploadMediaOutput(
    String videoId,
    MediaType mediaType
) {

    public static UploadMediaOutput with(final Video video, final MediaType mediaType) {
        return new UploadMediaOutput(video.getId().getValue(), mediaType);
    }

}
