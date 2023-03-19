package org.fullcycle.admin.catalog.application.genre.retrieve.get;

import org.fullcycle.admin.catalog.domain.genre.GenreID;

public record GetGenreByIdCommand(
    String id
) {

    public static GetGenreByIdCommand with(final String id) {
        return new GetGenreByIdCommand(
            GenreID.from(id).getValue()
        );
    }

}
