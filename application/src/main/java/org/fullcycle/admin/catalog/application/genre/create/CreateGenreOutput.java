package org.fullcycle.admin.catalog.application.genre.create;

import org.fullcycle.admin.catalog.domain.genre.Genre;

public record CreateGenreOutput(
    String id
) {

    public static CreateGenreOutput from(final String genreId) {
        return new CreateGenreOutput(genreId);
    }

    public static CreateGenreOutput from(final Genre genre) {
        return new CreateGenreOutput(genre.getId().getValue());
    }

}
