package org.fullcycle.admin.catalog.application.genre.create;

import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.validation.Error;
import org.fullcycle.admin.catalog.domain.validation.ValidationHandler;
import org.fullcycle.admin.catalog.domain.validation.handler.NotificationHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultCreateGenreUseCase extends CreateGenreUseCase {

    private final CategoryGateway categoryGateway;
    private final GenreGateway genreGateway;

    public DefaultCreateGenreUseCase(final CategoryGateway categoryGateway, final GenreGateway genreGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Override
    public CreateGenreOutput execute(final CreateGenreCommand command) {
        final var name = command.name();
        final var isActive = command.isActive();
        final var categories = command.categories()
            .stream()
            .map(CategoryID::from)
            .toList();

        final var notification = NotificationHandler.create();
        notification.append(validateCategories(categories));

        final var genre = notification.validate(() -> Genre.newGenre(name, isActive));

        if (notification.hasErrors()) {
            throw new NotificationException("Could not create aggregate genre", notification);
        }

        genre.addCategories(categories);

        return CreateGenreOutput.from(this.genreGateway.create(genre));
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
