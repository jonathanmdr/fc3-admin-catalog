package org.fullcycle.admin.catalog.application.genre.retrieve.get;

import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.fullcycle.admin.catalog.domain.genre.GenreID;

import java.time.Instant;
import java.util.List;

public record GetGenreByIdOutput(
    GenreID id,
    String name,
    List<CategoryID> categories,
    Boolean isActive,
    Instant createdAt,
    Instant updatedAt,
    Instant deletedAt
) {

    public static GetGenreByIdOutput from(final Genre genre) {
        return new GetGenreByIdOutput(
            genre.getId(),
            genre.getName(),
            genre.getCategories(),
            genre.isActive(),
            genre.getCreatedAt(),
            genre.getUpdatedAt(),
            genre.getDeletedAt()
        );
    }

}
