package org.fullcycle.admin.catalog.application.video.update;

import org.fullcycle.admin.catalog.domain.resource.Resource;

import java.util.Optional;
import java.util.Set;

public record UpdateVideoCommand(
    String id,
    String title,
    String description,
    Integer launchedAt,
    Double duration,
    Boolean opened,
    Boolean published,
    String rating,
    Set<String> categories,
    Set<String> genres,
    Set<String> castMembers,
    Optional<Resource> video,
    Optional<Resource> trailer,
    Optional<Resource> banner,
    Optional<Resource> thumbnail,
    Optional<Resource> thumbnailHalf
) {

    public static UpdateVideoCommand with(
        final String id,
        final String title,
        final String description,
        final Integer launchedAt,
        final Double duration,
        final Boolean opened,
        final Boolean published,
        final String rating,
        final Set<String> categories,
        final Set<String> genres,
        final Set<String> castMembers,
        final Resource video,
        final Resource trailer,
        final Resource banner,
        final Resource thumbnail,
        final Resource thumbnailHalf
    ) {
        return new UpdateVideoCommand(
            id,
            title,
            description,
            launchedAt,
            duration,
            opened,
            published,
            rating,
            categories,
            genres,
            castMembers,
            Optional.ofNullable(video),
            Optional.ofNullable(trailer),
            Optional.ofNullable(banner),
            Optional.ofNullable(thumbnail),
            Optional.ofNullable(thumbnailHalf)
        );
    }

}
