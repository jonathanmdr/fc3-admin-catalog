package org.fullcycle.admin.catalog.application.genre.retrieve.list;

public record ListGenresCommand(
    int page,
    int perPage,
    String terms,
    String sort,
    String direction
) {

    public static ListGenresCommand with(
        final int page,
        final int perPage,
        final String terms,
        final String sort,
        final String direction
    ) {
        return new ListGenresCommand(
            page,
            perPage,
            terms,
            sort,
            direction
        );
    }

}
