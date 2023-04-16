package org.fullcycle.admin.catalog.application.castmember.retrieve.list;

import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.domain.pagination.SearchQuery;

import java.util.Objects;

public class DefaultListCastMembersUseCase extends ListCastMembersUseCase {

    private final CastMemberGateway castMemberGateway;

    public DefaultListCastMembersUseCase(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public Pagination<ListCastMembersOutput> execute(final ListCastMembersCommand command) {
        final var page = command.page();
        final var perPage = command.perPage();
        final var terms = command.terms();
        final var sort = command.sort();
        final var direction = command.direction();

        final var query = new SearchQuery(page, perPage, terms, sort, direction);

        return this.castMemberGateway.findAll(query)
            .map(ListCastMembersOutput::from);
    }

}
