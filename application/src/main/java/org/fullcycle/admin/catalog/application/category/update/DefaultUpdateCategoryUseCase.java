package org.fullcycle.admin.catalog.application.category.update;

import io.vavr.control.Either;
import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.validation.handler.NotificationHandler;

import java.util.Objects;
import java.util.function.Supplier;

import static io.vavr.API.Left;
import static io.vavr.API.Try;

public class DefaultUpdateCategoryUseCase extends UpdateCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultUpdateCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<NotificationHandler, UpdateCategoryOutput> execute(final UpdateCategoryCommand updateCategoryCommand) {
        final var id = CategoryID.from(updateCategoryCommand.id());
        final var name = updateCategoryCommand.name();
        final var description = updateCategoryCommand.description();
        final var isActive = updateCategoryCommand.isActive();

        final var notification = NotificationHandler.create();
        final var category = this.categoryGateway.findById(id)
                .orElseThrow(categoryNotFound(id));

        category.update(name, description, isActive)
            .validate(notification);

        return notification.hasErrors() ? Left(notification) : update(category);
    }

    private Either<NotificationHandler, UpdateCategoryOutput> update(final Category category) {
        return Try(() -> this.categoryGateway.update(category))
            .toEither()
            .bimap(NotificationHandler::create, UpdateCategoryOutput::from);
    }

    private Supplier<NotFoundException> categoryNotFound(final CategoryID id) {
        return () -> NotFoundException.with(Category.class, id);
    }

}
