package org.fullcycle.admin.catalog.infrastructure.genre.presenters;

import org.fullcycle.admin.catalog.application.genre.create.CreateGenreOutput;
import org.fullcycle.admin.catalog.application.genre.retrieve.get.GetGenreByIdOutput;
import org.fullcycle.admin.catalog.application.genre.retrieve.list.ListGenresOutput;
import org.fullcycle.admin.catalog.application.genre.update.UpdateGenreOutput;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.infrastructure.genre.models.CreateGenreResponse;
import org.fullcycle.admin.catalog.infrastructure.genre.models.GetGenreResponse;
import org.fullcycle.admin.catalog.infrastructure.genre.models.ListGenreResponse;
import org.fullcycle.admin.catalog.infrastructure.genre.models.UpdateGenreResponse;

public interface GenreApiPresenter {

    static ListGenreResponse present(final ListGenresOutput output) {
        return new ListGenreResponse(
            output.id().getValue(),
            output.name(),
            output.isActive(),
            output.createdAt(),
            output.deletedAt()
        );
    }

    static GetGenreResponse present(final GetGenreByIdOutput output) {
        final var categories = output.categories()
            .stream()
            .map(CategoryID::getValue)
            .toList();

        return new GetGenreResponse(
            output.id().getValue(),
            output.name(),
            categories,
            output.isActive(),
            output.createdAt(),
            output.updatedAt(),
            output.deletedAt()
        );
    }

    static CreateGenreResponse present(final CreateGenreOutput output) {
        return new CreateGenreResponse(output.id());
    }

    static UpdateGenreResponse present(final UpdateGenreOutput output) {
        return new UpdateGenreResponse(output.id());
    }

}
