package org.fullcycle.admin.catalog.application.category.retrieve.get;

import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;

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

    private Supplier<NotFoundException> categoryNotFound(final CategoryID id) {
        return () -> NotFoundException.with(Category.class, id);
    }

}
