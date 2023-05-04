package org.fullcycle.admin.catalog.application.video.retrieve.list;

import java.util.Set;

public record ListVideosCommand(
    int page,
    int perPage,
    String terms,
    String sort,
    String direction,
    Set<String> categories,
    Set<String> genres,
    Set<String> castMembers
) {

    public static ListVideosCommand with(
        final int page,
        final int perPage,
        final String terms,
        final String sort,
        final String direction,
        final Set<String> categories,
        final Set<String> genres,
        final Set<String> castMembers
    ) {
        return new ListVideosCommand(
            page,
            perPage,
            terms,
            sort,
            direction,
            categories,
            genres,
            castMembers
        );
    }

}
