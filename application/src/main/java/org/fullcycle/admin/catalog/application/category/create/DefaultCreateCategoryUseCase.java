package org.fullcycle.admin.catalog.application.category.create;

import io.vavr.control.Either;
import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.validation.handler.NotificationValidationHandler;

import java.util.Objects;

import static io.vavr.API.Left;
import static io.vavr.API.Try;

public class DefaultCreateCategoryUseCase extends CreateCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultCreateCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<NotificationValidationHandler, CreateCategoryOutput> execute(final CreateCategoryCommand createCategoryCommand) {
        final var name = createCategoryCommand.name();
        final var description = createCategoryCommand.description();
        final var isActive = createCategoryCommand.isActive();

        final var notification = NotificationValidationHandler.create();
        final var category = Category.newCategory(name, description, isActive);
        category.validate(notification);

        return notification.hasErrors() ? Left(notification) : create(category);
    }

    private Either<NotificationValidationHandler, CreateCategoryOutput> create(final Category category) {
        return Try(() -> this.categoryGateway.create(category))
            .toEither()
            .bimap(NotificationValidationHandler::create, CreateCategoryOutput::from);
    }

}
