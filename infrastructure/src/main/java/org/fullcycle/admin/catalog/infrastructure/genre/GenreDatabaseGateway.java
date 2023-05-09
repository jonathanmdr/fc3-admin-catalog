package org.fullcycle.admin.catalog.infrastructure.genre;

import org.fullcycle.admin.catalog.domain.Identifier;
import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import org.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreJpaEntity;
import org.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreRepository;
import org.fullcycle.admin.catalog.infrastructure.utils.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.fullcycle.admin.catalog.infrastructure.utils.SpecificationUtils.like;

@Component
public class GenreDatabaseGateway implements GenreGateway {

    private final GenreRepository genreRepository;

    public GenreDatabaseGateway(final GenreRepository genreRepository) {
        this.genreRepository = Objects.requireNonNull(genreRepository);
    }

    @Override
    public Pagination<Genre> findAll(final SearchQuery searchQuery) {
        final var pageRequest = PageRequest.of(
            searchQuery.page(),
            searchQuery.perPage(),
            Sort.by(Sort.Direction.fromString(searchQuery.direction()), searchQuery.sort())
        );

        final var specifications = Optional.ofNullable(searchQuery.terms())
            .filter(StringUtils::isNotBlank)
            .map(this::applyTerms)
            .orElse(null);

        final var page = this.genreRepository.findAll(Specification.where(specifications), pageRequest);

        return new Pagination<>(
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.map(GenreJpaEntity::toAggregate).toList()
        );
    }

    @Override
    public Optional<Genre> findById(final GenreID genreID) {
        return this.genreRepository.findById(genreID.getValue())
            .map(GenreJpaEntity::toAggregate);
    }

    @Override
    public Genre create(final Genre genre) {
        return save(genre);
    }

    @Override
    public Genre update(final Genre genre) {
        return save(genre);
    }

    @Override
    public void deleteById(final GenreID genreID) {
        final var id = genreID.getValue();
        if (this.genreRepository.existsById(id)) {
            this.genreRepository.deleteById(id);
        }
    }

    @Override
    public List<GenreID> existsByIds(final Iterable<GenreID> genreIds) {
        final var ids = StreamSupport.stream(genreIds.spliterator(), false)
            .map(Identifier::getValue)
            .toList();

        return this.genreRepository.existsByIds(ids)
            .stream()
            .map(GenreID::from)
            .toList();
    }

    private Genre save(final Genre genre) {
        return this.genreRepository.save(
            GenreJpaEntity.from(genre)
        ).toAggregate();
    }

    private Specification<GenreJpaEntity> applyTerms(final String term) {
        return like("name", term);
    }

}
