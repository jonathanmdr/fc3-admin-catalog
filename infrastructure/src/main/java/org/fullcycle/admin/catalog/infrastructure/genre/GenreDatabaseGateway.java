package org.fullcycle.admin.catalog.infrastructure.genre;

import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import org.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreJpaEntity;
import org.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class GenreDatabaseGateway implements GenreGateway {

    private final GenreRepository genreRepository;

    public GenreDatabaseGateway(final GenreRepository genreRepository) {
        this.genreRepository = Objects.requireNonNull(genreRepository);
    }

    @Override
    public Pagination<Genre> findAll(final SearchQuery searchQuery) {
        return null;
    }

    @Override
    public Optional<Genre> findById(final GenreID genreID) {
        return Optional.empty();
    }

    @Override
    public Genre create(final Genre genre) {
        return save(genre);
    }

    @Override
    public Genre update(final Genre genre) {
        return null;
    }

    @Override
    public void deleteById(final GenreID genreID) {

    }

    private Genre save(final Genre genre) {
        return this.genreRepository.save(
            GenreJpaEntity.from(genre)
        ).toAggregate();
    }

}
