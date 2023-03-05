package org.fullcycle.admin.catalog.application.genre.update;

import org.fullcycle.admin.catalog.domain.genre.Genre;

public record UpdateGenreOutput(
    String id
) {

    public static UpdateGenreOutput from(final Genre genre) {
        return new UpdateGenreOutput(genre.getId().getValue());
    }

}
