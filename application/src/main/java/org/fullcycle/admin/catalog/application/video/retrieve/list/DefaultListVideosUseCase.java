package org.fullcycle.admin.catalog.application.video.retrieve.list;

import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.domain.video.VideoGateway;
import org.fullcycle.admin.catalog.domain.video.VideoSearchQuery;

import java.util.Objects;

import static org.fullcycle.admin.catalog.domain.utils.CollectionUtils.mapTo;

public class DefaultListVideosUseCase extends ListVideosUseCase {

    private final VideoGateway videoGateway;

    public DefaultListVideosUseCase(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public Pagination<ListVideosOutput> execute(final ListVideosCommand command) {
        final var page = command.page();
        final var perPage = command.perPage();
        final var terms = command.terms();
        final var sort = command.sort();
        final var direction = command.direction();
        final var categories = mapTo(command.categories(), CategoryID::from);
        final var genres = mapTo(command.categories(), GenreID::from);
        final var castMembers = mapTo(command.categories(), CastMemberID::from);

        final var query = new VideoSearchQuery(
            page,
            perPage,
            terms,
            sort,
            direction,
            categories,
            genres,
            castMembers
        );

        return this.videoGateway.findAll(query)
            .map(ListVideosOutput::from);
    }

}
