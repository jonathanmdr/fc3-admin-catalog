package org.fullcycle.admin.catalog.application.video.update;

import org.fullcycle.admin.catalog.domain.video.Video;

public record UpdateVideoOutput(
    String id
) {

    public static UpdateVideoOutput from(final String videoId) {
        return new UpdateVideoOutput(videoId);
    }

    public static UpdateVideoOutput from(final Video video) {
        return new UpdateVideoOutput(video.getId().getValue());
    }

}
