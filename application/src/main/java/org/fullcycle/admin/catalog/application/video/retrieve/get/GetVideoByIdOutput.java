package org.fullcycle.admin.catalog.application.video.retrieve.get;

import org.fullcycle.admin.catalog.domain.Identifier;
import org.fullcycle.admin.catalog.domain.utils.CollectionUtils;
import org.fullcycle.admin.catalog.domain.video.AudioVideoMedia;
import org.fullcycle.admin.catalog.domain.video.ImageMedia;
import org.fullcycle.admin.catalog.domain.video.Video;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

public record GetVideoByIdOutput(
    String id,
    String title,
    String description,
    int launchedAt,
    double duration,
    boolean opened,
    boolean published,
    String rating,
    Set<String> categories,
    Set<String> genres,
    Set<String> castMembers,
    Optional<AudioVideoMedia> video,
    Optional<AudioVideoMedia> trailer,
    Optional<ImageMedia> banner,
    Optional<ImageMedia> thumbnail,
    Optional<ImageMedia> thumbnailHalf,
    Instant createdAt,
    Instant updatedAt
) {

    public static GetVideoByIdOutput from(final Video video) {
        return new GetVideoByIdOutput(
            video.getId().getValue(),
            video.getTitle(),
            video.getDescription(),
            video.getLaunchedAt().getValue(),
            video.getDuration(),
            video.isOpened(),
            video.isPublished(),
            video.getRating().label(),
            CollectionUtils.mapTo(video.getCategories(), Identifier::getValue),
            CollectionUtils.mapTo(video.getGenres(), Identifier::getValue),
            CollectionUtils.mapTo(video.getCastMembers(), Identifier::getValue),
            video.getVideo(),
            video.getTrailer(),
            video.getBanner(),
            video.getThumbnail(),
            video.getThumbnailHalf(),
            video.getCreatedAt(),
            video.getUpdatedAt()
        );
    }

}
