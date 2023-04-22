package org.fullcycle.admin.catalog.application.video.create;

import org.fullcycle.admin.catalog.domain.video.Video;

public record CreateVideoOutput(
    String id
) {

    public static CreateVideoOutput from(final String videoId) {
        return new CreateVideoOutput(videoId);
    }

    public static CreateVideoOutput from(final Video video) {
        return new CreateVideoOutput(video.getId().getValue());
    }

}
