package org.fullcycle.admin.catalog.application.category.retrieve.get;

import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.DomainException;
import org.fullcycle.admin.catalog.domain.validation.Error;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultGetCategoryByIdUseCase extends GetCategoryByIdUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultGetCategoryByIdUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public GetCategoryByIdOutput execute(final GetCategoryByIdCommand getCategoryByIdCommand) {
        final var id = CategoryID.from(getCategoryByIdCommand.id());

        return this.categoryGateway.findById(id)
            .map(GetCategoryByIdOutput::from)
            .orElseThrow(categoryNotFound(id));
    }

    private Supplier<DomainException> categoryNotFound(final CategoryID id) {
        return () -> DomainException.with(
            new Error("Category with ID %s was not found".formatted(id.getValue()))
        );
    }

}
