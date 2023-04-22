package org.fullcycle.admin.catalog.application.video.create;

import org.fullcycle.admin.catalog.domain.video.Resource;

import java.util.Optional;
import java.util.Set;

public record CreateVideoCommand(
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
    Optional<Resource> video,
    Optional<Resource> trailer,
    Optional<Resource> banner,
    Optional<Resource> thumbnail,
    Optional<Resource> thumbnailHalf
) {

    public static CreateVideoCommand with(
        final String title,
        final String description,
        final int launchedAt,
        final double duration,
        final boolean opened,
        final boolean published,
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
        return new CreateVideoCommand(
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
