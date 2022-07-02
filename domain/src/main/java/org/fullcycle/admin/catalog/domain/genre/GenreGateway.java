package org.fullcycle.admin.catalog.domain.genre;

import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.domain.pagination.SearchQuery;

import java.util.Optional;

public interface GenreGateway {

    Pagination<Genre> findAll(final SearchQuery searchQuery);
    Optional<Genre> findById(final GenreID genreID);
    Genre create(final Genre genre);
    Genre update(final Genre genre);
    void deleteById(final GenreID genreID);

}
