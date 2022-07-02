package org.fullcycle.admin.catalog.application.category.retrieve.list;

import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;

import java.util.Objects;

public class DefaultListCategoriesUseCase extends ListCategoriesUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultListCategoriesUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Pagination<ListCategoryOutput> execute(final ListCategoriesCommand listCategoriesCommand) {
        final var page = listCategoriesCommand.page();
        final var perPage = listCategoriesCommand.perPage();
        final var terms = listCategoriesCommand.terms();
        final var sort = listCategoriesCommand.sort();
        final var direction = listCategoriesCommand.direction();

        final var query = new SearchQuery(page, perPage, terms, sort, direction);

        return this.categoryGateway.findAll(query)
            .map(ListCategoryOutput::from);
    }

}
