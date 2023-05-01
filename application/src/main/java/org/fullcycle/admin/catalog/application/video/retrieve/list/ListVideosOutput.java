package org.fullcycle.admin.catalog.application.video.retrieve.list;

import org.fullcycle.admin.catalog.domain.video.Video;

import java.time.Instant;

public record ListVideosOutput(
    String id,
    String title,
    String description,
    Instant createdAt,
    Instant updatedAt
) {

    public static ListVideosOutput from(final Video video) {
        return new ListVideosOutput(
            video.getId().getValue(),
            video.getTitle(),
            video.getDescription(),
            video.getCreatedAt(),
            video.getUpdatedAt()
        );
    }

}
