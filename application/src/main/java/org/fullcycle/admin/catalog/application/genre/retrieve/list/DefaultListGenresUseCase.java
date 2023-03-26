package org.fullcycle.admin.catalog.application.genre.retrieve.list;

import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.domain.pagination.SearchQuery;

import java.util.Objects;

public class DefaultListGenresUseCase extends ListGenresUseCase {

    private final GenreGateway genreGateway;

    public DefaultListGenresUseCase(final GenreGateway genreGateway) {
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Override
    public Pagination<ListGenresOutput> execute(final ListGenresCommand command) {
        final var page = command.page();
        final var perPage = command.perPage();
        final var terms = command.terms();
        final var sort = command.sort();
        final var direction = command.direction();

        final var query = new SearchQuery(page, perPage, terms, sort, direction);

        return this.genreGateway.findAll(query)
            .map(ListGenresOutput::from);
    }

}
