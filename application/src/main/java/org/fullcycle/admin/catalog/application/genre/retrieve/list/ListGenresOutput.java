package org.fullcycle.admin.catalog.application.genre.retrieve.list;

import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.fullcycle.admin.catalog.domain.genre.GenreID;

import java.time.Instant;

public record ListGenresOutput(
    GenreID id,
    String name,
    boolean isActive,
    Instant createdAt,
    Instant deletedAt
) {

    public static ListGenresOutput from(final Genre genre) {
        return new ListGenresOutput(
            genre.getId(),
            genre.getName(),
            genre.isActive(),
            genre.getCreatedAt(),
            genre.getDeletedAt()
        );
    }

}
