package org.fullcycle.admin.catalog.application.video.retrieve.list;

public record ListVideosCommand(
    int page,
    int perPage,
    String terms,
    String sort,
    String direction
) {

    public static ListVideosCommand with(
        final int page,
        final int perPage,
        final String terms,
        final String sort,
        final String direction
    ) {
        return new ListVideosCommand(
            page,
            perPage,
            terms,
            sort,
            direction
        );
    }

}
