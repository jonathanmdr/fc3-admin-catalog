package org.fullcycle.admin.catalog.application.genre.update;

import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.DomainException;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.domain.validation.Error;
import org.fullcycle.admin.catalog.domain.validation.ValidationHandler;
import org.fullcycle.admin.catalog.domain.validation.handler.NotificationHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DefaultUpdateGenreUseCase extends UpdateGenreUseCase {

    private final CategoryGateway categoryGateway;
    private final GenreGateway genreGateway;

    public DefaultUpdateGenreUseCase(
        final CategoryGateway categoryGateway,
        final GenreGateway genreGateway
    ) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Override
    public UpdateGenreOutput execute(final UpdateGenreCommand command) {
        final var id = GenreID.from(command.id());
        final var name = command.name();
        final var isActive = command.isActive();
        final var categories = toCategoryId(command.categories());

        final var genre = this.genreGateway.findById(id)
            .orElseThrow(notFoundException(id));

        final var notification = NotificationHandler.create();
        notification.append(validateCategories(categories));
        notification.validate(() -> genre.update(name, isActive, categories));

        if (notification.hasErrors()) {
            throw new NotificationException("Could not update aggregate genre %s".formatted(command.id()), notification);
        }

        return UpdateGenreOutput.from(this.genreGateway.update(genre));
    }

    private List<CategoryID> toCategoryId(final List<String> ids) {
        return ids.stream()
            .map(CategoryID::from)
            .toList();
    }

    private Supplier<DomainException> notFoundException(final GenreID id) {
        return () -> NotFoundException.with(Genre.class, id);
    }

    private ValidationHandler validateCategories(final List<CategoryID> categoryIds) {
        final var notification = NotificationHandler.create();
        if (Objects.isNull(categoryIds) || categoryIds.isEmpty()) {
            return notification;
        }

        final var retrievedIds = this.categoryGateway.existsByIds(categoryIds);

        if (categoryIds.size() != retrievedIds.size()) {
            final var missingIds = new ArrayList<>(categoryIds);
            missingIds.removeAll(retrievedIds);

            final var missingIdsToMessage = missingIds.stream()
                .map(CategoryID::getValue)
                .collect(Collectors.joining(", "));

            notification.append(new Error("Some categories could not be found: %s".formatted(missingIdsToMessage)));
        }

        return notification;
    }

}
