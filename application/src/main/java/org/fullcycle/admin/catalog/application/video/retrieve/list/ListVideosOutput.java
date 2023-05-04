package org.fullcycle.admin.catalog.application.video.retrieve.list;

import org.fullcycle.admin.catalog.domain.video.VideoPreview;

import java.time.Instant;

public record ListVideosOutput(
    String id,
    String title,
    String description,
    Instant createdAt,
    Instant updatedAt
) {

    public static ListVideosOutput from(final VideoPreview video) {
        return new ListVideosOutput(
            video.id(),
            video.title(),
            video.description(),
            video.createdAt(),
            video.updatedAt()
        );
    }

}
