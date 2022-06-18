package org.fullcycle.admin.catalog.infrastructure.category.presenters;

import org.fullcycle.admin.catalog.application.category.retrieve.get.GetCategoryByIdOutput;
import org.fullcycle.admin.catalog.infrastructure.category.models.GetCategoryApiOutput;

import java.util.function.Function;

public interface CategoryApiPresenter {

    static GetCategoryApiOutput present(final GetCategoryByIdOutput output) {
        return new GetCategoryApiOutput(
            output.id().getValue(),
            output.name(),
            output.description(),
            output.isActive(),
            output.createdAt(),
            output.updatedAt(),
            output.deletedAt()
        );
    }

}
