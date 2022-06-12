package org.fullcycle.admin.catalog.application.category.delete;

import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;

import java.util.Objects;

public class DefaultDeleteCategoryUseCase extends DeleteCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultDeleteCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public void execute(final DeleteCategoryCommand deleteCategoryCommand) {
        final var id = CategoryID.from(deleteCategoryCommand.id());
        this.categoryGateway.deleteById(id);
    }

}
